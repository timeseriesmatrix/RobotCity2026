#include "postgresApi.h"
#include "ocr.h"
#include "utilities.hxx"

#include <format>
#include <utility>
#include <regex>
#include <sstream>
#include <iomanip>
#include <ctime>
#include <fstream>
#include <cmath>
#include <algorithm>
#include <unordered_set>
#include <stdexcept>
#include <filesystem>
#include <system_error>
#include <limits>
#include <cctype>
#include <cstdlib>
#include <chrono>
#include <array>
#include <thread>
#include <mutex>
#include <unistd.h>
#include <openssl/sha.h>
#include <spdlog/spdlog.h>

using json = nlohmann::json;

namespace {
long long get_int64(const pqxx::row &r, const char* name, long long def = 0) {
    try {
        const auto &f = r[name];
        if (f.is_null()) return def;
        return f.as<long long>();
    } catch (...) {
        return def;
    }
}

double get_double(const pqxx::row &r, const char* name, double def = 0.0) {
    try {
        const auto &f = r[name];
        if (f.is_null()) return def;
        return f.as<double>();
    } catch (...) {
        return def;
    }
}

std::string get_string(const pqxx::row &r, const char* name, const std::string &def = {}) {
    try {
        const auto &f = r[name];
        if (f.is_null()) return def;
        return f.as<std::string>();
    } catch (...) {
        return def;
    }
}
} // namespace

namespace {
std::mutex g_tracker_schema_cache_mutex;
std::unordered_set<std::string> g_tracker_schema_ready;

std::string trim_copy(std::string s)
{
    const auto first = s.find_first_not_of(" \t\n\r");
    if (first == std::string::npos) return {};
    const auto last = s.find_last_not_of(" \t\n\r");
    return s.substr(first, last - first + 1);
}

std::string lower_ascii(std::string s)
{
    std::transform(s.begin(), s.end(), s.begin(), [](unsigned char c) {
        return static_cast<char>(std::tolower(c));
    });
    return s;
}

std::string openai_ocr_key_from_env()
{
    const char *ocr_key = std::getenv("OPENAI_OCR_KEY");
    if (ocr_key && *ocr_key) return trim_copy(ocr_key);

    const char *api_key = std::getenv("OPENAI_API_KEY");
    if (api_key && *api_key) return trim_copy(api_key);

    return {};
}

std::string join_strings(const std::vector<std::string> &parts, const std::string &separator)
{
    if (parts.empty()) return {};
    std::ostringstream out;
    for (std::size_t index = 0; index < parts.size(); ++index) {
        if (index > 0) out << separator;
        out << parts[index];
    }
    return out.str();
}

std::string collapse_prompt_whitespace(std::string s)
{
    s = trim_copy(std::move(s));
    if (s.empty()) return {};

    std::string out;
    out.reserve(s.size());
    bool previous_space = false;
    for (unsigned char c : s) {
        if (std::isspace(c)) {
            if (!previous_space) {
                out.push_back(' ');
                previous_space = true;
            }
            continue;
        }
        out.push_back(static_cast<char>(c));
        previous_space = false;
    }
    return trim_copy(std::move(out));
}

std::string normalize_identity_key(std::string s)
{
    s = trim_copy(std::move(s));
    if (s.empty()) return {};

    std::string out;
    out.reserve(s.size());
    for (unsigned char c : s) {
        if (c >= 128) {
            out.push_back(static_cast<char>(c));
            continue;
        }
        if (std::isalnum(c)) {
            out.push_back(static_cast<char>(std::toupper(c)));
        }
    }
    return out;
}

std::string normalize_supplier_tin_key(std::string s)
{
    s = trim_copy(std::move(s));
    if (s.empty()) return {};

    std::string digits;
    digits.reserve(s.size());
    bool saw_invalid = false;
    for (unsigned char c : s) {
        if (std::isdigit(c)) {
            digits.push_back(static_cast<char>(c));
        } else if (std::isspace(c) || c == '-' || c == '.' || c == ',' || c == '(' || c == ')' || c == '/') {
            continue;
        } else {
            saw_invalid = true;
            break;
        }
    }

    if (saw_invalid || digits.empty()) return {};
    return digits;
}

void refresh_supplier_identity_keys(pqxx::transaction_base &txn)
{
    const pqxx::result rows = txn.exec(
        "SELECT id, COALESCE(tin, '') AS tin, COALESCE(name, '') AS name, COALESCE(site, '') AS site "
        "FROM tracker.suppliers"
    );

    for (const auto &row : rows) {
        const int id = row["id"].as<int>();
        const std::string normalized_tin = normalize_supplier_tin_key(get_string(row, "tin"));
        const std::string name_key = normalize_identity_key(get_string(row, "name"));
        const std::string site_key = normalize_identity_key(get_string(row, "site"));

        txn.exec_params(R"(
            UPDATE tracker.suppliers
            SET tin = NULLIF($1, ''),
                tin_key = NULLIF($2, ''),
                name_key = NULLIF($3, ''),
                site_key = NULLIF($4, ''),
                updated_at = CASE
                    WHEN COALESCE(tin, '') IS DISTINCT FROM COALESCE(NULLIF($1, ''), '')
                      OR COALESCE(tin_key, '') IS DISTINCT FROM COALESCE(NULLIF($2, ''), '')
                      OR COALESCE(name_key, '') IS DISTINCT FROM COALESCE(NULLIF($3, ''), '')
                      OR COALESCE(site_key, '') IS DISTINCT FROM COALESCE(NULLIF($4, ''), '')
                    THEN CURRENT_TIMESTAMP
                    ELSE updated_at
                END
            WHERE id = $5
        )",
            normalized_tin,
            normalized_tin,
            name_key,
            site_key,
            id
        );
    }
}

void merge_duplicate_suppliers(pqxx::transaction_base &txn, const std::string &partition_sql, const std::string &where_sql)
{
    txn.exec(
        "WITH ranked AS ("
        "    SELECT id, MIN(id) OVER (PARTITION BY " + partition_sql + ") AS keep_id"
        "    FROM tracker.suppliers"
        "    WHERE " + where_sql +
        "), dup AS ("
        "    SELECT id AS drop_id, keep_id FROM ranked WHERE id <> keep_id"
        "), merged_values AS ("
        "    SELECT d.keep_id,"
        "           MAX(NULLIF(BTRIM(s.name), '')) AS name,"
        "           MAX(NULLIF(BTRIM(s.contact_info), '')) AS contact_info,"
        "           MAX(NULLIF(BTRIM(s.site), '')) AS site,"
        "           MAX(NULLIF(BTRIM(s.tin), '')) AS tin,"
        "           MAX(s.ocr_id) AS ocr_id,"
        "           MAX(s.ocr_page_id) AS ocr_page_id"
        "    FROM dup d"
        "    JOIN tracker.suppliers s ON s.id = d.drop_id"
        "    GROUP BY d.keep_id"
        ")"
        " UPDATE tracker.suppliers s"
        " SET name = COALESCE(NULLIF(BTRIM(s.name), ''), mv.name, s.name),"
        "     contact_info = COALESCE(NULLIF(BTRIM(s.contact_info), ''), mv.contact_info, s.contact_info),"
        "     site = COALESCE(NULLIF(BTRIM(s.site), ''), mv.site, s.site),"
        "     tin = COALESCE(NULLIF(BTRIM(s.tin), ''), mv.tin, s.tin),"
        "     ocr_id = COALESCE(s.ocr_id, mv.ocr_id),"
        "     ocr_page_id = COALESCE(s.ocr_page_id, mv.ocr_page_id),"
        "     updated_at = CURRENT_TIMESTAMP"
        " FROM merged_values mv"
        " WHERE s.id = mv.keep_id;"
    );

    txn.exec(
        "WITH ranked AS ("
        "    SELECT id, MIN(id) OVER (PARTITION BY " + partition_sql + ") AS keep_id"
        "    FROM tracker.suppliers"
        "    WHERE " + where_sql +
        "), dup AS ("
        "    SELECT id AS drop_id, keep_id FROM ranked WHERE id <> keep_id"
        ")"
        " UPDATE tracker.products p"
        " SET supplier_id = dup.keep_id"
        " FROM dup"
        " WHERE p.supplier_id = dup.drop_id;"
    );

    txn.exec(
        "WITH ranked AS ("
        "    SELECT id, MIN(id) OVER (PARTITION BY " + partition_sql + ") AS keep_id"
        "    FROM tracker.suppliers"
        "    WHERE " + where_sql +
        "), dup AS ("
        "    SELECT id AS drop_id, keep_id FROM ranked WHERE id <> keep_id"
        ")"
        " UPDATE tracker.purchase_orders po"
        " SET supplier_id = dup.keep_id"
        " FROM dup"
        " WHERE po.supplier_id = dup.drop_id;"
    );

    txn.exec(
        "WITH ranked AS ("
        "    SELECT id, MIN(id) OVER (PARTITION BY " + partition_sql + ") AS keep_id"
        "    FROM tracker.suppliers"
        "    WHERE " + where_sql +
        "), dup AS ("
        "    SELECT id AS drop_id FROM ranked WHERE id <> keep_id"
        ")"
        " DELETE FROM tracker.suppliers s"
        " USING dup"
        " WHERE s.id = dup.drop_id;"
    );
}

void refresh_product_identity_keys(pqxx::transaction_base &txn)
{
    const pqxx::result rows = txn.exec(
        "SELECT id, COALESCE(name, '') AS name FROM tracker.products"
    );

    for (const auto &row : rows) {
        const int id = row["id"].as<int>();
        const std::string name_key = normalize_identity_key(get_string(row, "name"));
        txn.exec_params(R"(
            UPDATE tracker.products
            SET name_key = NULLIF($1, ''),
                updated_at = CASE
                    WHEN COALESCE(name_key, '') IS DISTINCT FROM COALESCE(NULLIF($1, ''), '')
                    THEN CURRENT_TIMESTAMP
                    ELSE updated_at
                END
            WHERE id = $2
        )",
            name_key,
            id
        );
    }
}

void merge_duplicate_products(pqxx::transaction_base &txn)
{
    txn.exec(R"(
        WITH ranked AS (
            SELECT id,
                   MIN(id) OVER (PARTITION BY shop_id, COALESCE(supplier_id, 0), COALESCE(name_key, '')) AS keep_id
            FROM tracker.products
            WHERE name_key IS NOT NULL AND BTRIM(name_key) <> ''
        ), dup AS (
            SELECT id AS drop_id, keep_id FROM ranked WHERE id <> keep_id
        ), merged_values AS (
            SELECT d.keep_id,
                   MAX(NULLIF(BTRIM(p.category), '')) AS category,
                   MAX(p.default_unit_price) AS default_unit_price,
                   BOOL_OR(p.is_active) AS is_active,
                   MAX(p.ocr_id) AS ocr_id,
                   MAX(p.ocr_page_id) AS ocr_page_id
            FROM dup d
            JOIN tracker.products p ON p.id = d.drop_id
            GROUP BY d.keep_id
        )
        UPDATE tracker.products p
        SET category = COALESCE(NULLIF(BTRIM(p.category), ''), merged_values.category, p.category),
            default_unit_price = COALESCE(p.default_unit_price, merged_values.default_unit_price),
            is_active = p.is_active OR merged_values.is_active,
            ocr_id = COALESCE(p.ocr_id, merged_values.ocr_id),
            ocr_page_id = COALESCE(p.ocr_page_id, merged_values.ocr_page_id),
            updated_at = CURRENT_TIMESTAMP
        FROM merged_values
        WHERE p.id = merged_values.keep_id;
    )");

    txn.exec(R"(
        WITH ranked AS (
            SELECT id,
                   MIN(id) OVER (PARTITION BY shop_id, COALESCE(supplier_id, 0), COALESCE(name_key, '')) AS keep_id
            FROM tracker.products
            WHERE name_key IS NOT NULL AND BTRIM(name_key) <> ''
        ), dup AS (
            SELECT id AS drop_id, keep_id FROM ranked WHERE id <> keep_id
        )
        UPDATE tracker.purchase_items pi
        SET product_id = dup.keep_id
        FROM dup
        WHERE pi.product_id = dup.drop_id;
    )");

    txn.exec(R"(
        WITH ranked AS (
            SELECT id,
                   MIN(id) OVER (PARTITION BY shop_id, COALESCE(supplier_id, 0), COALESCE(name_key, '')) AS keep_id
            FROM tracker.products
            WHERE name_key IS NOT NULL AND BTRIM(name_key) <> ''
        ), dup AS (
            SELECT id AS drop_id, keep_id FROM ranked WHERE id <> keep_id
        )
        UPDATE tracker.purchase_draft_items pdi
        SET match_product_id = dup.keep_id
        FROM dup
        WHERE pdi.match_product_id = dup.drop_id;
    )");

    txn.exec(R"(
        WITH ranked AS (
            SELECT id,
                   MIN(id) OVER (PARTITION BY shop_id, COALESCE(supplier_id, 0), COALESCE(name_key, '')) AS keep_id
            FROM tracker.products
            WHERE name_key IS NOT NULL AND BTRIM(name_key) <> ''
        ), dup AS (
            SELECT id AS drop_id FROM ranked WHERE id <> keep_id
        )
        DELETE FROM tracker.products p
        USING dup
        WHERE p.id = dup.drop_id;
    )");
}

std::string json_string_or(const json &obj, const char *key, const std::string &def = {})
{
    if (!obj.is_object() || !obj.contains(key) || obj[key].is_null()) return def;
    const auto &value = obj[key];
    if (value.is_string()) return value.get<std::string>();
    if (value.is_number_integer()) return std::to_string(value.get<long long>());
    if (value.is_number_unsigned()) return std::to_string(value.get<unsigned long long>());
    if (value.is_number_float()) return std::to_string(value.get<double>());
    if (value.is_boolean()) return value.get<bool>() ? "true" : "false";
    return def;
}

std::vector<std::string> sanitize_category_names(const json &raw_categories);
json json_array_from_strings(const std::vector<std::string> &values);
std::vector<std::string> diff_category_names(const std::vector<std::string> &lhs, const std::vector<std::string> &rhs);
std::string canonicalize_category_name(const std::string &raw_category, const std::vector<std::string> &allowed_categories);

std::string conninfo_value(const std::string &conninfo, const std::string &key)
{
    if (conninfo.empty() || key.empty()) return {};
    std::regex pattern("(^|\\s)" + key + "=([^\\s]+)");
    std::smatch match;
    if (std::regex_search(conninfo, match, pattern) && match.size() >= 3) {
        return match[2].str();
    }
    return {};
}

std::string redact_conninfo_password(const std::string &conninfo)
{
    if (conninfo.empty()) return {};
    return std::regex_replace(conninfo, std::regex("(password=)([^\\s]+)"), "$1***");
}

std::string dbname_from_conninfo(const std::string &conninfo)
{
    return conninfo_value(conninfo, "dbname");
}

PostgresApi::DbSource settings_source_from_json(const json &src, const char *label)
{
    PostgresApi::DbSource out;
    if (!src.is_object()) return out;

    auto resolve_secret = [&](const char *env_key, const char *raw_key) {
        std::string env_name = trim_copy(json_string_or(src, env_key));
        if (!env_name.empty()) {
            if (const char *env = std::getenv(env_name.c_str())) {
                return std::string(env);
            }
            spdlog::warn("[PostgresApi] settings {} env '{}' is not set", label, env_name);
        }
        return json_string_or(src, raw_key);
    };

    out.host     = trim_copy(json_string_or(src, "host"));
    out.port     = trim_copy(json_string_or(src, "port"));
    out.dbname   = trim_copy(json_string_or(src, "dbname"));
    out.user_env = trim_copy(json_string_or(src, "user_env"));
    out.pass_env = trim_copy(json_string_or(src, "pass_env"));
    out.user     = trim_copy(resolve_secret("user_env", "user"));
    out.password = resolve_secret("pass_env", "password");
    out.conninfo = trim_copy(json_string_or(src, "conninfo"));

    if (!out.conninfo.empty()) {
        if (out.host.empty())   out.host = conninfo_value(out.conninfo, "host");
        if (out.port.empty())   out.port = conninfo_value(out.conninfo, "port");
        if (out.dbname.empty()) out.dbname = dbname_from_conninfo(out.conninfo);
        if (out.user.empty())   out.user = conninfo_value(out.conninfo, "user");
    } else if (!out.dbname.empty() && !out.user.empty()) {
        if (out.host.empty()) out.host = "localhost";
        if (out.port.empty()) out.port = "5432";
        out.conninfo = std::format(
            "host={} port={} dbname={} user={} password={}",
            out.host,
            out.port,
            out.dbname,
            out.user,
            out.password
        );
    }

    return out;
}

std::string source_label_for_kind(PostgresApi::SourceKind kind)
{
    return kind == PostgresApi::SourceKind::Pos ? "POS" : "expense";
}

bool is_test_db_name(const std::string &dbname)
{
    return dbname.size() >= 3 && dbname.ends_with("tst");
}

bool is_safe_created_database_name(const std::string &dbname)
{
    if (dbname.empty() || dbname.size() > 63) return false;
    return std::all_of(dbname.begin(), dbname.end(), [](unsigned char c) {
        return std::isalnum(c) || c == '_';
    });
}

std::string conninfo_with_dbname(const PostgresApi::DbSource &source, const std::string &dbname)
{
    if (source.conninfo.empty()) {
        std::string host = source.host.empty() ? "localhost" : source.host;
        std::string port = source.port.empty() ? "5432" : source.port;
        return std::format(
            "host={} port={} dbname={} user={} password={}",
            host,
            port,
            dbname,
            source.user,
            source.password
        );
    }

    static const std::regex dbname_pattern("(^|\\s)dbname=([^\\s]+)");
    if (std::regex_search(source.conninfo, dbname_pattern)) {
        return std::regex_replace(
            source.conninfo,
            dbname_pattern,
            "$1dbname=" + dbname,
            std::regex_constants::format_first_only
        );
    }
    return source.conninfo + " dbname=" + dbname;
}

bool create_database_if_missing(const PostgresApi::DbSource &source)
{
    const std::string dbname = trim_copy(source.dbname.empty() ? dbname_from_conninfo(source.conninfo) : source.dbname);
    if (!is_safe_created_database_name(dbname)) {
        throw std::runtime_error(
            "Expense database name is required and may contain only letters, numbers, and underscores when creating a database."
        );
    }

    pqxx::connection admin(conninfo_with_dbname(source, "postgres"));
    pqxx::nontransaction txn(admin);
    const pqxx::result existing = txn.exec_params(
        "SELECT 1 FROM pg_database WHERE datname = $1 LIMIT 1",
        dbname
    );
    if (!existing.empty()) {
        return false;
    }

    txn.exec("CREATE DATABASE " + txn.quote_name(dbname));
    return true;
}

std::string base64_decode(const std::string &input)
{
    static const std::array<int, 256> lookup = [] {
        std::array<int, 256> table{};
        table.fill(-1);
        for (std::size_t i = 0; i < base64_chars.size(); ++i) {
            table[static_cast<unsigned char>(base64_chars[i])] = static_cast<int>(i);
        }
        return table;
    }();

    std::string decoded;
    int val = 0;
    int valb = -8;
    for (unsigned char c : input) {
        if (std::isspace(c)) continue;
        if (c == '=') break;
        const int d = lookup[c];
        if (d < 0) {
            throw std::runtime_error("Invalid base64 input");
        }
        val = (val << 6) + d;
        valb += 6;
        if (valb >= 0) {
            decoded.push_back(static_cast<char>((val >> valb) & 0xFF));
            valb -= 8;
        }
    }
    return decoded;
}

cv::Rect clamp_rect_to_image(const cv::Rect &rect, const cv::Size &size)
{
    const cv::Rect image_rect(0, 0, size.width, size.height);
    return rect & image_rect;
}

cv::Rect expand_rect_in_image(const cv::Rect &rect, int pad_x, int pad_y, const cv::Size &size)
{
    const cv::Rect expanded(rect.x - pad_x,
                            rect.y - pad_y,
                            rect.width + pad_x * 2,
                            rect.height + pad_y * 2);
    return clamp_rect_to_image(expanded, size);
}

cv::Mat resize_receipt_image_for_ocr(const cv::Mat &raw, int max_width)
{
    if (raw.empty()) return {};
    if (raw.cols <= max_width) return raw.clone();

    cv::Mat resized;
    cv::resize(raw, resized, cv::Size(max_width, raw.rows * max_width / raw.cols));
    return resized;
}

std::string encode_png_base64(const cv::Mat &image)
{
    if (image.empty()) return {};

    std::vector<uchar> buf;
    cv::imencode(".png", image, buf);
    return base64_encode(std::string(reinterpret_cast<const char*>(buf.data()), buf.size()));
}

cv::Rect best_table_rect_from_mask(const cv::Mat &mask, const cv::Size &image_size)
{
    if (mask.empty()) return {};

    std::vector<std::vector<cv::Point>> contours;
    cv::findContours(mask, contours, cv::RETR_EXTERNAL, cv::CHAIN_APPROX_SIMPLE);

    double best_score = 0.0;
    cv::Rect best_rect;
    for (const auto &contour : contours) {
        const cv::Rect rect = cv::boundingRect(contour);
        if (rect.width <= 0 || rect.height <= 0) continue;

        const double width_ratio = static_cast<double>(rect.width) / static_cast<double>(image_size.width);
        const double height_ratio = static_cast<double>(rect.height) / static_cast<double>(image_size.height);
        const double top_ratio = static_cast<double>(rect.y) / static_cast<double>(image_size.height);
        const double bottom_ratio = static_cast<double>(rect.y + rect.height) / static_cast<double>(image_size.height);
        if (width_ratio < 0.42 || height_ratio < 0.08 || height_ratio > 0.70) continue;
        if (top_ratio > 0.72 || bottom_ratio < 0.16) continue;

        const double center_ratio = static_cast<double>(rect.y + rect.height / 2.0) / static_cast<double>(image_size.height);
        const double vertical_score = 1.0 - std::min(1.0, std::abs(center_ratio - 0.40) / 0.40);
        const double score = static_cast<double>(rect.area()) * (0.8 + 0.2 * vertical_score);
        if (score > best_score) {
            best_score = score;
            best_rect = rect;
        }
    }

    return best_rect;
}

cv::Mat extract_line_items_focus_crop(const cv::Mat &image)
{
    if (image.empty()) return {};

    cv::Mat gray;
    if (image.channels() == 3) {
        cv::cvtColor(image, gray, cv::COLOR_BGR2GRAY);
    } else {
        gray = image.clone();
    }

    cv::Mat blurred;
    cv::GaussianBlur(gray, blurred, cv::Size(3, 3), 0);

    cv::Mat binary_inv;
    cv::adaptiveThreshold(blurred,
                          binary_inv,
                          255,
                          cv::ADAPTIVE_THRESH_GAUSSIAN_C,
                          cv::THRESH_BINARY_INV,
                          31,
                          12);

    const int horizontal_len = std::max(40, image.cols / 12);
    const int vertical_len = std::max(24, image.rows / 18);

    cv::Mat horizontal;
    cv::morphologyEx(binary_inv,
                     horizontal,
                     cv::MORPH_OPEN,
                     cv::getStructuringElement(cv::MORPH_RECT, cv::Size(horizontal_len, 1)));

    cv::Mat vertical;
    cv::morphologyEx(binary_inv,
                     vertical,
                     cv::MORPH_OPEN,
                     cv::getStructuringElement(cv::MORPH_RECT, cv::Size(1, vertical_len)));

    cv::Mat grid_mask;
    cv::bitwise_or(horizontal, vertical, grid_mask);
    cv::dilate(grid_mask,
               grid_mask,
               cv::getStructuringElement(cv::MORPH_RECT,
                                         cv::Size(std::max(18, image.cols / 70), std::max(8, image.rows / 110))));

    cv::Rect best_rect = best_table_rect_from_mask(grid_mask, image.size());

    if (best_rect.area() == 0) {
        cv::Mat text_mask;
        cv::morphologyEx(binary_inv,
                         text_mask,
                         cv::MORPH_CLOSE,
                         cv::getStructuringElement(cv::MORPH_RECT,
                                                   cv::Size(std::max(16, image.cols / 45), std::max(5, image.rows / 120))));
        cv::dilate(text_mask,
                   text_mask,
                   cv::getStructuringElement(cv::MORPH_RECT,
                                             cv::Size(std::max(22, image.cols / 28), std::max(8, image.rows / 90))));
        best_rect = best_table_rect_from_mask(text_mask, image.size());
    }

    if (best_rect.area() == 0) {
        const int fallback_y = static_cast<int>(std::lround(image.rows * 0.16));
        const int fallback_height = static_cast<int>(std::lround(image.rows * 0.44));
        best_rect = clamp_rect_to_image(cv::Rect(static_cast<int>(std::lround(image.cols * 0.05)),
                                                 fallback_y,
                                                 static_cast<int>(std::lround(image.cols * 0.92)),
                                                 fallback_height),
                                        image.size());
    } else {
        best_rect = expand_rect_in_image(best_rect,
                                         std::max(18, image.cols / 50),
                                         std::max(16, image.rows / 60),
                                         image.size());
    }

    cv::Mat focus = image(best_rect).clone();
    if (focus.empty()) return {};

    const int focus_target_width = 1800;
    if (focus.cols < focus_target_width) {
        const double scale = std::min(2.25, static_cast<double>(focus_target_width) / static_cast<double>(focus.cols));
        if (scale > 1.05) {
            cv::resize(focus, focus, cv::Size(), scale, scale, cv::INTER_CUBIC);
        }
    } else if (focus.cols > focus_target_width) {
        cv::resize(focus, focus, cv::Size(focus_target_width, focus.rows * focus_target_width / focus.cols));
    }

    return focus;
}

std::string sha256_hex(const std::string &bytes)
{
    unsigned char digest[SHA256_DIGEST_LENGTH];
    SHA256(reinterpret_cast<const unsigned char*>(bytes.data()), bytes.size(), digest);

    std::ostringstream out;
    out << std::hex << std::setfill('0');
    for (unsigned char c : digest) {
        out << std::setw(2) << static_cast<int>(c);
    }
    return out.str();
}

std::string sanitize_filename_component(const std::string &input, std::string_view fallback = "receipt")
{
    std::string out;
    out.reserve(input.size());
    for (unsigned char ch : input) {
        if (std::isalnum(ch) || ch == '.' || ch == '_' || ch == '-') {
            out.push_back(static_cast<char>(ch));
        } else if (out.empty() || out.back() != '_') {
            out.push_back('_');
        }
    }

    while (!out.empty() && (out.front() == '.' || out.front() == '_')) out.erase(out.begin());
    while (!out.empty() && (out.back() == '.' || out.back() == '_')) out.pop_back();
    return out.empty() ? std::string(fallback) : out;
}

std::string extension_from_mime_type(const std::string &mime_type)
{
    const auto mime = lower_ascii(trim_copy(mime_type));
    if (mime == "application/pdf") return ".pdf";
    if (mime == "image/png") return ".png";
    if (mime == "image/jpeg" || mime == "image/jpg") return ".jpg";
    if (mime == "image/webp") return ".webp";
    if (mime == "image/bmp") return ".bmp";
    return {};
}

bool is_image_extension(const std::filesystem::path &path)
{
    const auto ext = lower_ascii(path.extension().string());
    return ext == ".png" || ext == ".jpg" || ext == ".jpeg" || ext == ".webp" || ext == ".bmp";
}

std::filesystem::path absolute_normalized_path(const std::filesystem::path &path)
{
    std::error_code ec;
    const auto absolute = std::filesystem::absolute(path, ec);
    return ec ? path.lexically_normal() : absolute.lexically_normal();
}

bool is_regular_file_path(const std::filesystem::path &path)
{
    if (path.empty()) return false;
    std::error_code ec;
    return std::filesystem::exists(path, ec) && std::filesystem::is_regular_file(path, ec);
}

bool path_is_within_root(const std::filesystem::path &path, const std::filesystem::path &root)
{
    if (path.empty() || root.empty()) return false;

    const auto normalized_path = absolute_normalized_path(path);
    const auto normalized_root = absolute_normalized_path(root);

    auto root_it = normalized_root.begin();
    auto path_it = normalized_path.begin();
    for (; root_it != normalized_root.end(); ++root_it, ++path_it) {
        if (path_it == normalized_path.end() || *path_it != *root_it) {
            return false;
        }
    }
    return true;
}

std::filesystem::path executable_dir_path()
{
    std::error_code ec;
    const auto exe_path = std::filesystem::read_symlink("/proc/self/exe", ec);
    if (ec || exe_path.empty()) return {};
    return absolute_normalized_path(exe_path).parent_path();
}

std::filesystem::path search_ancestor_paths_for_file(const std::filesystem::path &base_dir,
                                                     const std::filesystem::path &relative_path)
{
    if (base_dir.empty() || relative_path.empty() || relative_path.is_absolute()) return {};

    auto current = absolute_normalized_path(base_dir);
    while (!current.empty()) {
        const auto candidate = current / relative_path;
        if (is_regular_file_path(candidate)) {
            return absolute_normalized_path(candidate);
        }

        const auto parent = current.parent_path();
        if (parent == current) break;
        current = parent;
    }
    return {};
}

std::filesystem::path resolve_shop_config_path(const std::string &requested_path)
{
    const auto trimmed = trim_copy(requested_path);
    if (trimmed.empty()) return {};

    if (const char *env_path = std::getenv("FLAME_SHOP_CONFIG_PATH")) {
        const auto env_trimmed = trim_copy(env_path);
        if (!env_trimmed.empty()) {
            const std::filesystem::path env_candidate(env_trimmed);
            if (is_regular_file_path(env_candidate)) {
                return absolute_normalized_path(env_candidate);
            }
        }
    }

    const std::filesystem::path requested(trimmed);
    if (requested.is_absolute()) {
        return is_regular_file_path(requested) ? absolute_normalized_path(requested) : std::filesystem::path{};
    }

    if (is_regular_file_path(requested)) {
        return absolute_normalized_path(requested);
    }

    if (const auto from_cwd = search_ancestor_paths_for_file(std::filesystem::current_path(), requested);
        !from_cwd.empty()) {
        return from_cwd;
    }

    if (const auto exe_dir = executable_dir_path(); !exe_dir.empty()) {
        if (const auto from_exe = search_ancestor_paths_for_file(exe_dir, requested); !from_exe.empty()) {
            return from_exe;
        }
    }

    return {};
}

std::filesystem::path default_shop_config_path(const std::string &requested_path)
{
    const auto trimmed = trim_copy(requested_path);
    if (trimmed.empty()) {
        return absolute_normalized_path(std::filesystem::current_path() / "shop_databases.json");
    }

    const std::filesystem::path requested(trimmed);
    if (requested.is_absolute()) {
        return absolute_normalized_path(requested);
    }
    return absolute_normalized_path(std::filesystem::current_path() / requested);
}

std::filesystem::path users_config_path_from_shop_config(const std::filesystem::path &shop_config_path)
{
    if (shop_config_path.empty()) {
        return absolute_normalized_path(std::filesystem::current_path() / "users.json");
    }
    return absolute_normalized_path(shop_config_path.parent_path() / "users.json");
}

json read_json_array_file(const std::filesystem::path &path)
{
    if (path.empty() || !is_regular_file_path(path)) {
        return json::array();
    }
    std::ifstream in(path);
    if (!in.is_open()) {
        throw std::runtime_error(std::format("Cannot open JSON file '{}'", path.string()));
    }
    json doc;
    in >> doc;
    if (!doc.is_array()) {
        throw std::runtime_error(std::format("JSON file '{}' must contain an array", path.string()));
    }
    return doc;
}

json read_json_file(const std::filesystem::path &path, const json &fallback = json::object())
{
    if (path.empty() || !is_regular_file_path(path)) {
        return fallback;
    }
    std::ifstream in(path);
    if (!in.is_open()) {
        throw std::runtime_error(std::format("Cannot open JSON file '{}'", path.string()));
    }
    json doc;
    in >> doc;
    return doc;
}

json normalize_shop_config_document(const json &doc)
{
    json normalized = {
        {"default_shop_id", 0},
        {"shops", json::array()}
    };

    if (doc.is_array()) {
        normalized["shops"] = doc;
        return normalized;
    }

    if (!doc.is_object()) {
        throw std::runtime_error("Shop config must be a JSON array or object");
    }

    normalized["default_shop_id"] = doc.value("default_shop_id", 0);
    if (doc.contains("shops")) {
        if (!doc["shops"].is_array()) {
            throw std::runtime_error("Shop config field 'shops' must be a JSON array");
        }
        normalized["shops"] = doc["shops"];
    }
    return normalized;
}

void write_json_file_atomically(const std::filesystem::path &path, const json &doc)
{
    if (path.empty()) {
        throw std::runtime_error("Cannot write JSON file: empty path");
    }

    std::error_code ec;
    std::filesystem::create_directories(path.parent_path(), ec);
    if (ec) {
        throw std::runtime_error(std::format("Failed to create directory '{}': {}",
                                             path.parent_path().string(),
                                             ec.message()));
    }

    const auto temp_path = path.parent_path() / (path.filename().string() + ".tmp");
    {
        std::ofstream out(temp_path, std::ios::trunc);
        if (!out.is_open()) {
            throw std::runtime_error(std::format("Cannot write temporary file '{}'", temp_path.string()));
        }
        out << doc.dump(2) << "\n";
    }

    std::filesystem::rename(temp_path, path, ec);
    if (ec) {
        std::filesystem::remove(path, ec);
        ec.clear();
        std::filesystem::rename(temp_path, path, ec);
    }
    if (ec) {
        std::filesystem::remove(temp_path, ec);
        throw std::runtime_error(std::format("Failed to replace JSON file '{}': {}", path.string(), ec.message()));
    }
}

std::filesystem::path resolve_receipt_upload_root()
{
    if (const char *env_path = std::getenv("FLAME_RECEIPT_UPLOAD_ROOT")) {
        const auto trimmed = trim_copy(env_path);
        if (!trimmed.empty()) {
            return absolute_normalized_path(std::filesystem::path(trimmed));
        }
    }

    if (const auto cfg = resolve_shop_config_path("shop_databases.json"); !cfg.empty()) {
        return cfg.parent_path() / "receipt_uploads";
    }

    return absolute_normalized_path(std::filesystem::current_path() / "receipt_uploads");
}

std::filesystem::path ensure_receipt_upload_root(int shop_id)
{
    const auto root = resolve_receipt_upload_root() / std::format("shop_{:04d}", shop_id);
    std::error_code ec;
    std::filesystem::create_directories(root, ec);
    if (ec) {
        throw std::runtime_error(std::format("Failed to create upload directory '{}': {}", root.string(), ec.message()));
    }
    return root;
}

std::string read_file_binary_string(const std::filesystem::path &path)
{
    std::ifstream in(path, std::ios::binary);
    if (!in.is_open()) {
        throw std::runtime_error(std::format("Cannot open file '{}'", path.string()));
    }
    std::ostringstream buffer;
    buffer << in.rdbuf();
    return buffer.str();
}

const std::string &current_server_instance_id()
{
    static const std::string instance_id = []() {
        const auto now = std::chrono::duration_cast<std::chrono::milliseconds>(
            std::chrono::system_clock::now().time_since_epoch()
        ).count();
        return std::format("{}-{}", static_cast<long long>(::getpid()), now);
    }();
    return instance_id;
}
} // namespace

PostgresApi::PostgresApi(std::string_view shop_config_path)
{
    if (!shop_config_path.empty()) {
        load_shop_connections(std::string(shop_config_path));
    }
}

bool PostgresApi::load_shop_connections(const std::string &path)
{
    shop_connections_.clear();
    shop_index_.clear();
    default_shop_id_ = 0;

    const auto resolved_path = resolve_shop_config_path(path);
    if (resolved_path.empty()) {
        shop_config_path_ = default_shop_config_path(path).string();
        const auto cwd = absolute_normalized_path(std::filesystem::current_path());
        if (const char *env_path = std::getenv("FLAME_SHOP_CONFIG_PATH");
            env_path && !trim_copy(env_path).empty()) {
            spdlog::warn("[PostgresApi] FLAME_SHOP_CONFIG_PATH='{}' does not point to a readable file", env_path);
        }
        spdlog::warn("[PostgresApi] shop config file '{}' not found; cwd='{}'; no external shop connections loaded",
                     path,
                     cwd.string());
        return false;
    }

    shop_config_path_ = resolved_path.string();

    if (resolved_path.string() != path) {
        spdlog::info("[PostgresApi] Resolved shop config '{}' -> '{}'", path, resolved_path.string());
    }

    std::ifstream in(resolved_path);
    if (!in.is_open()) {
        spdlog::warn("[PostgresApi] shop config file '{}' is not readable; no external shop connections loaded",
                     resolved_path.string());
        return false;
    }

    json doc;
    try {
        in >> doc;
    } catch (const std::exception &e) {
        spdlog::error("[PostgresApi] Failed to parse shop config '{}': {}", resolved_path.string(), e.what());
        return false;
    }

    json config_doc;
    try {
        config_doc = normalize_shop_config_document(doc);
    } catch (const std::exception &e) {
        spdlog::error("[PostgresApi] Invalid shop config '{}': {}", resolved_path.string(), e.what());
        return false;
    }

    const json &shop_entries = config_doc["shops"];
    default_shop_id_ = config_doc.value("default_shop_id", 0);

    for (const auto &entry : shop_entries) {
        int shop_id = entry.value("shop_id", -1);
        if (shop_id < 0) {
            spdlog::warn("[PostgresApi] Skipping shop entry without valid shop_id in '{}'", resolved_path.string());
            continue;
        }
        if (shop_index_.count(shop_id)) {
            spdlog::warn("[PostgresApi] Duplicate shop_id {} in '{}', skipping", shop_id, resolved_path.string());
            continue;
        }

        auto resolve_secret = [&](const json &src, const char *env_key, const char *raw_key, const char *label) {
            std::string env_name = trim_copy(json_string_or(src, env_key));
            if (!env_name.empty()) {
                if (const char *env = std::getenv(env_name.c_str())) {
                    return std::string(env);
                }
                spdlog::warn("[PostgresApi] shop_id {} {} env '{}' is not set", shop_id, label, env_name);
            }
            return json_string_or(src, raw_key);
        };

        auto parse_source = [&](const json &src, const char *label) {
            DbSource out;
            if (!src.is_object()) return out;

            out.host     = trim_copy(json_string_or(src, "host"));
            out.port     = trim_copy(json_string_or(src, "port"));
            out.dbname   = trim_copy(json_string_or(src, "dbname"));
            out.user_env = trim_copy(json_string_or(src, "user_env"));
            out.pass_env = trim_copy(json_string_or(src, "pass_env"));
            out.user     = trim_copy(resolve_secret(src, "user_env", "user", label));
            out.password = resolve_secret(src, "pass_env", "password", label);
            out.conninfo = trim_copy(json_string_or(src, "conninfo"));

            if (!out.conninfo.empty()) {
                if (out.host.empty())   out.host = conninfo_value(out.conninfo, "host");
                if (out.port.empty())   out.port = conninfo_value(out.conninfo, "port");
                if (out.dbname.empty()) out.dbname = dbname_from_conninfo(out.conninfo);
                if (out.user.empty())   out.user = conninfo_value(out.conninfo, "user");
            } else if (!out.dbname.empty() && !out.user.empty()) {
                if (out.host.empty()) out.host = "localhost";
                if (out.port.empty()) out.port = "5432";
                out.conninfo = std::format(
                    "host={} port={} dbname={} user={} password={}",
                    out.host,
                    out.port,
                    out.dbname,
                    out.user,
                    out.password
                );
            }

            return out;
        };

        const bool has_nested_pos = entry.contains("pos") && entry["pos"].is_object();
        const bool has_nested_exp = entry.contains("expense") && entry["expense"].is_object();

        ShopConnection shop;
        shop.shop_id  = shop_id;
        shop.name     = entry.value("name", "");
        shop.description = entry.value("description", "");
        shop.timezone = entry.value("timezone", "");
        shop.categories = sanitize_category_names(entry.value("categories", json::array()));

        if (has_nested_pos || has_nested_exp) {
            if (has_nested_pos) {
                shop.pos = parse_source(entry["pos"], "pos");
            }
            if (has_nested_exp) {
                shop.expense = parse_source(entry["expense"], "expense");
            }
        } else {
            shop.pos = parse_source(entry, "pos");
        }

        shop.host     = shop.pos.host;
        shop.port     = shop.pos.port;
        shop.dbname   = shop.pos.dbname;
        shop.user     = shop.pos.user;
        shop.conninfo = shop.pos.conninfo;

        if (shop.pos.conninfo.empty() && shop.expense.conninfo.empty()) {
            spdlog::warn("[PostgresApi] shop_id {} has no usable pos/expense connection in '{}', skipping",
                         shop_id,
                         resolved_path.string());
            continue;
        }

        if (shop.pos.conninfo.empty()) {
            spdlog::warn("[PostgresApi] shop_id {} has no configured POS connection; POS summary/sync will be unavailable", shop_id);
        }

        shop_index_[shop_id] = shop_connections_.size();
        shop_connections_.push_back(std::move(shop));
    }

    if (default_shop_id_ > 0 && !shop_index_.count(default_shop_id_)) {
        spdlog::warn("[PostgresApi] default_shop_id {} not found in '{}'; ignoring", default_shop_id_, resolved_path.string());
        default_shop_id_ = 0;
    }

    spdlog::info("[PostgresApi] Loaded {} shop connection(s) from {}",
                 shop_connections_.size(),
                 resolved_path.string());
    return !shop_connections_.empty();
}

const PostgresApi::ShopConnection* PostgresApi::get_shop_connection(int shop_id) const
{
    auto it = shop_index_.find(shop_id);
    if (it == shop_index_.end()) {
        return nullptr;
    }
    return &shop_connections_[it->second];
}

const PostgresApi::DbSource* PostgresApi::get_shop_source(int shop_id, SourceKind kind) const
{
    const auto *shop = get_shop_connection(shop_id);
    if (!shop) return nullptr;
    return kind == SourceKind::Pos ? &shop->pos : &shop->expense;
}

std::vector<std::string> PostgresApi::category_options_for_shop(int shop_id) const
{
    const auto *shop = get_shop_connection(shop_id);
    if (!shop || shop->categories.empty()) {
        return {"Others"};
    }
    return shop->categories;
}

json PostgresApi::shop_connections_json() const
{
    auto source_to_json = [](const DbSource &src) {
        json obj = json::object();
        if (!src.host.empty()) obj["host"] = src.host;
        if (!src.port.empty()) obj["port"] = src.port;
        if (!src.dbname.empty()) obj["dbname"] = src.dbname;
        if (!src.user.empty()) obj["user"] = src.user;
        if (!src.user_env.empty()) obj["user_env"] = src.user_env;
        if (!src.pass_env.empty()) obj["pass_env"] = src.pass_env;
        if (!src.conninfo.empty()) obj["conninfo"] = redact_conninfo_password(src.conninfo);
        obj["configured"] = !src.conninfo.empty();
        return obj;
    };

    json shops = json::array();
    for (const auto &shop : shop_connections_) {
        shops.push_back({
            {"shop_id", shop.shop_id},
            {"name", shop.name},
            {"description", shop.description},
            {"timezone", shop.timezone},
            {"categories", json_array_from_strings(shop.categories)},
            {"host", shop.host},
            {"port", shop.port},
            {"dbname", shop.dbname},
            {"user", shop.user},
            {"conninfo", redact_conninfo_password(shop.conninfo)},
            {"pos", source_to_json(shop.pos)},
            {"expense", source_to_json(shop.expense)}
        });
    }
    return shops;
}

json PostgresApi::editable_shop_connections_json() const
{
    const auto path = shop_config_path_.empty() ? default_shop_config_path("shop_databases.json")
                                                : std::filesystem::path(shop_config_path_);
    try {
        return normalize_shop_config_document(read_json_file(path))["shops"];
    } catch (const std::exception &e) {
        spdlog::warn("[PostgresApi] Failed to load editable shop config '{}': {}", path.string(), e.what());
        return json::array();
    }
}

json PostgresApi::users_json(bool include_passwords) const
{
    const auto shop_path = shop_config_path_.empty() ? default_shop_config_path("shop_databases.json")
                                                     : std::filesystem::path(shop_config_path_);
    const auto user_path = users_config_path_from_shop_config(shop_path);

    json users = read_json_array_file(user_path);
    json sanitized = json::array();
    for (const auto &entry : users) {
        if (!entry.is_object()) continue;
        const std::string username = trim_copy(json_string_or(entry, "username"));
        if (username.empty()) continue;
        json user = {
            {"username", username},
            {"display_name", trim_copy(json_string_or(entry, "display_name"))},
            {"role", "user"}
        };
        if (include_passwords) {
            user["password"] = json_string_or(entry, "password");
        }
        sanitized.push_back(std::move(user));
    }
    return sanitized;
}

json PostgresApi::settings_state_json() const
{
    const auto shop_path = shop_config_path_.empty() ? default_shop_config_path("shop_databases.json")
                                                     : std::filesystem::path(shop_config_path_);
    const auto user_path = users_config_path_from_shop_config(shop_path);

    return {
        {"default_shop_id", default_shop_id_},
        {"shops", editable_shop_connections_json()},
        {"users", users_json(false)},
        {"root_user", {
            {"username", "root"},
            {"display_name", "Root"},
            {"role", "root"},
            {"read_only", true}
        }},
        {"paths", {
            {"shop_config", shop_path.string()},
            {"users_config", user_path.string()}
        }}
    };
}

json PostgresApi::save_settings(const json &shops, const json &users, int default_shop_id)
{
    if (!shops.is_array()) {
        throw std::runtime_error("shops must be a JSON array");
    }
    if (!users.is_array()) {
        throw std::runtime_error("users must be a JSON array");
    }

    auto clean_source = [](const json &src) {
        json out = json::object();
        if (!src.is_object()) return out;

        auto copy_field = [&](const char *key) {
            const std::string value = trim_copy(json_string_or(src, key));
            if (!value.empty()) out[key] = value;
        };

        copy_field("host");
        copy_field("port");
        copy_field("dbname");
        copy_field("user");
        copy_field("password");
        copy_field("user_env");
        copy_field("pass_env");
        copy_field("conninfo");
        return out;
    };

    std::unordered_set<int> seen_shop_ids;
    std::unordered_map<int, std::vector<std::string>> previous_categories_by_shop;
    for (const auto &shop : shop_connections_) {
        previous_categories_by_shop[shop.shop_id] = shop.categories;
    }
    json cleaned_shops = json::array();
    for (const auto &entry : shops) {
        if (!entry.is_object()) {
            throw std::runtime_error("Each shop entry must be an object");
        }

        const int shop_id = entry.value("shop_id", 0);
        if (shop_id <= 0) {
            throw std::runtime_error("Each shop must have a positive shop_id");
        }
        if (!seen_shop_ids.insert(shop_id).second) {
            throw std::runtime_error(std::format("Duplicate shop_id {}", shop_id));
        }

        json shop = {
            {"shop_id", shop_id},
            {"pos", clean_source(entry.value("pos", json::object()))},
            {"expense", clean_source(entry.value("expense", json::object()))}
        };

        const std::string name = trim_copy(json_string_or(entry, "name"));
        const std::string description = trim_copy(json_string_or(entry, "description"));
        const std::string timezone = trim_copy(json_string_or(entry, "timezone"));
        const std::vector<std::string> categories = sanitize_category_names(entry.value("categories", json::array()));
        if (!name.empty()) shop["name"] = name;
        if (!description.empty()) shop["description"] = description;
        if (!timezone.empty()) shop["timezone"] = timezone;
        shop["categories"] = json_array_from_strings(categories);

        cleaned_shops.push_back(std::move(shop));
    }

    int normalized_default_shop_id = default_shop_id;
    if (normalized_default_shop_id > 0 && !seen_shop_ids.count(normalized_default_shop_id)) {
        throw std::runtime_error(std::format("default_shop_id {} does not match any configured shop", normalized_default_shop_id));
    }
    if (normalized_default_shop_id <= 0) {
        normalized_default_shop_id = 0;
    }

    const json existing_users = users_json(true);
    std::unordered_map<std::string, std::string> existing_passwords;
    for (const auto &entry : existing_users) {
        const std::string username = trim_copy(json_string_or(entry, "username"));
        if (username.empty()) continue;
        existing_passwords[username] = json_string_or(entry, "password");
    }

    std::unordered_set<std::string> seen_usernames;
    json cleaned_users = json::array();
    for (const auto &entry : users) {
        if (!entry.is_object()) {
            throw std::runtime_error("Each user entry must be an object");
        }

        const std::string username = trim_copy(json_string_or(entry, "username"));
        const std::string display_name = trim_copy(json_string_or(entry, "display_name"));
        std::string password = trim_copy(json_string_or(entry, "password"));

        if (username.empty()) {
            throw std::runtime_error("Each user must have a username");
        }
        if (lower_ascii(username) == "root") {
            throw std::runtime_error("The root user is fixed and cannot be edited here");
        }
        if (!seen_usernames.insert(username).second) {
            throw std::runtime_error(std::format("Duplicate username '{}'", username));
        }

        if (password.empty()) {
            auto it = existing_passwords.find(username);
            if (it != existing_passwords.end()) {
                password = it->second;
            }
        }
        if (password.empty()) {
            throw std::runtime_error(std::format("User '{}' requires a password", username));
        }

        cleaned_users.push_back({
            {"username", username},
            {"display_name", display_name},
            {"password", password},
            {"role", "user"}
        });
    }

    const auto shop_path = shop_config_path_.empty() ? default_shop_config_path("shop_databases.json")
                                                     : std::filesystem::path(shop_config_path_);
    const auto user_path = users_config_path_from_shop_config(shop_path);

    json cleaned_shop_config = {
        {"default_shop_id", normalized_default_shop_id},
        {"shops", cleaned_shops}
    };

    write_json_file_atomically(shop_path, cleaned_shop_config);
    write_json_file_atomically(user_path, cleaned_users);
    load_shop_connections(shop_path.string());

    json category_sync = json::array();
    std::vector<std::string> sync_summaries;
    std::vector<std::string> sync_warnings;
    const std::string openai_api_key = openai_ocr_key_from_env();
    for (const auto &shop : shop_connections_) {
        const auto it = previous_categories_by_shop.find(shop.shop_id);
        const std::vector<std::string> previous_categories =
            it == previous_categories_by_shop.end() ? std::vector<std::string>{"Others"} : it->second;
        const std::vector<std::string> next_categories = shop.categories;
        if (diff_category_names(previous_categories, next_categories).empty() &&
            diff_category_names(next_categories, previous_categories).empty()) {
            continue;
        }

        json sync_result = sync_shop_categories_after_settings_save(
            shop.shop_id,
            previous_categories,
            next_categories,
            openai_api_key
        );
        category_sync.push_back(sync_result);

        std::vector<std::string> fragments;
        const int reassigned = sync_result.value("reassigned_to_others", 0);
        const int draft_reassigned = sync_result.value("draft_items_reassigned", 0);
        const int ai_backfilled = sync_result.value("ai_backfilled", 0);
        if (reassigned > 0) fragments.push_back(std::format("{} product{}", reassigned, reassigned == 1 ? "" : "s"));
        if (draft_reassigned > 0) fragments.push_back(std::format("{} draft item{}", draft_reassigned, draft_reassigned == 1 ? "" : "s"));
        if (ai_backfilled > 0) fragments.push_back(std::format("{} AI backfill{}", ai_backfilled, ai_backfilled == 1 ? "" : "s"));
        if (!fragments.empty()) {
            sync_summaries.push_back(std::format("{}: {}", shop.name.empty() ? std::format("Shop {}", shop.shop_id) : shop.name,
                                                 join_strings(fragments, ", ")));
        }
        if (sync_result.contains("warnings") && sync_result["warnings"].is_array()) {
            for (const auto &warning : sync_result["warnings"]) {
                if (!warning.is_string()) continue;
                const std::string text = trim_copy(warning.get<std::string>());
                if (text.empty()) continue;
                sync_warnings.push_back(std::format("{}: {}", shop.name.empty() ? std::format("Shop {}", shop.shop_id) : shop.name, text));
            }
        }
    }

    json state = settings_state_json();
    state["category_sync"] = category_sync;
    std::string status_message = "Settings saved.";
    if (!sync_summaries.empty()) {
        status_message += " Category sync: " + join_strings(sync_summaries, " | ") + ".";
    }
    if (!sync_warnings.empty()) {
        status_message += " Warnings: " + join_strings(sync_warnings, " | ");
    }
    state["status_message"] = status_message;
    return state;
}

json PostgresApi::settings_test_connection(const json &source, SourceKind kind) const
{
    const DbSource parsed = settings_source_from_json(source, kind == SourceKind::Pos ? "pos" : "expense");
    if (parsed.conninfo.empty()) {
        throw std::runtime_error(std::format(
            "{} database connection is incomplete. Provide conninfo or at least dbname and user.",
            source_label_for_kind(kind)
        ));
    }

    pqxx::connection conn(parsed.conninfo);
    pqxx::work txn(conn);
    const auto probe = txn.exec1("SELECT current_database()::text AS dbname");
    const auto version = txn.exec1("SHOW server_version");
    txn.commit();

    return {
        {"ok", true},
        {"source_kind", kind == SourceKind::Pos ? "pos" : "expense"},
        {"database", get_string(probe, "dbname", parsed.dbname)},
        {"server_version", version[0].is_null() ? "" : version[0].as<std::string>()},
        {"message", std::format("{} database connection succeeded.", source_label_for_kind(kind))}
    };
}

json PostgresApi::settings_init_expense_db(const json &source) const
{
    const DbSource parsed = settings_source_from_json(source, "expense");
    if (parsed.conninfo.empty()) {
        throw std::runtime_error("Expense database connection is incomplete. Provide conninfo or at least dbname and user.");
    }

    const std::string dbname = parsed.dbname.empty() ? dbname_from_conninfo(parsed.conninfo) : parsed.dbname;
    if (trim_copy(dbname).empty()) {
        throw std::runtime_error("Expense database name is required.");
    }

    bool database_created = false;
    try {
        pqxx::connection expense(parsed.conninfo);
        pqxx::work txn(expense);
        ensure_expense_tracker_schema(txn);
        txn.commit();
    } catch (const pqxx::broken_connection &) {
        database_created = create_database_if_missing(parsed);
        pqxx::connection expense(parsed.conninfo);
        pqxx::work txn(expense);
        ensure_expense_tracker_schema(txn);
        txn.commit();
    }

    return {
        {"ok", true},
        {"source_kind", "expense"},
        {"database", dbname},
        {"database_created", database_created},
        {"message", database_created ? "Expense database created and tracker schema is ready." : "Expense tracker schema is ready."}
    };
}

json PostgresApi::sync_shop_categories_after_settings_save(int shop_id,
                                                           const std::vector<std::string> &previous_categories,
                                                           const std::vector<std::string> &next_categories,
                                                           const std::string &openai_api_key)
{
    const auto *shop = get_shop_connection(shop_id);
    const std::vector<std::string> removed_categories = diff_category_names(previous_categories, next_categories);
    const std::vector<std::string> added_categories = diff_category_names(next_categories, previous_categories);

    json result = {
        {"shop_id", shop_id},
        {"shop_name", shop ? shop->name : ""},
        {"added_categories", json_array_from_strings(added_categories)},
        {"removed_categories", json_array_from_strings(removed_categories)},
        {"reassigned_to_others", 0},
        {"draft_items_reassigned", 0},
        {"ai_backfilled", 0},
        {"warnings", json::array()}
    };

    try {
        pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
        pqxx::work txn(conn);
        ensure_expense_tracker_schema(txn);

        int reassigned_products = 0;
        int reassigned_draft_items = 0;
        for (const auto &category : removed_categories) {
            reassigned_products += txn.exec_params(
                "UPDATE tracker.products SET category = 'Others', updated_at = CURRENT_TIMESTAMP "
                "WHERE shop_id = $1 AND category = $2",
                shop_id,
                category
            ).affected_rows();

            reassigned_draft_items += txn.exec_params(R"(
                UPDATE tracker.purchase_draft_items AS item
                SET category = 'Others'
                FROM tracker.purchase_drafts AS draft
                WHERE item.draft_id = draft.id
                  AND draft.shop_id = $1
                  AND item.category = $2
            )",
                shop_id,
                category
            ).affected_rows();
        }

        txn.commit();
        result["reassigned_to_others"] = reassigned_products;
        result["draft_items_reassigned"] = reassigned_draft_items;
    } catch (const std::exception &e) {
        result["warnings"].push_back(std::format("Category reassignment skipped: {}", e.what()));
        return result;
    }

    if (!added_categories.empty()) {
        if (trim_copy(openai_api_key).empty()) {
            result["warnings"].push_back("OPENAI_OCR_KEY/OPENAI_API_KEY is not set; AI category backfill was skipped.");
            return result;
        }

        try {
            const json backfill_result = backfill_other_product_categories_with_ai(shop_id, next_categories, openai_api_key);
            result["ai_backfilled"] = backfill_result.value("updated_count", 0);
            if (backfill_result.contains("warnings") && backfill_result["warnings"].is_array()) {
                for (const auto &warning : backfill_result["warnings"]) {
                    if (warning.is_string()) {
                        result["warnings"].push_back(warning.get<std::string>());
                    }
                }
            }
        } catch (const std::exception &e) {
            result["warnings"].push_back(std::format("AI category backfill failed: {}", e.what()));
        }
    }

    return result;
}

json PostgresApi::backfill_other_product_categories_with_ai(int shop_id,
                                                            const std::vector<std::string> &allowed_categories,
                                                            const std::string &openai_api_key)
{
    const std::vector<std::string> categories = allowed_categories.empty() ? std::vector<std::string>{"Others"} : allowed_categories;
    json result = {
        {"shop_id", shop_id},
        {"candidate_count", 0},
        {"updated_count", 0},
        {"warnings", json::array()}
    };

    if (categories.size() <= 1) {
        result["warnings"].push_back("No custom categories are configured; AI backfill has nothing to assign.");
        return result;
    }

    const auto *shop = get_shop_connection(shop_id);
    json candidates = json::array();
    json examples = json::array();

    {
        pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, false);
        pqxx::work txn(conn);
        ensure_expense_tracker_schema(txn);

        pqxx::result candidate_res = txn.exec_params(R"(
            SELECT
                p.id,
                p.name,
                COALESCE(p.product_type, '') AS product_type,
                COALESCE(p.category, '') AS category,
                COALESCE(s.name, '') AS supplier_name,
                COALESCE(s.tin, '') AS supplier_tin
            FROM tracker.products p
            LEFT JOIN tracker.suppliers s ON s.id = p.supplier_id
            WHERE p.shop_id = $1
              AND COALESCE(NULLIF(TRIM(p.category), ''), 'Others') = 'Others'
            ORDER BY p.id ASC
        )", shop_id);

        pqxx::result example_res = txn.exec_params(R"(
            SELECT
                p.name,
                COALESCE(p.product_type, '') AS product_type,
                COALESCE(p.category, '') AS category,
                COALESCE(s.name, '') AS supplier_name
            FROM tracker.products p
            LEFT JOIN tracker.suppliers s ON s.id = p.supplier_id
            WHERE p.shop_id = $1
              AND COALESCE(NULLIF(TRIM(p.category), ''), 'Others') <> 'Others'
            ORDER BY p.category ASC, p.name ASC
            LIMIT 40
        )", shop_id);

        for (const auto &row : candidate_res) {
            candidates.push_back({
                {"id", row["id"].as<int>()},
                {"name", get_string(row, "name")},
                {"product_type", get_string(row, "product_type")},
                {"supplier_name", get_string(row, "supplier_name")},
                {"supplier_tin", get_string(row, "supplier_tin")},
                {"current_category", "Others"}
            });
        }
        for (const auto &row : example_res) {
            examples.push_back({
                {"name", get_string(row, "name")},
                {"product_type", get_string(row, "product_type")},
                {"supplier_name", get_string(row, "supplier_name")},
                {"category", get_string(row, "category")}
            });
        }
    }

    result["candidate_count"] = candidates.size();
    if (candidates.empty()) {
        return result;
    }

    Ocr ocr("", openai_api_key);
    std::unordered_map<int, std::string> inferred_categories;
    constexpr std::size_t batch_size = 25;
    for (std::size_t offset = 0; offset < candidates.size(); offset += batch_size) {
        json batch = json::array();
        for (std::size_t index = offset; index < std::min(candidates.size(), offset + batch_size); ++index) {
            batch.push_back(candidates[index]);
        }

        std::ostringstream system_prompt;
        system_prompt
            << "You classify ERP product master records into one allowed category.\n"
            << "Choose exactly one category from this allowed list: " << json_array_from_strings(categories).dump() << "\n"
            << "Never invent a new category. If the product name and supplier do not support a confident match, return `Others`.\n"
            << "Return strict RFC 8259 JSON only as an array of objects.\n"
            << "Each output object must contain: `id`, `category`.\n";

        std::ostringstream user_prompt;
        user_prompt
            << "Shop: " << (shop && !shop->name.empty() ? shop->name : std::format("Shop {}", shop_id)) << "\n";
        if (shop && !trim_copy(shop->description).empty()) {
            user_prompt << "Shop description: " << collapse_prompt_whitespace(shop->description) << "\n";
        }
        user_prompt << "Allowed categories: " << json_array_from_strings(categories).dump() << "\n";
        user_prompt << "Existing categorized examples:\n" << examples.dump() << "\n\n";
        user_prompt << "Products to classify:\n" << batch.dump() << "\n\n";
        user_prompt << "Return JSON array only.";

        const std::string content = ocr.send_structured_prompt_to_openai(system_prompt.str(), user_prompt.str(), 1400);
        const json parsed = json::parse(clean_json(content));
        if (!parsed.is_array()) {
            throw std::runtime_error("AI category backfill response was not a JSON array");
        }

        for (const auto &entry : parsed) {
            if (!entry.is_object()) continue;
            const int product_id = entry.value("id", 0);
            if (product_id <= 0) continue;
            inferred_categories[product_id] = canonicalize_category_name(json_string_or(entry, "category"), categories);
        }
    }

    if (inferred_categories.empty()) {
        result["warnings"].push_back("AI backfill returned no product category assignments.");
        return result;
    }

    int updated_count = 0;
    {
        pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
        pqxx::work txn(conn);
        ensure_expense_tracker_schema(txn);
        for (const auto &[product_id, category] : inferred_categories) {
            if (category == "Others") continue;
            updated_count += txn.exec_params(R"(
                UPDATE tracker.products
                SET category = $1,
                    updated_at = CURRENT_TIMESTAMP
                WHERE shop_id = $2
                  AND id = $3
                  AND COALESCE(NULLIF(TRIM(category), ''), 'Others') = 'Others'
            )",
                category,
                shop_id,
                product_id
            ).affected_rows();
        }
        txn.commit();
    }

    result["updated_count"] = updated_count;
    return result;
}

pqxx::connection PostgresApi::open_shop_connection(int shop_id, SourceKind kind, bool write) const
{
    const auto *source = get_shop_source(shop_id, kind);
    if (!source || source->conninfo.empty()) {
        throw std::runtime_error(
            std::format("shop_id {} has no configured {} database connection",
                        shop_id,
                        kind == SourceKind::Pos ? "POS" : "expense")
        );
    }
    if (write) {
        require_write_allowed(source->conninfo,
                              kind,
                              kind == SourceKind::Pos ? "shop POS database" : "shop expense database");
    }
    return pqxx::connection(source->conninfo);
}

void PostgresApi::require_write_allowed(const std::string &conninfo, SourceKind kind, std::string_view context) const
{
    const std::string dbname = dbname_from_conninfo(conninfo);
    const std::string env = lower_ascii(trim_copy(std::getenv("FLAME_ENV") ? std::getenv("FLAME_ENV") : "test"));
    const bool allow_prod_write = std::getenv("FLAME_ALLOW_PROD_WRITE") != nullptr;

    if (kind == SourceKind::Expense || allow_prod_write || is_test_db_name(dbname)) {
        return;
    }

    throw std::runtime_error(std::format(
        "Refusing write access to {} database '{}' while FLAME_ENV='{}'. "
        "Set FLAME_ALLOW_PROD_WRITE=1 to override POS database write protection.",
        context,
        dbname.empty() ? "<unknown>" : dbname,
        env.empty() ? "test" : env
    ));
}

bool PostgresApi::is_read_only_sql(const std::string &sql) const
{
    std::string trimmed = trim_copy(sql);
    if (trimmed.empty()) return true;

    while (!trimmed.empty() && trimmed.starts_with("--")) {
        auto pos = trimmed.find('\n');
        if (pos == std::string::npos) return true;
        trimmed = trim_copy(trimmed.substr(pos + 1));
    }

    std::string keyword;
    for (char c : trimmed) {
        if (std::isspace(static_cast<unsigned char>(c)) || c == '(') break;
        keyword.push_back(static_cast<char>(std::tolower(static_cast<unsigned char>(c))));
    }

    if (keyword == "with") {
        const std::string lower = lower_ascii(trimmed);
        return lower.find(" insert ") == std::string::npos &&
               lower.find(" update ") == std::string::npos &&
               lower.find(" delete ") == std::string::npos &&
               lower.find(" merge ") == std::string::npos;
    }

    return keyword == "select" || keyword == "show" || keyword == "explain";
}

void PostgresApi::ensure_expense_tracker_schema(pqxx::transaction_base &txn) const
{
    std::string schema_cache_key = "tracker";
    try {
        pqxx::row identity = txn.exec1(
            "SELECT current_database()::text AS dbname, "
            "current_user::text AS dbuser, "
            "COALESCE(inet_server_addr()::text, 'local') AS host, "
            "COALESCE(inet_server_port()::text, '0') AS port"
        );
        schema_cache_key = std::format(
            "{}:{}|{}|{}",
            get_string(identity, "host", "local"),
            get_string(identity, "port", "0"),
            get_string(identity, "dbuser", ""),
            get_string(identity, "dbname", "")
        );
    } catch (...) {
    }

    std::unique_lock lock(g_tracker_schema_cache_mutex);
    if (g_tracker_schema_ready.contains(schema_cache_key)) {
        return;
    }

    bool schema_ready = false;
    try {
        pqxx::row readiness = txn.exec1(R"(
            WITH required_columns(table_name, column_name) AS (
                VALUES
                    ('ocr_scans', 'receipt_code_prefix'),
                    ('ocr_scans', 'file_sha256'),
                    ('ocr_scans', 'page_count'),
                    ('ocr_scans', 'ocr_status'),
                    ('ocr_scans', 'posted_at'),
                    ('ocr_scan_pages', 'ocr_id'),
                    ('suppliers', 'ocr_page_id'),
                    ('suppliers', 'tin_key'),
                    ('suppliers', 'name_key'),
                    ('suppliers', 'site_key'),
                    ('products', 'category'),
                    ('products', 'ocr_page_id'),
                    ('products', 'name_key'),
                    ('purchase_orders', 'ocr_page_id'),
                    ('purchase_orders', 'line_total_basis'),
                    ('purchase_items', 'ocr_page_id'),
                    ('receipt_reviews', 'last_saved_json'),
                    ('purchase_drafts', 'receipt_code'),
                    ('purchase_drafts', 'line_total_basis'),
                    ('purchase_drafts', 'validation_warnings'),
                    ('purchase_draft_items', 'ocr_id'),
                    ('purchase_draft_items', 'ocr_page_id'),
                    ('purchase_draft_items', 'category'),
                    ('purchase_draft_items', 'validation_warnings'),
                    ('job_runs', 'payload')
            ), required_indexes(index_name) AS (
                VALUES
                    ('tracker_suppliers_shop_tin_site_uidx'),
                    ('tracker_suppliers_shop_name_site_uidx'),
                    ('tracker_products_shop_supplier_name_key_uidx')
            ), forbidden_indexes(index_name) AS (
                VALUES
                    ('tracker_suppliers_shop_tin_uidx'),
                    ('tracker_products_shop_supplier_name_uidx')
            )
            SELECT
                (SELECT COUNT(*)
                 FROM required_columns rc
                 JOIN information_schema.columns c
                   ON c.table_schema = 'tracker'
                  AND c.table_name = rc.table_name
                  AND c.column_name = rc.column_name) = (SELECT COUNT(*) FROM required_columns)
                AND
                (SELECT COUNT(*)
                 FROM required_indexes ri
                 JOIN pg_indexes i
                   ON i.schemaname = 'tracker'
                  AND i.indexname = ri.index_name) = (SELECT COUNT(*) FROM required_indexes)
                AND
                (SELECT COUNT(*)
                 FROM forbidden_indexes fi
                 JOIN pg_indexes i
                   ON i.schemaname = 'tracker'
                  AND i.indexname = fi.index_name) = 0
                AS ready
        )");
        schema_ready = !readiness[0].is_null() && readiness[0].as<bool>(false);
    } catch (...) {
        schema_ready = false;
    }

    if (schema_ready) {
        g_tracker_schema_ready.insert(schema_cache_key);
        return;
    }

    txn.exec(R"(
        CREATE SCHEMA IF NOT EXISTS tracker;
    )");

    txn.exec(R"(
        CREATE TABLE IF NOT EXISTS tracker.ocr_scans (
            id SERIAL PRIMARY KEY,
            shop_id INTEGER NOT NULL,
            image_path TEXT NOT NULL UNIQUE,
            source_path TEXT,
            source_file_name TEXT,
            receipt_code_prefix TEXT,
            mime_type TEXT,
            file_sha256 TEXT,
            page_count INTEGER NOT NULL DEFAULT 0,
            scan_type TEXT NOT NULL DEFAULT 'receipt',
            ocr_status TEXT NOT NULL DEFAULT 'uploaded'
                CHECK (ocr_status IN ('uploaded', 'processing', 'extracted', 'needs_review', 'approved', 'posted', 'rejected', 'failed')),
            ocr_model TEXT,
            extracted_text TEXT,
            raw_response JSONB,
            parsed_json JSONB,
            ocr_error TEXT,
            review_status TEXT NOT NULL DEFAULT 'pending'
                CHECK (review_status IN ('pending', 'reviewed', 'approved', 'rejected')),
            scanned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            approved_at TIMESTAMPTZ,
            approved_by TEXT,
            posted_at TIMESTAMPTZ,
            posted_by TEXT,
            updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
        );
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS source_path TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS source_file_name TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS receipt_code_prefix TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS mime_type TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS file_sha256 TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS page_count INTEGER NOT NULL DEFAULT 0;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS ocr_status TEXT NOT NULL DEFAULT 'uploaded';
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS ocr_model TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS raw_response JSONB;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS parsed_json JSONB;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS ocr_error TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS approved_at TIMESTAMPTZ;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS approved_by TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS posted_at TIMESTAMPTZ;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.ocr_scans ADD COLUMN IF NOT EXISTS posted_by TEXT;
    )");
    txn.exec(R"(
        CREATE INDEX IF NOT EXISTS tracker_ocr_scans_shop_scanned_at_idx
        ON tracker.ocr_scans (shop_id, scanned_at DESC, id DESC);
    )");
    txn.exec(R"(
        CREATE INDEX IF NOT EXISTS tracker_ocr_scans_shop_status_idx
        ON tracker.ocr_scans (shop_id, ocr_status, updated_at DESC, id DESC);
    )");
    txn.exec(R"(
        CREATE INDEX IF NOT EXISTS tracker_ocr_scans_shop_receipt_code_prefix_idx
        ON tracker.ocr_scans (shop_id, receipt_code_prefix, updated_at DESC, id DESC);
    )");
    txn.exec(R"(
        CREATE TABLE IF NOT EXISTS tracker.ocr_scan_pages (
            id SERIAL PRIMARY KEY,
            ocr_id INTEGER NOT NULL REFERENCES tracker.ocr_scans(id) ON DELETE CASCADE,
            page_no INTEGER NOT NULL,
            image_path TEXT NOT NULL,
            width INTEGER,
            height INTEGER,
            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            UNIQUE (ocr_id, page_no)
        );
    )");
    txn.exec(R"(
        CREATE INDEX IF NOT EXISTS tracker_ocr_scan_pages_ocr_idx
        ON tracker.ocr_scan_pages (ocr_id, page_no);
    )");

    txn.exec(R"(
        CREATE TABLE IF NOT EXISTS tracker.suppliers (
            id SERIAL PRIMARY KEY,
            shop_id INTEGER NOT NULL,
            tin TEXT,
            tin_key TEXT,
            ocr_id INTEGER REFERENCES tracker.ocr_scans(id) ON DELETE SET NULL,
            ocr_page_id INTEGER REFERENCES tracker.ocr_scan_pages(id) ON DELETE SET NULL,
            name TEXT NOT NULL,
            name_key TEXT,
            contact_info TEXT,
            site TEXT,
            site_key TEXT,
            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
        );
    )");
    txn.exec(R"(
        ALTER TABLE tracker.suppliers ADD COLUMN IF NOT EXISTS ocr_page_id INTEGER REFERENCES tracker.ocr_scan_pages(id) ON DELETE SET NULL;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.suppliers ADD COLUMN IF NOT EXISTS tin_key TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.suppliers ADD COLUMN IF NOT EXISTS name_key TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.suppliers ADD COLUMN IF NOT EXISTS site_key TEXT;
    )");
    refresh_supplier_identity_keys(txn);
    merge_duplicate_suppliers(txn,
                              "shop_id, tin_key, COALESCE(site_key, '')",
                              "tin_key IS NOT NULL AND BTRIM(tin_key) <> ''");
    merge_duplicate_suppliers(txn,
                              "shop_id, COALESCE(name_key, ''), COALESCE(site_key, '')",
                              "(tin_key IS NULL OR BTRIM(tin_key) = '') AND name_key IS NOT NULL AND BTRIM(name_key) <> ''");
    refresh_supplier_identity_keys(txn);
    txn.exec(R"(
        DROP INDEX IF EXISTS tracker.tracker_suppliers_shop_tin_uidx;
    )");
    txn.exec(R"(
        DROP INDEX IF EXISTS tracker.tracker_suppliers_shop_name_idx;
    )");
    txn.exec(R"(
        CREATE UNIQUE INDEX IF NOT EXISTS tracker_suppliers_shop_tin_site_uidx
        ON tracker.suppliers (shop_id, tin_key, COALESCE(site_key, ''))
        WHERE tin_key IS NOT NULL AND BTRIM(tin_key) <> '';
    )");
    txn.exec(R"(
        CREATE UNIQUE INDEX IF NOT EXISTS tracker_suppliers_shop_name_site_uidx
        ON tracker.suppliers (shop_id, COALESCE(name_key, ''), COALESCE(site_key, ''))
        WHERE (tin_key IS NULL OR BTRIM(tin_key) = '')
          AND name_key IS NOT NULL
          AND BTRIM(name_key) <> '';
    )");
    txn.exec(R"(
        CREATE INDEX IF NOT EXISTS tracker_suppliers_shop_name_idx
        ON tracker.suppliers (shop_id, name);
    )");

    txn.exec(R"(
        CREATE TABLE IF NOT EXISTS tracker.products (
            id SERIAL PRIMARY KEY,
            shop_id INTEGER NOT NULL,
            sku TEXT,
            ocr_id INTEGER REFERENCES tracker.ocr_scans(id) ON DELETE SET NULL,
            ocr_page_id INTEGER REFERENCES tracker.ocr_scan_pages(id) ON DELETE SET NULL,
            name TEXT NOT NULL,
            name_key TEXT,
            product_type TEXT NOT NULL,
            category TEXT,
            supplier_id INTEGER REFERENCES tracker.suppliers(id) ON DELETE SET NULL,
            default_unit_price NUMERIC(12, 2),
            is_active BOOLEAN NOT NULL DEFAULT TRUE,
            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
        );
    )");
    txn.exec(R"(
        ALTER TABLE tracker.products ADD COLUMN IF NOT EXISTS category TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.products ADD COLUMN IF NOT EXISTS ocr_page_id INTEGER REFERENCES tracker.ocr_scan_pages(id) ON DELETE SET NULL;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.products ADD COLUMN IF NOT EXISTS name_key TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.products
        ALTER COLUMN default_unit_price TYPE NUMERIC(12, 2)
        USING CASE
            WHEN default_unit_price IS NULL THEN NULL
            ELSE default_unit_price::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.products DROP CONSTRAINT IF EXISTS products_product_type_check;
    )");
    refresh_product_identity_keys(txn);
    merge_duplicate_products(txn);
    refresh_product_identity_keys(txn);
    txn.exec(R"(
        DROP INDEX IF EXISTS tracker.tracker_products_shop_supplier_name_uidx;
    )");
    txn.exec(R"(
        CREATE UNIQUE INDEX IF NOT EXISTS tracker_products_shop_supplier_name_key_uidx
        ON tracker.products (shop_id, COALESCE(supplier_id, 0), COALESCE(name_key, ''))
        WHERE name_key IS NOT NULL AND BTRIM(name_key) <> '';
    )");

    txn.exec(R"(
        CREATE TABLE IF NOT EXISTS tracker.purchase_orders (
            id SERIAL PRIMARY KEY,
            shop_id INTEGER NOT NULL,
            invoice_id TEXT NOT NULL,
            ocr_id INTEGER REFERENCES tracker.ocr_scans(id) ON DELETE SET NULL,
            ocr_page_id INTEGER REFERENCES tracker.ocr_scan_pages(id) ON DELETE SET NULL,
            supplier_id INTEGER REFERENCES tracker.suppliers(id) ON DELETE SET NULL,
            purchase_date DATE NOT NULL DEFAULT CURRENT_DATE,
            total_cost NUMERIC(12, 2),
            subtotal_amount NUMERIC(12, 2),
            tax_amount NUMERIC(12, 2),
            discount_amount NUMERIC(12, 2),
            rounding_amount NUMERIC(12, 2),
            grand_total NUMERIC(12, 2),
            line_total_basis TEXT NOT NULL DEFAULT 'unknown'
                CHECK (line_total_basis IN ('inclusive', 'exclusive', 'unknown')),
            notes TEXT,
            status TEXT NOT NULL DEFAULT 'draft'
                CHECK (status IN ('draft', 'approved', 'posted', 'rejected')),
            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
        );
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_orders
        ALTER COLUMN total_cost TYPE NUMERIC(12, 2)
        USING CASE
            WHEN total_cost IS NULL THEN NULL
            ELSE total_cost::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_orders ADD COLUMN IF NOT EXISTS ocr_page_id INTEGER REFERENCES tracker.ocr_scan_pages(id) ON DELETE SET NULL;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_orders ADD COLUMN IF NOT EXISTS subtotal_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_orders ADD COLUMN IF NOT EXISTS tax_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_orders ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_orders ADD COLUMN IF NOT EXISTS rounding_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_orders ADD COLUMN IF NOT EXISTS grand_total NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_orders ADD COLUMN IF NOT EXISTS line_total_basis TEXT NOT NULL DEFAULT 'unknown';
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_orders DROP CONSTRAINT IF EXISTS tracker_purchase_orders_line_total_basis_check;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_orders
        ADD CONSTRAINT tracker_purchase_orders_line_total_basis_check
        CHECK (line_total_basis IN ('inclusive', 'exclusive', 'unknown'));
    )");
    txn.exec(R"(
        CREATE UNIQUE INDEX IF NOT EXISTS tracker_purchase_orders_shop_invoice_uidx
        ON tracker.purchase_orders (shop_id, invoice_id);
    )");
    txn.exec(R"(
        CREATE INDEX IF NOT EXISTS tracker_purchase_orders_shop_date_idx
        ON tracker.purchase_orders (shop_id, purchase_date DESC, id DESC);
    )");

    txn.exec(R"(
        CREATE TABLE IF NOT EXISTS tracker.purchase_items (
            id SERIAL PRIMARY KEY,
            shop_id INTEGER NOT NULL,
            purchase_id INTEGER REFERENCES tracker.purchase_orders(id) ON DELETE CASCADE,
            product_id INTEGER REFERENCES tracker.products(id) ON DELETE SET NULL,
            ocr_id INTEGER REFERENCES tracker.ocr_scans(id) ON DELETE SET NULL,
            ocr_page_id INTEGER REFERENCES tracker.ocr_scan_pages(id) ON DELETE SET NULL,
            quantity NUMERIC(10, 2),
            unit_price NUMERIC(12, 2),
            total_price NUMERIC(12, 2) NOT NULL,
            line_discount_percent NUMERIC(8, 4),
            line_discount_amount NUMERIC(12, 2),
            line_subtotal_amount NUMERIC(12, 2),
            line_tax_amount NUMERIC(12, 2),
            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
        );
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_items
        ALTER COLUMN unit_price TYPE NUMERIC(12, 2)
        USING CASE
            WHEN unit_price IS NULL THEN NULL
            ELSE unit_price::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_items ADD COLUMN IF NOT EXISTS ocr_page_id INTEGER REFERENCES tracker.ocr_scan_pages(id) ON DELETE SET NULL;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_items
        ALTER COLUMN total_price TYPE NUMERIC(12, 2)
        USING CASE
            WHEN total_price IS NULL THEN NULL
            ELSE total_price::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_items ADD COLUMN IF NOT EXISTS line_discount_percent NUMERIC(8, 4);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_items ADD COLUMN IF NOT EXISTS line_discount_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_items ADD COLUMN IF NOT EXISTS line_subtotal_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_items ADD COLUMN IF NOT EXISTS line_tax_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_items
        ALTER COLUMN line_discount_percent TYPE NUMERIC(8, 4)
        USING CASE
            WHEN line_discount_percent IS NULL THEN NULL
            ELSE line_discount_percent::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_items
        ALTER COLUMN line_discount_amount TYPE NUMERIC(12, 2)
        USING CASE
            WHEN line_discount_amount IS NULL THEN NULL
            ELSE line_discount_amount::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_items
        ALTER COLUMN line_subtotal_amount TYPE NUMERIC(12, 2)
        USING CASE
            WHEN line_subtotal_amount IS NULL THEN NULL
            ELSE line_subtotal_amount::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_items
        ALTER COLUMN line_tax_amount TYPE NUMERIC(12, 2)
        USING CASE
            WHEN line_tax_amount IS NULL THEN NULL
            ELSE line_tax_amount::numeric
        END;
    )");
    txn.exec(R"(
        CREATE INDEX IF NOT EXISTS tracker_purchase_items_purchase_idx
        ON tracker.purchase_items (purchase_id, id);
    )");
    txn.exec(R"(
        CREATE INDEX IF NOT EXISTS tracker_purchase_items_shop_ocr_idx
        ON tracker.purchase_items (shop_id, ocr_id);
    )");
    txn.exec(R"(
        UPDATE tracker.suppliers s
        SET ocr_id = COALESCE(s.ocr_id, src.ocr_id),
            ocr_page_id = COALESCE(s.ocr_page_id, src.ocr_page_id),
            updated_at = CURRENT_TIMESTAMP
        FROM (
            SELECT supplier_id, MIN(ocr_id) AS ocr_id, MIN(ocr_page_id) AS ocr_page_id
            FROM tracker.purchase_orders
            WHERE supplier_id IS NOT NULL
              AND ocr_id IS NOT NULL
            GROUP BY supplier_id
        ) src
        WHERE s.id = src.supplier_id
          AND (s.ocr_id IS NULL OR s.ocr_page_id IS NULL);
    )");
    txn.exec(R"(
        UPDATE tracker.products p
        SET ocr_id = COALESCE(p.ocr_id, src.ocr_id),
            ocr_page_id = COALESCE(p.ocr_page_id, src.ocr_page_id),
            updated_at = CURRENT_TIMESTAMP
        FROM (
            SELECT product_id, MIN(ocr_id) AS ocr_id, MIN(ocr_page_id) AS ocr_page_id
            FROM tracker.purchase_items
            WHERE product_id IS NOT NULL
              AND ocr_id IS NOT NULL
            GROUP BY product_id
        ) src
        WHERE p.id = src.product_id
          AND (p.ocr_id IS NULL OR p.ocr_page_id IS NULL);
    )");
    txn.exec(R"(
        UPDATE tracker.purchase_items pi
        SET ocr_page_id = po.ocr_page_id
        FROM tracker.purchase_orders po
        WHERE pi.purchase_id = po.id
          AND pi.ocr_page_id IS NULL
          AND po.ocr_page_id IS NOT NULL;
    )");
    txn.exec(R"(
        UPDATE tracker.suppliers s
        SET ocr_page_id = src.ocr_page_id,
            updated_at = CURRENT_TIMESTAMP
        FROM (
            SELECT supplier_id, MIN(ocr_page_id) AS ocr_page_id
            FROM tracker.purchase_orders
            WHERE supplier_id IS NOT NULL
              AND ocr_page_id IS NOT NULL
            GROUP BY supplier_id
        ) src
        WHERE s.id = src.supplier_id
          AND s.ocr_page_id IS NULL;
    )");
    txn.exec(R"(
        UPDATE tracker.products p
        SET ocr_page_id = src.ocr_page_id,
            updated_at = CURRENT_TIMESTAMP
        FROM (
            SELECT product_id, MIN(ocr_page_id) AS ocr_page_id
            FROM tracker.purchase_items
            WHERE product_id IS NOT NULL
              AND ocr_page_id IS NOT NULL
            GROUP BY product_id
        ) src
        WHERE p.id = src.product_id
          AND p.ocr_page_id IS NULL;
    )");

    txn.exec(R"(
        CREATE TABLE IF NOT EXISTS tracker.receipt_reviews (
            id SERIAL PRIMARY KEY,
            ocr_id INTEGER NOT NULL UNIQUE REFERENCES tracker.ocr_scans(id) ON DELETE CASCADE,
            review_status TEXT NOT NULL DEFAULT 'pending'
                CHECK (review_status IN ('pending', 'approved', 'rejected')),
            review_note TEXT,
            reviewed_by TEXT,
            reviewed_at TIMESTAMPTZ,
            assigned_to TEXT,
            draft_id INTEGER,
            last_saved_json JSONB,
            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
        );
    )");
    txn.exec(R"(
        ALTER TABLE tracker.receipt_reviews ADD COLUMN IF NOT EXISTS assigned_to TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.receipt_reviews ADD COLUMN IF NOT EXISTS draft_id INTEGER;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.receipt_reviews ADD COLUMN IF NOT EXISTS last_saved_json JSONB;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.receipt_reviews ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;
    )");

    txn.exec(R"(
        CREATE TABLE IF NOT EXISTS tracker.purchase_drafts (
            id SERIAL PRIMARY KEY,
            shop_id INTEGER NOT NULL,
            ocr_id INTEGER NOT NULL REFERENCES tracker.ocr_scans(id) ON DELETE CASCADE,
            receipt_index INTEGER NOT NULL DEFAULT 0,
            receipt_code TEXT,
            supplier_name TEXT,
            supplier_tin TEXT,
            supplier_site TEXT,
            supplier_contact_info TEXT,
            invoice_id TEXT,
            purchase_date DATE,
            total_cost NUMERIC(12, 2),
            subtotal_amount NUMERIC(12, 2),
            tax_amount NUMERIC(12, 2),
            discount_amount NUMERIC(12, 2),
            rounding_amount NUMERIC(12, 2),
            grand_total NUMERIC(12, 2),
            line_total_basis TEXT NOT NULL DEFAULT 'unknown'
                CHECK (line_total_basis IN ('inclusive', 'exclusive', 'unknown')),
            status TEXT NOT NULL DEFAULT 'draft'
                CHECK (status IN ('draft', 'needs_review', 'ready', 'approved', 'rejected', 'posted')),
            validation_errors JSONB NOT NULL DEFAULT '[]'::jsonb,
            validation_warnings JSONB NOT NULL DEFAULT '[]'::jsonb,
            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            UNIQUE (ocr_id, receipt_index)
        );
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_drafts
        ALTER COLUMN total_cost TYPE NUMERIC(12, 2)
        USING CASE
            WHEN total_cost IS NULL THEN NULL
            ELSE total_cost::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_drafts ADD COLUMN IF NOT EXISTS receipt_code TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_drafts ADD COLUMN IF NOT EXISTS subtotal_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_drafts ADD COLUMN IF NOT EXISTS tax_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_drafts ADD COLUMN IF NOT EXISTS discount_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_drafts ADD COLUMN IF NOT EXISTS rounding_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_drafts ADD COLUMN IF NOT EXISTS grand_total NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_drafts ADD COLUMN IF NOT EXISTS line_total_basis TEXT NOT NULL DEFAULT 'unknown';
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_drafts ADD COLUMN IF NOT EXISTS validation_warnings JSONB NOT NULL DEFAULT '[]'::jsonb;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_drafts DROP CONSTRAINT IF EXISTS tracker_purchase_drafts_line_total_basis_check;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_drafts
        ADD CONSTRAINT tracker_purchase_drafts_line_total_basis_check
        CHECK (line_total_basis IN ('inclusive', 'exclusive', 'unknown'));
    )");
    txn.exec(R"(
        CREATE INDEX IF NOT EXISTS tracker_purchase_drafts_shop_receipt_code_idx
        ON tracker.purchase_drafts (shop_id, receipt_code);
    )");
    txn.exec(R"(
        CREATE INDEX IF NOT EXISTS tracker_purchase_drafts_shop_status_idx
        ON tracker.purchase_drafts (shop_id, status, updated_at DESC, id DESC);
    )");
    txn.exec(R"(
        UPDATE tracker.purchase_orders po
        SET ocr_page_id = p.id,
            updated_at = CURRENT_TIMESTAMP
        FROM tracker.purchase_drafts d
        JOIN tracker.ocr_scan_pages p
          ON p.ocr_id = d.ocr_id
         AND p.page_no = d.receipt_index + 1
        WHERE po.shop_id = d.shop_id
          AND po.ocr_id = d.ocr_id
          AND (
            po.invoice_id = d.invoice_id
            OR po.invoice_id = (d.receipt_index + 1)::text || '#' || d.invoice_id
          )
          AND po.ocr_page_id IS NULL;
    )");

    txn.exec(R"(
        CREATE TABLE IF NOT EXISTS tracker.purchase_draft_items (
            id SERIAL PRIMARY KEY,
            draft_id INTEGER NOT NULL REFERENCES tracker.purchase_drafts(id) ON DELETE CASCADE,
            ocr_id INTEGER REFERENCES tracker.ocr_scans(id) ON DELETE CASCADE,
            ocr_page_id INTEGER REFERENCES tracker.ocr_scan_pages(id) ON DELETE CASCADE,
            line_no INTEGER NOT NULL,
            name TEXT,
            category TEXT,
            quantity NUMERIC(10, 2),
            unit_price NUMERIC(12, 2),
            total_price NUMERIC(12, 2),
            line_discount_percent NUMERIC(8, 4),
            line_discount_amount NUMERIC(12, 2),
            line_subtotal_amount NUMERIC(12, 2),
            line_tax_amount NUMERIC(12, 2),
            match_product_id INTEGER REFERENCES tracker.products(id) ON DELETE SET NULL,
            validation_errors JSONB NOT NULL DEFAULT '[]'::jsonb,
            validation_warnings JSONB NOT NULL DEFAULT '[]'::jsonb,
            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            UNIQUE (draft_id, line_no)
        );
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items ADD COLUMN IF NOT EXISTS ocr_id INTEGER REFERENCES tracker.ocr_scans(id) ON DELETE CASCADE;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items ADD COLUMN IF NOT EXISTS ocr_page_id INTEGER REFERENCES tracker.ocr_scan_pages(id) ON DELETE CASCADE;
    )");
    txn.exec(R"(
        UPDATE tracker.purchase_draft_items i
        SET ocr_id = d.ocr_id
        FROM tracker.purchase_drafts d
        WHERE i.draft_id = d.id
          AND i.ocr_id IS NULL;
    )");
    txn.exec(R"(
        UPDATE tracker.purchase_draft_items i
        SET ocr_page_id = p.id
        FROM tracker.purchase_drafts d
        JOIN tracker.ocr_scan_pages p
          ON p.ocr_id = d.ocr_id
         AND p.page_no = d.receipt_index + 1
        WHERE i.draft_id = d.id
          AND i.ocr_page_id IS NULL;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items ADD COLUMN IF NOT EXISTS category TEXT;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items ADD COLUMN IF NOT EXISTS validation_warnings JSONB NOT NULL DEFAULT '[]'::jsonb;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items ADD COLUMN IF NOT EXISTS line_discount_percent NUMERIC(8, 4);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items ADD COLUMN IF NOT EXISTS line_discount_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items ADD COLUMN IF NOT EXISTS line_subtotal_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items ADD COLUMN IF NOT EXISTS line_tax_amount NUMERIC(12, 2);
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items
        ALTER COLUMN unit_price TYPE NUMERIC(12, 2)
        USING CASE
            WHEN unit_price IS NULL THEN NULL
            ELSE unit_price::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items
        ALTER COLUMN total_price TYPE NUMERIC(12, 2)
        USING CASE
            WHEN total_price IS NULL THEN NULL
            ELSE total_price::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items
        ALTER COLUMN line_discount_percent TYPE NUMERIC(8, 4)
        USING CASE
            WHEN line_discount_percent IS NULL THEN NULL
            ELSE line_discount_percent::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items
        ALTER COLUMN line_discount_amount TYPE NUMERIC(12, 2)
        USING CASE
            WHEN line_discount_amount IS NULL THEN NULL
            ELSE line_discount_amount::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items
        ALTER COLUMN line_subtotal_amount TYPE NUMERIC(12, 2)
        USING CASE
            WHEN line_subtotal_amount IS NULL THEN NULL
            ELSE line_subtotal_amount::numeric
        END;
    )");
    txn.exec(R"(
        ALTER TABLE tracker.purchase_draft_items
        ALTER COLUMN line_tax_amount TYPE NUMERIC(12, 2)
        USING CASE
            WHEN line_tax_amount IS NULL THEN NULL
            ELSE line_tax_amount::numeric
        END;
    )");
    txn.exec(R"(
        CREATE INDEX IF NOT EXISTS tracker_purchase_draft_items_draft_idx
        ON tracker.purchase_draft_items (draft_id, line_no);
    )");

    txn.exec(R"(
        CREATE TABLE IF NOT EXISTS tracker.job_runs (
            id BIGSERIAL PRIMARY KEY,
            shop_id INTEGER NOT NULL,
            job_kind TEXT NOT NULL,
            status TEXT NOT NULL DEFAULT 'started'
                CHECK (status IN ('started', 'completed', 'failed')),
            payload JSONB NOT NULL DEFAULT '{}'::jsonb,
            error TEXT,
            started_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            finished_at TIMESTAMPTZ
        );
    )");
    txn.exec(R"(
        CREATE INDEX IF NOT EXISTS tracker_job_runs_shop_kind_idx
        ON tracker.job_runs (shop_id, job_kind, started_at DESC);
    )");

    g_tracker_schema_ready.insert(schema_cache_key);
}

nlohmann::json PostgresApi::init_expense_tracker_schemas(const std::vector<int> &shop_ids)
{
    std::unordered_set<int> filter(shop_ids.begin(), shop_ids.end());
    json summary = json::array();

    if (shop_connections_.empty()) {
        summary.push_back({
            {"shop_id", -1},
            {"name", ""},
            {"database", ""},
            {"created", false},
            {"error", "no shop connections loaded"}
        });
        return summary;
    }

    for (const auto &shop_conn : shop_connections_) {
        if (!filter.empty() && !filter.count(shop_conn.shop_id)) {
            continue;
        }

        json stat = {
            {"shop_id", shop_conn.shop_id},
            {"name", shop_conn.name},
            {"database", shop_conn.expense.dbname},
            {"created", false},
            {"error", ""}
        };

        try {
            pqxx::connection expense = open_shop_connection(shop_conn.shop_id, SourceKind::Expense, true);
            pqxx::work txn(expense);
            ensure_expense_tracker_schema(txn);
            txn.commit();
            stat["created"] = true;
        } catch (const std::exception &e) {
            stat["error"] = e.what();
            spdlog::error("[init_expense_tracker_schemas] shop_id={} failed: {}", shop_conn.shop_id, e.what());
        }

        summary.push_back(std::move(stat));
    }

    return summary;
}

json PostgresApi::shop_summary(int shop_id, const std::string &start_time, const std::string &end_time)
{
    if (shop_id < 0) {
        throw std::invalid_argument("shop_id must be non-negative");
    }

    const auto *shop_conn = get_shop_connection(shop_id);
    if (!shop_conn) {
        throw std::runtime_error("shop_id not found in shop_databases.json");
    }

    const std::string start = start_time.empty() ? "1970-01-01" : start_time;
    const std::string end   = end_time.empty()   ? "now"          : end_time;

    try {
        pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Pos, false);
        pqxx::work txn(conn);

        pqxx::result totals_res = txn.exec_params(R"(
            WITH create_date_tickets AS (
                SELECT
                    t.id,
                    COALESCE(t.paid_amount, 0) AS paid_amount
                FROM ticket t
                WHERE t.create_date BETWEEN $1::timestamp AND $2::timestamp
                  AND COALESCE((t.settled)::int, 0) = 1
                  AND COALESCE((t.voided)::int, 0) = 0
                  AND COALESCE((t.refunded)::int, 0) = 0
                  AND COALESCE((t.drawer_resetted)::int, 0) = 0
            ),
            closing_date_tickets AS (
                SELECT t.id
                FROM ticket t
                WHERE t.closing_date BETWEEN $1::timestamp AND $2::timestamp
                  AND COALESCE((t.settled)::int, 0) = 1
                  AND COALESCE((t.voided)::int, 0) = 0
                  AND COALESCE((t.refunded)::int, 0) = 0
                  AND COALESCE((t.drawer_resetted)::int, 0) = 0
            )
            SELECT
                COALESCE((
                    SELECT ROUND(SUM(COALESCE(ti.sub_total_without_modifiers, ti.sub_total, 0)))
                    FROM ticket_item ti
                    JOIN create_date_tickets ft2 ON ft2.id = ti.ticket_id
                ), 0)::bigint AS revenue_cents,
                COALESCE((
                    SELECT ROUND(SUM(COALESCE(ti.sub_total_without_modifiers, ti.sub_total, 0)))
                    FROM ticket_item ti
                    JOIN closing_date_tickets ct ON ct.id = ti.ticket_id
                ), 0)::bigint AS closing_revenue_cents,
                COALESCE(ROUND(SUM(ft.paid_amount)), 0)::bigint AS paid_revenue_cents,
                COUNT(ft.id)::bigint AS orders
            FROM create_date_tickets ft
        )", start, end);

        long long revenue_cents         = totals_res.empty() ? 0 : totals_res[0]["revenue_cents"].as<long long>(0);
        long long closing_revenue_cents = totals_res.empty() ? 0 : totals_res[0]["closing_revenue_cents"].as<long long>(0);
        long long paid_revenue_cents    = totals_res.empty() ? 0 : totals_res[0]["paid_revenue_cents"].as<long long>(0);
        long long total_orders          = totals_res.empty() ? 0 : totals_res[0]["orders"].as<long long>(0);

        pqxx::result items_res = txn.exec_params(R"(
            SELECT COALESCE(SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1)))), 0) AS item_qty
            FROM ticket_item ti
            JOIN ticket t ON t.id = ti.ticket_id
            WHERE COALESCE(t.closing_date, t.create_date) BETWEEN $1::timestamp AND $2::timestamp
        )", start, end);
        long long total_items = items_res.empty() ? 0 : items_res[0]["item_qty"].as<long long>(0);

        auto map_product_rows = [](const pqxx::result &res) {
            json arr = json::array();
            for (const auto &row : res) {
                long long rev = row["revenue_cents"].as<long long>(0);
                arr.push_back({
                    {"name", row["name"].is_null() ? "Unknown" : row["name"].as<std::string>()},
                    {"quantity", row["quantity"].as<long long>(0)},
                    {"revenue_cents", rev},
                    {"revenue", rev}
                });
            }
            return arr;
        };

        pqxx::result top_qty_res = txn.exec_params(R"(
            SELECT COALESCE(NULLIF(m.name, ''), 'Unknown') AS name,
                   SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1)))) AS quantity,
                   COALESCE(ROUND(SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1))) * COALESCE(ti.item_price, 0))), 0) AS revenue_cents
            FROM ticket_item ti
            JOIN ticket t ON t.id = ti.ticket_id
            LEFT JOIN menu_item m ON m.id = ti.item_id
            WHERE COALESCE(t.closing_date, t.create_date) BETWEEN $1::timestamp AND $2::timestamp
            GROUP BY name
            HAVING SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1)))) > 0
            ORDER BY quantity DESC
            LIMIT 10
        )", start, end);

        pqxx::result top_rev_res = txn.exec_params(R"(
            SELECT COALESCE(NULLIF(m.name, ''), 'Unknown') AS name,
                   SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1)))) AS quantity,
                   COALESCE(ROUND(SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1))) * COALESCE(ti.item_price, 0))), 0) AS revenue_cents
            FROM ticket_item ti
            JOIN ticket t ON t.id = ti.ticket_id
            LEFT JOIN menu_item m ON m.id = ti.item_id
            WHERE COALESCE(t.closing_date, t.create_date) BETWEEN $1::timestamp AND $2::timestamp
            GROUP BY name
            HAVING SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1))) * COALESCE(ti.item_price, 0)) > 0
            ORDER BY revenue_cents DESC
            LIMIT 10
        )", start, end);

        pqxx::result bottom_qty_res = txn.exec_params(R"(
            SELECT COALESCE(NULLIF(m.name, ''), 'Unknown') AS name,
                   SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1)))) AS quantity,
                   COALESCE(ROUND(SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1))) * COALESCE(ti.item_price, 0))), 0) AS revenue_cents
            FROM ticket_item ti
            JOIN ticket t ON t.id = ti.ticket_id
            LEFT JOIN menu_item m ON m.id = ti.item_id
            WHERE COALESCE(t.closing_date, t.create_date) BETWEEN $1::timestamp AND $2::timestamp
            GROUP BY name
            HAVING SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1)))) > 0
            ORDER BY quantity ASC
            LIMIT 5
        )", start, end);

        pqxx::result bottom_rev_res = txn.exec_params(R"(
            SELECT COALESCE(NULLIF(m.name, ''), 'Unknown') AS name,
                   SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1)))) AS quantity,
                   COALESCE(ROUND(SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1))) * COALESCE(ti.item_price, 0))), 0) AS revenue_cents
            FROM ticket_item ti
            JOIN ticket t ON t.id = ti.ticket_id
            LEFT JOIN menu_item m ON m.id = ti.item_id
            WHERE COALESCE(t.closing_date, t.create_date) BETWEEN $1::timestamp AND $2::timestamp
            GROUP BY name
            HAVING SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1))) * COALESCE(ti.item_price, 0)) > 0
            ORDER BY revenue_cents ASC
            LIMIT 5
        )", start, end);

        pqxx::result peak_hour_res = txn.exec_params(R"(
            SELECT to_char(COALESCE(closing_date, create_date), 'HH24:00') AS hour_label,
                   COUNT(*) AS orders,
                   COALESCE(ROUND(SUM(total_price)), 0) AS revenue_cents
            FROM ticket
            WHERE COALESCE(closing_date, create_date) BETWEEN $1::timestamp AND $2::timestamp
            GROUP BY hour_label
            ORDER BY orders DESC
            LIMIT 10
        )", start, end);

        pqxx::result order_type_res = txn.exec_params(R"(
            SELECT CASE
                     WHEN LOWER(COALESCE(ticket_type, '')) IN ('dine in', 'dinein', 'dine-in', 'dinein-guest') THEN 'dine-in'
                     WHEN LOWER(COALESCE(ticket_type, '')) IN ('takeaway', 'takeout', 'to-go') THEN 'takeaway'
                     WHEN LOWER(COALESCE(ticket_type, '')) IN ('delivery', 'deliver', 'delivery-guest') THEN 'delivery'
                     WHEN LOWER(COALESCE(ticket_type, '')) = 'retail' THEN 'retail'
                     ELSE 'dine-in'
                   END AS order_type,
                   COUNT(*) AS orders,
                   COALESCE(ROUND(SUM(total_price)), 0) AS revenue_cents
            FROM ticket
            WHERE COALESCE(closing_date, create_date) BETWEEN $1::timestamp AND $2::timestamp
            GROUP BY order_type
            ORDER BY orders DESC
        )", start, end);

        pqxx::result orders_res = txn.exec_params(R"(
            SELECT
                t.id AS order_id,
                COALESCE(t.closing_date, t.create_date) AS order_time,
                COALESCE(ROUND(COALESCE(t.total_price, t.paid_amount, 0)), 0)::bigint AS total_cents,
                CASE
                    WHEN LOWER(COALESCE(t.ticket_type, '')) IN ('dine in', 'dinein', 'dine-in', 'dinein-guest') THEN 'dine-in'
                    WHEN LOWER(COALESCE(t.ticket_type, '')) IN ('takeaway', 'takeout', 'to-go') THEN 'takeaway'
                    WHEN LOWER(COALESCE(t.ticket_type, '')) IN ('delivery', 'deliver', 'delivery-guest') THEN 'delivery'
                    WHEN LOWER(COALESCE(t.ticket_type, '')) = 'retail' THEN 'retail'
                    ELSE 'dine-in'
                END AS order_type,
                COALESCE((
                    SELECT SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1))))
                    FROM ticket_item ti
                    WHERE ti.ticket_id = t.id
                ), 0)::bigint AS items
            FROM ticket t
            WHERE COALESCE(t.closing_date, t.create_date) BETWEEN $1::timestamp AND $2::timestamp
            ORDER BY order_time DESC, t.id DESC
        )", start, end);

        pqxx::result order_items_res = txn.exec_params(R"(
            SELECT
                ti.ticket_id AS order_id,
                COALESCE(NULLIF(m.name, ''), 'Unknown') AS name,
                SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1))))::bigint AS quantity,
                COALESCE(ROUND(AVG(COALESCE(ti.item_price, 0))), 0)::bigint AS unit_price_cents,
                COALESCE(ROUND(SUM(GREATEST(1, ROUND(COALESCE(ti.item_count, 1))) * COALESCE(ti.item_price, 0))), 0)::bigint AS line_total_cents
            FROM ticket_item ti
            JOIN ticket t ON t.id = ti.ticket_id
            LEFT JOIN menu_item m ON m.id = ti.item_id
            WHERE COALESCE(t.closing_date, t.create_date) BETWEEN $1::timestamp AND $2::timestamp
            GROUP BY ti.ticket_id, name
            ORDER BY ti.ticket_id DESC, line_total_cents DESC, name ASC
        )", start, end);

        txn.commit();

        auto map_breakdown = [](const pqxx::result &res, const std::string &label_col) {
            json arr = json::array();
            for (const auto &row : res) {
                long long rev = row["revenue_cents"].as<long long>(0);
                arr.push_back({
                    {label_col, row[label_col].is_null() ? "unspecified" : row[label_col].as<std::string>()},
                    {"orders", row["orders"].as<long long>(0)},
                    {"revenue_cents", rev},
                    {"revenue", rev}
                });
            }
            return arr;
        };

        json peak_hours = json::array();
        for (const auto &row : peak_hour_res) {
            long long rev = row["revenue_cents"].as<long long>(0);
            peak_hours.push_back({
                {"hour", row["hour_label"].as<std::string>()},
                {"orders", row["orders"].as<long long>(0)},
                {"revenue_cents", rev},
                {"revenue", rev}
            });
        }

        json all_orders = json::array();
        std::unordered_map<long long, json> order_items_by_order;
        for (const auto &row : order_items_res) {
            long long order_id = get_int64(row, "order_id");
            auto &items = order_items_by_order[order_id];
            if (!items.is_array()) items = json::array();
            items.push_back({
                {"name", get_string(row, "name", "Unknown")},
                {"quantity", get_int64(row, "quantity")},
                {"unit_price_cents", get_int64(row, "unit_price_cents")},
                {"line_total_cents", get_int64(row, "line_total_cents")}
            });
        }

        for (const auto &row : orders_res) {
            long long order_id = get_int64(row, "order_id");
            all_orders.push_back({
                {"order_id", order_id},
                {"order_time", get_string(row, "order_time")},
                {"total_cents", get_int64(row, "total_cents")},
                {"order_type", get_string(row, "order_type", "dine-in")},
                {"items", get_int64(row, "items")},
                {"order_items", order_items_by_order.count(order_id) ? order_items_by_order[order_id] : json::array()}
            });
        }

        double revenue = static_cast<double>(revenue_cents);
        double aov     = total_orders > 0 ? static_cast<double>(revenue_cents) / static_cast<double>(total_orders) : 0.0;
        double avg_items = total_orders > 0 ? static_cast<double>(total_items) / static_cast<double>(total_orders) : 0.0;
        json payment_methods = json::array();
        if (total_orders > 0) {
            payment_methods.push_back({
                {"payment_method", "unspecified"},
                {"orders", total_orders},
                {"revenue_cents", revenue_cents},
                {"revenue", revenue_cents}
            });
        }

        json summary = {
            {"shop_id", shop_id},
            {"time_range", {{"start", start}, {"end", end}}},
            {"totals",
                {
                    {"revenue_cents", revenue_cents},
                    {"revenue", revenue},
                    {"closing_revenue_cents", closing_revenue_cents},
                    {"closing_revenue", static_cast<double>(closing_revenue_cents)},
                    {"paid_revenue_cents", paid_revenue_cents},
                    {"paid_revenue", static_cast<double>(paid_revenue_cents)},
                    {"orders", total_orders},
                    {"aov", aov},
                    {"items", total_items},
                    {"avg_items_per_order", avg_items}
                }
            },
            {"top_sellers_by_quantity", map_product_rows(top_qty_res)},
            {"top_revenue_products", map_product_rows(top_rev_res)},
            {"bottom_sellers_by_quantity", map_product_rows(bottom_qty_res)},
            {"bottom_revenue_products", map_product_rows(bottom_rev_res)},
            {"peak_hours", peak_hours},
            {"order_types", map_breakdown(order_type_res, "order_type")},
            {"payment_methods", payment_methods},
            {"all_orders", all_orders}
        };

        return summary;
    } catch (const std::exception &e) {
        spdlog::error("[shop_summary] shop_id={} failed: {}", shop_id, e.what());
        throw;
    }
}

json PostgresApi::purchased_summary(int shop_id, const std::string &start_time, const std::string &end_time)
{
    if (shop_id < 0) {
        throw std::invalid_argument("shop_id must be non-negative");
    }

    const auto *shop_conn = get_shop_connection(shop_id);
    if (!shop_conn) {
        throw std::runtime_error("shop_id not found in shop_databases.json");
    }

    const std::string start = start_time.empty() ? "1970-01-01" : start_time;
    const std::string end   = end_time.empty()   ? "now"        : end_time;

    try {
        pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, false);
        pqxx::work txn(conn);

        pqxx::result totals_res = txn.exec_params(R"(
            WITH window_orders AS (
                SELECT id, supplier_id, COALESCE(total_cost, 0) AS total_cost
                FROM tracker.purchase_orders
                WHERE purchase_date BETWEEN $1::date AND $2::date
                  AND shop_id = $3
            ),
            window_items AS (
                SELECT pi.product_id, COALESCE(pi.quantity, 0) AS quantity
                FROM tracker.purchase_items pi
                JOIN window_orders wo ON wo.id = pi.purchase_id
            )
            SELECT
                (SELECT COUNT(*)::bigint FROM window_orders) AS orders,
                (SELECT COALESCE(SUM(total_cost), 0)::bigint FROM window_orders) AS total_cost_cents,
                (SELECT COALESCE(SUM(quantity), 0)::bigint FROM window_items) AS items,
                (SELECT COUNT(DISTINCT supplier_id)::bigint FROM window_orders) AS suppliers,
                (SELECT COUNT(DISTINCT product_id)::bigint FROM window_items) AS products
        )", start, end, shop_id);

        long long orders = totals_res.empty() ? 0 : get_int64(totals_res[0], "orders");
        long long items = totals_res.empty() ? 0 : static_cast<long long>(std::llround(get_double(totals_res[0], "items")));
        long long total_cost_cents = totals_res.empty() ? 0 : get_int64(totals_res[0], "total_cost_cents");
        long long suppliers = totals_res.empty() ? 0 : get_int64(totals_res[0], "suppliers");
        long long products = totals_res.empty() ? 0 : get_int64(totals_res[0], "products");
        long long avg_order_cost_cents = orders > 0 ? static_cast<long long>(std::llround(static_cast<double>(total_cost_cents) / static_cast<double>(orders))) : 0;

        pqxx::result top_suppliers_res = txn.exec_params(R"(
            SELECT COALESCE(NULLIF(s.name, ''), 'Unknown') AS name,
                   COUNT(po.id)::bigint AS orders,
                   COALESCE(SUM(COALESCE(po.total_cost, 0)), 0)::bigint AS total_cost_cents
            FROM tracker.purchase_orders po
            LEFT JOIN tracker.suppliers s ON s.id = po.supplier_id
            WHERE po.purchase_date BETWEEN $1::date AND $2::date
              AND po.shop_id = $3
            GROUP BY name
            ORDER BY total_cost_cents DESC, orders DESC
            LIMIT 8
        )", start, end, shop_id);

        pqxx::result top_products_res = txn.exec_params(R"(
            SELECT COALESCE(NULLIF(p.name, ''), 'Unknown') AS name,
                   COALESCE(SUM(pi.quantity), 0) AS quantity,
                   COALESCE(SUM(COALESCE(pi.total_price, COALESCE(pi.quantity, 0) * COALESCE(pi.unit_price, 0))), 0)::numeric AS total_cost_cents
            FROM tracker.purchase_items pi
            JOIN tracker.purchase_orders po ON po.id = pi.purchase_id
            LEFT JOIN tracker.products p ON p.id = pi.product_id
            WHERE po.purchase_date BETWEEN $1::date AND $2::date
              AND po.shop_id = $3
            GROUP BY name
            ORDER BY total_cost_cents DESC
            LIMIT 10
        )", start, end, shop_id);

        pqxx::result recent_orders_res = txn.exec_params(R"(
            SELECT po.id AS purchase_id,
                   COALESCE(po.invoice_id, '') AS invoice_id,
                   COALESCE(NULLIF(s.name, ''), 'Unknown') AS supplier,
                   po.purchase_date::text AS purchase_date,
                   COALESCE(po.total_cost, 0)::bigint AS total_cost_cents
            FROM tracker.purchase_orders po
            LEFT JOIN tracker.suppliers s ON s.id = po.supplier_id
            WHERE po.purchase_date BETWEEN $1::date AND $2::date
              AND po.shop_id = $3
            ORDER BY po.purchase_date DESC, po.id DESC
            LIMIT 12
        )", start, end, shop_id);

        pqxx::result selected_items_res = txn.exec_params(R"(
            SELECT pi.id,
                   pi.purchase_id,
                   COALESCE(po.ocr_id, 0) AS ocr_id,
                   COALESCE(pi.ocr_page_id, po.ocr_page_id, 0) AS ocr_page_id,
                   COALESCE(po.invoice_id, '') AS invoice_id,
                   po.purchase_date::text AS purchase_date,
                   COALESCE(NULLIF(s.name, ''), 'Unknown') AS supplier,
                   COALESCE(NULLIF(p.name, ''), 'Unknown') AS product,
                   COALESCE(pi.quantity, 0) AS quantity,
                   COALESCE(pi.unit_price, 0)::numeric AS unit_price_cents,
                   COALESCE(pi.total_price, COALESCE(pi.quantity, 0) * COALESCE(pi.unit_price, 0))::numeric AS total_price_cents
            FROM tracker.purchase_items pi
            JOIN tracker.purchase_orders po ON po.id = pi.purchase_id
            LEFT JOIN tracker.suppliers s ON s.id = po.supplier_id
            LEFT JOIN tracker.products p ON p.id = pi.product_id
            WHERE po.purchase_date BETWEEN $1::date AND $2::date
              AND po.shop_id = $3
            ORDER BY po.purchase_date DESC, pi.purchase_id DESC, pi.id ASC
            LIMIT 500
        )", start, end, shop_id);

        txn.commit();

        json top_suppliers = json::array();
        for (const auto &row : top_suppliers_res) {
            top_suppliers.push_back({
                {"name", get_string(row, "name", "Unknown")},
                {"orders", get_int64(row, "orders")},
                {"total_cost_cents", get_int64(row, "total_cost_cents")}
            });
        }

        json top_products = json::array();
        for (const auto &row : top_products_res) {
            top_products.push_back({
                {"name", get_string(row, "name", "Unknown")},
                {"quantity", static_cast<long long>(std::llround(get_double(row, "quantity")))},
                {"total_cost_cents", get_double(row, "total_cost_cents")}
            });
        }

        json recent_orders = json::array();
        for (const auto &row : recent_orders_res) {
            recent_orders.push_back({
                {"purchase_id", get_int64(row, "purchase_id")},
                {"invoice_id", get_string(row, "invoice_id")},
                {"supplier", get_string(row, "supplier", "Unknown")},
                {"purchase_date", get_string(row, "purchase_date")},
                {"total_cost_cents", get_int64(row, "total_cost_cents")}
            });
        }

        json selected_items = json::array();
        for (const auto &row : selected_items_res) {
            selected_items.push_back({
                {"id", get_int64(row, "id")},
                {"purchase_id", get_int64(row, "purchase_id")},
                {"ocr_id", get_int64(row, "ocr_id")},
                {"ocr_page_id", get_int64(row, "ocr_page_id")},
                {"invoice_id", get_string(row, "invoice_id")},
                {"purchase_date", get_string(row, "purchase_date")},
                {"supplier", get_string(row, "supplier", "Unknown")},
                {"product", get_string(row, "product", "Unknown")},
                {"quantity", get_double(row, "quantity")},
                {"unit_price_cents", get_double(row, "unit_price_cents")},
                {"total_price_cents", get_double(row, "total_price_cents")}
            });
        }

        return json{
            {"shop_id", shop_id},
            {"shop_name", shop_conn->name},
            {"database", shop_conn->expense.dbname},
            {"time_range", {{"start", start}, {"end", end}}},
            {"totals",
                {
                    {"orders", orders},
                    {"items", items},
                    {"total_cost_cents", total_cost_cents},
                    {"avg_order_cost_cents", avg_order_cost_cents},
                    {"suppliers", suppliers},
                    {"products", products}
                }
            },
            {"top_suppliers", top_suppliers},
            {"top_products", top_products},
            {"recent_orders", recent_orders},
            {"selected_items", selected_items},
            {"error", ""}
        };
    } catch (const std::exception &e) {
        spdlog::error("[purchased_summary] shop_id={} failed: {}", shop_id, e.what());
        throw;
    }
}

json PostgresApi::db_schema_overview(int shop_id, SourceKind kind)
{
    try {
        pqxx::connection conn = open_shop_connection(shop_id, kind, false);
        pqxx::work txn(conn);

        pqxx::result tables_res = txn.exec(R"(
            SELECT t.table_schema,
                   t.table_name,
                   COALESCE(s.n_live_tup::bigint, 0) AS row_count
            FROM information_schema.tables t
            LEFT JOIN pg_stat_user_tables s
              ON s.schemaname = t.table_schema
             AND s.relname = t.table_name
            WHERE t.table_schema NOT IN ('pg_catalog', 'information_schema')
              AND t.table_type = 'BASE TABLE'
            ORDER BY t.table_schema, t.table_name
        )");

        json tables = json::array();
        for (const auto &tbl : tables_res) {
            const std::string table_schema = tbl["table_schema"].as<std::string>();
            const std::string table_name = tbl["table_name"].as<std::string>();
            const long long row_count = tbl["row_count"].as<long long>(0);

            pqxx::result cols_res = txn.exec_params(R"(
                SELECT column_name, data_type, is_nullable
                FROM information_schema.columns
                WHERE table_schema = $1 AND table_name = $2
                ORDER BY ordinal_position
            )", table_schema, table_name);

            json columns = json::array();
            for (const auto &col : cols_res) {
                columns.push_back({
                    {"name", col["column_name"].as<std::string>()},
                    {"data_type", col["data_type"].as<std::string>()},
                    {"is_nullable", col["is_nullable"].as<std::string>()}
                });
            }

            tables.push_back({
                {"schema_name", table_schema},
                {"table", table_name},
                {"row_count", row_count},
                {"columns", columns}
            });
        }

        txn.commit();
        return json{
            {"shop_id", shop_id},
            {"source_kind", kind == SourceKind::Pos ? "pos" : "expense"},
            {"database", conn.dbname()},
            {"schema", kind == SourceKind::Pos ? "public" : "tracker+public"},
            {"tables", tables},
            {"error", ""}
        };
    } catch (const std::exception &e) {
        spdlog::error("[db_schema_overview] shop_id={} kind={} {}", shop_id, kind == SourceKind::Pos ? "pos" : "expense", e.what());
        return json{
            {"shop_id", shop_id},
            {"source_kind", kind == SourceKind::Pos ? "pos" : "expense"},
            {"database", ""},
            {"schema", ""},
            {"tables", json::array()},
            {"error", e.what()}
        };
    }
}

namespace {
json parse_optional_json_field(const pqxx::field &field)
{
    if (field.is_null()) return nullptr;
    try {
        return json::parse(field.c_str());
    } catch (...) {
        return field.c_str();
    }
}

json normalize_drafts_payload(const json &drafts)
{
    if (drafts.is_array()) return drafts;
    if (drafts.is_object()) {
        if (drafts.contains("drafts") && drafts["drafts"].is_array()) {
            return drafts["drafts"];
        }
        return json::array({drafts});
    }
    throw std::runtime_error("drafts must be a JSON object or array");
}

long long json_int_or(const json &obj, const char *key, long long def = 0)
{
    if (!obj.is_object() || !obj.contains(key) || obj[key].is_null()) return def;
    const auto &value = obj[key];
    try {
        if (value.is_number_integer()) return value.get<long long>();
        if (value.is_number_unsigned()) return static_cast<long long>(value.get<unsigned long long>());
        if (value.is_number_float()) return static_cast<long long>(std::llround(value.get<double>()));
        if (value.is_string()) return std::stoll(value.get<std::string>());
    } catch (...) {
    }
    return def;
}

void append_message(json &messages, const std::string &message)
{
    const std::string trimmed = trim_copy(message);
    if (!messages.is_array()) {
        messages = json::array();
    }
    if (trimmed.empty()) return;

    for (const auto &existing : messages) {
        if (existing.is_string() && existing.get<std::string>() == trimmed) {
            return;
        }
    }
    messages.push_back(trimmed);
}

void append_messages(json &messages, const json &value)
{
    if (value.is_string()) {
        append_message(messages, value.get<std::string>());
        return;
    }
    if (!value.is_array()) return;

    for (const auto &entry : value) {
        if (entry.is_string()) {
            append_message(messages, entry.get<std::string>());
        }
    }
}

std::string normalize_lookup_key(std::string s)
{
    std::string out;
    out.reserve(s.size());
    for (unsigned char c : s) {
        if (std::isalnum(c)) {
            out.push_back(static_cast<char>(std::toupper(c)));
        }
    }
    return out;
}

bool normalized_text_contains_any(const std::string &text,
                                  std::initializer_list<std::string_view> needles)
{
    const std::string haystack = normalize_lookup_key(text);
    if (haystack.empty()) {
        return false;
    }
    for (const auto needle : needles) {
        if (!needle.empty() && haystack.find(std::string(needle)) != std::string::npos) {
            return true;
        }
    }
    return false;
}

bool warnings_hint_non_itemized_service_bill(const json &warnings)
{
    if (warnings.is_string()) {
        return normalized_text_contains_any(
            warnings.get<std::string>(),
            {"NOLINEITEMS", "NONITEMIZED", "SUMMARIZEDWITHOUTITEMIZED", "WITHOUTITEMIZEDBREAKDOWN"});
    }
    if (!warnings.is_array()) {
        return false;
    }
    for (const auto &warning : warnings) {
        if (warning.is_string() &&
            normalized_text_contains_any(
                warning.get<std::string>(),
                {"NOLINEITEMS", "NONITEMIZED", "SUMMARIZEDWITHOUTITEMIZED", "WITHOUTITEMIZEDBREAKDOWN"})) {
            return true;
        }
    }
    return false;
}

bool supplier_name_looks_like_service_bill(const std::string &supplier_name)
{
    return normalized_text_contains_any(
        supplier_name,
        {"UNELCO", "ELECTRIC", "POWER", "WATER", "TELECOM", "DIGICEL", "INTERNET", "PHONE", "RENT", "LEASE"});
}

bool item_name_looks_like_summary_charge(const std::string &item_name)
{
    return normalized_text_contains_any(
        item_name,
        {"BILL", "SERVICE", "CHARGE", "RENT", "ELECTRIC", "POWER", "WATER", "TELECOM", "INTERNET", "PHONE"});
}

std::string guess_non_itemized_service_item_name(const std::string &supplier_name,
                                                 const json        &warnings)
{
    const std::string combined_hint = supplier_name + " " + warnings.dump();
    if (normalized_text_contains_any(combined_hint, {"UNELCO", "ELECTRIC", "POWER"})) {
        return "Electricity bill";
    }
    if (normalized_text_contains_any(combined_hint, {"WATER"})) {
        return "Water bill";
    }
    if (normalized_text_contains_any(combined_hint, {"TELECOM", "DIGICEL", "INTERNET", "PHONE"})) {
        return "Telecom bill";
    }
    if (normalized_text_contains_any(combined_hint, {"RENT", "LEASE"})) {
        return "Rent";
    }
    return "Service charge";
}

bool should_synthesize_non_itemized_purchase_item(const std::string &supplier_name,
                                                  const json        &draft_warnings,
                                                  const json        &items,
                                                  double             grand_total)
{
    if (!items.is_array() || !items.empty() || grand_total <= 0.0) {
        return false;
    }
    return warnings_hint_non_itemized_service_bill(draft_warnings) ||
           supplier_name_looks_like_service_bill(supplier_name);
}

json make_non_itemized_purchase_item(const std::string &supplier_name,
                                     const json        &draft_warnings,
                                     double             grand_total)
{
    return {
        {"name", guess_non_itemized_service_item_name(supplier_name, draft_warnings)},
        {"category", "Others"},
        {"quantity", 1.0},
        {"unit_price", grand_total},
        {"total_price", grand_total},
        {"line_discount_percent", 0.0},
        {"line_discount_amount", 0.0},
        {"line_subtotal_amount", 0.0},
        {"line_tax_amount", 0.0},
        {"summary_synthesized", true},
        {"warnings", json::array({"No itemized purchase rows were printed; this expense was summarized into one bill line."})}
    };
}

bool nearly_equal_amount(double lhs, double rhs, double tolerance = 2.0)
{
    return std::fabs(lhs - rhs) <= tolerance;
}

double line_total_validation_tolerance(double lhs,
                                       double rhs,
                                       bool   has_pricing_adjustments)
{
    const double scale = std::max(std::fabs(lhs), std::fabs(rhs));
    if (has_pricing_adjustments) {
        return std::min(50.0, std::max(3.0, scale * 0.005));
    }
    return std::min(25.0, std::max(2.0, scale * 0.002));
}

double canonicalize_discount_percent(double value)
{
    if (value > 0.0 && value < 1.0) {
        return value * 100.0;
    }
    return value;
}

double derive_line_discount_amount(const json &item, double gross_total)
{
    const double explicit_discount = json_to_money(item, "line_discount_amount", 0.0);
    if (explicit_discount > 0.0) {
        return explicit_discount;
    }

    const double explicit_subtotal = json_to_money(item, "line_subtotal_amount", 0.0);
    if (explicit_subtotal > 0.0 && gross_total > explicit_subtotal) {
        return std::max(0.0, gross_total - explicit_subtotal);
    }

    const double discount_percent = canonicalize_discount_percent(json_to_double(item, "line_discount_percent", 0.0));
    if (discount_percent > 0.0 && gross_total > 0.0) {
        return gross_total * (discount_percent / 100.0);
    }

    return 0.0;
}

double derive_line_subtotal_amount(const json &item, double gross_total)
{
    const double explicit_subtotal = json_to_money(item, "line_subtotal_amount", 0.0);
    if (explicit_subtotal > 0.0) {
        return explicit_subtotal;
    }

    if (gross_total <= 0.0) {
        return 0.0;
    }

    const double discount_amount = derive_line_discount_amount(item, gross_total);
    if (discount_amount > 0.0) {
        return std::max(0.0, gross_total - discount_amount);
    }

    return 0.0;
}

bool item_has_line_pricing_adjustments(const json &item)
{
    return canonicalize_discount_percent(json_to_double(item, "line_discount_percent", 0.0)) > 0.0 ||
           json_to_money(item, "line_discount_amount", 0.0) > 0.0 ||
           json_to_money(item, "line_subtotal_amount", 0.0) > 0.0 ||
           json_to_money(item, "line_tax_amount", 0.0) > 0.0;
}

double round_money_amount(double value)
{
    return std::round(value * 100.0) / 100.0;
}

bool quantity_requires_distinct_unit_price(double quantity)
{
    return quantity > 0.0 && std::fabs(quantity - 1.0) > 0.01;
}

bool money_looks_integral(double value)
{
    return nearly_equal_amount(value, std::round(value), 0.01);
}

void reconcile_inclusive_line_tax_to_header(json &purchase_order, json &items)
{
    if (!purchase_order.is_object() || !items.is_array()) {
        return;
    }

    const std::string line_total_basis =
        lower_ascii(trim_copy(json_string_or(purchase_order, "line_total_basis", "unknown")));
    const double header_tax_amount = json_to_money(purchase_order, "tax_amount", 0.0);
    if (line_total_basis != "inclusive" || header_tax_amount <= 0.0 || !money_looks_integral(header_tax_amount)) {
        return;
    }

    struct Candidate {
        std::size_t index;
        double raw_tax;
        double current_tax;
        double total_price;
    };

    std::vector<Candidate> candidates;
    candidates.reserve(items.size());
    double current_tax_sum = 0.0;
    for (std::size_t i = 0; i < items.size(); ++i) {
        auto &item = items[i];
        if (!item.is_object()) {
            continue;
        }

        const double quantity = json_to_double(item, "quantity", 0.0);
        const double unit_price = json_to_money(item, "unit_price", 0.0);
        const double total_price = json_to_money(item, "total_price", 0.0);
        const double current_tax = json_to_money(item, "line_tax_amount", 0.0);
        const double current_subtotal = json_to_money(item, "line_subtotal_amount", 0.0);
        if (quantity <= 0.0 || unit_price <= 0.0 || total_price <= 0.0 ||
            current_tax <= 0.0 || current_subtotal <= 0.0 ||
            !money_looks_integral(current_tax) || !money_looks_integral(current_subtotal)) {
            continue;
        }

        const double gross_total = quantity * unit_price;
        const double discount_amount = derive_line_discount_amount(item, gross_total);
        const double raw_subtotal = std::max(0.0, gross_total - discount_amount);
        const double raw_tax = total_price - raw_subtotal;
        if (raw_tax <= 0.0) {
            continue;
        }

        current_tax_sum += current_tax;
        candidates.push_back({i, raw_tax, current_tax, total_price});
    }

    if (candidates.empty()) {
        return;
    }

    int delta = static_cast<int>(std::llround(header_tax_amount - current_tax_sum));
    if (delta == 0 || std::abs(delta) > static_cast<int>(candidates.size()) * 2) {
        return;
    }

    auto adjustment_cost = [](double current_tax, double raw_tax, int step) {
        return std::fabs((current_tax + static_cast<double>(step)) - raw_tax) -
               std::fabs(current_tax - raw_tax);
    };

    while (delta != 0) {
        const int step = delta > 0 ? 1 : -1;
        double best_cost = std::numeric_limits<double>::infinity();
        std::size_t best_pos = candidates.size();
        for (std::size_t i = 0; i < candidates.size(); ++i) {
            const auto &candidate = candidates[i];
            if (candidate.current_tax + step < 0.0) {
                continue;
            }
            const double next_subtotal = candidate.total_price - (candidate.current_tax + step);
            if (next_subtotal < 0.0) {
                continue;
            }
            const double cost = adjustment_cost(candidate.current_tax, candidate.raw_tax, step);
            if (cost < best_cost) {
                best_cost = cost;
                best_pos = i;
            }
        }

        if (best_pos >= candidates.size()) {
            break;
        }

        auto &candidate = candidates[best_pos];
        candidate.current_tax += step;
        auto &item = items[candidate.index];
        item["line_tax_amount"] = round_money_amount(candidate.current_tax);
        item["line_subtotal_amount"] = round_money_amount(candidate.total_price - candidate.current_tax);
        append_message(item["warnings"],
                       std::format("Line tax adjusted by {} VT to reconcile item tax rounding with printed header tax total {}.",
                                   step,
                                   header_tax_amount));
        delta -= step;
    }
}

void normalize_item_unit_price_from_quantity_and_total(json &item)
{
    if (!item.is_object()) {
        return;
    }

    const double quantity = json_to_double(item, "quantity", 0.0);
    const double unit_price = json_to_money(item, "unit_price", 0.0);
    const double total_price = json_to_money(item, "total_price", 0.0);
    if (quantity <= 0.0 || total_price <= 0.0 || item_has_line_pricing_adjustments(item) ||
        !quantity_requires_distinct_unit_price(quantity)) {
        return;
    }

    const double derived_unit_price = round_money_amount(total_price / quantity);
    if (derived_unit_price <= 0.0) {
        return;
    }

    const bool missing_unit_price = unit_price <= 0.0;
    const bool duplicated_total_as_unit =
        unit_price > 0.0 &&
        nearly_equal_amount(unit_price, total_price, std::max(1.0, total_price * 0.001));

    if (!missing_unit_price && !duplicated_total_as_unit) {
        return;
    }

    item["unit_price"] = derived_unit_price;
    if (duplicated_total_as_unit) {
        append_message(item["warnings"],
                       std::format("Unit price inferred as total/qty ({:.2f}/{:.3f} = {:.2f}) because OCR likely copied the line total into the unit field.",
                                   total_price,
                                   quantity,
                                   derived_unit_price));
    } else {
        append_message(item["warnings"],
                       std::format("Unit price inferred as total/qty ({:.2f}/{:.3f} = {:.2f}) because no separate printed unit price was detected.",
                                   total_price,
                                   quantity,
                                   derived_unit_price));
    }
}

void normalize_item_inclusive_tax_from_total(json &item, const std::string &line_total_basis)
{
    if (!item.is_object() || line_total_basis != "inclusive") {
        return;
    }

    const double quantity = json_to_double(item, "quantity", 0.0);
    const double unit_price = json_to_money(item, "unit_price", 0.0);
    const double total_price = json_to_money(item, "total_price", 0.0);
    const double line_tax_amount = json_to_money(item, "line_tax_amount", 0.0);
    const double line_subtotal_amount = json_to_money(item, "line_subtotal_amount", 0.0);
    if (quantity <= 0.0 || unit_price <= 0.0 || total_price <= 0.0) {
        return;
    }

    const double gross_total = quantity * unit_price;
    const double derived_discount_amount = derive_line_discount_amount(item, gross_total);
    double inferred_subtotal = gross_total;
    if (derived_discount_amount > 0.0) {
        inferred_subtotal = std::max(0.0, gross_total - derived_discount_amount);
        if (money_looks_integral(gross_total) && money_looks_integral(total_price)) {
            inferred_subtotal = std::round(inferred_subtotal);
        } else {
            inferred_subtotal = round_money_amount(inferred_subtotal);
        }
    } else {
        inferred_subtotal = round_money_amount(gross_total);
    }

    const double tolerance = std::min(25.0, std::max(2.0, std::max(inferred_subtotal, total_price) * 0.002));
    const bool has_existing_breakdown = line_tax_amount > 0.0 || line_subtotal_amount > 0.0;
    if (has_existing_breakdown) {
        const double current_expected_total =
            line_subtotal_amount > 0.0 && line_tax_amount > 0.0
                ? line_subtotal_amount + line_tax_amount
                : line_subtotal_amount;
        const bool current_total_matches =
            line_subtotal_amount > 0.0 &&
            nearly_equal_amount(current_expected_total, total_price, tolerance);
        const bool current_subtotal_matches_discount =
            line_subtotal_amount <= 0.0 ||
            derived_discount_amount <= 0.0 ||
            nearly_equal_amount(line_subtotal_amount, inferred_subtotal, tolerance);
        if (current_total_matches && current_subtotal_matches_discount) {
            return;
        }
    }

    if (total_price <= inferred_subtotal + tolerance) {
        return;
    }

    item["line_subtotal_amount"] = inferred_subtotal;
    item["line_tax_amount"] = round_money_amount(total_price - inferred_subtotal);
    if (json_to_money(item, "line_discount_amount", 0.0) <= 0.0 && derived_discount_amount > 0.0) {
        item["line_discount_amount"] = round_money_amount(derived_discount_amount);
    }

    if (derived_discount_amount > 0.0) {
        append_message(item["warnings"],
                       std::format("Line tax inferred after discount: gross {:.2f}, discount {:.2f}, ex-tax subtotal {:.2f}, tax {:.2f}, total {:.2f}.",
                                   gross_total,
                                   derived_discount_amount,
                                   inferred_subtotal,
                                   total_price - inferred_subtotal,
                                   total_price));
    } else {
        append_message(item["warnings"],
                       std::format("Line tax inferred as total - ex-tax subtotal ({:.2f} - {:.2f} = {:.2f}) because this receipt is marked tax inclusive.",
                                   total_price,
                                   inferred_subtotal,
                                   total_price - inferred_subtotal));
    }
}

int json_fractional_digits_hint(const json &value)
{
    std::string raw;
    if (value.is_string()) {
        raw = value.get<std::string>();
    } else if (value.is_number()) {
        raw = value.dump();
    } else {
        return 0;
    }

    const auto dot_pos = raw.rfind('.');
    const auto comma_pos = raw.rfind(',');
    const auto pos = std::max(dot_pos, comma_pos);
    if (pos == std::string::npos || pos + 1 >= raw.size()) {
        return 0;
    }

    int digits = 0;
    for (std::size_t i = pos + 1; i < raw.size(); ++i) {
        if (!std::isdigit(static_cast<unsigned char>(raw[i]))) {
            return 0;
        }
        ++digits;
    }
    return digits;
}

bool likely_money_separator_drift(const json &order, const json &items)
{
    int positive_money_fields = 0;
    int order_fractional3_fields = 0;
    int item_fractional3_fields = 0;

    auto observe_money_field = [&](const json &obj, const char *key, bool order_field) {
        if (!obj.is_object() || !obj.contains(key) || obj.at(key).is_null()) {
            return;
        }
        const double value = json_to_money(obj, key, 0.0);
        if (value <= 0.0) {
            return;
        }
        ++positive_money_fields;
        if (json_fractional_digits_hint(obj.at(key)) == 3) {
            if (order_field) {
                ++order_fractional3_fields;
            } else {
                ++item_fractional3_fields;
            }
        }
    };

    observe_money_field(order, "subtotal_amount", true);
    observe_money_field(order, "tax_amount", true);
    observe_money_field(order, "discount_amount", true);
    observe_money_field(order, "rounding_amount", true);
    observe_money_field(order, "grand_total", true);
    observe_money_field(order, "total_cost", true);

    if (items.is_array()) {
        for (const auto &item : items) {
            observe_money_field(item, "unit_price", false);
            observe_money_field(item, "line_discount_amount", false);
            observe_money_field(item, "line_subtotal_amount", false);
            observe_money_field(item, "line_tax_amount", false);
            observe_money_field(item, "total_price", false);
        }
    }

    const double grand_total = json_to_money(order, "grand_total", json_to_money(order, "total_cost", 0.0));
    return positive_money_fields >= 4 &&
           grand_total > 0.0 &&
           grand_total < 100.0 &&
           order_fractional3_fields >= 1 &&
           item_fractional3_fields >= 2;
}

std::string canonicalize_line_total_basis(const std::string &raw_basis)
{
    const std::string normalized = lower_ascii(trim_copy(raw_basis));
    if (normalized == "inclusive" || normalized == "tax_inclusive" || normalized == "including_tax" ||
        normalized == "inc" || normalized == "gross") {
        return "inclusive";
    }
    if (normalized == "exclusive" || normalized == "tax_exclusive" || normalized == "excluding_tax" ||
        normalized == "exc" || normalized == "net") {
        return "exclusive";
    }
    return "unknown";
}

std::vector<std::string> sanitize_category_names(const json &raw_categories)
{
    std::vector<std::string> categories;
    auto push_unique = [&](const std::string &raw_category) {
        const std::string trimmed = trim_copy(raw_category);
        if (trimmed.empty()) return;
        const std::string normalized = normalize_lookup_key(trimmed);
        if (normalized.empty()) return;
        for (const auto &existing : categories) {
            if (normalize_lookup_key(existing) == normalized) {
                return;
            }
        }
        categories.push_back(trimmed);
    };

    push_unique("Others");
    if (raw_categories.is_array()) {
        for (const auto &entry : raw_categories) {
            if (!entry.is_string()) continue;
            const std::string value = trim_copy(entry.get<std::string>());
            if (value.empty()) continue;
            const std::string normalized = normalize_lookup_key(value);
            if (normalized == "OTHER" || normalized == "OTHERS" || normalized == "MISC" ||
                normalized == "MISCELLANEOUS" || normalized == "UNCATEGORIZED") {
                continue;
            }
            push_unique(value);
        }
    }

    if (categories.empty()) {
        categories.push_back("Others");
    }

    std::stable_sort(categories.begin() + std::min<std::size_t>(1, categories.size()), categories.end());
    return categories;
}

json json_array_from_strings(const std::vector<std::string> &values)
{
    json out = json::array();
    for (const auto &value : values) {
        out.push_back(value);
    }
    return out;
}

bool contains_category_name(const std::vector<std::string> &categories, const std::string &name)
{
    const std::string normalized = normalize_lookup_key(name);
    if (normalized.empty()) return false;
    for (const auto &category : categories) {
        if (normalize_lookup_key(category) == normalized) {
            return true;
        }
    }
    return false;
}

std::vector<std::string> diff_category_names(const std::vector<std::string> &lhs, const std::vector<std::string> &rhs)
{
    std::vector<std::string> out;
    for (const auto &value : lhs) {
        if (normalize_lookup_key(value) == "OTHERS") continue;
        if (!contains_category_name(rhs, value)) {
            out.push_back(value);
        }
    }
    return out;
}

std::string canonicalize_category_name(const std::string &raw_category, const std::vector<std::string> &allowed_categories)
{
    const std::string trimmed = trim_copy(raw_category);
    if (trimmed.empty()) {
        return "Others";
    }

    const std::string normalized = normalize_lookup_key(trimmed);
    if (normalized.empty()) {
        return "Others";
    }

    if (normalized == "OTHER" || normalized == "OTHERS" || normalized == "MISC" ||
        normalized == "MISCELLANEOUS" || normalized == "UNCATEGORIZED") {
        return "Others";
    }

    const auto categories = allowed_categories.empty() ? std::vector<std::string>{"Others"} : allowed_categories;
    for (const auto &category : categories) {
        if (normalize_lookup_key(category) == normalized) {
            return category;
        }
    }

    return "Others";
}

json category_options_from_config(const std::vector<std::string> &allowed_categories)
{
    return json_array_from_strings(allowed_categories.empty() ? std::vector<std::string>{"Others"} : allowed_categories);
}

int levenshtein_distance(const std::string &lhs, const std::string &rhs)
{
    if (lhs.empty()) return static_cast<int>(rhs.size());
    if (rhs.empty()) return static_cast<int>(lhs.size());

    std::vector<int> prev(rhs.size() + 1);
    std::vector<int> curr(rhs.size() + 1);

    for (std::size_t j = 0; j <= rhs.size(); ++j) {
        prev[j] = static_cast<int>(j);
    }

    for (std::size_t i = 1; i <= lhs.size(); ++i) {
        curr[0] = static_cast<int>(i);
        for (std::size_t j = 1; j <= rhs.size(); ++j) {
            const int cost = lhs[i - 1] == rhs[j - 1] ? 0 : 1;
            curr[j] = std::min({
                prev[j] + 1,
                curr[j - 1] + 1,
                prev[j - 1] + cost
            });
        }
        std::swap(prev, curr);
    }

    return prev[rhs.size()];
}

json load_shop_receipt_prompt_history(pqxx::transaction_base &txn, int shop_id, int exclude_ocr_id)
{
    pqxx::result supplier_res = txn.exec_params(R"(
        SELECT
            btrim(COALESCE(s.name, '')) AS name,
            btrim(COALESCE(s.tin, '')) AS tin,
            btrim(COALESCE(s.site, '')) AS site,
            COUNT(*)::bigint AS seen_count,
            MAX(s.updated_at)::text AS last_seen
        FROM tracker.suppliers s
        WHERE s.shop_id = $1
          AND COALESCE(s.ocr_id, 0) <> $2
          AND btrim(COALESCE(s.name, '')) <> ''
        GROUP BY name, tin, site
        ORDER BY COUNT(*) DESC, MAX(s.updated_at) DESC, name
        LIMIT 12
    )", shop_id, exclude_ocr_id);

    pqxx::result product_res = txn.exec_params(R"(
        SELECT
            p.id,
            btrim(COALESCE(p.name, '')) AS name,
            btrim(COALESCE(s.name, '')) AS supplier_name,
            btrim(COALESCE(s.tin, '')) AS supplier_tin,
            COALESCE(p.default_unit_price, 0)::numeric AS default_unit_price,
            COALESCE(p.product_type, '') AS product_type,
            COALESCE(p.category, '') AS category,
            COALESCE(p.is_active, TRUE) AS is_active
        FROM tracker.products p
        LEFT JOIN tracker.suppliers s ON s.id = p.supplier_id
        WHERE p.shop_id = $1
          AND COALESCE(p.ocr_id, 0) <> $2
          AND btrim(COALESCE(p.name, '')) <> ''
          AND COALESCE(p.is_active, TRUE)
        ORDER BY p.updated_at DESC, p.id DESC
        LIMIT 30
    )", shop_id, exclude_ocr_id);

    json suppliers = json::array();
    json prompt_suppliers = json::array();
    for (const auto &row : supplier_res) {
        const std::string name = get_string(row, "name");
        const std::string tin = get_string(row, "tin");
        const std::string site = get_string(row, "site");
        suppliers.push_back({
            {"name", name},
            {"tin", tin},
            {"site", site},
            {"normalized_name", normalize_lookup_key(name)}
        });

        json supplier = {
            {"name", name},
            {"tin", tin}
        };
        if (!site.empty()) {
            supplier["site"] = site;
        }
        prompt_suppliers.push_back(std::move(supplier));
    }

    json products = json::array();
    json prompt_products = json::array();
    for (const auto &row : product_res) {
        const int product_id = row["id"].as<int>();
        const std::string name = get_string(row, "name");
        const std::string supplier_name = get_string(row, "supplier_name");
        const std::string supplier_tin = get_string(row, "supplier_tin");
        const double default_unit_price = get_double(row, "default_unit_price");
        const std::string product_type = get_string(row, "product_type");
        const std::string category = get_string(row, "category");

        products.push_back({
            {"id", product_id},
            {"name", name},
            {"supplier_name", supplier_name},
            {"supplier_tin", supplier_tin},
            {"default_unit_price", default_unit_price},
            {"product_type", product_type},
            {"category", category},
            {"normalized_name", normalize_lookup_key(name)},
            {"normalized_supplier_name", normalize_lookup_key(supplier_name)}
        });

        json product = {
            {"name", name}
        };

        if (!supplier_name.empty()) {
            product["supplier_name"] = supplier_name;
        }
        if (!supplier_tin.empty()) {
            product["supplier_tin"] = supplier_tin;
        }
        if (default_unit_price > 0) {
            product["default_unit_price"] = default_unit_price;
        }
        if (!product_type.empty()) {
            product["product_type"] = product_type;
        }
        if (!category.empty()) {
            product["category"] = category;
        }

        prompt_products.push_back(std::move(product));
    }

    return {
        {"suppliers", suppliers},
        {"products", products},
        {"prompt_suppliers", prompt_suppliers},
        {"prompt_products", prompt_products}
    };
}

std::string build_shop_receipt_prompt_context(const std::string &shop_name,
                                              const std::string &shop_description,
                                              const std::string &database_name,
                                              const json &history,
                                              const std::vector<std::string> &allowed_categories,
                                              const std::string &source_file_name,
                                              const std::string &inferred_receipt_date,
                                              int page_no,
                                              int total_pages)
{
    std::ostringstream out;
    out << "Shop context:\n";
    out << "- shop_name: " << (shop_name.empty() ? "unknown" : shop_name) << "\n";
    if (const std::string description = collapse_prompt_whitespace(shop_description); !description.empty()) {
        out << "- shop_description: " << description << "\n";
        out << "- use shop_description only as a soft hint for supplier/item normalization or receipt interpretation; do not override clearly visible receipt text because of it\n";
    }
    out << "- expense_database: " << (database_name.empty() ? "unknown" : database_name) << "\n";
    out << "- source_file_name: " << (source_file_name.empty() ? "unknown" : source_file_name) << "\n";
    out << "- allowed_product_categories: " << json_array_from_strings(allowed_categories.empty() ? std::vector<std::string>{"Others"} : allowed_categories).dump() << "\n";
    out << "- every inferred item category must be one of allowed_product_categories\n";
    out << "- if no allowed category matches confidently, use `Others`\n";
    out << "- current_page: " << page_no << "\n";
    out << "- total_pages: " << total_pages << "\n";
    out << "- document rule: this PDF contains receipts from one day and there is exactly one receipt on each page\n";
    if (!inferred_receipt_date.empty()) {
        out << "- inferred_receipt_date_from_source_filename: " << inferred_receipt_date << "\n";
        out << "- use the inferred filename date as a strong hint for `purchase_order.purchase_date` when the printed date is missing or unclear\n";
        out << "- if the source filename contains a date plus suffix, such as `2026-03-02_a.pdf`, `2026-03-02-b.pdf`, or `2026-03-02_1.jpg`, infer the receipt date from the date part only; treat suffixes like `_a`, `_b`, `-1`, or page/bundle labels as receipt bundle identifiers, not date information\n";
        out << "- if the visible printed date clearly conflicts with the filename date, keep the visible date and add a warning\n";
    }
    out << "- historical context source tables: tracker.suppliers, tracker.products\n";
    out << "- historical context is intentionally reduced to compact supplier and product master data to save tokens\n";
    out << "- use historical references as hints only; visible receipt evidence wins when clear\n";
    out << "- historical product categories are canonical when a clear match exists and the category is in allowed_product_categories; reuse those exact category names for consistency\n";
    out << "- return exactly one receipt object for this page; do not split the page into multiple receipts\n";
    out << "- if you normalize a supplier or item to a historical record because OCR text is degraded, add a warning\n";
    out << "- if the new receipt differs from supplier/product history, add a warning\n";
    out << "- if a supplier or item is genuinely new, keep it and add a warning instead of forcing a match\n\n";

    out << "Historical suppliers (compact JSON):\n";
    out << history.value("prompt_suppliers", json::array()).dump();
    out << "\n\nHistorical products (compact JSON):\n";
    out << history.value("prompt_products", json::array()).dump();

    return out.str();
}

std::string infer_receipt_date_from_filename(const std::string &source_file_name)
{
    const std::string file_name = trim_copy(std::filesystem::path(source_file_name).filename().string());
    if (file_name.empty()) return {};

    std::smatch match;
    if (std::regex_search(file_name, match, std::regex(R"((\d{4}-\d{2}-\d{2}))")) && match.size() >= 2) {
        return match[1].str();
    }

    return {};
}

std::string compact_receipt_date_code(const std::string &iso_date)
{
    if (iso_date.size() != 10) return {};
    std::string compact;
    compact.reserve(8);
    for (char c : iso_date) {
        if (std::isdigit(static_cast<unsigned char>(c))) {
            compact.push_back(c);
        }
    }
    return compact.size() == 8 ? compact : std::string{};
}

std::string derive_receipt_code_prefix(const std::string &source_file_name, const std::string &file_hash)
{
    const std::string stem = sanitize_filename_component(std::filesystem::path(source_file_name).stem().string(), "receipt");
    const std::string inferred_date = infer_receipt_date_from_filename(source_file_name);
    if (!inferred_date.empty()) {
        const std::string compact = compact_receipt_date_code(inferred_date);
        if (!compact.empty()) {
            std::smatch match;
            if (std::regex_search(stem, match, std::regex(R"((\d{4}-\d{2}-\d{2})(.*)$)")) && match.size() >= 3) {
                std::string suffix = trim_copy(match[2].str());
                while (!suffix.empty() && (suffix.front() == '_' || suffix.front() == '-' || suffix.front() == '.' || std::isspace(static_cast<unsigned char>(suffix.front())))) {
                    suffix.erase(suffix.begin());
                }
                suffix = sanitize_filename_component(suffix);
                if (!suffix.empty() && suffix != "receipt") {
                    return compact + "_" + suffix;
                }
            }
            return compact;
        }
    }

    if (!trim_copy(stem).empty()) {
        return stem;
    }

    if (file_hash.size() >= 8) {
        return std::format("receipt-{}", file_hash.substr(0, 8));
    }

    return "receipt";
}

std::string derive_receipt_code(const std::string &receipt_code_prefix, int page_number)
{
    const std::string prefix = trim_copy(receipt_code_prefix).empty() ? "receipt" : trim_copy(receipt_code_prefix);
    return std::format("{}#{:02d}", prefix, std::max(page_number, 1));
}

std::vector<std::string> tokenize_history_match_words(const std::string &value)
{
    static const std::unordered_set<std::string> ignored_tokens = {
        "KG", "G", "GM", "GRAM", "GRAMS", "ML", "L", "LT", "LTR", "EA", "EACH",
        "PCS", "PC", "PKT", "PACK", "PACKS", "CTN", "CAN", "BTLE", "BOTTLE",
        "BOX", "BAG", "BAGS", "PER", "X", "MM"
    };

    std::vector<std::string> tokens;
    std::string current;
    auto flush_current = [&]() {
        if (current.size() >= 2 && !ignored_tokens.contains(current)) {
            if (std::find(tokens.begin(), tokens.end(), current) == tokens.end()) {
                tokens.push_back(current);
            }
        }
        current.clear();
    };

    for (unsigned char c : value) {
        if (std::isalpha(c)) {
            current.push_back(static_cast<char>(std::toupper(c)));
        } else {
            flush_current();
        }
    }
    flush_current();
    return tokens;
}

double token_overlap_similarity(const std::vector<std::string> &lhs, const std::vector<std::string> &rhs)
{
    if (lhs.empty() || rhs.empty()) return 0.0;

    std::size_t shared = 0;
    for (const auto &token : lhs) {
        if (std::find(rhs.begin(), rhs.end(), token) != rhs.end()) {
            shared += 1;
        }
    }

    if (shared == 0) return 0.0;
    const std::size_t denominator = std::min(lhs.size(), rhs.size());
    if (denominator == 0) return 0.0;
    return static_cast<double>(shared) / static_cast<double>(denominator);
}

const json* find_best_history_name_match(const json &history_entries,
                                         const std::string &normalized_name,
                                         const char *normalized_key,
                                         const std::string &supplier_normalized_name = {})
{
    if (normalized_name.empty() || !history_entries.is_array()) return nullptr;

    const json *exact = nullptr;
    const json *closest = nullptr;
    int best_distance = std::numeric_limits<int>::max();

    for (const auto &entry : history_entries) {
        if (!entry.is_object()) continue;
        if (!supplier_normalized_name.empty()) {
            const std::string history_supplier = entry.value("normalized_supplier_name", "");
            if (!history_supplier.empty() && history_supplier != supplier_normalized_name) {
                continue;
            }
        }

        const std::string history_name = entry.value(normalized_key, "");
        if (history_name.empty()) continue;
        if (history_name == normalized_name) {
            exact = &entry;
            break;
        }

        const int distance = levenshtein_distance(normalized_name, history_name);
        if (distance < best_distance) {
            best_distance = distance;
            closest = &entry;
        }
    }

    if (exact) return exact;
    if (!closest) return nullptr;

    const int threshold = std::max(2, static_cast<int>(normalized_name.size() / 8));
    return best_distance <= threshold ? closest : nullptr;
}

const json* find_best_history_product_match(const json &history_products,
                                            const std::string &item_name,
                                            const std::string &supplier_normalized_name = {})
{
    const std::string normalized_item_name = normalize_lookup_key(item_name);
    if (normalized_item_name.empty() || !history_products.is_array()) return nullptr;

    const json *best = find_best_history_name_match(history_products,
                                                    normalized_item_name,
                                                    "normalized_name",
                                                    supplier_normalized_name);
    if (best) {
        return best;
    }

    const std::vector<std::string> item_tokens = tokenize_history_match_words(item_name);
    if (item_tokens.empty()) return nullptr;

    const json *closest = nullptr;
    double best_similarity = 0.0;

    for (const auto &entry : history_products) {
        if (!entry.is_object()) continue;
        if (!supplier_normalized_name.empty()) {
            const std::string history_supplier = entry.value("normalized_supplier_name", "");
            if (!history_supplier.empty() && history_supplier != supplier_normalized_name) {
                continue;
            }
        }

        const std::vector<std::string> history_tokens =
            tokenize_history_match_words(entry.value("name", ""));
        if (history_tokens.empty()) continue;

        const double similarity = token_overlap_similarity(item_tokens, history_tokens);
        if (similarity > best_similarity) {
            best_similarity = similarity;
            closest = &entry;
        }
    }

    return best_similarity >= 0.66 ? closest : nullptr;
}

json history_product_suggestions_for_item(const json &history_products,
                                          const std::string &item_name,
                                          const std::string &supplier_normalized_name = {},
                                          int limit = 5)
{
    struct Candidate {
        const json *entry{};
        double score{};
    };

    const std::string normalized_item_name = normalize_lookup_key(item_name);
    const std::vector<std::string> item_tokens = tokenize_history_match_words(item_name);
    if (normalized_item_name.empty() || !history_products.is_array()) {
        return json::array();
    }

    std::vector<Candidate> candidates;
    candidates.reserve(history_products.size());
    for (const auto &entry : history_products) {
        if (!entry.is_object()) continue;
        if (!supplier_normalized_name.empty()) {
            const std::string history_supplier = entry.value("normalized_supplier_name", "");
            if (!history_supplier.empty() && history_supplier != supplier_normalized_name) {
                continue;
            }
        }

        const std::string history_normalized_name = entry.value("normalized_name", "");
        if (history_normalized_name.empty()) continue;

        double score = 0.0;
        if (history_normalized_name == normalized_item_name) {
            score = 1.0;
        } else {
            const std::vector<std::string> history_tokens =
                tokenize_history_match_words(entry.value("name", ""));
            const double token_similarity = token_overlap_similarity(item_tokens, history_tokens);
            const int distance = levenshtein_distance(normalized_item_name, history_normalized_name);
            const double levenshtein_similarity =
                1.0 - (static_cast<double>(distance) /
                       static_cast<double>(std::max<std::size_t>(normalized_item_name.size(), history_normalized_name.size())));
            score = std::max(token_similarity, levenshtein_similarity * 0.75);
        }

        if (score >= 0.5) {
            candidates.push_back({&entry, score});
        }
    }

    std::sort(candidates.begin(), candidates.end(), [](const Candidate &lhs, const Candidate &rhs) {
        if (lhs.score != rhs.score) return lhs.score > rhs.score;
        const std::string lhs_name = lhs.entry ? lhs.entry->value("name", "") : "";
        const std::string rhs_name = rhs.entry ? rhs.entry->value("name", "") : "";
        return lhs_name < rhs_name;
    });

    json out = json::array();
    std::unordered_set<int> seen_ids;
    for (const auto &candidate : candidates) {
        if (!candidate.entry || !candidate.entry->is_object()) continue;
        const int product_id = candidate.entry->value("id", 0);
        if (product_id > 0 && seen_ids.contains(product_id)) continue;
        if (product_id > 0) seen_ids.insert(product_id);
        out.push_back({
            {"id", product_id},
            {"name", candidate.entry->value("name", "")},
            {"category", candidate.entry->value("category", "")},
            {"supplier_name", candidate.entry->value("supplier_name", "")},
            {"supplier_tin", candidate.entry->value("supplier_tin", "")},
            {"score", candidate.score}
        });
        if (static_cast<int>(out.size()) >= std::max(limit, 1)) break;
    }
    return out;
}

const json* find_history_supplier_by_tin(const json &history_suppliers, const std::string &tin)
{
    const std::string trimmed_tin = trim_copy(tin);
    if (trimmed_tin.empty() || !history_suppliers.is_array()) return nullptr;

    for (const auto &supplier : history_suppliers) {
        if (!supplier.is_object()) continue;
        if (trim_copy(supplier.value("tin", "")) == trimmed_tin) {
            return &supplier;
        }
    }
    return nullptr;
}

const json* find_history_supplier_by_tin_and_site(const json &history_suppliers,
                                                  const std::string &tin,
                                                  const std::string &site)
{
    const std::string trimmed_tin = trim_copy(tin);
    const std::string normalized_site = normalize_identity_key(site);
    if (trimmed_tin.empty() || normalized_site.empty() || !history_suppliers.is_array()) return nullptr;

    for (const auto &supplier : history_suppliers) {
        if (!supplier.is_object()) continue;
        if (trim_copy(supplier.value("tin", "")) != trimmed_tin) continue;
        if (normalize_identity_key(supplier.value("site", "")) == normalized_site) {
            return &supplier;
        }
    }
    return nullptr;
}

const json* find_best_history_supplier_match(const json &history_suppliers,
                                             const std::string &supplier_name,
                                             const std::string &supplier_tin,
                                             const std::string &supplier_site)
{
    if (!trim_copy(supplier_tin).empty()) {
        if (const json *exact_tin_site = find_history_supplier_by_tin_and_site(history_suppliers, supplier_tin, supplier_site)) {
            return exact_tin_site;
        }
        if (const json *exact_tin = find_history_supplier_by_tin(history_suppliers, supplier_tin)) {
            return exact_tin;
        }
    }

    const std::string normalized_supplier_name = normalize_lookup_key(supplier_name);
    if (!normalized_supplier_name.empty()) {
        if (const json *name_match = find_best_history_name_match(history_suppliers,
                                                                  normalized_supplier_name,
                                                                  "normalized_name")) {
            return name_match;
        }
    }

    return nullptr;
}

int count_supplier_history_items(const json &history_items, const std::string &normalized_supplier_name)
{
    if (normalized_supplier_name.empty() || !history_items.is_array()) return 0;

    int count = 0;
    for (const auto &item : history_items) {
        if (!item.is_object()) continue;
        if (item.value("normalized_supplier_name", "") == normalized_supplier_name) {
            ++count;
        }
    }
    return count;
}

json build_receipt_job_payload(int ocr_id,
                               const std::string &source_path,
                               const std::string &source_file_name,
                               std::string_view stage,
                               int processed_pages = 0,
                               int total_pages = 0,
                               int current_page = 0,
                               int draft_receipts = 0,
                               const std::string &current_image_path = {})
{
    json payload = {
        {"ocr_id", ocr_id},
        {"source_path", source_path},
        {"source_file_name", source_file_name},
        {"server_instance_id", current_server_instance_id()},
        {"stage", std::string(stage)},
        {"processed_pages", processed_pages},
        {"total_pages", total_pages},
        {"current_page", current_page},
        {"draft_receipts", draft_receipts}
    };
    if (!current_image_path.empty()) {
        payload["current_image_path"] = current_image_path;
    }
    return payload;
}

json build_receipt_job_json(const pqxx::row &row,
                            const char *id_col = "job_id",
                            const char *status_col = "job_status",
                            const char *payload_col = "job_payload",
                            const char *error_col = "job_error",
                            const char *started_col = "job_started_at",
                            const char *finished_col = "job_finished_at")
{
    if (get_int64(row, id_col, 0) <= 0) {
        return nullptr;
    }

    json payload = parse_optional_json_field(row[payload_col]);
    if (!payload.is_object()) {
        payload = json::object();
    }

    return {
        {"id", get_int64(row, id_col, 0)},
        {"status", get_string(row, status_col)},
        {"error", get_string(row, error_col)},
        {"started_at", get_string(row, started_col)},
        {"finished_at", get_string(row, finished_col)},
        {"stage", json_string_or(payload, "stage")},
        {"processed_pages", json_int_or(payload, "processed_pages", 0)},
        {"total_pages", json_int_or(payload, "total_pages", 0)},
        {"current_page", json_int_or(payload, "current_page", 0)},
        {"draft_receipts", json_int_or(payload, "draft_receipts", 0)},
        {"current_image_path", json_string_or(payload, "current_image_path")},
        {"server_instance_id", json_string_or(payload, "server_instance_id")},
        {"payload", payload}
    };
}
} // namespace

nlohmann::json PostgresApi::receipt_upload(int shop_id,
                                           const std::string &file_name,
                                           const std::string &mime_type,
                                           const std::string &content_base64)
{
    if (shop_id <= 0) {
        throw std::invalid_argument("shop_id is required");
    }
    if (trim_copy(file_name).empty()) {
        throw std::invalid_argument("file_name is required");
    }
    if (trim_copy(content_base64).empty()) {
        throw std::invalid_argument("content_base64 is required");
    }

    const std::string bytes = base64_decode(content_base64);
    const std::string file_hash = sha256_hex(bytes);
    std::filesystem::path original_name = sanitize_filename_component(std::filesystem::path(file_name).filename().string());
    const std::string original_file_name = std::filesystem::path(file_name).filename().string();
    std::string extension = lower_ascii(original_name.extension().string());
    if (extension.empty()) {
        extension = extension_from_mime_type(mime_type);
    }
    if (extension != ".pdf" && extension != ".png" && extension != ".jpg" && extension != ".jpeg" &&
        extension != ".webp" && extension != ".bmp") {
        throw std::runtime_error("Only PDF and image uploads are supported");
    }

    const std::string receipt_code_prefix = derive_receipt_code_prefix(original_file_name, file_hash);

    const auto upload_root = ensure_receipt_upload_root(shop_id);
    const auto now_ms = std::chrono::duration_cast<std::chrono::milliseconds>(
        std::chrono::system_clock::now().time_since_epoch()
    ).count();
    const std::string stem = sanitize_filename_component(original_name.stem().string(), "receipt");
    const std::string stored_name = std::format("{}_{}_{}{}",
                                                now_ms,
                                                stem,
                                                file_hash.substr(0, 12),
                                                extension);
    const auto stored_path = upload_root / stored_name;

    {
        std::ofstream out(stored_path, std::ios::binary);
        if (!out.is_open()) {
            throw std::runtime_error(std::format("Cannot write upload file '{}'", stored_path.string()));
        }
        out.write(bytes.data(), static_cast<std::streamsize>(bytes.size()));
    }

    int page_count = 0;
    int page_id = 0;
    int scan_id = 0;

    pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
    pqxx::work txn(conn);
    ensure_expense_tracker_schema(txn);

    pqxx::result duplicate_candidates = txn.exec_params(R"(
        SELECT
            id,
            COALESCE(source_file_name, '') AS source_file_name,
            COALESCE(receipt_code_prefix, '') AS receipt_code_prefix,
            COALESCE(file_sha256, '') AS file_sha256,
            COALESCE(ocr_status, 'uploaded') AS ocr_status
        FROM tracker.ocr_scans
        WHERE shop_id = $1
        ORDER BY id DESC
    )", shop_id);

    int duplicate_index = -1;
    for (pqxx::result::size_type idx = 0; idx < duplicate_candidates.size(); ++idx) {
        const auto &candidate = duplicate_candidates[idx];
        const std::string existing_file_hash = get_string(candidate, "file_sha256");
        const std::string existing_source_file_name = get_string(candidate, "source_file_name");
        const std::string existing_prefix =
            trim_copy(get_string(candidate, "receipt_code_prefix")).empty()
                ? derive_receipt_code_prefix(existing_source_file_name, existing_file_hash)
                : trim_copy(get_string(candidate, "receipt_code_prefix"));

        if ((!existing_file_hash.empty() && existing_file_hash == file_hash) ||
            (!existing_prefix.empty() && existing_prefix == receipt_code_prefix)) {
            duplicate_index = static_cast<int>(idx);
            break;
        }
    }

    if (duplicate_index >= 0) {
        txn.commit();

        std::error_code ec;
        std::filesystem::remove(stored_path, ec);

        const auto &duplicate_row = duplicate_candidates[static_cast<std::size_t>(duplicate_index)];
        const int existing_ocr_id = duplicate_row["id"].as<int>();
        const std::string existing_source_file_name = get_string(duplicate_row, "source_file_name", original_file_name);
        const std::string existing_prefix =
            trim_copy(get_string(duplicate_row, "receipt_code_prefix")).empty()
                ? derive_receipt_code_prefix(existing_source_file_name, get_string(duplicate_row, "file_sha256"))
                : trim_copy(get_string(duplicate_row, "receipt_code_prefix"));
        const std::string existing_status = get_string(duplicate_row, "ocr_status", "uploaded");

        return {
            {"shop_id", shop_id},
            {"uploaded", false},
            {"duplicate", true},
            {"ocr_id", existing_ocr_id},
            {"existing_ocr_id", existing_ocr_id},
            {"receipt_code_prefix", existing_prefix},
            {"ocr_status", existing_status},
            {"file_name", existing_source_file_name},
            {"message", std::format(
                "Upload refused: receipt bundle '{}' already exists as receipt #{}. Use Reprocess receipt or Reopen posted receipt instead of uploading it again.",
                existing_prefix.empty() ? existing_source_file_name : existing_prefix,
                existing_ocr_id
            )}
        };
    }

    pqxx::result inserted = txn.exec_params(R"(
        INSERT INTO tracker.ocr_scans (
            shop_id, image_path, source_path, source_file_name, receipt_code_prefix, mime_type, file_sha256, page_count, scan_type, ocr_status
        )
        VALUES ($1, $2, $3, $4, $5, $6, $7, $8, 'receipt', 'uploaded')
        RETURNING id
    )",
        shop_id,
        stored_path.string(),
        stored_path.string(),
        original_file_name,
        receipt_code_prefix,
        mime_type,
        file_hash,
        is_image_extension(stored_path) ? 1 : 0
    );
    if (inserted.empty()) {
        throw std::runtime_error("Failed to create receipt scan");
    }
    scan_id = inserted[0]["id"].as<int>();

    if (is_image_extension(stored_path)) {
        cv::Mat image = cv::imread(stored_path.string(), cv::IMREAD_COLOR);
        int width = image.empty() ? 0 : image.cols;
        int height = image.empty() ? 0 : image.rows;
        pqxx::result page_res = txn.exec_params(R"(
            INSERT INTO tracker.ocr_scan_pages (ocr_id, page_no, image_path, width, height)
            VALUES ($1, 1, $2, $3, $4)
            RETURNING id
        )",
            scan_id,
            stored_path.string(),
            width,
            height
        );
        page_id = page_res.empty() ? 0 : page_res[0]["id"].as<int>();
        page_count = 1;
    }

    txn.commit();

    return {
        {"shop_id", shop_id},
        {"ocr_id", scan_id},
        {"page_id", page_id},
        {"page_count", page_count},
        {"file_name", original_file_name},
        {"receipt_code_prefix", receipt_code_prefix},
        {"stored_path", stored_path.string()},
        {"mime_type", mime_type},
        {"ocr_status", "uploaded"},
        {"file_sha256", file_hash}
    };
}

nlohmann::json PostgresApi::receipt_queue(int shop_id, const std::string &ocr_status, int limit)
{
    if (shop_id <= 0) {
        throw std::invalid_argument("shop_id is required");
    }

    pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
    pqxx::work txn(conn);
    ensure_expense_tracker_schema(txn);

    pqxx::result rows = txn.exec_params(R"(
        SELECT
            s.id,
            s.shop_id,
            COALESCE(s.source_file_name, '') AS source_file_name,
            COALESCE(s.receipt_code_prefix, '') AS receipt_code_prefix,
            COALESCE(s.mime_type, '') AS mime_type,
            COALESCE(s.image_path, '') AS image_path,
            COALESCE(s.source_path, '') AS source_path,
            COALESCE(s.ocr_status, 'uploaded') AS ocr_status,
            COALESCE(s.review_status, 'pending') AS review_status,
            COALESCE(s.page_count, 0) AS page_count,
            s.scanned_at::text AS scanned_at,
            s.updated_at::text AS updated_at,
            COALESCE(d.draft_count, 0) AS draft_count,
            COALESCE(d.total_cost, 0) AS draft_total_cost,
            COALESCE(d.status, '') AS draft_status,
            COALESCE(p.first_page_id, 0) AS first_page_id,
            COALESCE(j.job_id, 0) AS job_id,
            COALESCE(j.job_status, '') AS job_status,
            j.job_payload,
            COALESCE(j.job_error, '') AS job_error,
            COALESCE(j.job_started_at, '') AS job_started_at,
            COALESCE(j.job_finished_at, '') AS job_finished_at
        FROM tracker.ocr_scans s
        LEFT JOIN LATERAL (
            SELECT
                COUNT(*)::int AS draft_count,
                COALESCE(SUM(COALESCE(grand_total, total_cost, 0)), 0)::numeric AS total_cost,
                COALESCE(MAX(status), '') AS status
            FROM tracker.purchase_drafts d
            WHERE d.ocr_id = s.id
        ) d ON TRUE
        LEFT JOIN LATERAL (
            SELECT id AS first_page_id
            FROM tracker.ocr_scan_pages p
            WHERE p.ocr_id = s.id
            ORDER BY p.page_no ASC
            LIMIT 1
        ) p ON TRUE
        LEFT JOIN LATERAL (
            SELECT
                j.id AS job_id,
                j.status AS job_status,
                j.payload AS job_payload,
                j.error AS job_error,
                j.started_at::text AS job_started_at,
                COALESCE(j.finished_at::text, '') AS job_finished_at
            FROM tracker.job_runs j
            WHERE j.shop_id = s.shop_id
              AND j.job_kind = 'receipt_ocr'
              AND COALESCE(j.payload->>'ocr_id', '') = s.id::text
            ORDER BY j.id DESC
            LIMIT 1
        ) j ON TRUE
        WHERE s.shop_id = $1
          AND ($2 = '' OR s.ocr_status = $2)
        ORDER BY s.updated_at DESC, s.id DESC
        LIMIT $3
    )", shop_id, ocr_status, std::max(limit, 1));

    pqxx::result counts = txn.exec_params(R"(
        SELECT ocr_status, COUNT(*)::bigint AS count
        FROM tracker.ocr_scans
        WHERE shop_id = $1
        GROUP BY ocr_status
        ORDER BY ocr_status
    )", shop_id);
    json items = json::array();
    for (const auto &row : rows) {
        const json job = build_receipt_job_json(row);
        const std::string source_file_name = get_string(row, "source_file_name");
        const std::string receipt_code_prefix =
            trim_copy(get_string(row, "receipt_code_prefix")).empty()
                ? derive_receipt_code_prefix(source_file_name, "")
                : trim_copy(get_string(row, "receipt_code_prefix"));
        items.push_back({
            {"id", row["id"].as<int>()},
            {"shop_id", row["shop_id"].as<int>()},
            {"source_file_name", source_file_name},
            {"receipt_code_prefix", receipt_code_prefix},
            {"mime_type", get_string(row, "mime_type")},
            {"image_path", get_string(row, "image_path")},
            {"source_path", get_string(row, "source_path")},
            {"ocr_status", get_string(row, "ocr_status", "uploaded")},
            {"review_status", get_string(row, "review_status", "pending")},
            {"page_count", get_int64(row, "page_count")},
            {"scanned_at", get_string(row, "scanned_at")},
            {"updated_at", get_string(row, "updated_at")},
            {"draft_count", get_int64(row, "draft_count")},
            {"draft_total_cost", get_double(row, "draft_total_cost")},
            {"draft_status", get_string(row, "draft_status")},
            {"first_page_id", get_int64(row, "first_page_id")},
            {"job", job}
        });
    }

    json status_counts = json::object();
    for (const auto &row : counts) {
        status_counts[get_string(row, "ocr_status", "uploaded")] = get_int64(row, "count");
    }

    return {
        {"shop_id", shop_id},
        {"status_filter", ocr_status},
        {"items", items},
        {"counts", status_counts}
    };
}

nlohmann::json PostgresApi::receipt_detail(int shop_id, int ocr_id)
{
    if (shop_id <= 0 || ocr_id <= 0) {
        throw std::invalid_argument("shop_id and ocr_id are required");
    }

    pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
    pqxx::work txn(conn);
    ensure_expense_tracker_schema(txn);

    pqxx::result scan_res = txn.exec_params(R"(
        SELECT
            id, shop_id, image_path, source_path, source_file_name, receipt_code_prefix, mime_type, file_sha256, page_count,
            scan_type, ocr_status, ocr_model, extracted_text, raw_response, parsed_json, ocr_error,
            review_status, scanned_at::text AS scanned_at, updated_at::text AS updated_at
        FROM tracker.ocr_scans
        WHERE shop_id = $1 AND id = $2
        LIMIT 1
    )", shop_id, ocr_id);
    if (scan_res.empty()) {
        throw std::runtime_error("Receipt scan not found");
    }

    const auto &scan_row = scan_res[0];
    const std::string source_file_name = get_string(scan_row, "source_file_name");
    const std::string receipt_code_prefix =
        trim_copy(get_string(scan_row, "receipt_code_prefix")).empty()
            ? derive_receipt_code_prefix(source_file_name, get_string(scan_row, "file_sha256"))
            : trim_copy(get_string(scan_row, "receipt_code_prefix"));
    json scan = {
        {"id", scan_row["id"].as<int>()},
        {"shop_id", scan_row["shop_id"].as<int>()},
        {"image_path", get_string(scan_row, "image_path")},
        {"source_path", get_string(scan_row, "source_path")},
        {"source_file_name", source_file_name},
        {"receipt_code_prefix", receipt_code_prefix},
        {"mime_type", get_string(scan_row, "mime_type")},
        {"file_sha256", get_string(scan_row, "file_sha256")},
        {"page_count", get_int64(scan_row, "page_count")},
        {"scan_type", get_string(scan_row, "scan_type", "receipt")},
        {"ocr_status", get_string(scan_row, "ocr_status", "uploaded")},
        {"ocr_model", get_string(scan_row, "ocr_model")},
        {"extracted_text", get_string(scan_row, "extracted_text")},
        {"raw_response", parse_optional_json_field(scan_row["raw_response"])},
        {"parsed_json", parse_optional_json_field(scan_row["parsed_json"])},
        {"ocr_error", get_string(scan_row, "ocr_error")},
        {"review_status", get_string(scan_row, "review_status", "pending")},
        {"scanned_at", get_string(scan_row, "scanned_at")},
        {"updated_at", get_string(scan_row, "updated_at")}
    };

    pqxx::result page_res = txn.exec_params(R"(
        SELECT id, page_no, image_path, COALESCE(width, 0) AS width, COALESCE(height, 0) AS height, created_at::text AS created_at
        FROM tracker.ocr_scan_pages
        WHERE ocr_id = $1
        ORDER BY page_no ASC
    )", ocr_id);
    const json shop_history = load_shop_receipt_prompt_history(txn, shop_id, ocr_id);
    const json history_products = shop_history.value("products", json::array());
    json pages = json::array();
    for (const auto &row : page_res) {
        pages.push_back({
            {"id", row["id"].as<int>()},
            {"page_no", row["page_no"].as<int>()},
            {"image_path", get_string(row, "image_path")},
            {"width", get_int64(row, "width")},
            {"height", get_int64(row, "height")},
            {"created_at", get_string(row, "created_at")}
        });
    }

    pqxx::result draft_res = txn.exec_params(R"(
        SELECT
            id, receipt_index, COALESCE(receipt_code, '') AS receipt_code, supplier_name, supplier_tin, supplier_site, supplier_contact_info,
            invoice_id,
            purchase_date::text AS purchase_date,
            COALESCE(total_cost, 0) AS total_cost,
            COALESCE(subtotal_amount, 0) AS subtotal_amount,
            COALESCE(tax_amount, 0) AS tax_amount,
            COALESCE(discount_amount, 0) AS discount_amount,
            COALESCE(rounding_amount, 0) AS rounding_amount,
            COALESCE(grand_total, COALESCE(total_cost, 0)) AS grand_total,
            COALESCE(line_total_basis, 'unknown') AS line_total_basis,
            status, validation_errors, validation_warnings, created_at::text AS created_at, updated_at::text AS updated_at
        FROM tracker.purchase_drafts
        WHERE shop_id = $1 AND ocr_id = $2
        ORDER BY receipt_index ASC, id ASC
    )", shop_id, ocr_id);

    pqxx::result item_res = txn.exec_params(R"(
        SELECT
            id, draft_id, line_no, COALESCE(name, '') AS name, quantity, COALESCE(unit_price, 0) AS unit_price,
            COALESCE(total_price, 0) AS total_price,
            COALESCE(line_discount_percent, 0) AS line_discount_percent,
            COALESCE(line_discount_amount, 0) AS line_discount_amount,
            COALESCE(line_subtotal_amount, 0) AS line_subtotal_amount,
            COALESCE(line_tax_amount, 0) AS line_tax_amount,
            COALESCE(category, '') AS category,
            COALESCE(match_product_id, 0) AS match_product_id,
            COALESCE(ocr_page_id, 0) AS ocr_page_id,
            validation_errors, validation_warnings, created_at::text AS created_at, updated_at::text AS updated_at
        FROM tracker.purchase_draft_items
        WHERE draft_id IN (
            SELECT id FROM tracker.purchase_drafts WHERE shop_id = $1 AND ocr_id = $2
        )
        ORDER BY draft_id ASC, line_no ASC, id ASC
    )", shop_id, ocr_id);

    std::unordered_map<int, json> items_by_draft;
    for (const auto &row : item_res) {
        if (!items_by_draft.contains(row["draft_id"].as<int>())) {
            items_by_draft[row["draft_id"].as<int>()] = json::array();
        }
        items_by_draft[row["draft_id"].as<int>()].push_back({
            {"id", row["id"].as<int>()},
            {"line_no", row["line_no"].as<int>()},
            {"name", get_string(row, "name")},
            {"category", get_string(row, "category", "Others")},
            {"quantity", get_double(row, "quantity")},
            {"unit_price", get_double(row, "unit_price")},
            {"total_price", get_double(row, "total_price")},
            {"line_discount_percent", get_double(row, "line_discount_percent")},
            {"line_discount_amount", get_double(row, "line_discount_amount")},
            {"line_subtotal_amount", get_double(row, "line_subtotal_amount")},
            {"line_tax_amount", get_double(row, "line_tax_amount")},
            {"match_product_id", get_int64(row, "match_product_id")},
            {"ocr_page_id", get_int64(row, "ocr_page_id")},
            {"validation_errors", parse_optional_json_field(row["validation_errors"])},
            {"validation_warnings", parse_optional_json_field(row["validation_warnings"])},
            {"created_at", get_string(row, "created_at")},
            {"updated_at", get_string(row, "updated_at")}
        });
    }

    json drafts = json::array();
    for (const auto &row : draft_res) {
        const int draft_id = row["id"].as<int>();
        const int receipt_index = row["receipt_index"].as<int>();
        const std::string receipt_code =
            trim_copy(get_string(row, "receipt_code")).empty()
                ? derive_receipt_code(receipt_code_prefix, receipt_index + 1)
                : trim_copy(get_string(row, "receipt_code"));
        drafts.push_back({
            {"id", draft_id},
            {"receipt_index", receipt_index},
            {"receipt_code", receipt_code},
            {"supplier", {
                {"name", get_string(row, "supplier_name")},
                {"tin", get_string(row, "supplier_tin")},
                {"site", get_string(row, "supplier_site")},
                {"contact_info", get_string(row, "supplier_contact_info")}
            }},
            {"purchase_order", {
                {"invoice_id", get_string(row, "invoice_id")},
                {"purchase_date", get_string(row, "purchase_date")},
                {"total_cost", get_double(row, "total_cost")},
                {"subtotal_amount", get_double(row, "subtotal_amount")},
                {"tax_amount", get_double(row, "tax_amount")},
                {"discount_amount", get_double(row, "discount_amount")},
                {"rounding_amount", get_double(row, "rounding_amount")},
                {"grand_total", get_double(row, "grand_total")},
                {"line_total_basis", get_string(row, "line_total_basis", "unknown")}
            }},
            {"status", get_string(row, "status", "draft")},
            {"validation_errors", parse_optional_json_field(row["validation_errors"])},
            {"validation_warnings", parse_optional_json_field(row["validation_warnings"])},
            {"purchase_items", items_by_draft.contains(draft_id) ? items_by_draft[draft_id] : json::array()},
            {"created_at", get_string(row, "created_at")},
            {"updated_at", get_string(row, "updated_at")}
        });
    }

    for (auto &draft : drafts) {
        if (!draft.is_object()) continue;
        const std::string supplier_name = draft.value("supplier", json::object()).value("name", "");
        const std::string normalized_supplier_name = normalize_lookup_key(supplier_name);
        if (!draft.contains("purchase_items") || !draft["purchase_items"].is_array()) continue;
        for (auto &item : draft["purchase_items"]) {
            if (!item.is_object()) continue;
            const std::string item_name = trim_copy(item.value("name", ""));
            item["historical_matches"] = history_product_suggestions_for_item(
                history_products,
                item_name,
                normalized_supplier_name,
                5
            );
        }
    }

    pqxx::result review_res = txn.exec_params(R"(
        SELECT id, review_status, review_note, reviewed_by, reviewed_at::text AS reviewed_at,
               assigned_to, draft_id, last_saved_json, created_at::text AS created_at, updated_at::text AS updated_at
        FROM tracker.receipt_reviews
        WHERE ocr_id = $1
        LIMIT 1
    )", ocr_id);

    json review = nullptr;
    if (!review_res.empty()) {
        const auto &row = review_res[0];
        review = {
            {"id", row["id"].as<int>()},
            {"review_status", get_string(row, "review_status", "pending")},
            {"review_note", get_string(row, "review_note")},
            {"reviewed_by", get_string(row, "reviewed_by")},
            {"reviewed_at", get_string(row, "reviewed_at")},
            {"assigned_to", get_string(row, "assigned_to")},
            {"draft_id", get_int64(row, "draft_id")},
            {"last_saved_json", parse_optional_json_field(row["last_saved_json"])},
            {"created_at", get_string(row, "created_at")},
            {"updated_at", get_string(row, "updated_at")}
        };
    }

    pqxx::result job_res = txn.exec_params(R"(
        SELECT
            j.id AS job_id,
            j.status AS job_status,
            j.payload AS job_payload,
            COALESCE(j.error, '') AS job_error,
            j.started_at::text AS job_started_at,
            COALESCE(j.finished_at::text, '') AS job_finished_at
        FROM tracker.job_runs j
        WHERE j.shop_id = $1
          AND j.job_kind = 'receipt_ocr'
          AND COALESCE(j.payload->>'ocr_id', '') = $2::text
        ORDER BY j.id DESC
        LIMIT 1
    )", shop_id, ocr_id);

    json job = nullptr;
    if (!job_res.empty()) {
        job = build_receipt_job_json(job_res[0]);
    }

    const json category_options = category_options_from_config(category_options_for_shop(shop_id));

    return {
        {"shop_id", shop_id},
        {"scan", scan},
        {"pages", pages},
        {"drafts", drafts},
        {"category_options", category_options},
        {"review", review},
        {"job", job}
    };
}

nlohmann::json PostgresApi::receipt_page_image(int shop_id, int page_id)
{
    if (shop_id <= 0 || page_id <= 0) {
        throw std::invalid_argument("shop_id and page_id are required");
    }

    pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, false);
    pqxx::work txn(conn);
    pqxx::result res = txn.exec_params(R"(
        SELECT p.id, p.page_no, p.image_path
        FROM tracker.ocr_scan_pages p
        JOIN tracker.ocr_scans s ON s.id = p.ocr_id
        WHERE s.shop_id = $1 AND p.id = $2
        LIMIT 1
    )", shop_id, page_id);
    if (res.empty()) {
        throw std::runtime_error("Receipt page not found");
    }

    const auto &row = res[0];
    const auto path = std::filesystem::path(get_string(row, "image_path"));
    const std::string bytes = read_file_binary_string(path);
    return {
        {"shop_id", shop_id},
        {"page_id", row["id"].as<int>()},
        {"page_no", row["page_no"].as<int>()},
        {"image_path", path.string()},
        {"image_base64", base64_encode(bytes)}
    };
}

nlohmann::json PostgresApi::save_receipt_drafts(int shop_id,
                                                int ocr_id,
                                                const nlohmann::json &drafts,
                                                const std::string &review_note,
                                                const std::string &reviewed_by)
{
    if (shop_id <= 0 || ocr_id <= 0) {
        throw std::invalid_argument("shop_id and ocr_id are required");
    }

    json normalized = normalize_drafts_payload(drafts);

    pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
    pqxx::work txn(conn);
    ensure_expense_tracker_schema(txn);

    pqxx::result scan_res = txn.exec_params(
        "SELECT id, COALESCE(source_file_name, '') AS source_file_name, COALESCE(receipt_code_prefix, '') AS receipt_code_prefix "
        "FROM tracker.ocr_scans WHERE shop_id = $1 AND id = $2 LIMIT 1",
        shop_id,
        ocr_id
    );
    if (scan_res.empty()) {
        throw std::runtime_error("Receipt scan not found");
    }

    const std::string source_file_name = get_string(scan_res[0], "source_file_name");
    const std::string receipt_code_prefix =
        trim_copy(get_string(scan_res[0], "receipt_code_prefix")).empty()
            ? derive_receipt_code_prefix(source_file_name, "")
            : trim_copy(get_string(scan_res[0], "receipt_code_prefix"));
    const std::string inferred_receipt_date = infer_receipt_date_from_filename(source_file_name);
    const std::vector<std::string> allowed_categories = category_options_for_shop(shop_id);

    const json shop_history = load_shop_receipt_prompt_history(txn, shop_id, ocr_id);
    const json history_suppliers = shop_history.value("suppliers", json::array());
    const json history_products = shop_history.value("products", json::array());

    txn.exec_params(
        "DELETE FROM tracker.purchase_drafts WHERE shop_id = $1 AND ocr_id = $2",
        shop_id,
        ocr_id
    );

    auto build_item_feedback = [&](const json &item,
                                   const std::string &supplier_name,
                                   const std::string &supplier_tin,
                                   const json *historical_supplier,
                                   const std::string &line_total_basis) {
        json errors = json::array();
        json warnings = json::array();
        append_messages(warnings, item.value("warnings", json::array()));

        if (trim_copy(json_to_str(item, "name")).empty()) {
            errors.push_back("Missing item name");
        }
        if (json_to_money(item, "total_price", 0.0) <= 0.0) {
            errors.push_back("Missing or invalid total_price");
        }

        const std::string item_name = trim_copy(json_to_str(item, "name"));
        const std::string normalized_item_name = normalize_lookup_key(item_name);
        std::string supplier_lookup_name = normalize_lookup_key(supplier_name);
        if (supplier_lookup_name.empty() && historical_supplier && historical_supplier->is_object()) {
            supplier_lookup_name = historical_supplier->value("normalized_name", "");
        }

        const json *history_item = nullptr;
        if (!normalized_item_name.empty()) {
            history_item = find_best_history_product_match(history_products,
                                                           item_name,
                                                           supplier_lookup_name);
            if (!history_item) {
                history_item = find_best_history_product_match(history_products,
                                                               item_name);
            }
        }

        if (history_item && history_item->is_object()) {
            const std::string historical_name = trim_copy(history_item->value("name", ""));
            const std::string historical_normalized_name = history_item->value("normalized_name", "");
            if (!historical_name.empty() &&
                !normalized_item_name.empty() &&
                !historical_normalized_name.empty() &&
                historical_normalized_name != normalized_item_name) {
                append_message(warnings,
                               std::format("Item name is close to historical '{}'; verify OCR spelling.",
                                           historical_name));
            }

            const double default_unit_price = history_item->value("default_unit_price", 0.0);
            const double quantity = json_to_double(item, "quantity", 0.0);
            const double unit_price = json_to_money(item, "unit_price", 0.0);
            const double gross_total = quantity > 0.0 ? quantity * unit_price : 0.0;
            const double effective_subtotal = derive_line_subtotal_amount(item, gross_total);
            const double reference_unit_price =
                effective_subtotal > 0.0 && quantity > 0.0
                    ? (effective_subtotal / quantity)
                    : unit_price;
            if (reference_unit_price > 0 && default_unit_price > 0) {
                const double tolerance = std::max(5.0, default_unit_price * 0.15);
                if (std::fabs(reference_unit_price - default_unit_price) > tolerance * 2.0) {
                    append_message(warnings,
                                   std::format("Unit price {:.2f} differs from default unit price {:.2f} for '{}'.",
                                               reference_unit_price,
                                               default_unit_price,
                                               historical_name.empty() ? item_name : historical_name));
                }
            }
        } else if (!normalized_item_name.empty() &&
                   !item.value("summary_synthesized", false) &&
                   !(supplier_name_looks_like_service_bill(supplier_name) &&
                     item_name_looks_like_summary_charge(item_name))) {
            const int supplier_history_count = count_supplier_history_items(history_products, supplier_lookup_name);
            if (supplier_history_count >= 3) {
                const std::string supplier_label =
                    !trim_copy(supplier_name).empty()
                        ? trim_copy(supplier_name)
                        : (historical_supplier && historical_supplier->is_object()
                               ? trim_copy(historical_supplier->value("name", ""))
                               : std::string{});
                if (!supplier_label.empty()) {
                    append_message(warnings,
                                   std::format("Item '{}' is not present in tracker.products for supplier '{}'; verify that it is genuinely new.",
                                               item_name,
                                               supplier_label));
                }
            }
        }

        const double quantity = json_to_double(item, "quantity", 0.0);
        const double unit_price = json_to_money(item, "unit_price", 0.0);
        const double total_price = json_to_money(item, "total_price", 0.0);
        const double line_discount_percent = canonicalize_discount_percent(json_to_double(item, "line_discount_percent", 0.0));
        const double line_discount_amount = json_to_money(item, "line_discount_amount", 0.0);
        const double line_subtotal_amount = json_to_money(item, "line_subtotal_amount", 0.0);
        const double line_tax_amount = json_to_money(item, "line_tax_amount", 0.0);
        if (quantity > 0.0 && unit_price > 0.0 && total_price > 0.0) {
            const double gross_total = quantity * unit_price;
            const bool has_pricing_adjustments = item_has_line_pricing_adjustments(item);
            const double tolerance = line_total_validation_tolerance(gross_total,
                                                                     total_price,
                                                                     has_pricing_adjustments);
            if (has_pricing_adjustments) {
                const double derived_discount_amount = derive_line_discount_amount(item, gross_total);
                const double derived_subtotal_amount = derive_line_subtotal_amount(item, gross_total);

                if (line_subtotal_amount > 0.0 &&
                    (line_discount_percent > 0.0 || line_discount_amount > 0.0) &&
                    !nearly_equal_amount(gross_total - derived_discount_amount, line_subtotal_amount, tolerance)) {
                    append_message(errors,
                                   std::format("Discounted line subtotal mismatch: qty {:.2f} × unit {:.2f} = {:.2f}, discount {:.2f}, expected subtotal {:.2f}, but printed subtotal is {:.2f}. Verify discount OCR.",
                                               quantity,
                                               unit_price,
                                               gross_total,
                                               derived_discount_amount,
                                               gross_total - derived_discount_amount,
                                               line_subtotal_amount));
                }

                bool can_validate_total = false;
                double expected_total = total_price;
                if (line_total_basis == "inclusive") {
                    if (line_subtotal_amount > 0.0 && line_tax_amount > 0.0) {
                        expected_total = line_subtotal_amount + line_tax_amount;
                        can_validate_total = true;
                    } else if (derived_subtotal_amount > 0.0 && line_tax_amount > 0.0) {
                        expected_total = derived_subtotal_amount + line_tax_amount;
                        can_validate_total = true;
                    } else if (line_tax_amount <= 0.0 && line_subtotal_amount > 0.0) {
                        expected_total = line_subtotal_amount;
                        can_validate_total = true;
                    }
                } else if (line_total_basis == "exclusive") {
                    if (line_subtotal_amount > 0.0) {
                        expected_total = line_subtotal_amount;
                        can_validate_total = true;
                    } else if (derived_subtotal_amount > 0.0) {
                        expected_total = derived_subtotal_amount;
                        can_validate_total = true;
                    }
                } else {
                    if (line_subtotal_amount > 0.0 && line_tax_amount > 0.0) {
                        expected_total = line_subtotal_amount + line_tax_amount;
                        can_validate_total = true;
                    } else if (line_subtotal_amount > 0.0) {
                        expected_total = line_subtotal_amount;
                        can_validate_total = true;
                    } else if (derived_subtotal_amount > 0.0 && line_tax_amount > 0.0) {
                        expected_total = derived_subtotal_amount + line_tax_amount;
                        can_validate_total = true;
                    }
                }

                if (can_validate_total && std::fabs(expected_total - total_price) > tolerance) {
                    append_message(errors,
                                   std::format("Discount/tax-aware line mismatch: qty {:.2f} × unit {:.2f}, discount {:.2f}, subtotal {:.2f}, tax {:.2f}, expected total {:.2f}, but total is {:.2f}. Verify row alignment or discount/tax OCR.",
                                               quantity,
                                               unit_price,
                                               derived_discount_amount,
                                               line_subtotal_amount > 0.0 ? line_subtotal_amount : derived_subtotal_amount,
                                               line_tax_amount,
                                               expected_total,
                                               total_price));
                }
            } else if (std::fabs(gross_total - total_price) > tolerance) {
                append_message(errors,
                               std::format("Line arithmetic mismatch: qty {:.2f} × unit {:.2f} = {:.2f}, but total is {:.2f}. Verify row alignment or OCR separator reading.",
                                           quantity,
                                           unit_price,
                                           gross_total,
                                           total_price));
            }
        }

        return std::make_pair(errors, warnings);
    };

    int saved_drafts = 0;
    bool all_ready = true;

    for (std::size_t idx = 0; idx < normalized.size(); ++idx) {
        auto &entry = normalized[idx];
        json supplier = entry.contains("supplier") && entry["supplier"].is_object() ? entry["supplier"] : json::object();
        json order = entry.contains("purchase_order") && entry["purchase_order"].is_object() ? entry["purchase_order"] : json::object();
        if (!entry.contains("purchase_items") || !entry["purchase_items"].is_array()) {
            entry["purchase_items"] = json::array();
        }
        json &items = entry["purchase_items"];
        const std::string receipt_code = derive_receipt_code(receipt_code_prefix, entry.value("receipt_index", static_cast<int>(idx)) + 1);

        const std::string supplier_name = trim_copy(json_to_str(supplier, "name"));
        const std::string supplier_tin = trim_copy(json_to_str(supplier, "tin"));
        const std::string supplier_site = trim_copy(json_to_str(supplier, "site"));
        const std::string supplier_contact = trim_copy(json_to_str(supplier, "contact_info"));
        const std::string parsed_invoice_id = trim_copy(json_to_str(order, "invoice_id"));
        const std::string effective_invoice_id = parsed_invoice_id.empty() ? receipt_code : parsed_invoice_id;
        const std::string date_iso = fix_date(trim_copy(json_to_str(order, "purchase_date")));
        const bool valid_date = !date_iso.empty() && is_valid_iso_date(date_iso);
        const bool has_inferred_receipt_date = !inferred_receipt_date.empty();
        const std::string effective_date = valid_date ? date_iso : (has_inferred_receipt_date ? inferred_receipt_date : std::string{});
        double subtotal_amount = json_to_money(order, "subtotal_amount", 0.0);
        double tax_amount = json_to_money(order, "tax_amount", 0.0);
        double discount_amount = json_to_money(order, "discount_amount", 0.0);
        double rounding_amount = json_to_money(order, "rounding_amount", 0.0);
        double grand_total = json_to_money(order, "grand_total", json_to_money(order, "total_cost", 0.0));
        std::string line_total_basis = canonicalize_line_total_basis(json_to_str(order, "line_total_basis", "unknown"));
        const std::string normalized_supplier_name = normalize_lookup_key(supplier_name);

        const json *historical_supplier = find_best_history_supplier_match(history_suppliers,
                                                                           supplier_name,
                                                                           supplier_tin,
                                                                           supplier_site);

        json draft_errors = json::array();
        json draft_warnings = json::array();
        append_messages(draft_warnings, entry.value("warnings", json::array()));
        if (should_synthesize_non_itemized_purchase_item(supplier_name, draft_warnings, items, grand_total)) {
            items.push_back(make_non_itemized_purchase_item(supplier_name, draft_warnings, grand_total));
            if (subtotal_amount <= 0.0) {
                subtotal_amount = grand_total;
            }
            if (line_total_basis == "unknown") {
                line_total_basis = "inclusive";
            }
            append_message(draft_warnings,
                           "No itemized purchase rows were printed; this bill was saved as one summarized expense line.");
        }
        if (supplier_name.empty() && supplier_tin.empty()) {
            draft_errors.push_back("Missing supplier name or tin");
        }
        if (parsed_invoice_id.empty()) {
            append_message(draft_warnings,
                           std::format("Missing invoice_id on receipt; using generated fallback '{}'.",
                                       receipt_code));
        }
        if (!valid_date && !has_inferred_receipt_date) {
            draft_errors.push_back("Missing or invalid purchase_date");
        }
        if (grand_total <= 0.0) {
            draft_errors.push_back("Missing or invalid grand_total");
        }
        if (!items.is_array() || items.empty()) {
            draft_errors.push_back("No purchase items");
        }
        if (valid_date && has_inferred_receipt_date && date_iso != inferred_receipt_date) {
            append_message(draft_warnings,
                           std::format("Purchase date {} differs from PDF filename date {}; verify the receipt date.",
                                       date_iso,
                                       inferred_receipt_date));
        }

        if (historical_supplier && historical_supplier->is_object()) {
            const std::string historical_name = trim_copy(historical_supplier->value("name", ""));
            const std::string historical_tin = trim_copy(historical_supplier->value("tin", ""));
            const std::string historical_normalized_name = historical_supplier->value("normalized_name", "");

            if (!historical_name.empty()) {
                if (!supplier_tin.empty() &&
                    !historical_tin.empty() &&
                    supplier_tin == historical_tin &&
                    !normalized_supplier_name.empty() &&
                    !historical_normalized_name.empty() &&
                    normalized_supplier_name != historical_normalized_name) {
                    append_message(draft_warnings,
                                   std::format("Supplier tin {} matches historical supplier '{}' but the parsed supplier name differs.",
                                               supplier_tin,
                                               historical_name));
                } else if (!normalized_supplier_name.empty() &&
                           !historical_normalized_name.empty() &&
                           historical_normalized_name != normalized_supplier_name) {
                    append_message(draft_warnings,
                                   std::format("Supplier name is close to historical '{}'; verify OCR spelling.",
                                               historical_name));
                }
            }

            if (!supplier_tin.empty() &&
                !historical_tin.empty() &&
                supplier_tin != historical_tin &&
                !normalized_supplier_name.empty() &&
                normalized_supplier_name == historical_normalized_name) {
                append_message(draft_warnings,
                               std::format("Supplier '{}' historically uses tin {}; parsed tin {} differs.",
                                           historical_name.empty() ? supplier_name : historical_name,
                                           historical_tin,
                                           supplier_tin));
            } else if (supplier_tin.empty() &&
                       !historical_tin.empty() &&
                       !normalized_supplier_name.empty() &&
                       normalized_supplier_name == historical_normalized_name) {
                append_message(draft_warnings,
                               std::format("Historical supplier '{}' usually has tin {}; verify the missing tin.",
                                           historical_name.empty() ? supplier_name : historical_name,
                                           historical_tin));
            }
        }

        double item_sum = 0.0;
        std::vector<json> item_errors_by_line;
        std::vector<json> item_warnings_by_line;
        item_errors_by_line.reserve(items.size());
        item_warnings_by_line.reserve(items.size());
        bool draft_ready = draft_errors.empty();
        for (auto &item : items) {
            item["quantity"] = json_to_double(item, "quantity", 0.0);
            item["unit_price"] = json_to_money(item, "unit_price", 0.0);
            item["total_price"] = json_to_money(item, "total_price", 0.0);
            item["line_discount_percent"] = canonicalize_discount_percent(json_to_double(item, "line_discount_percent", 0.0));
            item["line_discount_amount"] = json_to_money(item, "line_discount_amount", 0.0);
            item["line_subtotal_amount"] = json_to_money(item, "line_subtotal_amount", 0.0);
            item["line_tax_amount"] = json_to_money(item, "line_tax_amount", 0.0);
            normalize_item_unit_price_from_quantity_and_total(item);
            normalize_item_inclusive_tax_from_total(item, line_total_basis);
        }
        reconcile_inclusive_line_tax_to_header(order, items);
        for (auto &item : items) {
            const std::string raw_category = trim_copy(json_to_str(item, "category"));
            const std::string item_name = trim_copy(json_to_str(item, "name"));
            const std::string normalized_item_name = normalize_lookup_key(item_name);
            std::string supplier_lookup_name = normalized_supplier_name;
            if (supplier_lookup_name.empty() && historical_supplier && historical_supplier->is_object()) {
                supplier_lookup_name = historical_supplier->value("normalized_name", "");
            }
            const json *history_item = nullptr;
            if (!normalized_item_name.empty()) {
                history_item = find_best_history_product_match(history_products,
                                                               item_name,
                                                               supplier_lookup_name);
                if (!history_item) {
                    history_item = find_best_history_product_match(history_products,
                                                                   item_name);
                }
            }

            const std::string effective_category =
                canonicalize_category_name(raw_category, allowed_categories);
            std::string resolved_category = effective_category;
            if (history_item && history_item->is_object() &&
                (resolved_category.empty() || resolved_category == "Others")) {
                const std::string historical_category =
                    canonicalize_category_name(trim_copy(history_item->value("category", "")), allowed_categories);
                if (!historical_category.empty() && historical_category != "Others") {
                    resolved_category = historical_category;
                    append_message(item["warnings"],
                                   std::format("Category inferred from historical product '{}' -> '{}'.",
                                               trim_copy(history_item->value("name", item_name)),
                                               historical_category));
                }
            }
            item["category"] = resolved_category;
            item_sum += json_to_money(item, "total_price", 0.0);
            auto [item_errors, item_warnings] = build_item_feedback(item, supplier_name, supplier_tin, historical_supplier, line_total_basis);
            if (!raw_category.empty() &&
                normalize_lookup_key(raw_category) != "OTHER" &&
                normalize_lookup_key(raw_category) != "OTHERS" &&
                resolved_category == "Others" &&
                !contains_category_name(allowed_categories, raw_category)) {
                append_message(item_warnings,
                               std::format("Category '{}' is not in this shop's allowed category list; reset to Others.",
                                           raw_category));
            }
            if (!item_errors.empty()) {
                draft_ready = false;
            }
            item_errors_by_line.push_back(std::move(item_errors));
            item_warnings_by_line.push_back(std::move(item_warnings));
        }
        if (likely_money_separator_drift(order, items)) {
            append_message(draft_errors,
                           "Likely thousands-separator/decimal-separator OCR drift in monetary fields; values such as 12,044 may have been interpreted as 12.044. Verify receipt amounts carefully.");
            draft_ready = false;
        }
        const double computed_grand_total_exclusive = subtotal_amount + tax_amount - discount_amount + rounding_amount;
        const double computed_grand_total_inclusive = subtotal_amount - discount_amount + rounding_amount;
        if (line_total_basis == "unknown") {
            if (item_sum > 0.0 && grand_total > 0.0 && nearly_equal_amount(item_sum, grand_total)) {
                line_total_basis = "inclusive";
            } else if (item_sum > 0.0 && subtotal_amount > 0.0 && nearly_equal_amount(item_sum, subtotal_amount)) {
                line_total_basis = "exclusive";
            } else if (item_sum > 0.0 && tax_amount > 0.0 && grand_total > 0.0 &&
                       nearly_equal_amount(item_sum + tax_amount - discount_amount + rounding_amount, grand_total)) {
                line_total_basis = "exclusive";
            }
        }

        if (subtotal_amount > 0.0 && grand_total > 0.0 &&
            (tax_amount > 0.0 || discount_amount > 0.0 || std::fabs(rounding_amount) > 0.0) &&
            !nearly_equal_amount(computed_grand_total_exclusive, grand_total) &&
            !nearly_equal_amount(computed_grand_total_inclusive, grand_total)) {
            draft_errors.push_back(std::format(
                "Header totals mismatch: subtotal={:.2f} tax={:.2f} discount={:.2f} rounding={:.2f} grand_total={:.2f}",
                subtotal_amount,
                tax_amount,
                discount_amount,
                rounding_amount,
                grand_total));
            draft_ready = false;
        }

        if (item_sum > 0.0 && grand_total > 0.0) {
            bool totals_ok = false;
            if (line_total_basis == "inclusive") {
                totals_ok = nearly_equal_amount(item_sum, grand_total);
            } else if (line_total_basis == "exclusive") {
                if (subtotal_amount > 0.0) {
                    totals_ok = nearly_equal_amount(item_sum, subtotal_amount);
                } else {
                    totals_ok = nearly_equal_amount(item_sum + tax_amount - discount_amount + rounding_amount, grand_total);
                }
            } else {
                totals_ok =
                    nearly_equal_amount(item_sum, grand_total) ||
                    (subtotal_amount > 0.0 && nearly_equal_amount(item_sum, subtotal_amount)) ||
                    ((tax_amount > 0.0 || discount_amount > 0.0 || std::fabs(rounding_amount) > 0.0) &&
                     nearly_equal_amount(item_sum + tax_amount - discount_amount + rounding_amount, grand_total));
            }

            if (!totals_ok) {
                draft_errors.push_back(std::format(
                    "Total mismatch: grand_total={:.2f} items={:.2f} subtotal={:.2f} tax={:.2f} discount={:.2f} rounding={:.2f}",
                    grand_total,
                    item_sum,
                    subtotal_amount,
                    tax_amount,
                    discount_amount,
                    rounding_amount));
                draft_ready = false;
            }
        }

        const std::string draft_status = draft_ready ? "ready" : "needs_review";
        all_ready = all_ready && draft_ready;

        entry["receipt_code"] = receipt_code;
        order["invoice_id"] = effective_invoice_id;
        order["subtotal_amount"] = subtotal_amount;
        order["tax_amount"] = tax_amount;
        order["discount_amount"] = discount_amount;
        order["rounding_amount"] = rounding_amount;
        order["grand_total"] = grand_total;
        order["total_cost"] = grand_total;
        order["line_total_basis"] = line_total_basis;

        pqxx::result draft_res = txn.exec_params(R"(
            INSERT INTO tracker.purchase_drafts (
                shop_id, ocr_id, receipt_index, receipt_code, supplier_name, supplier_tin, supplier_site, supplier_contact_info,
                invoice_id, purchase_date, total_cost, subtotal_amount, tax_amount, discount_amount, rounding_amount, grand_total,
                line_total_basis, status, validation_errors, validation_warnings
            )
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, NULLIF($10, '')::date, $11, $12, $13, $14, $15, $16, $17, $18, $19::jsonb, $20::jsonb)
            RETURNING id
        )",
            shop_id,
            ocr_id,
            entry.value("receipt_index", static_cast<int>(idx)),
            receipt_code,
            supplier_name,
            supplier_tin,
            supplier_site,
            supplier_contact,
            effective_invoice_id,
            effective_date,
            grand_total,
            subtotal_amount,
            tax_amount,
            discount_amount,
            rounding_amount,
            grand_total,
            line_total_basis,
            draft_status,
            draft_errors.dump(),
            draft_warnings.dump()
        );
        if (draft_res.empty()) {
            throw std::runtime_error("Failed to save purchase draft");
        }

        const int draft_id = draft_res[0]["id"].as<int>();
        for (std::size_t line_no = 0; line_no < items.size(); ++line_no) {
            const auto &item = items[line_no];
            txn.exec_params(R"(
                INSERT INTO tracker.purchase_draft_items (
                    draft_id, ocr_id, ocr_page_id, line_no, name, category, quantity, unit_price, total_price,
                    line_discount_percent, line_discount_amount, line_subtotal_amount, line_tax_amount,
                    match_product_id, validation_errors, validation_warnings
                )
                VALUES (
                    $1,
                    $2,
                    (SELECT id FROM tracker.ocr_scan_pages WHERE ocr_id = $2 AND page_no = $3 LIMIT 1),
                    $4,
                    $5,
                    $6,
                    $7::numeric,
                    $8,
                    $9,
                    $10,
                    $11,
                    $12,
                    $13,
                    NULLIF($14, 0),
                    $15::jsonb,
                    $16::jsonb
                )
            )",
                draft_id,
                ocr_id,
                entry.value("receipt_index", static_cast<int>(idx)) + 1,
                static_cast<int>(line_no + 1),
                trim_copy(json_to_str(item, "name")),
                canonicalize_category_name(trim_copy(json_to_str(item, "category")), allowed_categories),
                pqxx::to_string(json_to_double(item, "quantity", 0.0)),
                pqxx::to_string(json_to_money(item, "unit_price", 0.0)),
                pqxx::to_string(json_to_money(item, "total_price", 0.0)),
                pqxx::to_string(canonicalize_discount_percent(json_to_double(item, "line_discount_percent", 0.0))),
                pqxx::to_string(json_to_money(item, "line_discount_amount", 0.0)),
                pqxx::to_string(json_to_money(item, "line_subtotal_amount", 0.0)),
                pqxx::to_string(json_to_money(item, "line_tax_amount", 0.0)),
                json_int_or(item, "match_product_id", 0),
                item_errors_by_line[line_no].dump(),
                item_warnings_by_line[line_no].dump()
            );
        }
        ++saved_drafts;
    }

    const std::string next_scan_status = normalized.empty() ? "needs_review" : (all_ready ? "extracted" : "needs_review");
    txn.exec_params(R"(
        UPDATE tracker.ocr_scans
        SET parsed_json = $1::jsonb,
            extracted_text = $2,
            ocr_status = $3,
            review_status = 'reviewed',
            updated_at = CURRENT_TIMESTAMP
        WHERE shop_id = $4 AND id = $5
    )",
        normalized.dump(),
        normalized.dump(),
        next_scan_status,
        shop_id,
        ocr_id
    );

    txn.exec_params(R"(
        INSERT INTO tracker.receipt_reviews (
            ocr_id, review_status, review_note, reviewed_by, reviewed_at, last_saved_json, updated_at
        )
        VALUES ($1, 'pending', $2, $3, CASE WHEN NULLIF($3, '') IS NULL THEN NULL ELSE CURRENT_TIMESTAMP END, $4::jsonb, CURRENT_TIMESTAMP)
        ON CONFLICT (ocr_id) DO UPDATE
        SET review_status = EXCLUDED.review_status,
            review_note = EXCLUDED.review_note,
            reviewed_by = EXCLUDED.reviewed_by,
            reviewed_at = EXCLUDED.reviewed_at,
            last_saved_json = EXCLUDED.last_saved_json,
            updated_at = CURRENT_TIMESTAMP
    )",
        ocr_id,
        review_note,
        reviewed_by,
        normalized.dump()
    );

    txn.commit();

    return {
        {"shop_id", shop_id},
        {"ocr_id", ocr_id},
        {"saved", true},
        {"draft_count", saved_drafts},
        {"ocr_status", next_scan_status}
    };
}

nlohmann::json PostgresApi::receipt_approve(int shop_id,
                                            int ocr_id,
                                            const std::string &approved_by,
                                            const std::string &review_note)
{
    if (shop_id <= 0 || ocr_id <= 0) {
        throw std::invalid_argument("shop_id and ocr_id are required");
    }

    pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
    pqxx::work txn(conn);
    ensure_expense_tracker_schema(txn);

    pqxx::result scan_res = txn.exec_params(R"(
        SELECT COALESCE(ocr_status, 'uploaded') AS ocr_status
        FROM tracker.ocr_scans
        WHERE shop_id = $1 AND id = $2
        LIMIT 1
    )", shop_id, ocr_id);
    if (scan_res.empty()) {
        throw std::runtime_error("Receipt scan not found");
    }

    const std::string current_status = get_string(scan_res[0], "ocr_status", "uploaded");
    if (current_status == "posted") {
        txn.commit();
        return {
            {"shop_id", shop_id},
            {"ocr_id", ocr_id},
            {"approved", true},
            {"already_posted", true},
            {"ocr_status", "posted"}
        };
    }

    pqxx::result draft_rows = txn.exec_params(R"(
        SELECT id, status, validation_errors
        FROM tracker.purchase_drafts
        WHERE shop_id = $1 AND ocr_id = $2
        ORDER BY receipt_index ASC, id ASC
    )", shop_id, ocr_id);
    if (draft_rows.empty()) {
        throw std::runtime_error("No saved drafts found for this receipt. Save drafts before approving.");
    }

    std::size_t invalid_drafts = 0;
    for (const auto &row : draft_rows) {
        const std::string draft_status = get_string(row, "status", "draft");
        const json validation_errors = parse_optional_json_field(row["validation_errors"]);
        if (draft_status != "ready" && draft_status != "approved" && draft_status != "posted") {
            ++invalid_drafts;
            continue;
        }
        if (validation_errors.is_array() && !validation_errors.empty()) {
            ++invalid_drafts;
        }
    }

    pqxx::result invalid_item_res = txn.exec_params(R"(
        SELECT COUNT(*)::bigint AS invalid_items
        FROM tracker.purchase_draft_items i
        JOIN tracker.purchase_drafts d ON d.id = i.draft_id
        WHERE d.shop_id = $1
          AND d.ocr_id = $2
          AND jsonb_array_length(COALESCE(i.validation_errors, '[]'::jsonb)) > 0
    )", shop_id, ocr_id);
    const long long invalid_items = invalid_item_res.empty() ? 0 : get_int64(invalid_item_res[0], "invalid_items");

    if (invalid_drafts > 0 || invalid_items > 0) {
        throw std::runtime_error(std::format(
            "Receipt still has validation issues (drafts: {}, item lines: {}). Save/fix drafts before approving.",
            invalid_drafts,
            invalid_items
        ));
    }

    txn.exec_params(R"(
        UPDATE tracker.purchase_drafts
        SET status = 'approved',
            updated_at = CURRENT_TIMESTAMP
        WHERE shop_id = $1 AND ocr_id = $2
    )", shop_id, ocr_id);

    txn.exec_params(R"(
        UPDATE tracker.ocr_scans
        SET ocr_status = 'approved',
            review_status = 'approved',
            approved_at = COALESCE(approved_at, CURRENT_TIMESTAMP),
            approved_by = CASE
                WHEN NULLIF($1, '') IS NULL THEN approved_by
                ELSE $1
            END,
            updated_at = CURRENT_TIMESTAMP
        WHERE shop_id = $2 AND id = $3
    )", approved_by, shop_id, ocr_id);

    txn.exec_params(R"(
        INSERT INTO tracker.receipt_reviews (
            ocr_id, review_status, review_note, reviewed_by, reviewed_at, updated_at
        )
        VALUES ($1, 'approved', NULLIF($2, ''), NULLIF($3, ''), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ON CONFLICT (ocr_id) DO UPDATE
        SET review_status = 'approved',
            review_note = CASE
                WHEN NULLIF(EXCLUDED.review_note, '') IS NULL THEN tracker.receipt_reviews.review_note
                ELSE EXCLUDED.review_note
            END,
            reviewed_by = CASE
                WHEN NULLIF(EXCLUDED.reviewed_by, '') IS NULL THEN tracker.receipt_reviews.reviewed_by
                ELSE EXCLUDED.reviewed_by
            END,
            reviewed_at = CURRENT_TIMESTAMP,
            updated_at = CURRENT_TIMESTAMP
    )", ocr_id, review_note, approved_by);

    txn.commit();

    return {
        {"shop_id", shop_id},
        {"ocr_id", ocr_id},
        {"approved", true},
        {"draft_count", static_cast<long long>(draft_rows.size())},
        {"ocr_status", "approved"}
    };
}

nlohmann::json PostgresApi::receipt_post(int shop_id,
                                         int ocr_id,
                                         const std::string &posted_by)
{
    if (shop_id <= 0 || ocr_id <= 0) {
        throw std::invalid_argument("shop_id and ocr_id are required");
    }

    pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
    pqxx::work txn(conn);
    ensure_expense_tracker_schema(txn);

    pqxx::result scan_res = txn.exec_params(R"(
        SELECT COALESCE(ocr_status, 'uploaded') AS ocr_status
        FROM tracker.ocr_scans
        WHERE shop_id = $1 AND id = $2
        LIMIT 1
    )", shop_id, ocr_id);
    if (scan_res.empty()) {
        throw std::runtime_error("Receipt scan not found");
    }

    const std::string current_status = get_string(scan_res[0], "ocr_status", "uploaded");
    if (current_status != "approved" && current_status != "posted") {
        throw std::runtime_error("Approve the receipt before posting it.");
    }

    pqxx::result draft_rows = txn.exec_params(R"(
        SELECT
            d.id,
            d.receipt_index,
            COALESCE(p.id, 0) AS ocr_page_id,
            COALESCE(d.supplier_name, '') AS supplier_name,
            COALESCE(d.supplier_tin, '') AS supplier_tin,
            COALESCE(d.supplier_site, '') AS supplier_site,
            COALESCE(d.supplier_contact_info, '') AS supplier_contact_info,
            COALESCE(d.invoice_id, '') AS invoice_id,
            COALESCE(d.purchase_date::text, '') AS purchase_date,
            COALESCE(d.total_cost, 0) AS total_cost,
            COALESCE(d.subtotal_amount, 0) AS subtotal_amount,
            COALESCE(d.tax_amount, 0) AS tax_amount,
            COALESCE(d.discount_amount, 0) AS discount_amount,
            COALESCE(d.rounding_amount, 0) AS rounding_amount,
            COALESCE(d.grand_total, COALESCE(d.total_cost, 0)) AS grand_total,
            COALESCE(d.line_total_basis, 'unknown') AS line_total_basis,
            COALESCE(d.status, 'draft') AS status,
            d.validation_errors
        FROM tracker.purchase_drafts d
        LEFT JOIN tracker.ocr_scan_pages p
          ON p.ocr_id = d.ocr_id
         AND p.page_no = d.receipt_index + 1
        WHERE d.shop_id = $1 AND d.ocr_id = $2
        ORDER BY d.receipt_index ASC, d.id ASC
    )", shop_id, ocr_id);
    if (draft_rows.empty()) {
        throw std::runtime_error("No approved drafts found for this receipt.");
    }

    pqxx::result item_rows = txn.exec_params(R"(
        SELECT
            i.id,
            i.draft_id,
            COALESCE(i.ocr_page_id, 0) AS ocr_page_id,
            i.line_no,
            COALESCE(i.name, '') AS name,
            COALESCE(i.category, '') AS category,
            COALESCE(i.quantity, 0) AS quantity,
            COALESCE(i.unit_price, 0) AS unit_price,
            COALESCE(i.total_price, 0) AS total_price,
            COALESCE(i.line_discount_percent, 0) AS line_discount_percent,
            COALESCE(i.line_discount_amount, 0) AS line_discount_amount,
            COALESCE(i.line_subtotal_amount, 0) AS line_subtotal_amount,
            COALESCE(i.line_tax_amount, 0) AS line_tax_amount,
            COALESCE(i.match_product_id, 0) AS match_product_id,
            i.validation_errors
        FROM tracker.purchase_draft_items i
        JOIN tracker.purchase_drafts d ON d.id = i.draft_id
        WHERE d.shop_id = $1 AND d.ocr_id = $2
        ORDER BY d.receipt_index ASC, i.line_no ASC, i.id ASC
    )", shop_id, ocr_id);

    std::unordered_map<int, json> items_by_draft;
    for (const auto &row : item_rows) {
        const int draft_id = row["draft_id"].as<int>();
        if (!items_by_draft.contains(draft_id)) {
            items_by_draft[draft_id] = json::array();
        }
        items_by_draft[draft_id].push_back({
            {"name", get_string(row, "name")},
            {"ocr_page_id", get_int64(row, "ocr_page_id")},
            {"category", get_string(row, "category", "Others")},
            {"quantity", get_double(row, "quantity")},
            {"unit_price", get_double(row, "unit_price")},
            {"total_price", get_double(row, "total_price")},
            {"line_discount_percent", get_double(row, "line_discount_percent")},
            {"line_discount_amount", get_double(row, "line_discount_amount")},
            {"line_subtotal_amount", get_double(row, "line_subtotal_amount")},
            {"line_tax_amount", get_double(row, "line_tax_amount")},
            {"match_product_id", get_int64(row, "match_product_id")},
            {"validation_errors", parse_optional_json_field(row["validation_errors"])}
        });
    }

    std::size_t invalid_drafts = 0;
    std::size_t invalid_items = 0;
    for (const auto &row : draft_rows) {
        const std::string draft_status = get_string(row, "status", "draft");
        const json validation_errors = parse_optional_json_field(row["validation_errors"]);
        if (draft_status != "approved" && draft_status != "posted") {
            ++invalid_drafts;
        }
        if (validation_errors.is_array() && !validation_errors.empty()) {
            ++invalid_drafts;
        }

        const int draft_id = row["id"].as<int>();
        const auto it = items_by_draft.find(draft_id);
        if (it == items_by_draft.end() || !it->second.is_array() || it->second.empty()) {
            ++invalid_drafts;
            continue;
        }
        for (const auto &item : it->second) {
            const json item_errors = item.value("validation_errors", json::array());
            if (item_errors.is_array() && !item_errors.empty()) {
                ++invalid_items;
            }
        }
    }

    if (invalid_drafts > 0 || invalid_items > 0) {
        throw std::runtime_error(std::format(
            "Receipt cannot be posted while validation issues remain (drafts: {}, item lines: {}). Approve a clean draft set first.",
            invalid_drafts,
            invalid_items
        ));
    }

    long long posted_orders = 0;
    long long posted_items = 0;

    auto resolve_product_id = [&](long long match_product_id,
                                  int ocr_page_id,
                                  const std::string &item_name,
                                  int supplier_id,
                                  const std::string &item_category) {
        if (match_product_id > 0) {
            pqxx::result existing = txn.exec_params(
                "SELECT id FROM tracker.products WHERE shop_id = $1 AND id = $2 LIMIT 1",
                shop_id,
                match_product_id
            );
            if (!existing.empty()) {
                const int existing_id = existing[0]["id"].as<int>();
                txn.exec_params(R"(
                    UPDATE tracker.products
                    SET ocr_id = COALESCE(ocr_id, $1),
                        ocr_page_id = COALESCE(ocr_page_id, NULLIF($2, 0)),
                        updated_at = CURRENT_TIMESTAMP
                    WHERE id = $3
                )", ocr_id, ocr_page_id, existing_id);
                return existing_id;
            }
        }
        return ensure_product_exists(shop_id, ocr_id, ocr_page_id, trim_copy(item_name), supplier_id, txn, "ingredient", item_category);
    };

    for (const auto &row : draft_rows) {
        const int draft_id = row["id"].as<int>();
        const int draft_page_id = get_int64(row, "ocr_page_id");
        json supplier = {
            {"name", get_string(row, "supplier_name")},
            {"tin", get_string(row, "supplier_tin")},
            {"site", get_string(row, "supplier_site")},
            {"contact_info", get_string(row, "supplier_contact_info")}
        };
        json order = {
            {"invoice_id", get_string(row, "invoice_id")},
            {"purchase_date", get_string(row, "purchase_date")},
            {"total_cost", get_double(row, "total_cost")},
            {"subtotal_amount", get_double(row, "subtotal_amount")},
            {"tax_amount", get_double(row, "tax_amount")},
            {"discount_amount", get_double(row, "discount_amount")},
            {"rounding_amount", get_double(row, "rounding_amount")},
            {"grand_total", get_double(row, "grand_total")},
            {"line_total_basis", get_string(row, "line_total_basis", "unknown")}
        };

        const int supplier_id = ensure_supplier_exists(shop_id, ocr_id, draft_page_id, supplier, txn);
        const int purchase_id = insert_purchase_order(shop_id, ocr_id, draft_page_id, order, supplier_id, txn);

        txn.exec_params(R"(
            DELETE FROM tracker.purchase_items
            WHERE shop_id = $1
              AND purchase_id = $2
              AND COALESCE(ocr_id, 0) = COALESCE($3, 0)
        )", shop_id, purchase_id, ocr_id);

        const auto items_it = items_by_draft.find(draft_id);
        if (items_it == items_by_draft.end()) {
            throw std::runtime_error(std::format("No purchase items found for draft {}", draft_id));
        }

        for (const auto &item : items_it->second) {
            const int item_page_id = static_cast<int>(item.value("ocr_page_id", static_cast<long long>(draft_page_id)));
            const int product_id = resolve_product_id(item.value("match_product_id", 0LL),
                                                      item_page_id,
                                                      item.value("name", std::string{}),
                                                      supplier_id,
                                                      json_to_str(item, "category", "Others"));
            insert_purchase_item(shop_id, ocr_id, item_page_id, purchase_id, product_id, item, txn);
            ++posted_items;
        }

        txn.exec_params(R"(
            UPDATE tracker.purchase_orders
            SET supplier_id = $1,
                purchase_date = NULLIF($2, '')::date,
                total_cost = $3,
                subtotal_amount = $4,
                tax_amount = $5,
                discount_amount = $6,
                rounding_amount = $7,
                grand_total = $8,
                line_total_basis = $9,
                ocr_id = $10,
                ocr_page_id = NULLIF($11, 0),
                status = 'posted',
                updated_at = CURRENT_TIMESTAMP
            WHERE id = $12
        )",
            supplier_id,
            get_string(row, "purchase_date"),
            get_double(row, "total_cost"),
            get_double(row, "subtotal_amount"),
            get_double(row, "tax_amount"),
            get_double(row, "discount_amount"),
            get_double(row, "rounding_amount"),
            get_double(row, "grand_total"),
            get_string(row, "line_total_basis", "unknown"),
            ocr_id,
            draft_page_id,
            purchase_id
        );
        ++posted_orders;
    }

    txn.exec_params(R"(
        UPDATE tracker.purchase_drafts
        SET status = 'posted',
            updated_at = CURRENT_TIMESTAMP
        WHERE shop_id = $1 AND ocr_id = $2
    )", shop_id, ocr_id);

    txn.exec_params(R"(
        UPDATE tracker.ocr_scans
        SET ocr_status = 'posted',
            review_status = 'approved',
            posted_at = CURRENT_TIMESTAMP,
            posted_by = CASE
                WHEN NULLIF($1, '') IS NULL THEN posted_by
                ELSE $1
            END,
            updated_at = CURRENT_TIMESTAMP
        WHERE shop_id = $2 AND id = $3
    )", posted_by, shop_id, ocr_id);

    txn.exec_params(R"(
        INSERT INTO tracker.receipt_reviews (
            ocr_id, review_status, reviewed_by, reviewed_at, updated_at
        )
        VALUES ($1, 'approved', NULLIF($2, ''), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ON CONFLICT (ocr_id) DO UPDATE
        SET review_status = 'approved',
            reviewed_by = CASE
                WHEN NULLIF(EXCLUDED.reviewed_by, '') IS NULL THEN tracker.receipt_reviews.reviewed_by
                ELSE EXCLUDED.reviewed_by
            END,
            reviewed_at = COALESCE(tracker.receipt_reviews.reviewed_at, CURRENT_TIMESTAMP),
            updated_at = CURRENT_TIMESTAMP
    )", ocr_id, posted_by);

    txn.commit();

    return {
        {"shop_id", shop_id},
        {"ocr_id", ocr_id},
        {"posted", true},
        {"draft_count", static_cast<long long>(draft_rows.size())},
        {"order_count", posted_orders},
        {"item_count", posted_items},
        {"ocr_status", "posted"}
    };
}

nlohmann::json PostgresApi::receipt_run_ocr(int shop_id, int ocr_id, const std::string &openai_api_key)
{
    if (shop_id <= 0 || ocr_id <= 0) {
        throw std::invalid_argument("shop_id and ocr_id are required");
    }
    if (trim_copy(openai_api_key).empty()) {
        throw std::invalid_argument("openai_api_key is required");
    }

    long long job_id = 0;
    int latest_job_id = 0;
    std::string source_path;
    std::string source_file_name;
    std::string current_status;
    std::string latest_job_status;
    std::string latest_job_server_instance_id;
    json latest_job = nullptr;

    {
        pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
        pqxx::work txn(conn);
        ensure_expense_tracker_schema(txn);
        pqxx::result scan_res = txn.exec_params(R"(
            SELECT COALESCE(source_path, image_path) AS source_path,
                   COALESCE(source_file_name, '') AS source_file_name,
                   COALESCE(ocr_status, 'uploaded') AS ocr_status
            FROM tracker.ocr_scans
            WHERE shop_id = $1 AND id = $2
            LIMIT 1
        )", shop_id, ocr_id);
        if (scan_res.empty()) {
            throw std::runtime_error("Receipt scan not found");
        }
        source_path = get_string(scan_res[0], "source_path");
        source_file_name = get_string(scan_res[0], "source_file_name");
        current_status = get_string(scan_res[0], "ocr_status", "uploaded");
        if (current_status == "posted") {
            throw std::runtime_error("Receipt is already posted. Use Reopen posted receipt first, then Reprocess receipt if you need a new OCR pass.");
        }

        pqxx::result latest_job_res = txn.exec_params(R"(
            SELECT
                id AS job_id,
                status AS job_status,
                payload AS job_payload,
                COALESCE(error, '') AS job_error,
                started_at::text AS job_started_at,
                COALESCE(finished_at::text, '') AS job_finished_at
            FROM tracker.job_runs
            WHERE shop_id = $1
              AND job_kind = 'receipt_ocr'
              AND COALESCE(payload->>'ocr_id', '') = $2::text
            ORDER BY id DESC
            LIMIT 1
        )", shop_id, ocr_id);
        if (!latest_job_res.empty()) {
            latest_job_id = latest_job_res[0]["job_id"].as<int>();
            latest_job_status = get_string(latest_job_res[0], "job_status");
            latest_job = build_receipt_job_json(latest_job_res[0]);
            const json latest_payload = parse_optional_json_field(latest_job_res[0]["job_payload"]);
            if (latest_payload.is_object()) {
                latest_job_server_instance_id = json_string_or(latest_payload, "server_instance_id");
            }
        }

        if (current_status == "processing") {
            const bool active_job = latest_job_id > 0 && latest_job_status == "started";
            const bool owned_by_current_server =
                !latest_job_server_instance_id.empty() &&
                latest_job_server_instance_id == current_server_instance_id();

            if (active_job && owned_by_current_server) {
                txn.commit();
                return {
                    {"shop_id", shop_id},
                    {"ocr_id", ocr_id},
                    {"accepted", false},
                    {"already_running", true},
                    {"ocr_status", "processing"},
                    {"job", latest_job},
                    {"message", "OCR is already running for this receipt."}
                };
            }

            if (active_job) {
                const std::string abandoned_error = "Abandoned OCR job from previous server instance";
                const std::string server_instance_id = current_server_instance_id();
                txn.exec_params(R"(
                    UPDATE tracker.job_runs
                    SET status = 'failed',
                        error = $1,
                        payload = COALESCE(payload, '{}'::jsonb) || jsonb_build_object('stage', 'failed', 'server_instance_id', $2::text),
                        finished_at = CURRENT_TIMESTAMP
                    WHERE id = $3
                )",
                    abandoned_error,
                    server_instance_id,
                    static_cast<int>(latest_job_id)
                );
            }
        }

        pqxx::result job_res = txn.exec_params(R"(
            INSERT INTO tracker.job_runs (shop_id, job_kind, status, payload)
            VALUES ($1, 'receipt_ocr', 'started', $2::jsonb)
            RETURNING id
        )",
            shop_id,
            build_receipt_job_payload(ocr_id, source_path, source_file_name, "queued").dump()
        );
        job_id = job_res.empty() ? 0 : job_res[0]["id"].as<long long>();
        txn.exec_params(R"(
            UPDATE tracker.ocr_scans
            SET ocr_status = 'processing',
                ocr_error = NULL,
                updated_at = CURRENT_TIMESTAMP
            WHERE shop_id = $1 AND id = $2
        )", shop_id, ocr_id);
        txn.commit();
    }

    std::thread([shop_id, ocr_id, openai_api_key, job_id]() {
        try {
            PostgresApi db;
            db.execute_receipt_ocr_job(shop_id, ocr_id, openai_api_key, job_id);
        } catch (const std::exception &e) {
            spdlog::error("[receipt_run_ocr] background OCR failed shop_id={} ocr_id={} {}", shop_id, ocr_id, e.what());
        } catch (...) {
            spdlog::error("[receipt_run_ocr] background OCR failed shop_id={} ocr_id={} with unknown error", shop_id, ocr_id);
        }
    }).detach();

    return {
        {"shop_id", shop_id},
        {"ocr_id", ocr_id},
        {"accepted", true},
        {"already_running", false},
        {"job_id", job_id},
        {"ocr_status", "processing"},
        {"message", "OCR started. Poll receipt status for progress."}
    };
}

nlohmann::json PostgresApi::receipt_reprocess(int shop_id, int ocr_id, const std::string &openai_api_key)
{
    if (shop_id <= 0 || ocr_id <= 0) {
        throw std::invalid_argument("shop_id and ocr_id are required");
    }

    {
        pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
        pqxx::work txn(conn);
        ensure_expense_tracker_schema(txn);

        pqxx::result scan_res = txn.exec_params(R"(
            SELECT COALESCE(ocr_status, 'uploaded') AS ocr_status
            FROM tracker.ocr_scans
            WHERE shop_id = $1 AND id = $2
            LIMIT 1
        )", shop_id, ocr_id);
        if (scan_res.empty()) {
            throw std::runtime_error("Receipt scan not found");
        }

        const std::string current_status = get_string(scan_res[0], "ocr_status", "uploaded");
        if (current_status == "posted") {
            throw std::runtime_error("Receipt is already posted. Use Reopen posted receipt before reprocessing it.");
        }

        txn.exec_params(R"(
            UPDATE tracker.receipt_reviews
            SET review_status = 'pending',
                updated_at = CURRENT_TIMESTAMP
            WHERE ocr_id = $1
        )", ocr_id);
        txn.commit();
    }

    json started = receipt_run_ocr(shop_id, ocr_id, openai_api_key);
    started["reprocessed"] = true;
    started["message"] = std::format("Reprocessing receipt #{} with a fresh OCR pass.", ocr_id);
    return started;
}

nlohmann::json PostgresApi::receipt_reopen(int shop_id,
                                           int ocr_id,
                                           const std::string &reopened_by,
                                           const std::string &review_note)
{
    if (shop_id <= 0 || ocr_id <= 0) {
        throw std::invalid_argument("shop_id and ocr_id are required");
    }

    pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
    pqxx::work txn(conn);
    ensure_expense_tracker_schema(txn);

    pqxx::result scan_res = txn.exec_params(R"(
        SELECT COALESCE(ocr_status, 'uploaded') AS ocr_status
        FROM tracker.ocr_scans
        WHERE shop_id = $1 AND id = $2
        LIMIT 1
    )", shop_id, ocr_id);
    if (scan_res.empty()) {
        throw std::runtime_error("Receipt scan not found");
    }

    const std::string current_status = get_string(scan_res[0], "ocr_status", "uploaded");
    if (current_status != "posted") {
        throw std::runtime_error("Only posted receipts can be reopened.");
    }

    const long long removed_items = txn.exec_params(
        "DELETE FROM tracker.purchase_items WHERE shop_id = $1 AND COALESCE(ocr_id, 0) = COALESCE($2, 0)",
        shop_id,
        ocr_id
    ).affected_rows();

    const long long removed_orders = txn.exec_params(
        "DELETE FROM tracker.purchase_orders WHERE shop_id = $1 AND COALESCE(ocr_id, 0) = COALESCE($2, 0)",
        shop_id,
        ocr_id
    ).affected_rows();

    pqxx::result draft_count_res = txn.exec_params(R"(
        SELECT COUNT(*)::bigint AS draft_count
        FROM tracker.purchase_drafts
        WHERE shop_id = $1 AND ocr_id = $2
    )", shop_id, ocr_id);
    const long long draft_count = draft_count_res.empty() ? 0 : get_int64(draft_count_res[0], "draft_count");
    if (draft_count <= 0) {
        throw std::runtime_error("Cannot reopen posted receipt because no staged drafts exist for it.");
    }

    pqxx::result invalid_draft_res = txn.exec_params(R"(
        SELECT COUNT(*)::bigint AS invalid_drafts
        FROM tracker.purchase_drafts
        WHERE shop_id = $1
          AND ocr_id = $2
          AND (
                COALESCE(status, 'draft') NOT IN ('ready', 'approved', 'posted')
                OR jsonb_array_length(COALESCE(validation_errors, '[]'::jsonb)) > 0
              )
    )", shop_id, ocr_id);
    const long long invalid_drafts = invalid_draft_res.empty() ? 0 : get_int64(invalid_draft_res[0], "invalid_drafts");

    pqxx::result invalid_item_res = txn.exec_params(R"(
        SELECT COUNT(*)::bigint AS invalid_items
        FROM tracker.purchase_draft_items i
        JOIN tracker.purchase_drafts d ON d.id = i.draft_id
        WHERE d.shop_id = $1
          AND d.ocr_id = $2
          AND jsonb_array_length(COALESCE(i.validation_errors, '[]'::jsonb)) > 0
    )", shop_id, ocr_id);
    const long long invalid_items = invalid_item_res.empty() ? 0 : get_int64(invalid_item_res[0], "invalid_items");

    const std::string reopened_status = (invalid_drafts == 0 && invalid_items == 0) ? "extracted" : "needs_review";
    const std::string reopened_draft_status = reopened_status == "extracted" ? "ready" : "needs_review";

    txn.exec_params(R"(
        UPDATE tracker.purchase_drafts
        SET status = $1,
            updated_at = CURRENT_TIMESTAMP
        WHERE shop_id = $2 AND ocr_id = $3
    )", reopened_draft_status, shop_id, ocr_id);

    txn.exec_params(R"(
        UPDATE tracker.ocr_scans
        SET ocr_status = $1,
            review_status = 'reviewed',
            approved_at = NULL,
            approved_by = NULL,
            posted_at = NULL,
            posted_by = NULL,
            updated_at = CURRENT_TIMESTAMP
        WHERE shop_id = $2 AND id = $3
    )", reopened_status, shop_id, ocr_id);

    txn.exec_params(R"(
        INSERT INTO tracker.receipt_reviews (
            ocr_id, review_status, review_note, reviewed_by, reviewed_at, updated_at
        )
        VALUES ($1, 'pending', NULLIF($2, ''), NULLIF($3, ''), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ON CONFLICT (ocr_id) DO UPDATE
        SET review_status = 'pending',
            review_note = CASE
                WHEN NULLIF($2, '') IS NULL THEN tracker.receipt_reviews.review_note
                ELSE NULLIF($2, '')
            END,
            reviewed_by = CASE
                WHEN NULLIF($3, '') IS NULL THEN tracker.receipt_reviews.reviewed_by
                ELSE NULLIF($3, '')
            END,
            reviewed_at = CURRENT_TIMESTAMP,
            updated_at = CURRENT_TIMESTAMP
    )", ocr_id, review_note, reopened_by);

    txn.commit();

    return {
        {"shop_id", shop_id},
        {"ocr_id", ocr_id},
        {"reopened", true},
        {"ocr_status", reopened_status},
        {"draft_count", draft_count},
        {"removed_orders", removed_orders},
        {"removed_items", removed_items},
        {"message", "Posted receipt reopened. Final purchase rows were removed; review and post again when ready."}
    };
}

nlohmann::json PostgresApi::receipt_delete(int shop_id, int ocr_id)
{
    if (shop_id <= 0 || ocr_id <= 0) {
        throw std::invalid_argument("shop_id and ocr_id are required");
    }

    std::string source_path;
    std::string image_path;
    std::string source_file_name;
    std::vector<std::string> page_paths;
    int deleted_jobs = 0;
    int deleted_scan_rows = 0;

    {
        pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
        pqxx::work txn(conn);
        ensure_expense_tracker_schema(txn);

        pqxx::result scan_res = txn.exec_params(R"(
            SELECT
                COALESCE(source_path, '') AS source_path,
                COALESCE(image_path, '') AS image_path,
                COALESCE(source_file_name, '') AS source_file_name
            FROM tracker.ocr_scans
            WHERE shop_id = $1 AND id = $2
            LIMIT 1
        )", shop_id, ocr_id);
        if (scan_res.empty()) {
            throw std::runtime_error("Receipt scan not found");
        }

        source_path = get_string(scan_res[0], "source_path");
        image_path = get_string(scan_res[0], "image_path");
        source_file_name = get_string(scan_res[0], "source_file_name");

        const long long purchase_order_count = txn.exec_params1(
            "SELECT COUNT(*)::bigint FROM tracker.purchase_orders WHERE shop_id = $1 AND ocr_id = $2",
            shop_id,
            ocr_id
        )[0].as<long long>();
        const long long purchase_item_count = txn.exec_params1(
            "SELECT COUNT(*)::bigint FROM tracker.purchase_items WHERE shop_id = $1 AND ocr_id = $2",
            shop_id,
            ocr_id
        )[0].as<long long>();
        if (purchase_order_count > 0 || purchase_item_count > 0) {
            throw std::runtime_error("Cannot delete receipt after final purchase rows exist. Use Reopen posted receipt first.");
        }
        const long long supplier_ref_count = txn.exec_params1(
            "SELECT COUNT(*)::bigint FROM tracker.suppliers WHERE shop_id = $1 AND ocr_id = $2",
            shop_id,
            ocr_id
        )[0].as<long long>();
        const long long product_ref_count = txn.exec_params1(
            "SELECT COUNT(*)::bigint FROM tracker.products WHERE shop_id = $1 AND ocr_id = $2",
            shop_id,
            ocr_id
        )[0].as<long long>();
        if (supplier_ref_count > 0 || product_ref_count > 0) {
            throw std::runtime_error("Cannot delete receipt while supplier/product master rows still reference its OCR images. Reprocess or repost the receipt instead.");
        }

        pqxx::result page_res = txn.exec_params(
            "SELECT image_path FROM tracker.ocr_scan_pages WHERE ocr_id = $1 ORDER BY page_no ASC",
            ocr_id
        );
        page_paths.reserve(page_res.size());
        for (const auto &row : page_res) {
            page_paths.push_back(get_string(row, "image_path"));
        }

        deleted_jobs = txn.exec_params(
            R"(
                DELETE FROM tracker.job_runs
                WHERE shop_id = $1
                  AND job_kind = 'receipt_ocr'
                  AND COALESCE(payload->>'ocr_id', '') = $2::text
            )",
            shop_id,
            ocr_id
        ).affected_rows();

        deleted_scan_rows = txn.exec_params(
            "DELETE FROM tracker.ocr_scans WHERE shop_id = $1 AND id = $2",
            shop_id,
            ocr_id
        ).affected_rows();

        txn.commit();
    }

    const auto upload_root = resolve_receipt_upload_root();
    std::unordered_set<std::string> directories_to_remove;
    std::unordered_set<std::string> files_to_remove;

    auto queue_file = [&](const std::string &raw_path) {
        if (trim_copy(raw_path).empty()) return;
        const auto path = absolute_normalized_path(std::filesystem::path(raw_path));
        if (!path_is_within_root(path, upload_root)) return;

        if (is_regular_file_path(path)) {
            files_to_remove.insert(path.string());
        }

        const auto parent = path.parent_path();
        if (!parent.empty() && path_is_within_root(parent, upload_root) && parent.filename().string().ends_with("_pages")) {
            directories_to_remove.insert(parent.string());
        }
    };

    queue_file(source_path);
    queue_file(image_path);
    for (const auto &page_path : page_paths) {
        queue_file(page_path);
    }

    long long removed_directory_entries = 0;
    int removed_file_count = 0;

    for (const auto &dir : directories_to_remove) {
        std::error_code ec;
        removed_directory_entries += static_cast<long long>(std::filesystem::remove_all(dir, ec));
        if (ec) {
            spdlog::warn("[receipt_delete] failed to remove directory '{}': {}", dir, ec.message());
        }
    }

    for (const auto &file : files_to_remove) {
        const auto path = std::filesystem::path(file);
        if (directories_to_remove.contains(path.parent_path().string())) continue;
        std::error_code ec;
        const bool removed = std::filesystem::remove(path, ec);
        if (ec) {
            spdlog::warn("[receipt_delete] failed to remove file '{}': {}", file, ec.message());
            continue;
        }
        if (removed) ++removed_file_count;
    }

    return {
        {"shop_id", shop_id},
        {"ocr_id", ocr_id},
        {"source_file_name", source_file_name},
        {"deleted", deleted_scan_rows > 0},
        {"deleted_scan_rows", deleted_scan_rows},
        {"deleted_jobs", deleted_jobs},
        {"removed_directories", static_cast<long long>(directories_to_remove.size())},
        {"removed_directory_entries", removed_directory_entries},
        {"removed_files", removed_file_count}
    };
}

nlohmann::json PostgresApi::receipt_delete_page(int shop_id, int ocr_id, int page_id)
{
    if (shop_id <= 0 || ocr_id <= 0 || page_id <= 0) {
        throw std::invalid_argument("shop_id, ocr_id, and page_id are required");
    }

    std::string source_file_name;
    std::string receipt_code_prefix;
    std::string page_image_path;
    int page_no = 0;
    long long remaining_pages = 0;
    long long deleted_drafts = 0;
    long long deleted_pages = 0;

    {
        pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
        pqxx::work txn(conn);
        ensure_expense_tracker_schema(txn);

        pqxx::result scan_res = txn.exec_params(R"(
            SELECT
                COALESCE(source_file_name, '') AS source_file_name,
                COALESCE(receipt_code_prefix, '') AS receipt_code_prefix,
                COALESCE(page_count, 0) AS page_count
            FROM tracker.ocr_scans
            WHERE shop_id = $1 AND id = $2
            LIMIT 1
        )", shop_id, ocr_id);
        if (scan_res.empty()) {
            throw std::runtime_error("Receipt scan not found");
        }

        source_file_name = get_string(scan_res[0], "source_file_name");
        receipt_code_prefix =
            trim_copy(get_string(scan_res[0], "receipt_code_prefix")).empty()
                ? derive_receipt_code_prefix(source_file_name, "")
                : trim_copy(get_string(scan_res[0], "receipt_code_prefix"));

        pqxx::result page_res = txn.exec_params(R"(
            SELECT page_no, COALESCE(image_path, '') AS image_path
            FROM tracker.ocr_scan_pages
            WHERE ocr_id = $1 AND id = $2
            LIMIT 1
        )", ocr_id, page_id);
        if (page_res.empty()) {
            throw std::runtime_error("Receipt page not found");
        }

        page_no = page_res[0]["page_no"].as<int>();
        page_image_path = get_string(page_res[0], "image_path");

        const long long purchase_order_count = txn.exec_params1(
            "SELECT COUNT(*)::bigint FROM tracker.purchase_orders WHERE shop_id = $1 AND ocr_id = $2",
            shop_id,
            ocr_id
        )[0].as<long long>();
        const long long purchase_item_count = txn.exec_params1(
            "SELECT COUNT(*)::bigint FROM tracker.purchase_items WHERE shop_id = $1 AND ocr_id = $2",
            shop_id,
            ocr_id
        )[0].as<long long>();
        if (purchase_order_count > 0 || purchase_item_count > 0) {
            throw std::runtime_error("Cannot delete a page after final purchase rows exist for this receipt. Reopen the receipt first.");
        }

        const long long supplier_ref_count = txn.exec_params1(
            "SELECT COUNT(*)::bigint FROM tracker.suppliers WHERE shop_id = $1 AND ocr_id = $2",
            shop_id,
            ocr_id
        )[0].as<long long>();
        const long long product_ref_count = txn.exec_params1(
            "SELECT COUNT(*)::bigint FROM tracker.products WHERE shop_id = $1 AND ocr_id = $2",
            shop_id,
            ocr_id
        )[0].as<long long>();
        if (supplier_ref_count > 0 || product_ref_count > 0) {
            throw std::runtime_error("Cannot delete a page while supplier/product master rows still reference this receipt OCR. Reprocess or repost the receipt instead.");
        }

        const long long page_count = get_int64(scan_res[0], "page_count");
        if (page_count <= 1) {
            txn.commit();
            json deleted_receipt = receipt_delete(shop_id, ocr_id);
            deleted_receipt["deleted_page"] = true;
            deleted_receipt["deleted_receipt"] = true;
            deleted_receipt["page_id"] = page_id;
            deleted_receipt["page_no"] = page_no;
            deleted_receipt["remaining_pages"] = 0;
            return deleted_receipt;
        }

        const int deleted_receipt_index = page_no - 1;
        deleted_drafts = txn.exec_params(
            "DELETE FROM tracker.purchase_drafts WHERE shop_id = $1 AND ocr_id = $2 AND receipt_index = $3",
            shop_id,
            ocr_id,
            deleted_receipt_index
        ).affected_rows();

        deleted_pages = txn.exec_params(
            "DELETE FROM tracker.ocr_scan_pages WHERE ocr_id = $1 AND id = $2",
            ocr_id,
            page_id
        ).affected_rows();

        txn.exec_params(
            "UPDATE tracker.ocr_scan_pages SET page_no = page_no - 1 WHERE ocr_id = $1 AND page_no > $2",
            ocr_id,
            page_no
        );

        txn.exec_params(
            "UPDATE tracker.purchase_drafts SET receipt_index = receipt_index - 1, updated_at = CURRENT_TIMESTAMP WHERE shop_id = $1 AND ocr_id = $2 AND receipt_index > $3",
            shop_id,
            ocr_id,
            deleted_receipt_index
        );

        pqxx::result remaining_drafts_res = txn.exec_params(R"(
            SELECT id, receipt_index
            FROM tracker.purchase_drafts
            WHERE shop_id = $1 AND ocr_id = $2
            ORDER BY receipt_index ASC, id ASC
        )", shop_id, ocr_id);
        for (const auto &row : remaining_drafts_res) {
            const int draft_id = row["id"].as<int>();
            const int receipt_index = row["receipt_index"].as<int>();
            txn.exec_params(
                "UPDATE tracker.purchase_drafts SET receipt_code = $1, updated_at = CURRENT_TIMESTAMP WHERE id = $2",
                derive_receipt_code(receipt_code_prefix, receipt_index + 1),
                draft_id
            );
        }

        remaining_pages = txn.exec_params1(
            "SELECT COUNT(*)::bigint FROM tracker.ocr_scan_pages WHERE ocr_id = $1",
            ocr_id
        )[0].as<long long>();

        const long long remaining_draft_count = txn.exec_params1(
            "SELECT COUNT(*)::bigint FROM tracker.purchase_drafts WHERE shop_id = $1 AND ocr_id = $2",
            shop_id,
            ocr_id
        )[0].as<long long>();
        const long long invalid_drafts = txn.exec_params1(R"(
            SELECT COUNT(*)::bigint
            FROM tracker.purchase_drafts
            WHERE shop_id = $1
              AND ocr_id = $2
              AND (
                    COALESCE(status, 'draft') NOT IN ('ready', 'approved', 'posted')
                    OR jsonb_array_length(COALESCE(validation_errors, '[]'::jsonb)) > 0
                  )
        )", shop_id, ocr_id)[0].as<long long>();
        const long long invalid_items = txn.exec_params1(R"(
            SELECT COUNT(*)::bigint
            FROM tracker.purchase_draft_items i
            JOIN tracker.purchase_drafts d ON d.id = i.draft_id
            WHERE d.shop_id = $1
              AND d.ocr_id = $2
              AND jsonb_array_length(COALESCE(i.validation_errors, '[]'::jsonb)) > 0
        )", shop_id, ocr_id)[0].as<long long>();

        const std::string next_scan_status =
            remaining_draft_count <= 0 ? "uploaded" : ((invalid_drafts == 0 && invalid_items == 0) ? "extracted" : "needs_review");
        const std::string next_review_status = remaining_draft_count <= 0 ? "pending" : "reviewed";

        txn.exec_params(R"(
            UPDATE tracker.ocr_scans
            SET page_count = $1,
                parsed_json = NULL,
                extracted_text = NULL,
                ocr_status = $2,
                review_status = $3,
                updated_at = CURRENT_TIMESTAMP
            WHERE shop_id = $4 AND id = $5
        )", remaining_pages, next_scan_status, next_review_status, shop_id, ocr_id);

        txn.exec_params(R"(
            UPDATE tracker.receipt_reviews
            SET review_status = 'pending',
                last_saved_json = NULL,
                updated_at = CURRENT_TIMESTAMP
            WHERE ocr_id = $1
        )", ocr_id);

        txn.commit();
    }

    const auto upload_root = resolve_receipt_upload_root();
    if (!trim_copy(page_image_path).empty()) {
        const auto page_path = absolute_normalized_path(std::filesystem::path(page_image_path));
        if (path_is_within_root(page_path, upload_root)) {
            std::error_code ec;
            std::filesystem::remove(page_path, ec);
            if (ec) {
                spdlog::warn("[receipt_delete_page] failed to remove file '{}': {}", page_path.string(), ec.message());
            }
        }
    }

    return {
        {"shop_id", shop_id},
        {"ocr_id", ocr_id},
        {"page_id", page_id},
        {"page_no", page_no},
        {"source_file_name", source_file_name},
        {"deleted", deleted_pages > 0},
        {"deleted_page", deleted_pages > 0},
        {"deleted_receipt", false},
        {"deleted_drafts", deleted_drafts},
        {"remaining_pages", remaining_pages}
    };
}

void PostgresApi::execute_receipt_ocr_job(int shop_id, int ocr_id, const std::string &openai_api_key, long long job_id)
{
    std::string source_path;
    std::string mime_type;
    std::string source_file_name;
    std::string expense_database_name;
    std::string shop_name;
    std::string shop_description;
    std::string inferred_receipt_date;
    std::vector<std::string> allowed_categories;
    int processed_pages = 0;
    int total_pages = 0;
    int current_page = 0;
    std::string current_image_path;
    json parsed_receipts = json::array();
    json shop_prompt_history = json::object();

    {
        pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, false);
        pqxx::work txn(conn);
        pqxx::result scan_res = txn.exec_params(R"(
            SELECT COALESCE(source_path, image_path) AS source_path,
                   COALESCE(mime_type, '') AS mime_type,
                   COALESCE(source_file_name, '') AS source_file_name
            FROM tracker.ocr_scans
            WHERE shop_id = $1 AND id = $2
            LIMIT 1
        )", shop_id, ocr_id);
        if (scan_res.empty()) {
            throw std::runtime_error("Receipt scan not found");
        }
        source_path = get_string(scan_res[0], "source_path");
        mime_type = get_string(scan_res[0], "mime_type");
        source_file_name = get_string(scan_res[0], "source_file_name");
        expense_database_name = conn.dbname();
        shop_prompt_history = load_shop_receipt_prompt_history(txn, shop_id, ocr_id);
        inferred_receipt_date = infer_receipt_date_from_filename(source_file_name);
    }

    if (const auto *shop_conn = get_shop_connection(shop_id)) {
        shop_name = shop_conn->name;
        shop_description = shop_conn->description;
        allowed_categories = shop_conn->categories;
        if (expense_database_name.empty()) {
            expense_database_name = shop_conn->expense.dbname.empty() ? shop_conn->dbname : shop_conn->expense.dbname;
        }
    }

    try {
        if (trim_copy(source_path).empty()) {
            throw std::runtime_error("Receipt source_path is empty");
        }

        const auto source = std::filesystem::path(source_path);
        if (!std::filesystem::exists(source)) {
            throw std::runtime_error(std::format("Receipt file '{}' does not exist", source.string()));
        }

        struct PageInfo {
            int page_no{};
            std::filesystem::path image_path;
            int width{};
            int height{};
        };

        std::vector<PageInfo> pages;
        json raw_responses = json::array();
        Ocr ocr("", openai_api_key);
        bool pages_inserted_incrementally = false;
        auto parse_receipt_content = [&](const std::string &model_content, int page_no) -> json {
            const auto parse_candidate = [&](const std::string &candidate) -> json {
                if (candidate.empty()) {
                    throw std::runtime_error("OCR response content is empty after JSON cleanup");
                }
                return json::parse(candidate);
            };

            const std::string cleaned_content = clean_json(model_content);
            try {
                return parse_candidate(cleaned_content);
            } catch (const std::exception &parse_error) {
                spdlog::warn("[PostgresApi] OCR JSON parse failed for receipt {} page {}: {}. Attempting repair.",
                             ocr_id,
                             page_no,
                             parse_error.what());

                std::ostringstream system_prompt;
                system_prompt
                    << "You repair malformed Flame ERP receipt OCR output into strict RFC 8259 JSON.\n"
                    << "Return exactly one JSON object and nothing else.\n"
                    << "Preserve the existing keys and values whenever possible.\n"
                    << "Do not invent missing fields beyond what is already present in the malformed input.\n"
                    << "If a numeric field is written as a formula or expression, convert it to one plain numeric value only.\n"
                    << "No comments. No markdown fences. No explanations.\n";

                std::ostringstream user_prompt;
                user_prompt
                    << "This OCR output failed JSON parsing.\n"
                    << "Page: " << page_no << "\n"
                    << "Parse error: " << parse_error.what() << "\n"
                    << "Repair it into strict JSON only.\n\n"
                    << (cleaned_content.empty() ? model_content : cleaned_content);

                const std::string repaired_content = clean_json(
                    ocr.send_structured_prompt_to_openai(system_prompt.str(), user_prompt.str(), 2400)
                );
                try {
                    return parse_candidate(repaired_content);
                } catch (const std::exception &repair_error) {
                    throw std::runtime_error(std::format(
                        "OCR JSON repair failed for page {}: initial parse error: {}; repair parse error: {}",
                        page_no,
                        parse_error.what(),
                        repair_error.what()));
                }
            }
        };

        {
            pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
            pqxx::work txn(conn);
            ensure_expense_tracker_schema(txn);
            txn.exec_params("DELETE FROM tracker.ocr_scan_pages WHERE ocr_id = $1", ocr_id);
            txn.exec_params(R"(
                UPDATE tracker.ocr_scans
                SET page_count = 0,
                    ocr_status = 'processing',
                    ocr_error = NULL,
                    updated_at = CURRENT_TIMESTAMP
                WHERE shop_id = $1 AND id = $2
            )", shop_id, ocr_id);
            txn.commit();
        }

        const bool is_pdf = lower_ascii(source.extension().string()) == ".pdf" || lower_ascii(trim_copy(mime_type)) == "application/pdf";
        if (is_pdf) {
            if (job_id > 0) {
                pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
                pqxx::work txn(conn);
                ensure_expense_tracker_schema(txn);
                txn.exec_params(R"(
                    UPDATE tracker.job_runs
                    SET payload = $1::jsonb
                    WHERE id = $2
                )",
                    build_receipt_job_payload(ocr_id, source_path, source_file_name, "render").dump(),
                    job_id
                );
                txn.commit();
            }

            const auto pages_dir = source.parent_path() / (source.stem().string() + "_pages");
            std::error_code rm_ec;
            std::filesystem::remove_all(pages_dir, rm_ec);
            pages_inserted_incrementally = true;
            total_pages = ocr.convert_pdf_to_png_incremental(
                source.string(),
                pages_dir.string(),
                [&](int page_no, int rendered_total_pages, const std::string &image_path) {
                    cv::Mat page = cv::imread(image_path, cv::IMREAD_COLOR);
                    const int width = page.empty() ? 0 : page.cols;
                    const int height = page.empty() ? 0 : page.rows;
                    pages.push_back({
                        page_no,
                        std::filesystem::path(image_path),
                        width,
                        height
                    });

                    current_page = page_no;
                    current_image_path = image_path;
                    total_pages = rendered_total_pages;

                    pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
                    pqxx::work txn(conn);
                    ensure_expense_tracker_schema(txn);
                    txn.exec_params(R"(
                        INSERT INTO tracker.ocr_scan_pages (ocr_id, page_no, image_path, width, height)
                        VALUES ($1, $2, $3, $4, $5)
                    )",
                        ocr_id,
                        page_no,
                        image_path,
                        width,
                        height
                    );
                    txn.exec_params(R"(
                        UPDATE tracker.ocr_scans
                        SET image_path = CASE WHEN $1 = 1 THEN $2 ELSE image_path END,
                            page_count = $1,
                            ocr_status = 'processing',
                            ocr_error = NULL,
                            updated_at = CURRENT_TIMESTAMP
                        WHERE shop_id = $3 AND id = $4
                    )",
                        page_no,
                        image_path,
                        shop_id,
                        ocr_id
                    );
                    if (job_id > 0) {
                        txn.exec_params(R"(
                            UPDATE tracker.job_runs
                            SET payload = $1::jsonb
                            WHERE id = $2
                        )",
                            build_receipt_job_payload(
                                ocr_id,
                                source_path,
                                source_file_name,
                                "render",
                                page_no,
                                rendered_total_pages,
                                page_no,
                                0,
                                image_path
                            ).dump(),
                            job_id
                        );
                    }
                    txn.commit();
                }
            );
        } else if (is_image_extension(source)) {
            cv::Mat page = cv::imread(source.string(), cv::IMREAD_COLOR);
            pages.push_back({
                1,
                source,
                page.empty() ? 0 : page.cols,
                page.empty() ? 0 : page.rows
            });
        } else {
            throw std::runtime_error("Unsupported receipt file type for OCR");
        }

        if (pages.empty()) {
            throw std::runtime_error("No OCR pages were generated");
        }

        if (total_pages <= 0) {
            total_pages = static_cast<int>(pages.size());
        }

        if (!pages_inserted_incrementally) {
            pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
            pqxx::work txn(conn);
            ensure_expense_tracker_schema(txn);
            for (const auto &page : pages) {
                txn.exec_params(R"(
                    INSERT INTO tracker.ocr_scan_pages (ocr_id, page_no, image_path, width, height)
                    VALUES ($1, $2, $3, $4, $5)
                )",
                    ocr_id,
                    page.page_no,
                    page.image_path.string(),
                    page.width,
                    page.height
                );
            }

            txn.exec_params(R"(
                UPDATE tracker.ocr_scans
                SET image_path = $1,
                    page_count = $2,
                    ocr_status = 'processing',
                    ocr_error = NULL,
                    updated_at = CURRENT_TIMESTAMP
                WHERE shop_id = $3 AND id = $4
            )",
                pages.front().image_path.string(),
                total_pages,
                shop_id,
                ocr_id
            );

            if (job_id > 0) {
                txn.exec_params(R"(
                    UPDATE tracker.job_runs
                    SET payload = $1::jsonb
                    WHERE id = $2
                )",
                    build_receipt_job_payload(ocr_id, source_path, source_file_name, "ocr", 0, total_pages).dump(),
                    job_id
                );
            }
            txn.commit();
        } else if (job_id > 0) {
            pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
            pqxx::work txn(conn);
            ensure_expense_tracker_schema(txn);
            txn.exec_params(R"(
                UPDATE tracker.job_runs
                SET payload = $1::jsonb
                WHERE id = $2
            )",
                build_receipt_job_payload(ocr_id, source_path, source_file_name, "ocr", 0, total_pages).dump(),
                job_id
            );
            txn.commit();
        }

        for (const auto &page : pages) {
            current_page = page.page_no;
            current_image_path = page.image_path.string();

            cv::Mat raw = cv::imread(current_image_path, cv::IMREAD_COLOR);
            if (raw.empty()) {
                throw std::runtime_error(std::format("Failed to open OCR page '{}'", current_image_path));
            }

            constexpr int max_ocr_width = 1600;
            cv::Mat resized = resize_receipt_image_for_ocr(raw, max_ocr_width);
            const std::string full_image_base64 = encode_png_base64(resized);
            const cv::Mat line_items_focus = extract_line_items_focus_crop(resized);
            const std::string line_items_focus_base64 = encode_png_base64(line_items_focus);
            const std::string prompt_context = build_shop_receipt_prompt_context(shop_name,
                                                                                 shop_description,
                                                                                 expense_database_name,
                                                                                 shop_prompt_history,
                                                                                 allowed_categories,
                                                                                 source_file_name,
                                                                                 inferred_receipt_date,
                                                                                 page.page_no,
                                                                                 total_pages);
            std::string raw_response = ocr.send_receipt_to_openai(full_image_base64,
                                                                  prompt_context,
                                                                  line_items_focus_base64);
            json response_json = json::parse(raw_response);
            raw_responses.push_back(response_json);

            const std::string content = response_json["choices"][0]["message"]["content"].get<std::string>();
            json parsed_page = parse_receipt_content(content, page.page_no);
            json receipt_entry = json::object();
            if (parsed_page.is_object()) {
                receipt_entry = parsed_page;
            } else if (parsed_page.is_array()) {
                if (parsed_page.empty()) {
                    throw std::runtime_error(std::format("OCR response array is empty for page {}", page.page_no));
                }
                if (parsed_page.size() != 1) {
                    throw std::runtime_error(std::format(
                        "OCR response for page {} must contain exactly one receipt object, got {} entries",
                        page.page_no,
                        parsed_page.size()));
                }
                if (!parsed_page[0].is_object()) {
                    throw std::runtime_error(std::format("OCR response array entry for page {} is not an object", page.page_no));
                }
                receipt_entry = parsed_page[0];
            } else {
                throw std::runtime_error("OCR response is not a JSON object or array");
            }

            if (!receipt_entry.contains("purchase_order") || !receipt_entry["purchase_order"].is_object()) {
                receipt_entry["purchase_order"] = json::object();
            }
            if (!receipt_entry.contains("purchase_items") || !receipt_entry["purchase_items"].is_array()) {
                receipt_entry["purchase_items"] = json::array();
            }

            json &purchase_order = receipt_entry["purchase_order"];
            const std::string parsed_purchase_date = fix_date(trim_copy(json_string_or(purchase_order, "purchase_date")));
            const bool parsed_purchase_date_valid = !parsed_purchase_date.empty() && is_valid_iso_date(parsed_purchase_date);
            if (!parsed_purchase_date_valid && !inferred_receipt_date.empty()) {
                purchase_order["purchase_date"] = inferred_receipt_date;
            }
            const double grand_total = json_to_money(purchase_order, "grand_total", json_to_money(purchase_order, "total_cost", 0.0));
            purchase_order["grand_total"] = grand_total;
            purchase_order["total_cost"] = grand_total;
            purchase_order["subtotal_amount"] = json_to_money(purchase_order, "subtotal_amount", 0.0);
            purchase_order["tax_amount"] = json_to_money(purchase_order, "tax_amount", 0.0);
            purchase_order["discount_amount"] = json_to_money(purchase_order, "discount_amount", 0.0);
            purchase_order["rounding_amount"] = json_to_money(purchase_order, "rounding_amount", 0.0);
            purchase_order["line_total_basis"] = canonicalize_line_total_basis(json_string_or(purchase_order, "line_total_basis", "unknown"));
            for (auto &item : receipt_entry["purchase_items"]) {
                if (!item.is_object()) {
                    continue;
                }
                item["quantity"] = json_to_double(item, "quantity", 0.0);
                item["unit_price"] = json_to_money(item, "unit_price", 0.0);
                item["total_price"] = json_to_money(item, "total_price", 0.0);
                item["line_discount_percent"] = canonicalize_discount_percent(json_to_double(item, "line_discount_percent", 0.0));
                item["line_discount_amount"] = json_to_money(item, "line_discount_amount", 0.0);
                item["line_subtotal_amount"] = json_to_money(item, "line_subtotal_amount", 0.0);
                item["line_tax_amount"] = json_to_money(item, "line_tax_amount", 0.0);
                normalize_item_unit_price_from_quantity_and_total(item);
                normalize_item_inclusive_tax_from_total(item,
                                                        canonicalize_line_total_basis(
                                                            json_string_or(purchase_order, "line_total_basis", "unknown")));
            }
            reconcile_inclusive_line_tax_to_header(purchase_order, receipt_entry["purchase_items"]);
            if (likely_money_separator_drift(purchase_order, receipt_entry["purchase_items"])) {
                append_message(receipt_entry["warnings"],
                               "Likely thousands-separator/decimal-separator OCR drift in monetary fields; values such as 12,044 may have been interpreted as 12.044. Verify receipt amounts carefully.");
            }

            receipt_entry["receipt_index"] = page.page_no - 1;
            parsed_receipts.push_back(receipt_entry);

            processed_pages += 1;
            if (job_id > 0) {
                pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
                pqxx::work txn(conn);
                ensure_expense_tracker_schema(txn);
                txn.exec_params(R"(
                    UPDATE tracker.job_runs
                    SET payload = $1::jsonb
                    WHERE id = $2
                )",
                    build_receipt_job_payload(
                        ocr_id,
                        source_path,
                        source_file_name,
                        "ocr",
                        processed_pages,
                        total_pages,
                        current_page,
                        static_cast<int>(parsed_receipts.size()),
                        current_image_path
                    ).dump(),
                    job_id
                );
                txn.commit();
            }
        }

        {
            pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
            pqxx::work txn(conn);
            ensure_expense_tracker_schema(txn);
            txn.exec_params(R"(
                UPDATE tracker.ocr_scans
                SET ocr_model = 'gpt-4o',
                    raw_response = $1::jsonb,
                    parsed_json = $2::jsonb,
                    extracted_text = $3,
                    ocr_error = NULL,
                    updated_at = CURRENT_TIMESTAMP
                WHERE shop_id = $4 AND id = $5
            )",
                raw_responses.dump(),
                parsed_receipts.dump(),
                parsed_receipts.dump(),
                shop_id,
                ocr_id
            );
            txn.commit();
        }

        json save_result = save_receipt_drafts(shop_id, ocr_id, parsed_receipts);

        if (job_id > 0) {
            pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
            pqxx::work txn(conn);
            ensure_expense_tracker_schema(txn);
            txn.exec_params(R"(
                UPDATE tracker.job_runs
                SET status = 'completed',
                    payload = $1::jsonb,
                    finished_at = CURRENT_TIMESTAMP
                WHERE id = $2
            )",
                build_receipt_job_payload(
                    ocr_id,
                    source_path,
                    source_file_name,
                    "completed",
                    processed_pages,
                    total_pages,
                    total_pages > 0 ? total_pages : current_page,
                    save_result.value("draft_count", 0)
                ).dump(),
                job_id
            );
            txn.commit();
        }
    } catch (const std::exception &e) {
        const std::string error_text = e.what();
        try {
            pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, true);
            pqxx::work txn(conn);
            ensure_expense_tracker_schema(txn);
            txn.exec_params(R"(
                UPDATE tracker.ocr_scans
                SET ocr_status = 'failed',
                    ocr_error = $1,
                    updated_at = CURRENT_TIMESTAMP
                WHERE shop_id = $2 AND id = $3
            )", error_text, shop_id, ocr_id);
            if (job_id > 0) {
                txn.exec_params(R"(
                    UPDATE tracker.job_runs
                    SET status = 'failed',
                        payload = $1::jsonb,
                        error = $2,
                        finished_at = CURRENT_TIMESTAMP
                    WHERE id = $3
                )",
                    build_receipt_job_payload(
                        ocr_id,
                        source_path,
                        source_file_name,
                        "failed",
                        processed_pages,
                        total_pages,
                        current_page,
                        static_cast<int>(parsed_receipts.size()),
                        current_image_path
                    ).dump(),
                    error_text,
                    job_id
                );
            }
            txn.commit();
        } catch (...) {
        }
        throw;
    }
}

int PostgresApi::ensure_supplier_exists(int shop_id, const int ocr_id, int ocr_page_id, const json &supplier, pqxx::work &txn)
{
    std::string tin     = trim_copy(json_to_str(supplier, "tin"));
    std::string name    = trim_copy(json_to_str(supplier, "name"));
    std::string site    = trim_copy(json_to_str(supplier, "site"));
    std::string contact = trim_copy(json_to_str(supplier, "contact_info"));

    const std::string tin_key_initial = normalize_supplier_tin_key(tin);
    std::string tin_key = tin_key_initial;
    std::string name_key = normalize_identity_key(name);
    const std::string site_key = normalize_identity_key(site);

    if (!tin_key.empty()) {
        tin = tin_key;
    } else {
        tin.clear();
    }

    if (name.empty()) {
        name = "Unknown";
    }
    if (name_key.empty()) {
        name_key = std::format("UNKNOWN#{}#{}", ocr_id, std::max(ocr_page_id, 0));
    }

    pqxx::result r;
    if (!tin_key.empty()) {
        r = txn.exec_params(
            "SELECT id FROM tracker.suppliers "
            "WHERE shop_id = $1 AND tin_key = $2 AND COALESCE(site_key, '') = COALESCE($3, '')",
            shop_id,
            tin_key,
            site_key
        );
    } else {
        r = txn.exec_params(
            "SELECT id FROM tracker.suppliers "
            "WHERE shop_id = $1 "
            "  AND (tin_key IS NULL OR BTRIM(tin_key) = '') "
            "  AND COALESCE(name_key, '') = COALESCE($2, '') "
            "  AND COALESCE(site_key, '') = COALESCE($3, '')",
            shop_id,
            name_key,
            site_key
        );
    }

    if (!r.empty()) {
        const int existing_id = r[0][0].as<int>();
        txn.exec_params(R"(
            UPDATE tracker.suppliers
            SET tin = COALESCE(NULLIF(BTRIM(tin), ''), NULLIF($1, ''), tin),
                tin_key = COALESCE(NULLIF(BTRIM(tin_key), ''), NULLIF($2, ''), tin_key),
                name = COALESCE(NULLIF(BTRIM(name), ''), NULLIF($3, ''), name),
                name_key = COALESCE(NULLIF(BTRIM(name_key), ''), NULLIF($4, ''), name_key),
                contact_info = COALESCE(NULLIF(BTRIM(contact_info), ''), NULLIF($5, ''), contact_info),
                site = COALESCE(NULLIF(BTRIM(site), ''), NULLIF($6, ''), site),
                site_key = COALESCE(NULLIF(BTRIM(site_key), ''), NULLIF($7, ''), site_key),
                ocr_id = COALESCE(ocr_id, $8),
                ocr_page_id = COALESCE(ocr_page_id, NULLIF($9, 0)),
                updated_at = CURRENT_TIMESTAMP
            WHERE id = $10
        )",
            tin,
            tin_key,
            name,
            name_key,
            contact,
            site,
            site_key,
            ocr_id,
            ocr_page_id,
            existing_id
        );
        return existing_id;
    }

    pqxx::result inserted = txn.exec_params(R"(
        INSERT INTO tracker.suppliers (shop_id, tin, tin_key, name, name_key, contact_info, site, site_key, ocr_id, ocr_page_id)
        VALUES ($1, NULLIF($2, ''), NULLIF($3, ''), $4, NULLIF($5, ''), $6, $7, NULLIF($8, ''), $9, NULLIF($10, 0))
        RETURNING id
    )",
        shop_id, tin, tin_key, name, name_key, contact, site, site_key, ocr_id, ocr_page_id
    );

    if (inserted.empty()) {
        throw std::runtime_error("[ensure_supplier_exists] Insert failed");
    }

    return inserted[0][0].as<int>();
}

int PostgresApi::ensure_product_exists(int shop_id,
                                       const int ocr_id,
                                       int ocr_page_id,
                                       const std::string &name,
                                       int supplier_id,
                                       pqxx::work &txn,
                                       const std::string &product_type,
                                       const std::string &category)
{
    const std::string effective_category = canonicalize_category_name(category, category_options_for_shop(shop_id));
    const std::string name_key = normalize_identity_key(name);
    pqxx::result r = txn.exec_params(
        "SELECT id, COALESCE(category, '') AS category FROM tracker.products "
        "WHERE shop_id = $1 AND COALESCE(supplier_id, 0) = COALESCE($2, 0) AND COALESCE(name_key, '') = COALESCE($3, '')",
        shop_id, supplier_id, name_key
    );

    if (!r.empty()) {
        const int existing_id = r[0]["id"].as<int>();
        const std::string existing_category = canonicalize_category_name(get_string(r[0], "category"), category_options_for_shop(shop_id));
        if ((existing_category.empty() || existing_category == "Others") &&
            !effective_category.empty() &&
            effective_category != "Others") {
            txn.exec_params(
                "UPDATE tracker.products SET category = $1, name_key = COALESCE(NULLIF(BTRIM(name_key), ''), NULLIF($2, ''), name_key), ocr_id = COALESCE(ocr_id, $3), ocr_page_id = COALESCE(ocr_page_id, NULLIF($4, 0)), updated_at = CURRENT_TIMESTAMP WHERE id = $5",
                effective_category,
                name_key,
                ocr_id,
                ocr_page_id,
                existing_id
            );
        } else {
            txn.exec_params(
                "UPDATE tracker.products SET name_key = COALESCE(NULLIF(BTRIM(name_key), ''), NULLIF($1, ''), name_key), ocr_id = COALESCE(ocr_id, $2), ocr_page_id = COALESCE(ocr_page_id, NULLIF($3, 0)), updated_at = CURRENT_TIMESTAMP WHERE id = $4",
                name_key,
                ocr_id,
                ocr_page_id,
                existing_id
            );
        }
        return existing_id;
    }

    pqxx::result inserted = txn.exec_params(R"(
        INSERT INTO tracker.products (shop_id, name, name_key, product_type, supplier_id, ocr_id, ocr_page_id, category)
        VALUES ($1, $2, NULLIF($3, ''), $4, $5, $6, NULLIF($7, 0), $8)
        RETURNING id
    )", shop_id, name, name_key, product_type, supplier_id, ocr_id, ocr_page_id, effective_category);

    return inserted[0][0].as<int>();
}

// If you want to keep using pqxx::work, change transaction_base to work here.
int PostgresApi::insert_purchase_order(int shop_id, const int ocr_id, int ocr_page_id, const json &order, int supplier_id, pqxx::transaction_base &txn)
{
    std::string invoice_raw = json_to_str(order, "invoice_id");
    std::string date_raw    = json_to_str(order, "purchase_date");
    std::string date_iso    = fix_date(date_raw);
    double      total_cost  = json_to_money(order, "grand_total", json_to_money(order, "total_cost", 0.0));
    double      subtotal_amount = json_to_money(order, "subtotal_amount", 0.0);
    double      tax_amount = json_to_money(order, "tax_amount", 0.0);
    double      discount_amount = json_to_money(order, "discount_amount", 0.0);
    double      rounding_amount = json_to_money(order, "rounding_amount", 0.0);
    std::string line_total_basis = canonicalize_line_total_basis(json_to_str(order, "line_total_basis", "unknown"));

    bool has_valid_date = is_valid_iso_date(date_iso);

    // Fallback date used when missing/invalid, so purchase_date is NEVER NULL.
    const std::string fallback_date = "1900-01-01";

    std::string effective_date;
    if (has_valid_date) {
        effective_date = date_iso;
    } else {
        effective_date = fallback_date;
        spdlog::warn(
            "[insert_purchase_order] Missing/invalid purchase_date "
            "(raw='{}', normalized='{}') for ocr_id={} – using fallback date {}",
            date_raw, date_iso, ocr_id, fallback_date
        );
    }

    // ===== Case 1: invoice_id present in JSON =====
    if (!invoice_raw.empty()) {
        // Canonical stored form: "supplier_id#invoice_id"
        std::string invoice_stored = std::to_string(supplier_id) + "#" + invoice_raw;

        // Check for duplicate
        pqxx::result r = txn.exec_params(
            "SELECT id, ocr_id, supplier_id "
            "FROM tracker.purchase_orders "
            "WHERE shop_id = $1 AND invoice_id = $2",
            shop_id,
            invoice_stored
        );

        if (!r.empty()) {
            const int existing_id  = r[0]["id"].as<int>();
            const int existing_ocr = r[0]["ocr_id"].as<int>();
            const int existing_sup = r[0]["supplier_id"].as<int>();

            // Update existing purchase_order with latest date/total_cost/ocr_id
            txn.exec_params(
                "UPDATE tracker.purchase_orders "
                "SET purchase_date = $1::date, total_cost = $2, subtotal_amount = $3, tax_amount = $4, discount_amount = $5, rounding_amount = $6, grand_total = $7, line_total_basis = $8, ocr_id = $9, ocr_page_id = NULLIF($10, 0), updated_at = CURRENT_TIMESTAMP "
                "WHERE id = $11",
                effective_date,
                total_cost,
                subtotal_amount,
                tax_amount,
                discount_amount,
                rounding_amount,
                total_cost,
                line_total_basis,
                ocr_id,
                ocr_page_id,
                existing_id
            );

            spdlog::info(
                "[insert_purchase_order] Duplicate invoice_id: '{}' (stored as: '{}') supplier_id: {} "
                "existing(id={}, ocr_id={}, supplier_id={}) -> updated with new data",
                invoice_raw,
                invoice_stored,
                supplier_id,
                existing_id,
                existing_ocr,
                existing_sup
            );
            return existing_id;
        }

        // Insert new purchase_order with canonical invoice_id
        pqxx::result inserted = txn.exec_params(
            "INSERT INTO tracker.purchase_orders "
            "(shop_id, invoice_id, supplier_id, purchase_date, total_cost, subtotal_amount, tax_amount, discount_amount, rounding_amount, grand_total, line_total_basis, ocr_id, ocr_page_id) "
            "VALUES ($1, $2, $3, $4::date, $5, $6, $7, $8, $9, $10, $11, $12, NULLIF($13, 0)) "
            "RETURNING id",
            shop_id,
            invoice_stored,
            supplier_id,
            effective_date,   // always valid and non-empty
            total_cost,
            subtotal_amount,
            tax_amount,
            discount_amount,
            rounding_amount,
            total_cost,
            line_total_basis,
            ocr_id,
            ocr_page_id
        );

        if (inserted.empty()) {
            throw std::runtime_error("[insert_purchase_order] Insert failed (non-empty invoice_id)");
        }

        return inserted[0]["id"].as<int>();
    }

    // ===== Case 2: invoice_id is empty in JSON =====

    // 1) Insert row without invoice_id, but with effective_date
    pqxx::result ins = txn.exec_params(
        "INSERT INTO tracker.purchase_orders "
        "(shop_id, supplier_id, purchase_date, total_cost, subtotal_amount, tax_amount, discount_amount, rounding_amount, grand_total, line_total_basis, ocr_id, ocr_page_id, invoice_id) "
        "VALUES ($1, $2, $3::date, $4, $5, $6, $7, $8, $9, $10, $11, NULLIF($12, 0), '') "
        "RETURNING id",
        shop_id,
        supplier_id,
        effective_date,   // always valid and non-empty
        total_cost,
        subtotal_amount,
        tax_amount,
        discount_amount,
        rounding_amount,
        total_cost,
        line_total_basis,
        ocr_id,
        ocr_page_id
    );

    if (ins.empty()) {
        throw std::runtime_error("[insert_purchase_order] Insert failed (empty invoice_id, step 1)");
    }

    int new_id = ins[0]["id"].as<int>();

    // 2) Generate invoice_id string: "supplier_id#None#<id>"
    std::string auto_invoice =
        std::to_string(supplier_id) + "#None#" + std::to_string(new_id);

    // 3) Update invoice_id
    pqxx::result upd = txn.exec_params(
        "UPDATE tracker.purchase_orders "
        "SET invoice_id = $1, updated_at = CURRENT_TIMESTAMP "
        "WHERE id = $2 "
        "RETURNING id",
        auto_invoice,
        new_id
    );

    if (upd.empty()) {
        throw std::runtime_error("[insert_purchase_order] Update failed (step 2)");
    }

    return new_id;
}

void PostgresApi::insert_purchase_item(int shop_id, const int ocr_id, int ocr_page_id, int purchase_id, int product_id, const json &item, pqxx::work &txn)
{
    double quantity              = json_to_double(item, "quantity", 0.0);
    double unit_price            = json_to_money(item, "unit_price", 0.0);
    double total_price           = json_to_money(item, "total_price", 0.0);
    double line_discount_percent = canonicalize_discount_percent(json_to_double(item, "line_discount_percent", 0.0));
    double line_discount_amount  = json_to_money(item, "line_discount_amount", 0.0);
    double line_subtotal_amount  = json_to_money(item, "line_subtotal_amount", 0.0);
    double line_tax_amount       = json_to_money(item, "line_tax_amount", 0.0);

    txn.exec_params(R"(
        INSERT INTO tracker.purchase_items (
            shop_id, purchase_id, product_id, quantity, unit_price, total_price,
            line_discount_percent, line_discount_amount, line_subtotal_amount, line_tax_amount,
            ocr_id, ocr_page_id
        )
        VALUES ($1, $2, $3, $4::numeric, $5, $6, $7, $8, $9, $10, $11, NULLIF($12, 0))
    )",
        shop_id,
        purchase_id,
        product_id,
        pqxx::to_string(quantity),
        pqxx::to_string(unit_price),
        pqxx::to_string(total_price),
        pqxx::to_string(line_discount_percent),
        pqxx::to_string(line_discount_amount),
        pqxx::to_string(line_subtotal_amount),
        pqxx::to_string(line_tax_amount),
        ocr_id,
        ocr_page_id
    );
}

std::string PostgresApi::get_image_path(int shop_id, int ocr_id)
{
    pqxx::connection conn = open_shop_connection(shop_id, SourceKind::Expense, false);
    pqxx::work txn(conn);
    pqxx::result res = txn.exec_params(
        "SELECT image_path FROM tracker.ocr_scans WHERE shop_id = $1 AND id = $2 LIMIT 1",
        shop_id,
        ocr_id
    );
    if (res.empty()) return {};
    return res[0]["image_path"].as<std::string>();
}

std::string PostgresApi::clean_json(const std::string &raw_text)
{
    const std::string fence_json = "```json";
    const std::string fence_any  = "```";

    // 1. Slice out the fenced JSON block (if present)
    size_t start = 0;
    if (auto pos = raw_text.find(fence_json); pos != std::string::npos) {
        start = pos + fence_json.size();  // after ```json
    }

    size_t end = raw_text.size();
    if (auto pos = raw_text.rfind(fence_any); pos != std::string::npos &&
        pos > start) {
        end = pos;                        // before final ```
    }

    std::string sliced = raw_text.substr(start, end - start);

    // 2. Remove // comments line by line (but ignore // inside strings)
    std::stringstream in(sliced);
    std::ostringstream out;
    std::string line;

    while (std::getline(in, line)) {
        bool in_string = false;

        for (std::size_t i = 0; i + 1 < line.size(); ++i) {
            char c  = line[i];
            char c2 = line[i + 1];

            // toggle in_string on unescaped "
            if (c == '"' && (i == 0 || line[i - 1] != '\\')) {
                in_string = !in_string;
            }

            // found // while not inside a string → comment start
            if (!in_string && c == '/' && c2 == '/') {
                line.erase(i);   // remove from // to end-of-line
                break;
            }
        }

        out << line << '\n';
    }

    std::string cleaned = out.str();

    // 3. Final safe trim
    auto first = cleaned.find_first_not_of(" \n\r\t");
    if (first == std::string::npos) {
        // only whitespace
        return {};
    }
    auto last = cleaned.find_last_not_of(" \n\r\t");
    return cleaned.substr(first, last - first + 1);
}

std::string PostgresApi::fix_date(const std::string &raw_date)
{
    auto trim = [](const std::string &s) {
        const auto first = s.find_first_not_of(" \t\n\r");
        if (first == std::string::npos) return std::string();
        const auto last = s.find_last_not_of(" \t\n\r");
        return s.substr(first, last - first + 1);
    };

    const std::string cleaned = trim(raw_date);
    if (cleaned.empty()) return {};

    auto to_iso = [](const std::tm &tm_val) {
        char buf[11];
        std::strftime(buf, sizeof(buf), "%Y-%m-%d", &tm_val);
        return std::string(buf);
    };

    auto parse = [](const std::string &s, const char *fmt, std::tm &out) {
        std::istringstream ss(s);
        ss >> std::get_time(&out, fmt);
        return !ss.fail();
    };

    std::tm tm_iso{};
    if (parse(cleaned, "%Y-%m-%d", tm_iso)) {
        return to_iso(tm_iso);
    }

    std::tm tm_dmy{};
    if (parse(cleaned, "%d/%m/%Y", tm_dmy) || parse(cleaned, "%d-%m-%Y", tm_dmy)) {
        return to_iso(tm_dmy);
    }

    return {};
}

bool PostgresApi::is_valid_iso_date(const std::string &s)
{
    if (s.size() != 10) return false;
    if (s[4] != '-' || s[7] != '-') return false;

    auto to_int = [](const std::string &str, std::size_t pos, std::size_t len) {
        return std::stoi(str.substr(pos, len));
    };

    try {
        int year  = to_int(s, 0, 4);
        int month = to_int(s, 5, 2);
        int day   = to_int(s, 8, 2);

        if (year < 2025)      return false;
        if (month < 1 || month > 12) return false;
        if (day   < 1 || day   > 31) return false;  // simple check, enough for now

        return true;
    } catch (...) {
        return false;
    }
}

json PostgresApi::result_to_json(const pqxx::result &res)
{
    json rows = json::array();

    for (const auto &row : res) {
        json obj = json::object();
        for (const auto &field : row) {
            obj[field.name()] = field.is_null() ? json(nullptr) : json(field.as<std::string>());
        }
        rows.push_back(std::move(obj));
    }

    return json{
        {"rows", rows},
        {"row_count", res.size()},
        {"affected_rows", res.affected_rows()},
        {"command_tag", res.query()},
        {"error", ""}
    };
}

bool PostgresApi::is_identifier_safe(const std::string &id)
{
    static const std::regex re(R"(^[A-Za-z_][A-Za-z0-9_]*$)");
    return std::regex_match(id, re);
}

bool PostgresApi::is_table_name_safe(const std::string &name) const
{
    static const std::regex re(R"(^[A-Za-z_][A-Za-z0-9_]*(\.[A-Za-z_][A-Za-z0-9_]*)?$)");
    return std::regex_match(name, re);
}

static std::string to_sql_literal(const json &val, pqxx::work &txn)
{
    if (val.is_null()) return "NULL";
    if (val.is_boolean() || val.is_number()) return val.dump();
    // Strings, arrays, objects → escape and wrap in single quotes
    std::string s;
    if (val.is_string()) {
        s = val.get<std::string>();
    } else {
        s = val.dump();
    }
    return "'" + txn.esc(s) + "'";
}

int PostgresApi::update_rows(int shop_id, SourceKind kind, const std::string &table, const std::string &key_column, const json &rows)
{
    if (!is_table_name_safe(table) || !is_identifier_safe(key_column) || !rows.is_array()) {
        spdlog::error("[update_rows] Invalid table/key/rows");
        return 0;
    }

    pqxx::connection conn = open_shop_connection(shop_id, kind, true);
    pqxx::work txn(conn);
    if (kind == SourceKind::Expense) {
        ensure_expense_tracker_schema(txn);
    }
    int updated = 0;

    for (const auto &row : rows) {
        if (!row.is_object()) continue;

        if (!row.contains("changes") || !row.contains("key")) continue;
        const auto &changes = row["changes"];
        if (!changes.is_object() || changes.empty()) continue;

        std::string set_clause;
        bool first = true;

        for (auto it = changes.begin(); it != changes.end(); ++it) {
            const std::string col = it.key();
            if (!is_identifier_safe(col)) {
                spdlog::warn("[update_rows] Skip unsafe column: {}", col);
                continue;
            }

            std::string literal = to_sql_literal(it.value(), txn);
            if (literal.empty()) continue;

            if (!first) set_clause += ", ";
            set_clause += col + " = " + literal;
            first = false;
        }

        if (first) continue; // no valid changes

        std::string key_literal = to_sql_literal(row["key"], txn);
        std::string query =
            "UPDATE " + table + " SET " + set_clause + " WHERE " + key_column + " = " + key_literal + ";";

        txn.exec(query);
        ++updated;
    }

    txn.commit();
    return updated;
}

int PostgresApi::delete_rows(int shop_id, SourceKind kind, const std::string &table, const std::string &key_column, const json &keys)
{
    if (!is_table_name_safe(table) || !is_identifier_safe(key_column) || !keys.is_array()) {
        spdlog::error("[delete_rows] Invalid table/key/keys");
        return 0;
    }

    std::vector<std::string> params;
    for (const auto &k : keys) {
        if (k.is_null()) continue;
        if (k.is_string()) {
            params.push_back(k.get<std::string>());
        } else if (k.is_number_integer()) {
            params.push_back(std::to_string(k.get<long long>()));
        } else if (k.is_number_float()) {
            params.push_back(std::to_string(k.get<double>()));
        }
    }

    if (params.empty()) return 0;

    pqxx::connection conn = open_shop_connection(shop_id, kind, true);
    pqxx::work txn(conn);
    if (kind == SourceKind::Expense) {
        ensure_expense_tracker_schema(txn);
    }

    std::string placeholders;
    for (size_t i = 0; i < params.size(); ++i) {
        if (i) placeholders += ", ";
        placeholders += "$" + std::to_string(i + 1);
    }

    std::string sql = "DELETE FROM " + table + " WHERE " + key_column + " IN (" + placeholders + ") RETURNING " + key_column;

    pqxx::params pack;
    for (auto &p : params) {
        pack.append(p);
    }

    pqxx::result res = txn.exec_params(sql, pack);
    txn.commit();
    return static_cast<int>(res.size());
}

json PostgresApi::execute_sql(int shop_id, SourceKind kind, const std::string &sql)
{
    if (sql.empty()) {
        return json{
            {"shop_id", shop_id},
            {"source_kind", kind == SourceKind::Pos ? "pos" : "expense"},
            {"rows", json::array()},
            {"row_count", 0},
            {"affected_rows", 0},
            {"command_tag", ""},
            {"error", "SQL string is empty"}
        };
    }

    try {
        pqxx::connection conn = open_shop_connection(shop_id, kind, !is_read_only_sql(sql));
        pqxx::work txn(conn);
        if (kind == SourceKind::Expense) {
            ensure_expense_tracker_schema(txn);
        }

        pqxx::result res = txn.exec(sql);
        json payload = result_to_json(res);
        payload["shop_id"] = shop_id;
        payload["source_kind"] = kind == SourceKind::Pos ? "pos" : "expense";
        payload["database"] = conn.dbname();
        txn.commit();
        return payload;

    } catch (const std::exception &e) {
        spdlog::error("[execute_sql] shop_id={} kind={} {}", shop_id, kind == SourceKind::Pos ? "pos" : "expense", e.what());
        return json{
            {"shop_id", shop_id},
            {"source_kind", kind == SourceKind::Pos ? "pos" : "expense"},
            {"rows", json::array()},
            {"row_count", 0},
            {"affected_rows", 0},
            {"command_tag", ""},
            {"error", e.what()}
        };
    }
}
