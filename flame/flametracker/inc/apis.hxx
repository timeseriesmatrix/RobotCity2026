#pragma once

#include <boost/asio/ip/tcp.hpp>
#include <boost/beast/core.hpp>
#include <boost/beast/http.hpp>

#include <boost/url/url.hpp>
#include <boost/url/parse.hpp>

#include <boost/version.hpp>
#include <boost/asio/co_spawn.hpp>
#include <boost/asio/detached.hpp>
#include <boost/asio/use_future.hpp>

#include <string>
#include <format>
#include <malloc.h>
#include <ranges>
#include <cstdlib>   // std::llabs
#include <fstream>
#include <sstream>
#include <vector>
#include <cctype>
#include <mutex>
#include <optional>
#include <unordered_map>
#include <random>
#include <chrono>

#include <spdlog/spdlog.h>
#include <nlohmann/json.hpp>

#include "utilities.hxx"
#include "postgresApi.h"
#include "ocr.h"

using boost::asio::ip::tcp;

using boost::asio::co_spawn;
using boost::asio::detached;
using boost::asio::awaitable;

namespace http      =   boost::beast::http;
namespace urls      =   boost::urls;

using json = nlohmann::json;
using namespace std::string_literals;

const char * _server_name_  =   "FLAMETRACKER@QUASAR";
const size_t _server_port_  =   20000;

auto get_response(boost::beast::string_view body, http::status status = http::status::ok
, bool keep_alive = false, unsigned version = 11)
{
    auto res = http::response<http::string_body>{status, version};
    res.set(http::field::server, _server_name_);
    res.set(http::field::content_type, "text/html");
    res.body() = std::string(body) + "\n";

    res.keep_alive(keep_alive);
    res.prepare_payload();
    return res;
}

auto server_error(boost::beast::string_view what)
{
    auto body = "An error occurred: '" + std::string(what) + "'";
    return get_response(body, http::status::internal_server_error);
}
auto not_found(boost::beast::string_view target)
{
    auto body = "The resource '" + std::string(target) + "' was not found.";
    return get_response(body, http::status::not_found);
}
auto bad_request(boost::beast::string_view why)
{
    return get_response(why, http::status::bad_request);
}

auto json_response(const json &payload, http::status status = http::status::ok,
                   bool keep_alive = false, unsigned version = 11)
{
    http::response<http::string_body> res{status, version};
    res.set(http::field::server, _server_name_);
    res.set(http::field::content_type, "application/json");
    res.body() = payload.dump();
    res.keep_alive(keep_alive);
    res.prepare_payload();
    return res;
}

// Apply permissive CORS headers for browser clients.
template <class Body>
void add_cors(http::response<Body> &res)
{
    res.set(http::field::access_control_allow_origin, "*");
    res.set(http::field::access_control_allow_methods, "GET, POST, OPTIONS");
    res.set(http::field::access_control_allow_headers, "Content-Type, Authorization");
    res.set(http::field::access_control_max_age, "600");
}

inline json load_shop_config_json(const std::string &path)
{
    std::ifstream in(path);
    if (!in) {
        throw std::runtime_error(std::format("Shop config '{}' not found", path));
    }
    json doc;
    in >> doc;
    if (!doc.is_array()) {
        throw std::runtime_error(std::format("Shop config '{}' must be a JSON array", path));
    }
    return doc;
}

inline PostgresApi::SourceKind parse_source_kind(const json &body_json, const std::string &key = "source_kind")
{
    std::string value = body_json.value(key, "expense");
    for (char &c : value) c = static_cast<char>(std::tolower(static_cast<unsigned char>(c)));
    return value == "pos" ? PostgresApi::SourceKind::Pos : PostgresApi::SourceKind::Expense;
}

namespace {
struct AuthSessionInfo {
    std::string token;
    std::string username;
    std::string display_name;
    std::string role;
    std::chrono::system_clock::time_point expires_at;
};

std::mutex g_auth_sessions_mutex;
std::unordered_map<std::string, AuthSessionInfo> g_auth_sessions;

constexpr auto k_root_username = "root";
constexpr auto k_root_password = "liam@113078";

std::string generate_auth_token()
{
    static std::random_device rd;
    static std::mt19937_64 gen(rd());
    static std::uniform_int_distribution<unsigned long long> dist;

    std::ostringstream out;
    out << std::hex;
    for (int i = 0; i < 4; ++i) {
        out << dist(gen);
    }
    return out.str();
}

void prune_expired_auth_sessions_locked()
{
    const auto now = std::chrono::system_clock::now();
    for (auto it = g_auth_sessions.begin(); it != g_auth_sessions.end();) {
        if (it->second.expires_at <= now) {
            it = g_auth_sessions.erase(it);
        } else {
            ++it;
        }
    }
}

json auth_session_payload(const AuthSessionInfo &session)
{
    return {
        {"token", session.token},
        {"user", {
            {"username", session.username},
            {"display_name", session.display_name},
            {"role", session.role}
        }}
    };
}

std::string bearer_token_from_request(const http::request<http::string_body> &req)
{
    const auto auth = req[http::field::authorization];
    if (auth.empty()) return {};
    std::string value(auth.data(), auth.size());
    constexpr std::string_view prefix = "Bearer ";
    if (!value.starts_with(prefix)) return {};
    value.erase(0, prefix.size());
    return value;
}

std::optional<AuthSessionInfo> authorize_request(const http::request<http::string_body> &req)
{
    const std::string token = bearer_token_from_request(req);
    if (token.empty()) return std::nullopt;

    std::scoped_lock lock(g_auth_sessions_mutex);
    prune_expired_auth_sessions_locked();
    auto it = g_auth_sessions.find(token);
    if (it == g_auth_sessions.end()) return std::nullopt;
    return it->second;
}

auto auth_error_response(http::status status, const std::string &error,
                         bool keep_alive = false, unsigned version = 11)
{
    json body = {{"error", error}};
    auto res = json_response(body, status, keep_alive, version);
    add_cors(res);
    res.keep_alive(false);
    return res;
}

bool is_public_target(boost::beast::string_view target)
{
    return target == "/" || target == "/list" || target == "/test" ||
           target == "/tsr_ocr_pdfs" || target == "/cvt_pdf_png" || target == "/ext_txt_img" ||
           target == "/auth_login";
}

std::string env_value(const char *name)
{
    const char *value = std::getenv(name);
    return value && *value ? std::string(value) : std::string{};
}

std::string resolve_openai_ocr_key()
{
    std::string key = env_value("OPENAI_OCR_KEY");
    if (!key.empty()) return key;
    return env_value("OPENAI_API_KEY");
}

std::string openai_ocr_key_missing_message()
{
    return "Missing OPENAI_OCR_KEY env var. Set OPENAI_OCR_KEY or OPENAI_API_KEY in the shell, FLAME_ENV_FILE, or flametracker/.env.";
}
} // namespace

auto async_apis(http::request<http::string_body> &&req, boost::asio::any_io_executor ioc) 
    -> awaitable<http::response<http::string_body>>
{
    // Request path must be absolute and not contain "..".
    if( req.target().empty() ||
        req.target()[0] != '/' ||
        req.target().find("..") != boost::beast::string_view::npos) {
        auto res = bad_request("Illegal request-target");
        add_cors(res);
        co_return res;
    }

    // Parse target and response
    auto target = req.target();

    // CORS preflight support
    if (req.method() == http::verb::options) {
        http::response<http::string_body> res{http::status::no_content, req.version()};
        res.set(http::field::server, _server_name_);
        res.keep_alive(false);
        res.content_length(0);
        add_cors(res);
        co_return res;
    }

    if (req.method() == http::verb::post && target == "/auth_login") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            const std::string username = body_json.value("username", "");
            const std::string password = body_json.value("password", "");
            if (username.empty() || password.empty()) {
                co_return auth_error_response(http::status::bad_request, "username and password are required",
                                              req.keep_alive(), req.version());
            }

            AuthSessionInfo session;
            if (username == k_root_username && password == k_root_password) {
                session.username = k_root_username;
                session.display_name = "Root";
                session.role = "root";
            } else {
                PostgresApi db;
                const json users = db.users_json(true);
                bool matched = false;
                for (const auto &user : users) {
                    if (!user.is_object()) continue;
                    if (user.value("username", "") != username) continue;
                    if (user.value("password", "") != password) continue;
                    session.username = username;
                    session.display_name = user.value("display_name", username);
                    session.role = "user";
                    matched = true;
                    break;
                }
                if (!matched) {
                    co_return auth_error_response(http::status::unauthorized, "Invalid username or password",
                                                  req.keep_alive(), req.version());
                }
            }

            session.token = generate_auth_token();
            session.expires_at = std::chrono::system_clock::now() + std::chrono::hours(12);

            {
                std::scoped_lock lock(g_auth_sessions_mutex);
                prune_expired_auth_sessions_locked();
                g_auth_sessions[session.token] = session;
            }

            auto res = json_response(auth_session_payload(session), http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            co_return auth_error_response(http::status::bad_request, e.what(), req.keep_alive(), req.version());
        }
    }

    if (req.method() == http::verb::get && target == "/auth_session") {
        auto session = authorize_request(req);
        if (!session) {
            co_return auth_error_response(http::status::unauthorized, "Authentication required",
                                          req.keep_alive(), req.version());
        }
        auto res = json_response(auth_session_payload(*session), http::status::ok, req.keep_alive(), req.version());
        add_cors(res);
        res.keep_alive(false);
        co_return res;
    }

    if (!is_public_target(target)) {
        auto session = authorize_request(req);
        if (!session) {
            co_return auth_error_response(http::status::unauthorized, "Authentication required",
                                          req.keep_alive(), req.version());
        }
    }

    // Fetch OCR image by id
    if (req.method() == http::verb::post && target == "/ocr_image") {
        try {
            json body_json = json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            int ocr_id = body_json.value("ocr_id", 0);
            if (shop_id <= 0) {
                auto res = bad_request("shop_id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }
            if (ocr_id <= 0) {
                auto res = bad_request("ocr_id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            std::string image_path = db.get_image_path(shop_id, ocr_id);
            if (image_path.empty()) {
                json resp = {{"shop_id", shop_id}, {"image_path", ""}, {"image_base64", ""}, {"error", "Not found"}};
                auto res = json_response(resp, http::status::not_found, req.keep_alive(), req.version());
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            std::ifstream file(image_path, std::ios::binary);
            if (!file) {
                json resp = {{"shop_id", shop_id}, {"image_path", image_path}, {"image_base64", ""}, {"error", "Cannot open file"}};
                auto res = json_response(resp, http::status::bad_request, req.keep_alive(), req.version());
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }
            std::ostringstream buffer;
            buffer << file.rdbuf();
            std::string bytes = buffer.str();
            std::string b64   = base64_encode(bytes);

            json resp = {{"shop_id", shop_id}, {"image_path", image_path}, {"image_base64", b64}, {"error", ""}};
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"image_path", ""}, {"image_base64", ""}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    // Serve normalized shop source config for clients (frontend nodes view)
    if (req.method() == http::verb::get && (target == "/shop_databases.json" || target == "/shop_databases")) {
        try {
            PostgresApi db;
            json shops = db.shop_connections_json();
            json body  = {{"default_shop_id", db.default_shop_id()}, {"shops", shops}};
            auto res = json_response(body, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::get && target == "/settings_state") {
        auto session = authorize_request(req);
        if (!session || session->role != "root") {
            co_return auth_error_response(http::status::forbidden, "Root access required",
                                          req.keep_alive(), req.version());
        }
        try {
            PostgresApi db;
            json state = db.settings_state_json();
            auto res = json_response(state, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/settings_save") {
        auto session = authorize_request(req);
        if (!session || session->role != "root") {
            co_return auth_error_response(http::status::forbidden, "Root access required",
                                          req.keep_alive(), req.version());
        }
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            PostgresApi db;
            const std::string action = body_json.value("action", "");
            json state;
            if (action == "test_connection") {
                state = db.settings_test_connection(body_json.value("source", json::object()),
                                                    parse_source_kind(body_json));
            } else if (action == "init_expense_db") {
                state = db.settings_init_expense_db(body_json.value("source", json::object()));
            } else {
                state = db.save_settings(body_json.value("shops", json::array()),
                                         body_json.value("users", json::array()),
                                         body_json.value("default_shop_id", 0));
            }
            auto res = json_response(state, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/shop_summary") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", -1);
            std::string start = body_json.value("start_time", "");
            std::string end   = body_json.value("end_time", "");

            if (shop_id < 0) {
                auto res = bad_request("shop_id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json summary = db.shop_summary(shop_id, start, end);
            auto res = json_response(summary, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/purchased_summary") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", -1);
            std::string start = body_json.value("start_time", "");
            std::string end   = body_json.value("end_time", "");

            if (shop_id < 0) {
                auto res = bad_request("shop_id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json summary = db.purchased_summary(shop_id, start, end);
            auto res = json_response(summary, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/db_schema_overview") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", -1);
            if (shop_id < 0) {
                auto res = bad_request("shop_id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }
            auto source_kind = parse_source_kind(body_json);
            PostgresApi db;
            json overview = db.db_schema_overview(shop_id, source_kind);
            auto res = json_response(overview, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/init_expense_tracker") {
        try {
            std::vector<int> shop_ids;
            if (!req.body().empty()) {
                json body_json = json::parse(req.body());
                if (body_json.contains("shop_ids") && body_json["shop_ids"].is_array()) {
                    for (const auto &sid : body_json["shop_ids"]) {
                        if (sid.is_number_integer()) {
                            shop_ids.push_back(sid.get<int>());
                        }
                    }
                } else if (body_json.contains("shop_id") && body_json["shop_id"].is_number_integer()) {
                    shop_ids.push_back(body_json["shop_id"].get<int>());
                }
            }

            PostgresApi db;
            json stats = db.init_expense_tracker_schemas(shop_ids);
            json resp = {{"shops", stats}};
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    // Table update endpoint (editable Data View)
    if (req.method() == http::verb::post && target == "/table_update") {
        try {
            json body_json   = json::parse(req.body());
            int shop_id      = body_json.value("shop_id", -1);
            auto source_kind = parse_source_kind(body_json);
            std::string table      = body_json.value("table", "");
            std::string key_column = body_json.value("key_column", "id");
            json rows              = body_json.value("rows", json::array());

            if (shop_id < 0) {
                auto res = bad_request("shop_id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }
            if (table.empty() || !rows.is_array()) {
                auto res = bad_request("table and rows[] are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            int updated = db.update_rows(shop_id, source_kind, table, key_column, rows);
            json resp = {
                {"shop_id", shop_id},
                {"source_kind", source_kind == PostgresApi::SourceKind::Pos ? "pos" : "expense"},
                {"updated", updated},
                {"error", ""}
            };
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"updated", 0}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    // Delete rows in a table by key
    if (req.method() == http::verb::post && target == "/table_delete") {
        try {
            json body_json   = json::parse(req.body());
            int shop_id      = body_json.value("shop_id", -1);
            auto source_kind = parse_source_kind(body_json);
            std::string table      = body_json.value("table", "");
            std::string key_column = body_json.value("key_column", "id");
            json keys              = body_json.value("keys", json::array());

            if (shop_id < 0) {
                auto res = bad_request("shop_id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }
            if (table.empty() || !keys.is_array()) {
                auto res = bad_request("table and keys[] are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            int deleted = db.delete_rows(shop_id, source_kind, table, key_column, keys);
            json resp = {
                {"shop_id", shop_id},
                {"source_kind", source_kind == PostgresApi::SourceKind::Pos ? "pos" : "expense"},
                {"deleted", deleted},
                {"error", ""}
            };
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"deleted", 0}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/receipt_upload") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            std::string file_name = body_json.value("file_name", "");
            std::string mime_type = body_json.value("mime_type", "application/octet-stream");
            std::string content_base64 = body_json.value("content_base64", "");

            if (shop_id <= 0) {
                auto res = bad_request("shop_id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.receipt_upload(shop_id, file_name, mime_type, content_base64);
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"uploaded", false}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/receipt_queue") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            std::string ocr_status = body_json.value("ocr_status", "");
            int limit = body_json.value("limit", 50);

            if (shop_id <= 0) {
                auto res = bad_request("shop_id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.receipt_queue(shop_id, ocr_status, limit);
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"items", json::array()}, {"counts", json::object()}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/receipt_detail") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            int ocr_id = body_json.value("ocr_id", 0);

            if (shop_id <= 0 || ocr_id <= 0) {
                auto res = bad_request("shop_id and ocr_id are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.receipt_detail(shop_id, ocr_id);
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/receipt_page_image") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            int page_id = body_json.value("page_id", 0);

            if (shop_id <= 0 || page_id <= 0) {
                auto res = bad_request("shop_id and page_id are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.receipt_page_image(shop_id, page_id);
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/receipt_run_ocr") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            int ocr_id = body_json.value("ocr_id", 0);
            std::string api_key = resolve_openai_ocr_key();

            if (shop_id <= 0 || ocr_id <= 0) {
                auto res = bad_request("shop_id and ocr_id are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }
            if (api_key.empty()) {
                json err = {{"error", openai_ocr_key_missing_message()}};
                auto res = json_response(err, http::status::service_unavailable, req.keep_alive(), req.version());
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.receipt_run_ocr(shop_id, ocr_id, api_key);
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/receipt_reprocess") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            int ocr_id = body_json.value("ocr_id", 0);
            std::string api_key = resolve_openai_ocr_key();

            if (shop_id <= 0 || ocr_id <= 0) {
                auto res = bad_request("shop_id and ocr_id are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }
            if (api_key.empty()) {
                json err = {{"error", openai_ocr_key_missing_message()}};
                auto res = json_response(err, http::status::service_unavailable, req.keep_alive(), req.version());
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.receipt_reprocess(shop_id, ocr_id, api_key);
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"reprocessed", false}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/receipt_reopen") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            int ocr_id = body_json.value("ocr_id", 0);
            std::string reopened_by = body_json.value("reopened_by", "");
            std::string review_note = body_json.value("review_note", "");

            if (shop_id <= 0 || ocr_id <= 0) {
                auto res = bad_request("shop_id and ocr_id are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.receipt_reopen(shop_id, ocr_id, reopened_by, review_note);
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"reopened", false}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/receipt_delete") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            int ocr_id = body_json.value("ocr_id", 0);

            if (shop_id <= 0 || ocr_id <= 0) {
                auto res = bad_request("shop_id and ocr_id are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.receipt_delete(shop_id, ocr_id);
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"deleted", false}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/receipt_delete_page") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            int ocr_id = body_json.value("ocr_id", 0);
            int page_id = body_json.value("page_id", 0);

            if (shop_id <= 0 || ocr_id <= 0 || page_id <= 0) {
                auto res = bad_request("shop_id, ocr_id, and page_id are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.receipt_delete_page(shop_id, ocr_id, page_id);
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"deleted", false}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/receipt_save_draft") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            int ocr_id = body_json.value("ocr_id", 0);
            json drafts = body_json.contains("drafts") ? body_json["drafts"] : json::array();
            std::string review_note = body_json.value("review_note", "");
            std::string reviewed_by = body_json.value("reviewed_by", "");

            if (shop_id <= 0 || ocr_id <= 0) {
                auto res = bad_request("shop_id and ocr_id are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.save_receipt_drafts(shop_id, ocr_id, drafts, review_note, reviewed_by);
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"saved", false}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/receipt_approve") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            int ocr_id = body_json.value("ocr_id", 0);
            std::string approved_by = body_json.value("approved_by", "");
            std::string review_note = body_json.value("review_note", "");

            if (shop_id <= 0 || ocr_id <= 0) {
                auto res = bad_request("shop_id and ocr_id are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.receipt_approve(shop_id, ocr_id, approved_by, review_note);
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"approved", false}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/receipt_post") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int shop_id = body_json.value("shop_id", 0);
            int ocr_id = body_json.value("ocr_id", 0);
            std::string posted_by = body_json.value("posted_by", "");

            if (shop_id <= 0 || ocr_id <= 0) {
                auto res = bad_request("shop_id and ocr_id are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.receipt_post(shop_id, ocr_id, posted_by);
            auto res = json_response(resp, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"posted", false}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post && target == "/execute_sql") {
        try {
            auto body_json           =  json::parse(req.body());
            int shop_id              =  body_json.value("shop_id", -1);
            auto source_kind         =  parse_source_kind(body_json);
            std::string sql          =  body_json.value("sql", "");

            if (shop_id < 0) {
                auto res = bad_request("shop_id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }
            if (sql.empty()) {
                auto res = bad_request("sql is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json exec_result = db.execute_sql(shop_id, source_kind, sql);
            std::string db_error = exec_result.value("error", "");

            json response = {
                {"shop_id", shop_id},
                {"source_kind", source_kind == PostgresApi::SourceKind::Pos ? "pos" : "expense"},
                {"sql", sql},
                {"error", db_error},
                {"result", exec_result}
            };

            auto res = json_response(response, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {
                {"sql", ""},
                {"error", e.what()},
                {"result", json::object()}
            };
            auto res = json_response(err, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    if (req.method() == http::verb::post &&
        (target == "/scan_nav" || target == "/scan_update" || target == "/scan_delete" ||
         target == "/ingest_from_ocr" || target.starts_with("/gpt_ocr_pdfs"))) {
        json err = {
            {"error", "Legacy scan endpoints were removed. Use /receipt_upload, /receipt_queue, /receipt_detail, /receipt_page_image, /receipt_run_ocr, /receipt_delete, and /receipt_save_draft."}
        };
        auto res = json_response(err, http::status::gone, req.keep_alive(), req.version());
        add_cors(res);
        res.keep_alive(false);
        co_return res;
    }

    if (req.method() != http::verb::get) {
        auto res = bad_request("Only the method of GET allowed!");
        add_cors(res);
        res.keep_alive(false);
        co_return res;
    }

    if (target == "/") {
        
        co_return get_response("\nWelcome to "s + _server_name_ + "\n"s);

    } else if (target == "/list") {

        std::string msg = R"(
            /test:
            
            /tsr_ocr_pdfs:
            /cvt_pdf_png:

            /ext_txt_img:

            /execute_sql:
                POST body json: {"shop_id": <int>, "source_kind": "pos|expense", "sql": "SELECT ..."}
                executes SQL directly on the selected shop source database

            /table_update:
                POST body: {"shop_id": <int>, "source_kind": "pos|expense", "table": "...", "key_column": "id", "rows": [{"key": ..., "changes": {...}}]}
                applies updates to the selected shop source database

            /table_delete:
                POST body: {"shop_id": <int>, "source_kind": "pos|expense", "table": "...", "key_column": "id", "keys": [...]}
                deletes rows in the selected shop source database

            /ocr_image:
                POST body: {"shop_id": <int>, "ocr_id": <int>}
                returns image_path and base64 image content for that OCR scan

            /shop_summary:
                POST body: {"shop_id": <int>, "start_time": "...", "end_time": "..."}
                returns revenue/orders/product rollups for a shop within the window

            /purchased_summary:
                POST body: {"shop_id": <int>, "start_time": "...", "end_time": "..."}
                returns purchased summary from tracker tables in that shop expense DB

            /init_expense_tracker:
                POST body: {"shop_id": <int>} or {"shop_ids": [<int>, ...]}
                creates tracker schema/tables inside selected shop expense DBs

            /receipt_upload:
                POST body: {"shop_id": <int>, "file_name": "...", "mime_type": "image/png|application/pdf", "content_base64": "..."}
                stores one uploaded receipt file in the selected shop expense tracker

            /receipt_queue:
                POST body: {"shop_id": <int>, "ocr_status": "", "limit": 100}
                lists uploaded receipts and OCR status for the selected shop expense tracker

            /receipt_detail:
                POST body: {"shop_id": <int>, "ocr_id": <int>}
                returns receipt metadata, pages, drafts, and review state

            /receipt_page_image:
                POST body: {"shop_id": <int>, "page_id": <int>}
                returns one receipt page image in base64

            /receipt_run_ocr:
                POST body: {"shop_id": <int>, "ocr_id": <int>}
                runs OCR for one uploaded receipt and stages structured drafts

            /receipt_delete:
                POST body: {"shop_id": <int>, "ocr_id": <int>}
                deletes one uploaded receipt plus its staged tracker rows and OCR job rows

            /receipt_save_draft:
                POST body: {"shop_id": <int>, "ocr_id": <int>, "drafts": [...], "review_note": "...", "reviewed_by": "..."}
                saves structured drafts and review notes for one receipt

            /db_schema_overview:
                POST body: {"shop_id": <int>, "source_kind": "pos|expense"}
                returns database tables/columns/row counts for that shop source
        )";

        co_return get_response(msg);
    
    } else if (target == "/test") {
        ScopedTimer timer("test");
         
        co_return get_response("test running has completed!");
    
    } else if (target == "/tsr_ocr_pdfs") {
        
        Ocr ocr("/home/liam/Data/wokandflame/ocr");
        ocr.tsr_ocr_pdfs();

        co_return get_response("ocr_pdfs has completed!");

    } else if (target == "/cvt_pdf_png") {

        Ocr ocr;
        //ocr.convert_pdf_to_png("/home/liam/Data/wokandflame/ocr/2025-10-10.pdf", "/home/liam/Data/wokandflame/ocr/images");
        ocr.convert_pdf_to_png("/home/liam/Data/wokandflame/p23.pdf", "/home/liam/Data/wokandflame");
        co_return get_response("Converting has completed!");

    } else if (target == "/ext_txt_img") {

        Ocr ocr;
        cv::Mat src         =   cv::imread("./images/receipt_page1.png");
        cv::Mat pre         =   ocr.preprocess_picture(src);          // your improved preprocess
        auto clean          =   ocr.enhance_for_ocr(pre);
        std::string text    =   ocr.extract_text_from_image(clean);
        std::cout << text << std::endl;

        co_return get_response("Extracting text from image has completed!");
    }

    co_return not_found(req.target());
}
