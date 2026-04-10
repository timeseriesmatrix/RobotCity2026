#include "postgresApi.h"
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
#include <spdlog/spdlog.h>

using json = nlohmann::json;

namespace {
int cents_from_amount(double v) {
    if (std::isnan(v) || std::isinf(v)) return 0;
    return static_cast<int>(std::llround(v));
}

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
std::string normalize_order_type(const std::string &raw)
{
    std::string s;
    s.reserve(raw.size());
    for (char c : raw) {
        if (c == '_' || c == ' ') {
            s.push_back('-');
        } else {
            s.push_back(static_cast<char>(std::tolower(static_cast<unsigned char>(c))));
        }
    }

    if (s.empty()) return "dine-in";

    // Map common Floreant/legacy variants to our check constraint values
    if (s == "dine-in" || s == "dinein" || s == "dine" || s == "table" || s == "eat-in" || s == "eat-in-guest") {
        return "dine-in";
    }
    if (s == "takeaway" || s == "take-away" || s == "takeout" || s == "take-out" || s == "to-go" || s == "to-go-" || s == "to-go-guest" || s == "pickup" || s == "pick-up" || s == "carryout" || s == "carry-out") {
        return "takeaway";
    }
    if (s == "retail") {
        return "retail";
    }
    if (s == "delivery" || s == "deliver" || s == "delivery-guest") {
        return "delivery";
    }

    return "dine-in";
}
} // namespace

int PostgresApi::ensure_shop_exists(int shop_id, const std::string &name, pqxx::work &txn)
{
    std::string shop_name = name.empty() ? std::format("Shop {}", shop_id) : name;
    pqxx::result r = txn.exec_params(
        "INSERT INTO shops (id, name, pos_info) "
        "VALUES ($1, $2, 'FloreantPOS') "
        "ON CONFLICT (id) DO UPDATE "
        "SET name = EXCLUDED.name, pos_info = EXCLUDED.pos_info "
        "RETURNING id",
        shop_id,
        shop_name
    );

    if (r.empty()) {
        throw std::runtime_error("Failed to ensure shop exists in shops table");
    }
    return r[0][0].as<int>();
}

int PostgresApi::ensure_pos_sync_scan(int shop_id, pqxx::work &txn)
{
    // Create/reuse a placeholder OCR scan row to satisfy NOT NULL FK on products. Image path unique per shop.
    const std::string path = std::format("pos_sync_shop_{}", shop_id);
    pqxx::result r = txn.exec_params(
        "SELECT id FROM ocr_scans WHERE image_path = $1",
        path
    );
    if (!r.empty()) return r[0][0].as<int>();

    pqxx::result ins = txn.exec_params(
        "INSERT INTO ocr_scans (shop_id, image_path, scan_type, extracted_text) "
        "VALUES ($1, $2, 'pos_sync', '') RETURNING id",
        shop_id,
        path
    );
    if (ins.empty()) throw std::runtime_error("Failed to insert placeholder ocr_scans row for POS sync");
    return ins[0][0].as<int>();
}

PostgresApi::PostgresApi(std::string_view dbname, std::string_view user, std::string_view pass 
        , std::string_view host, std::string_view port, std::string_view shop_config_path)
{
    conninfo_   =   std::format("host={} port={} dbname={} user={} password={}"
            , host, port, dbname, user, pass);

    if (!shop_config_path.empty()) {
        load_shop_connections(std::string(shop_config_path));
    }
}

bool PostgresApi::load_shop_connections(const std::string &path)
{
    shop_connections_.clear();
    shop_index_.clear();

    std::ifstream in(path);
    if (!in.is_open()) {
        spdlog::warn("[PostgresApi] shop config file '{}' not found; no external shop connections loaded", path);
        return false;
    }

    json doc;
    try {
        in >> doc;
    } catch (const std::exception &e) {
        spdlog::error("[PostgresApi] Failed to parse shop config '{}': {}", path, e.what());
        return false;
    }

    if (!doc.is_array()) {
        spdlog::error("[PostgresApi] Shop config '{}' must be a JSON array of objects", path);
        return false;
    }

    for (const auto &entry : doc) {
        int shop_id = entry.value("shop_id", -1);
        if (shop_id < 0) {
            spdlog::warn("[PostgresApi] Skipping shop entry without valid shop_id in '{}'", path);
            continue;
        }
        if (shop_index_.count(shop_id)) {
            spdlog::warn("[PostgresApi] Duplicate shop_id {} in '{}', skipping", shop_id, path);
            continue;
        }

        std::string name = entry.value("name", "");
        std::string conninfo;

        if (entry.contains("conninfo") && entry["conninfo"].is_string()) {
            conninfo = entry.value("conninfo", "");
        } else {
            std::string host = entry.value("host", "localhost");
            std::string port = entry.value("port", "5432");
            std::string db   = entry.value("dbname", "");
            std::string user = entry.value("user", "");
            std::string pass = entry.value("password", "");

            if (db.empty() || user.empty()) {
                spdlog::warn("[PostgresApi] shop_id {} missing dbname/user in '{}', skipping", shop_id, path);
                continue;
            }

            conninfo = std::format(
                "host={} port={} dbname={} user={} password={}",
                host, port, db, user, pass
            );
        }

        if (conninfo.empty()) {
            spdlog::warn("[PostgresApi] shop_id {} has empty conninfo in '{}', skipping", shop_id, path);
            continue;
        }

        shop_index_[shop_id] = shop_connections_.size();
        shop_connections_.push_back(ShopConnection{
            shop_id,
            name,
            conninfo
        });
    }

    spdlog::info("[PostgresApi] Loaded {} shop connection(s) from {}", shop_connections_.size(), path);
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

void PostgresApi::crt_tbs_ft()
{
    try {
        // 1. Connect to the PostgreSQL database
        pqxx::connection conn(conninfo_);
        pqxx::work txn(conn);

        txn.exec(R"(
            CREATE TABLE IF NOT EXISTS shops (
                id SERIAL PRIMARY KEY,
                name TEXT NOT NULL,
                pos_info TEXT,
                contact_info TEXT,
                location TEXT
            );
        )");

        txn.exec(R"(
            INSERT INTO shops (id, name, pos_info)
            VALUES (0, 'Default Shop', 'Unassigned')
            ON CONFLICT (id) DO NOTHING;
        )");

        txn.exec(R"(
            CREATE TABLE IF NOT EXISTS ocr_scans (
                id SERIAL PRIMARY KEY,
                shop_id INTEGER NOT NULL DEFAULT 0 REFERENCES shops(id) ON DELETE SET DEFAULT,
                image_path TEXT UNIQUE NOT NULL,
                scan_type TEXT NOT NULL,
                extracted_text TEXT,
                scanned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
        )");

        txn.exec(R"(
            CREATE TABLE IF NOT EXISTS suppliers (
                id SERIAL PRIMARY KEY,
                shop_id INTEGER NOT NULL DEFAULT 0 REFERENCES shops(id) ON DELETE SET DEFAULT,
                tin TEXT UNIQUE,
                ocr_id INTEGER NOT NULL REFERENCES ocr_scans(id),
                name TEXT NOT NULL,
                contact_info TEXT,
                site TEXT
            );
        )");

        txn.exec(R"(
            CREATE TABLE IF NOT EXISTS products (
                id SERIAL PRIMARY KEY,
                shop_id INTEGER NOT NULL DEFAULT 0 REFERENCES shops(id) ON DELETE SET DEFAULT,
                pos_menu_item_id BIGINT,
                sku TEXT UNIQUE,
                ocr_id INTEGER NOT NULL REFERENCES ocr_scans(id),
                name TEXT NOT NULL,
                product_type TEXT NOT NULL CHECK (product_type IN ('ingredient', 'menu')),
                class_name TEXT,
                type_name TEXT,
                supplier_id INTEGER REFERENCES suppliers(id),
                default_unit_price INTEGER,
                is_active BOOLEAN DEFAULT TRUE,
                stock_qty INTEGER DEFAULT 0
            );
        )");
        
        txn.exec(R"(
            CREATE TABLE IF NOT EXISTS purchase_orders (
                id SERIAL PRIMARY KEY,
                shop_id INTEGER NOT NULL DEFAULT 0 REFERENCES shops(id) ON DELETE SET DEFAULT,
                invoice_id TEXT UNIQUE,
                ocr_id INTEGER NOT NULL REFERENCES ocr_scans(id),
                supplier_id INTEGER REFERENCES suppliers(id),
                purchase_date DATE NOT NULL DEFAULT CURRENT_DATE,
                total_cost INTEGER,
                notes TEXT
            );
        )");

        txn.exec(R"(
            CREATE TABLE IF NOT EXISTS purchase_items (
                id SERIAL PRIMARY KEY,
                shop_id INTEGER NOT NULL DEFAULT 0 REFERENCES shops(id) ON DELETE SET DEFAULT,
                purchase_id INTEGER REFERENCES purchase_orders(id),
                product_id INTEGER REFERENCES products(id),
                ocr_id INTEGER NOT NULL REFERENCES ocr_scans(id),
                quantity NUMERIC(10, 2),
                unit_price INTEGER,
                total_price INTEGER NOT NULL
            );
        )");

        txn.exec(R"(
            CREATE TABLE IF NOT EXISTS customer_orders (
                id SERIAL PRIMARY KEY,
                shop_id INTEGER NOT NULL DEFAULT 0 REFERENCES shops(id) ON DELETE SET DEFAULT,
                pos_ticket_id BIGINT,
                order_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                total_price INTEGER,
                payment_method TEXT,
                order_type TEXT CHECK (order_type IN ('dine-in', 'takeaway', 'delivery', 'retail'))
            );
        )");
        
        txn.exec(R"(
            CREATE TABLE order_items (
                id SERIAL PRIMARY KEY,
                shop_id INTEGER NOT NULL DEFAULT 0 REFERENCES shops(id) ON DELETE SET DEFAULT,
                pos_ticket_item_id BIGINT,
                order_id INTEGER REFERENCES customer_orders(id),
                product_id INTEGER REFERENCES products(id),
                quantity INTEGER NOT NULL,
                unit_price INTEGER,
                total_price INTEGER GENERATED ALWAYS AS (quantity * unit_price) STORED
            ); 
        )");

        txn.exec(R"(
            CREATE UNIQUE INDEX IF NOT EXISTS customer_orders_shop_ticket_uidx
            ON customer_orders (shop_id, pos_ticket_id);
        )");
        // Ensure order_type check constraint allows 'retail' (upgrade safe).
        txn.exec(R"(
            DO $$
            BEGIN
                IF EXISTS (
                    SELECT 1
                    FROM pg_constraint c
                    JOIN pg_class t ON c.conrelid = t.oid
                    WHERE c.conname = 'customer_orders_order_type_check'
                      AND t.relname = 'customer_orders'
                      AND pg_get_constraintdef(c.oid) NOT LIKE '%retail%'
                ) THEN
                    ALTER TABLE customer_orders DROP CONSTRAINT customer_orders_order_type_check;
                END IF;

                IF NOT EXISTS (
                    SELECT 1
                    FROM pg_constraint c
                    JOIN pg_class t ON c.conrelid = t.oid
                    WHERE c.conname = 'customer_orders_order_type_check'
                      AND t.relname = 'customer_orders'
                ) THEN
                    ALTER TABLE customer_orders
                    ADD CONSTRAINT customer_orders_order_type_check
                    CHECK (order_type IN ('dine-in', 'takeaway', 'delivery', 'retail'));
                END IF;
            END$$;
        )");
        txn.exec(R"(
            CREATE UNIQUE INDEX IF NOT EXISTS order_items_shop_ticket_item_uidx
            ON order_items (shop_id, pos_ticket_item_id);
        )");
        txn.exec(R"(
            CREATE UNIQUE INDEX IF NOT EXISTS products_shop_menu_item_uidx
            ON products (shop_id, pos_menu_item_id);
        )");

        txn.commit();
        spdlog::info("[crt_tbs_ft] Tables in flametrack created successfully.");
    }
    catch (const std::exception &e) {
        spdlog::error("[crt_tbs_ft] Creating tables Failed - Error: {}", e.what());
    }
}

nlohmann::json PostgresApi::sync_floreant_shops(const std::vector<int> &shop_ids, bool reset_pos)
{
    std::unordered_set<int> filter(shop_ids.begin(), shop_ids.end());
    json summary = json::array();

    if (shop_connections_.empty()) {
        spdlog::warn("[sync_floreant_shops] No shop connections loaded");
        summary.push_back({{"shop_id", -1}, {"name", ""}, {"products", 0}, {"orders", 0}, {"order_items", 0}, {"error", "no shop connections loaded"}});
        return summary;
    }

    for (const auto &shop_conn : shop_connections_) {
        if (!filter.empty() && !filter.count(shop_conn.shop_id)) {
            continue;
        }

        json stat = {
            {"shop_id", shop_conn.shop_id},
            {"name", shop_conn.name},
            {"products", 0},
            {"orders", 0},
            {"order_items", 0},
            {"error", ""}
        };

        try {
            pqxx::connection pos_conn(shop_conn.conninfo);
            pqxx::connection target(conninfo_);
            pqxx::work       pos_tx(pos_conn);
            pqxx::work       target_tx(target);

            ensure_shop_exists(shop_conn.shop_id, shop_conn.name, target_tx);
            if (reset_pos) {
                target_tx.exec_params(
                    "DELETE FROM order_items "
                    "WHERE shop_id = $1 AND pos_ticket_item_id IS NOT NULL",
                    shop_conn.shop_id
                );
                target_tx.exec_params(
                    "DELETE FROM customer_orders "
                    "WHERE shop_id = $1 AND pos_ticket_id IS NOT NULL",
                    shop_conn.shop_id
                );
                target_tx.exec_params(
                    "DELETE FROM products "
                    "WHERE shop_id = $1 AND pos_menu_item_id IS NOT NULL",
                    shop_conn.shop_id
                );
            }
            int pos_scan_id = ensure_pos_sync_scan(shop_conn.shop_id, target_tx);

            // 1) Ingest menu_item -> products
            pqxx::result menu_rows = pos_tx.exec("SELECT * FROM menu_item");
            std::unordered_map<long long, int> menu_to_product;
            menu_to_product.reserve(menu_rows.size());

            for (const auto &row : menu_rows) {
                long long menu_id = get_int64(row, "id");
                if (menu_id == 0) continue;

                std::string name      = get_string(row, "name");
                std::string classname = get_string(row, "category_name");
                std::string typename_ = get_string(row, "group_name");
                double price          = get_double(row, "price");
                int price_cents       = cents_from_amount(price);

                pqxx::result r = target_tx.exec_params(
                    "INSERT INTO products (shop_id, name, product_type, class_name, type_name, default_unit_price, pos_menu_item_id, ocr_id) "
                    "VALUES ($1, $2, 'menu', $3, $4, $5, $6, $7) "
                    "ON CONFLICT (shop_id, pos_menu_item_id) DO UPDATE "
                    "SET name = EXCLUDED.name, class_name = EXCLUDED.class_name, type_name = EXCLUDED.type_name, default_unit_price = EXCLUDED.default_unit_price "
                    "RETURNING id",
                    shop_conn.shop_id,
                    name,
                    classname,
                    typename_,
                    price_cents,
                    menu_id,
                    pos_scan_id
                );

                if (!r.empty()) {
                    menu_to_product[menu_id] = r[0][0].as<int>();
                }
            }
            stat["products"] = static_cast<int>(menu_to_product.size());

            // 2) Ingest ticket -> customer_orders
            pqxx::result ticket_rows = pos_tx.exec("SELECT * FROM ticket");
            std::unordered_map<long long, int> ticket_to_order;
            ticket_to_order.reserve(ticket_rows.size());

            for (const auto &row : ticket_rows) {
                long long ticket_id = get_int64(row, "id");
                if (ticket_id == 0) continue;

                std::string order_time = get_string(row, "closing_date");
                // If there is no closing_date, skip syncing this ticket.
                if (order_time.empty()) {
                    continue;
                }

                if (order_time.empty()) {
                    order_time = get_string(row, "create_date");
                }

                double total_amount    = get_double(row, "total_price");
                int total_cents        = cents_from_amount(total_amount);
                std::string payment    = ""; // ticket table doesn't have explicit payment method
                std::string order_type = normalize_order_type(get_string(row, "ticket_type", "dine-in"));
                if (order_type.empty()) order_type = "dine-in";

                pqxx::result r = target_tx.exec_params(
                    "INSERT INTO customer_orders (shop_id, order_time, total_price, payment_method, order_type, pos_ticket_id) "
                    "VALUES ($1, COALESCE($2::timestamp, CURRENT_TIMESTAMP), $3, $4, $5, $6) "
                    "ON CONFLICT (shop_id, pos_ticket_id) DO UPDATE "
                    "SET order_time = EXCLUDED.order_time, total_price = EXCLUDED.total_price, payment_method = EXCLUDED.payment_method, order_type = EXCLUDED.order_type "
                    "RETURNING id",
                    shop_conn.shop_id,
                    order_time,
                    total_cents,
                    payment,
                    order_type,
                    ticket_id
                );

                if (!r.empty()) {
                    ticket_to_order[ticket_id] = r[0][0].as<int>();
                }
            }
            stat["orders"] = static_cast<int>(ticket_to_order.size());

            // 3) Ingest ticket_item -> order_items
            pqxx::result item_rows = pos_tx.exec("SELECT * FROM ticket_item");
            int items_ingested = 0;

            for (const auto &row : item_rows) {
                long long ticket_item_id = get_int64(row, "id");
                long long ticket_id      = get_int64(row, "ticket_id");
                long long menu_id        = get_int64(row, "item_id");

                if (ticket_id == 0 || menu_id == 0) continue;
                auto order_it  = ticket_to_order.find(ticket_id);
                auto prod_it   = menu_to_product.find(menu_id);
                if (order_it == ticket_to_order.end() || prod_it == menu_to_product.end()) continue;

                double qty_val     = get_double(row, "item_count", 1.0);
                int quantity       = static_cast<int>(std::max(1.0, std::round(qty_val)));
                double unit_price  = get_double(row, "item_price");
                int unit_price_cents = cents_from_amount(unit_price);

                target_tx.exec_params(
                    "INSERT INTO order_items (shop_id, order_id, product_id, quantity, unit_price, pos_ticket_item_id) "
                    "VALUES ($1, $2, $3, $4, $5, $6) "
                    "ON CONFLICT (shop_id, pos_ticket_item_id) DO UPDATE "
                    "SET quantity = EXCLUDED.quantity, unit_price = EXCLUDED.unit_price",
                    shop_conn.shop_id,
                    order_it->second,
                    prod_it->second,
                    quantity,
                    unit_price_cents,
                    ticket_item_id
                );

                ++items_ingested;
            }
            stat["order_items"] = items_ingested;

            target_tx.commit();
        } catch (const std::exception &e) {
            stat["error"] = e.what();
            spdlog::error("[sync_floreant_shops] shop_id={} failed: {}", shop_conn.shop_id, e.what());
        }

        summary.push_back(stat);
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
        pqxx::connection conn(shop_conn->conninfo);
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

json PostgresApi::purchased_summary(const std::string &start_time, const std::string &end_time)
{
    const std::string start = start_time.empty() ? "1970-01-01" : start_time;
    const std::string end   = end_time.empty()   ? "now"        : end_time;

    try {
        pqxx::connection conn(conninfo_);
        pqxx::work txn(conn);

        pqxx::result totals_res = txn.exec_params(R"(
            WITH window_orders AS (
                SELECT id, supplier_id, COALESCE(total_cost, 0) AS total_cost
                FROM purchase_orders
                WHERE purchase_date::timestamp BETWEEN $1::timestamp AND $2::timestamp
            ),
            window_items AS (
                SELECT pi.product_id, COALESCE(pi.quantity, 0) AS quantity
                FROM purchase_items pi
                JOIN window_orders wo ON wo.id = pi.purchase_id
            )
            SELECT
                (SELECT COUNT(*)::bigint FROM window_orders) AS orders,
                (SELECT COALESCE(SUM(total_cost), 0)::bigint FROM window_orders) AS total_cost_cents,
                (SELECT COALESCE(SUM(quantity), 0)::bigint FROM window_items) AS items,
                (SELECT COUNT(DISTINCT supplier_id)::bigint FROM window_orders) AS suppliers,
                (SELECT COUNT(DISTINCT product_id)::bigint FROM window_items) AS products
        )", start, end);

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
            FROM purchase_orders po
            LEFT JOIN suppliers s ON s.id = po.supplier_id
            WHERE po.purchase_date::timestamp BETWEEN $1::timestamp AND $2::timestamp
            GROUP BY name
            ORDER BY total_cost_cents DESC, orders DESC
            LIMIT 8
        )", start, end);

        pqxx::result top_products_res = txn.exec_params(R"(
            SELECT COALESCE(NULLIF(p.name, ''), 'Unknown') AS name,
                   COALESCE(SUM(pi.quantity), 0) AS quantity,
                   COALESCE(SUM(COALESCE(pi.total_price, COALESCE(pi.quantity, 0) * COALESCE(pi.unit_price, 0))), 0) AS total_cost_cents
            FROM purchase_items pi
            JOIN purchase_orders po ON po.id = pi.purchase_id
            LEFT JOIN products p ON p.id = pi.product_id
            WHERE po.purchase_date::timestamp BETWEEN $1::timestamp AND $2::timestamp
            GROUP BY name
            ORDER BY total_cost_cents DESC
            LIMIT 10
        )", start, end);

        pqxx::result recent_orders_res = txn.exec_params(R"(
            SELECT po.id AS purchase_id,
                   COALESCE(po.invoice_id, '') AS invoice_id,
                   COALESCE(NULLIF(s.name, ''), 'Unknown') AS supplier,
                   po.purchase_date::text AS purchase_date,
                   COALESCE(po.total_cost, 0)::bigint AS total_cost_cents
            FROM purchase_orders po
            LEFT JOIN suppliers s ON s.id = po.supplier_id
            WHERE po.purchase_date::timestamp BETWEEN $1::timestamp AND $2::timestamp
            ORDER BY po.purchase_date DESC, po.id DESC
            LIMIT 12
        )", start, end);

        pqxx::result selected_items_res = txn.exec_params(R"(
            SELECT pi.id,
                   pi.purchase_id,
                   COALESCE(po.ocr_id, 0) AS ocr_id,
                   COALESCE(po.invoice_id, '') AS invoice_id,
                   po.purchase_date::text AS purchase_date,
                   COALESCE(NULLIF(s.name, ''), 'Unknown') AS supplier,
                   COALESCE(NULLIF(p.name, ''), 'Unknown') AS product,
                   COALESCE(pi.quantity, 0) AS quantity,
                   COALESCE(pi.unit_price, 0) AS unit_price_cents,
                   COALESCE(pi.total_price, COALESCE(pi.quantity, 0) * COALESCE(pi.unit_price, 0)) AS total_price_cents
            FROM purchase_items pi
            JOIN purchase_orders po ON po.id = pi.purchase_id
            LEFT JOIN suppliers s ON s.id = po.supplier_id
            LEFT JOIN products p ON p.id = pi.product_id
            WHERE po.purchase_date::timestamp BETWEEN $1::timestamp AND $2::timestamp
            ORDER BY po.purchase_date DESC, pi.purchase_id DESC, pi.id ASC
            LIMIT 500
        )", start, end);

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
                {"total_cost_cents", get_int64(row, "total_cost_cents")}
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
                {"invoice_id", get_string(row, "invoice_id")},
                {"purchase_date", get_string(row, "purchase_date")},
                {"supplier", get_string(row, "supplier", "Unknown")},
                {"product", get_string(row, "product", "Unknown")},
                {"quantity", get_double(row, "quantity")},
                {"unit_price_cents", get_int64(row, "unit_price_cents")},
                {"total_price_cents", get_int64(row, "total_price_cents")}
            });
        }

        return json{
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
        spdlog::error("[purchased_summary] failed: {}", e.what());
        throw;
    }
}

json PostgresApi::db_schema_overview()
{
    try {
        pqxx::connection conn(conninfo_);
        pqxx::work txn(conn);

        pqxx::result tables_res = txn.exec(R"(
            SELECT t.table_name,
                   COALESCE(s.n_live_tup::bigint, 0) AS row_count
            FROM information_schema.tables t
            LEFT JOIN pg_stat_user_tables s ON s.relname = t.table_name
            WHERE t.table_schema = 'public'
              AND t.table_type = 'BASE TABLE'
            ORDER BY t.table_name
        )");

        json tables = json::array();
        for (const auto &tbl : tables_res) {
            const std::string table_name = tbl["table_name"].as<std::string>();
            const long long row_count = tbl["row_count"].as<long long>(0);

            pqxx::result cols_res = txn.exec_params(R"(
                SELECT column_name, data_type, is_nullable
                FROM information_schema.columns
                WHERE table_schema = 'public' AND table_name = $1
                ORDER BY ordinal_position
            )", table_name);

            json columns = json::array();
            for (const auto &col : cols_res) {
                columns.push_back({
                    {"name", col["column_name"].as<std::string>()},
                    {"data_type", col["data_type"].as<std::string>()},
                    {"is_nullable", col["is_nullable"].as<std::string>()}
                });
            }

            tables.push_back({
                {"table", table_name},
                {"row_count", row_count},
                {"columns", columns}
            });
        }

        txn.commit();
        return json{
            {"database", conn.dbname()},
            {"schema", "public"},
            {"tables", tables},
            {"error", ""}
        };
    } catch (const std::exception &e) {
        spdlog::error("[db_schema_overview] {}", e.what());
        return json{
            {"database", ""},
            {"schema", "public"},
            {"tables", json::array()},
            {"error", e.what()}
        };
    }
}


bool PostgresApi::is_image_scanned(const std::string &image_path)
{
    pqxx::connection conn(conninfo_);
    pqxx::work txn(conn);

    std::string filename = image_path;
    auto pos = filename.find_last_of("/\\");
    if (pos != std::string::npos && pos + 1 < filename.size()) {
        filename = filename.substr(pos + 1);
    }

    pqxx::result r;
    if (!filename.empty()) {
        r = txn.exec_params(
            "SELECT 1 FROM ocr_scans WHERE image_path = $1 OR RIGHT(image_path, LENGTH($2)) = $2 LIMIT 1",
            image_path,
            filename
        );
    } else {
        r = txn.exec_params(
            "SELECT 1 FROM ocr_scans WHERE image_path = $1 LIMIT 1",
            image_path
        );
    }

    return !r.empty();
}

void PostgresApi::insert_ocr_scan(const std::string &image_path, const std::string &extracted_text, const std::string &scan_type) 
{
    pqxx::connection conn(conninfo_);
    pqxx::work txn(conn);

    txn.exec_params(R"(
        INSERT INTO ocr_scans (image_path, scan_type, extracted_text)
        VALUES ($1, $2, $3)
        ON CONFLICT (image_path) DO NOTHING
    )", image_path, scan_type, extracted_text);

    txn.commit();
}

nlohmann::json PostgresApi::ingest_from_ocr_scans(const std::string &product_type,
                                                  const std::string &scan_type,
                                                  const std::string &since,
                                                  const std::string &end_time)
{
    pqxx::connection conn(conninfo_);
    pqxx::work txn(conn);

    // Build query dynamically to allow optional scan_type filter.
    std::string sql =
        "SELECT id, image_path, extracted_text FROM ocr_scans "
        "WHERE extracted_text IS NOT NULL ";

    pqxx::result r;
    if (scan_type.empty() && since.empty() && end_time.empty()) {
        sql += "AND scanned_at >= CURRENT_DATE ORDER BY id ASC";
        r = txn.exec(sql);
    } else if (scan_type.empty() && !since.empty() && end_time.empty()) {
        sql += "AND scanned_at >= $1::timestamp ORDER BY id ASC";
        r = txn.exec_params(sql, since);
    } else if (scan_type.empty() && since.empty() && !end_time.empty()) {
        sql += "AND scanned_at <= $1::timestamp ORDER BY id ASC";
        r = txn.exec_params(sql, end_time);
    } else if (scan_type.empty() && !since.empty() && !end_time.empty()) {
        sql += "AND scanned_at BETWEEN $1::timestamp AND $2::timestamp ORDER BY id ASC";
        r = txn.exec_params(sql, since, end_time);
    } else if (!scan_type.empty() && since.empty() && end_time.empty()) {
        sql += "AND scan_type = $1 AND scanned_at >= CURRENT_DATE ORDER BY id ASC";
        r = txn.exec_params(sql, scan_type);
    } else if (!scan_type.empty() && !since.empty() && end_time.empty()) {
        sql += "AND scan_type = $1 AND scanned_at >= $2::timestamp ORDER BY id ASC";
        r = txn.exec_params(sql, scan_type, since);
    } else if (!scan_type.empty() && since.empty() && !end_time.empty()) {
        sql += "AND scan_type = $1 AND scanned_at <= $2::timestamp ORDER BY id ASC";
        r = txn.exec_params(sql, scan_type, end_time);
    } else { // scan_type && since && end_time
        sql += "AND scan_type = $1 AND scanned_at BETWEEN $2::timestamp AND $3::timestamp ORDER BY id ASC";
        r = txn.exec_params(sql, scan_type, since, end_time);
    }

    spdlog::info(
        "[ingest_from_ocr_scans] Start ingesting {} records with scan_type='{}', since='{}', end_time='{}'"
        , r.size(), scan_type, (since.empty() ? "CURRENT_DATE" : since.c_str()), (end_time.empty() ? "MAX" : end_time.c_str())
    );

    int processed = 0;
    int failed    = 0;
    int skipped   = 0;
    json errors   = json::array();

    for (const auto &row : r) {
        std::string image_path      = row["image_path"].as<std::string>();
        std::string extracted_text  = row["extracted_text"].as<std::string>();
        int         ocr_id          = row["id"].as<int>();

        try {
            bool ingested_current = false;
            auto cleaned  = clean_json(extracted_text);
            json receipts = json::parse(cleaned);
            
            // Normalize: if it's a single object, wrap it into an array
            if (receipts.is_object()) {
                receipts = json::array({receipts});
            }

            if (!receipts.is_array()) {
                spdlog::error("[ingest_from_ocr_scans] Unexpected JSON root in ocr_id: {}", ocr_id);
                continue;
            }

            for (const auto &receipt : receipts) {
                // Extract supplier
                auto supplier       =   receipt["supplier"];
                std::string tin     =   json_to_str(supplier, "tin");
                std::string name    =   json_to_str(supplier, "name");
                if (tin.empty() && name.empty()) {
                    const std::string msg = "[ingest_from_ocr_scans] Missing supplier tin and name, skip receipt";
                    spdlog::warn("{} ocr_id={} image={}", msg, ocr_id, image_path);
                    errors.push_back({{"ocr_id", ocr_id}, {"image_path", image_path}, {"error", "Missing supplier tin/name"}});
                    ++skipped;
                    continue;
                }
                int  supplier_id    =   ensure_supplier_exists(ocr_id, supplier, txn);

                // Insert purchase_order
                auto order          =   receipt["purchase_order"];
                std::string invoice_raw = json_to_str(order, "invoice_id");
                if (invoice_raw.empty()) {
                    invoice_raw = "NONE#ocr_id=" + std::to_string(ocr_id);
                    order["invoice_id"] = invoice_raw;
                    spdlog::warn(
                        "[ingest_from_ocr_scans] Missing invoice_id, using placeholder '{}' ocr_id={} image={}",
                        invoice_raw,
                        ocr_id,
                        image_path
                    );
                }
                int  purchase_id    =   insert_purchase_order(ocr_id, order, supplier_id, txn);

                // If this receipt was already ingested, remove existing items so we can reingest.
                pqxx::result existing_items = txn.exec_params(
                    "SELECT 1 FROM purchase_items WHERE purchase_id = $1 AND ocr_id = $2 LIMIT 1",
                    purchase_id,
                    ocr_id
                );
                if (!existing_items.empty()) {
                    txn.exec_params(
                        "DELETE FROM purchase_items WHERE purchase_id = $1 AND ocr_id = $2",
                        purchase_id,
                        ocr_id
                    );
                    spdlog::info("[ingest_from_ocr_scans] Reingest: removed existing items for purchase_id={} ocr_id={}", purchase_id, ocr_id);
                }

                // Insert purchase_items
                for (const auto &item : receipt["purchase_items"]) {
                    int product_id = ensure_product_exists(
                        ocr_id,
                        item["name"],
                        supplier_id,
                        txn,
                        product_type
                    );
                    insert_purchase_item(ocr_id, purchase_id, product_id, item, txn);
                }

                ingested_current = true;
            }

            if (ingested_current) {
                ++processed;
            }
        } catch (const std::exception &e) {
            ++failed;
            spdlog::error(
                "[ingest_from_ocr_scans] Failed to parse or insert OCR data from {}: {}",
                image_path,
                e.what()
            );
            errors.push_back({{"ocr_id", ocr_id}, {"image_path", image_path}, {"error", e.what()}});
        }
        spdlog::info("[ingest_from_ocr_scans] Finish ingestion from {}", image_path);
    }

    txn.commit();

    return {
        {"processed", processed},
        {"failed", failed},
        {"skipped", skipped},
        {"total", static_cast<int>(r.size())},
        {"scan_type", scan_type},
        {"since", since.empty() ? "CURRENT_DATE" : since},
        {"end_time", end_time.empty() ? "MAX" : end_time},
        {"product_type", product_type},
        {"errors", errors}
    };
}

int PostgresApi::ensure_supplier_exists(const int ocr_id, const json &supplier, pqxx::work &txn)
{
    std::string tin     = json_to_str(supplier, "tin");
    std::string name    = json_to_str(supplier, "name");
    std::string site    = json_to_str(supplier, "site");
    std::string contact = json_to_str(supplier, "contact_info");

    // === 1. Fully unknown supplier: tin empty AND name empty or garbage like "未知供应商" ===
    if (tin.empty() && (name.empty()))
    {
        // We create a row with placeholder tin "", and then update it to
        // "None#Unknown#<id>" where <id> is the auto-generated primary key.
        
        // Step 1: Insert supplier with placeholder TIN
        pqxx::result ins = txn.exec_params(R"(
            INSERT INTO suppliers (tin, name, contact_info, site, ocr_id)
            VALUES ('', 'Unknown', $1, $2, $3)
            RETURNING id;
        )", contact, site, ocr_id);

        if (ins.empty()) {
            throw std::runtime_error("Insert failed (unknown supplier)");
        }

        int new_id = ins[0][0].as<int>();
        std::string auto_tin = "None#Unknown#" + std::to_string(new_id);

        // Step 2: Update the inserted row with real TIN
        pqxx::result upd = txn.exec_params(
            "UPDATE suppliers SET tin = $1 WHERE id = $2 RETURNING id;",
            auto_tin, new_id
        );

        if (upd.empty()) {
            throw std::runtime_error("Update failed (unknown supplier TIN)");
        }

        return new_id;
    }

    // === 2. tin is empty, but we have a name: use "None#<name>" ===
    if (tin.empty()) {
        tin = "None." + name;
    }

    // === 3. Normal path: if a supplier with this tin exists, reuse it ===
    pqxx::result r = txn.exec_params("SELECT id FROM suppliers WHERE tin = $1", tin);
    if (!r.empty()) return r[0][0].as<int>();

    // === 4. Otherwise insert a new supplier with this tin ===
    pqxx::result inserted = txn.exec_params(R"(
        INSERT INTO suppliers (tin, name, contact_info, site, ocr_id)
        VALUES ($1, $2, $3, $4, $5)
        RETURNING id
    )",
        tin, name, contact, site, ocr_id
    );

    if (inserted.empty()) {
        throw std::runtime_error("[ensure_supplier_exists] Insert failed");
    }

    return inserted[0][0].as<int>();
}

int PostgresApi::ensure_product_exists(const int ocr_id, const std::string &name, int supplier_id, pqxx::work &txn, const std::string &product_type) 
{
    pqxx::result r = txn.exec_params(
        "SELECT id FROM products WHERE name = $1 AND supplier_id = $2",
        name, supplier_id
    );

    if (!r.empty()) return r[0][0].as<int>();

    pqxx::result inserted = txn.exec_params(R"(
        INSERT INTO products (name, product_type, supplier_id, ocr_id)
        VALUES ($1, $2, $3, $4)
        RETURNING id
    )", name, product_type, supplier_id, ocr_id);

    return inserted[0][0].as<int>();
}

// If you want to keep using pqxx::work, change transaction_base to work here.
int PostgresApi::insert_purchase_order(const int ocr_id, const json &order, int supplier_id, pqxx::transaction_base &txn)
{
    std::string invoice_raw = json_to_str(order, "invoice_id");
    std::string date_raw    = json_to_str(order, "purchase_date");
    std::string date_iso    = fix_date(date_raw);
    int         total_cost  = json_to_int(order, "total_cost", 0);

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
            "FROM purchase_orders "
            "WHERE invoice_id = $1",
            invoice_stored
        );

        if (!r.empty()) {
            const int existing_id  = r[0]["id"].as<int>();
            const int existing_ocr = r[0]["ocr_id"].as<int>();
            const int existing_sup = r[0]["supplier_id"].as<int>();

            // Update existing purchase_order with latest date/total_cost/ocr_id
            txn.exec_params(
                "UPDATE purchase_orders "
                "SET purchase_date = $1::date, total_cost = $2, ocr_id = $3 "
                "WHERE id = $4",
                effective_date,
                total_cost,
                ocr_id,
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
            "INSERT INTO purchase_orders "
            "(invoice_id, supplier_id, purchase_date, total_cost, ocr_id) "
            "VALUES ($1, $2, $3::date, $4, $5) "
            "RETURNING id",
            invoice_stored,
            supplier_id,
            effective_date,   // always valid and non-empty
            total_cost,
            ocr_id
        );

        if (inserted.empty()) {
            throw std::runtime_error("[insert_purchase_order] Insert failed (non-empty invoice_id)");
        }

        return inserted[0]["id"].as<int>();
    }

    // ===== Case 2: invoice_id is empty in JSON =====

    // 1) Insert row without invoice_id, but with effective_date
    pqxx::result ins = txn.exec_params(
        "INSERT INTO purchase_orders "
        "(supplier_id, purchase_date, total_cost, ocr_id) "
        "VALUES ($1, $2::date, $3, $4) "
        "RETURNING id",
        supplier_id,
        effective_date,   // always valid and non-empty
        total_cost,
        ocr_id
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
        "UPDATE purchase_orders "
        "SET invoice_id = $1 "
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

void PostgresApi::insert_purchase_item(const int ocr_id, int purchase_id, int product_id, const json &item, pqxx::work &txn)
{
    double quantity    = json_to_double(item, "quantity", 0.0);
    int unit_price     = json_to_int(item, "unit_price", 0);
    int total_price    = json_to_int(item, "total_price", 0);

    txn.exec_params(R"(
        INSERT INTO purchase_items (purchase_id, product_id, quantity, unit_price, total_price, ocr_id)
        VALUES ($1, $2, $3::numeric, $4, $5, $6)
    )",
        purchase_id,
        product_id,
        pqxx::to_string(quantity),  // to avoid variant issue
        unit_price,
        total_price,
        ocr_id
    );
}

std::string PostgresApi::get_image_path(int ocr_id)
{
    pqxx::connection conn(conninfo_);
    pqxx::work txn(conn);
    pqxx::result res = txn.exec_params("SELECT image_path FROM ocr_scans WHERE id = $1 LIMIT 1", ocr_id);
    if (res.empty()) return {};
    return res[0]["image_path"].as<std::string>();
}

nlohmann::json PostgresApi::fetch_ocr_scan(int current_id, const std::string &direction,
                                           const std::string &start_time, const std::string &end_time)
{
    pqxx::connection conn(conninfo_);
    pqxx::work txn(conn);
    pqxx::result res;

    const bool filter_time = !start_time.empty() && !end_time.empty();
    const bool has_current = current_id > 0;
    auto build_with_time = [&](const std::string &base_sql, int id) {
        return txn.exec_params(base_sql, start_time, end_time, id);
    };
    auto build_with_time_no_id = [&](const std::string &base_sql) {
        return txn.exec_params(base_sql, start_time, end_time);
    };

    if (filter_time) {
        if (direction == "current" && has_current) {
            res = build_with_time(
                "SELECT id, image_path, scan_type, extracted_text, scanned_at, shop_id "
                "FROM ocr_scans WHERE scanned_at BETWEEN $1 AND $2 AND id = $3 LIMIT 1",
                current_id
            );
        } else if (direction == "prev" && has_current) {
            res = build_with_time(
                "SELECT id, image_path, scan_type, extracted_text, scanned_at, shop_id "
                "FROM ocr_scans WHERE scanned_at BETWEEN $1 AND $2 AND id < $3 ORDER BY id DESC LIMIT 1",
                current_id
            );
        } else if (direction == "next" && has_current) {
            res = build_with_time(
                "SELECT id, image_path, scan_type, extracted_text, scanned_at, shop_id "
                "FROM ocr_scans WHERE scanned_at BETWEEN $1 AND $2 AND id > $3 ORDER BY id ASC LIMIT 1",
                current_id
            );
        } else {
            res = build_with_time_no_id(
                "SELECT id, image_path, scan_type, extracted_text, scanned_at, shop_id "
                "FROM ocr_scans WHERE scanned_at BETWEEN $1 AND $2 ORDER BY id ASC LIMIT 1"
            );
        }
    } else {
        if (direction == "current" && has_current) {
            res = txn.exec_params(
                "SELECT id, image_path, scan_type, extracted_text, scanned_at, shop_id "
                "FROM ocr_scans WHERE id = $1 LIMIT 1",
                current_id
            );
        } else if (direction == "prev" && has_current) {
            res = txn.exec_params(
                "SELECT id, image_path, scan_type, extracted_text, scanned_at, shop_id "
                "FROM ocr_scans WHERE id < $1 ORDER BY id DESC LIMIT 1",
                current_id
            );
        } else if (direction == "next" && has_current) {
            res = txn.exec_params(
                "SELECT id, image_path, scan_type, extracted_text, scanned_at, shop_id "
                "FROM ocr_scans WHERE id > $1 ORDER BY id ASC LIMIT 1",
                current_id
            );
        } else {
            res = txn.exec(
                "SELECT id, image_path, scan_type, extracted_text, scanned_at, shop_id "
                "FROM ocr_scans ORDER BY id ASC LIMIT 1"
            );
        }
    }

    if (res.empty()) {
        return {{"error", "No OCR scans found"}};
    }

    const auto &row = res[0];
    const int record_id = row["id"].as<int>();
    json record = {
        {"id", record_id},
        {"image_path", row["image_path"].is_null() ? "" : row["image_path"].as<std::string>()},
        {"scan_type", row["scan_type"].is_null() ? "" : row["scan_type"].as<std::string>()},
        {"extracted_text", row["extracted_text"].is_null() ? "" : row["extracted_text"].as<std::string>()},
        {"scanned_at", row["scanned_at"].is_null() ? "" : row["scanned_at"].as<std::string>()},
        {"shop_id", row["shop_id"].is_null() ? 0 : row["shop_id"].as<int>()}
    };

    // Compute pagination metadata within the filtered window.
    long total_count = 0;
    if (filter_time) {
        pqxx::result total_res = txn.exec_params(
            "SELECT COUNT(*) FROM ocr_scans WHERE scanned_at BETWEEN $1 AND $2",
            start_time,
            end_time
        );
        total_count = total_res.empty() ? 0 : total_res[0][0].as<long>();
    } else {
        pqxx::result total_res = txn.exec("SELECT COUNT(*) FROM ocr_scans");
        total_count = total_res.empty() ? 0 : total_res[0][0].as<long>();
    }

    long position = 0;
    if (filter_time) {
        pqxx::result pos_res = txn.exec_params(
            "SELECT COUNT(*) FROM ocr_scans WHERE scanned_at BETWEEN $1 AND $2 AND id <= $3",
            start_time,
            end_time,
            record_id
        );
        position = pos_res.empty() ? 0 : pos_res[0][0].as<long>();
    } else {
        pqxx::result pos_res = txn.exec_params(
            "SELECT COUNT(*) FROM ocr_scans WHERE id <= $1",
            record_id
        );
        position = pos_res.empty() ? 0 : pos_res[0][0].as<long>();
    }

    bool has_prev = false;
    bool has_next = false;
    if (filter_time) {
        pqxx::result prev_res = txn.exec_params(
            "SELECT 1 FROM ocr_scans WHERE scanned_at BETWEEN $1 AND $2 AND id < $3 ORDER BY id DESC LIMIT 1",
            start_time,
            end_time,
            record_id
        );
        pqxx::result next_res = txn.exec_params(
            "SELECT 1 FROM ocr_scans WHERE scanned_at BETWEEN $1 AND $2 AND id > $3 ORDER BY id ASC LIMIT 1",
            start_time,
            end_time,
            record_id
        );
        has_prev = !prev_res.empty();
        has_next = !next_res.empty();
    } else {
        pqxx::result prev_res = txn.exec_params(
            "SELECT 1 FROM ocr_scans WHERE id < $1 ORDER BY id DESC LIMIT 1",
            record_id
        );
        pqxx::result next_res = txn.exec_params(
            "SELECT 1 FROM ocr_scans WHERE id > $1 ORDER BY id ASC LIMIT 1",
            record_id
        );
        has_prev = !prev_res.empty();
        has_next = !next_res.empty();
    }

    record["position"] = position;
    record["total"]    = total_count;
    record["has_prev"] = has_prev;
    record["has_next"] = has_next;

    return record;
}

bool PostgresApi::update_ocr_scan_text(int id, const std::string &text)
{
    pqxx::connection conn(conninfo_);
    pqxx::work txn(conn);
    pqxx::result res = txn.exec_params(
        "UPDATE ocr_scans SET extracted_text = $1 WHERE id = $2 RETURNING id",
        text,
        id
    );
    if (res.empty()) {
        return false;
    }
    txn.commit();
    return true;
}

nlohmann::json PostgresApi::delete_ocr_scan(int id)
{
    if (id <= 0) {
        return {
            {"id", id},
            {"deleted", false},
            {"image_path", ""},
            {"file_deleted", false},
            {"file_status", ""},
            {"error", "id is required"}
        };
    }

    pqxx::connection conn(conninfo_);
    pqxx::work txn(conn);
    pqxx::result scan_res = txn.exec_params(
        "SELECT image_path FROM ocr_scans WHERE id = $1 LIMIT 1",
        id
    );

    if (scan_res.empty()) {
        return {
            {"id", id},
            {"deleted", false},
            {"image_path", ""},
            {"file_deleted", false},
            {"file_status", ""},
            {"error", "OCR scan not found"}
        };
    }

    std::string image_path = scan_res[0]["image_path"].is_null() ? "" : scan_res[0]["image_path"].as<std::string>();
    pqxx::result del_res = txn.exec_params(
        "DELETE FROM ocr_scans WHERE id = $1 RETURNING id",
        id
    );
    if (del_res.empty()) {
        return {
            {"id", id},
            {"deleted", false},
            {"image_path", image_path},
            {"file_deleted", false},
            {"file_status", ""},
            {"error", "Delete failed"}
        };
    }

    bool file_deleted = false;
    std::string file_status;
    if (!image_path.empty()) {
        std::error_code ec;
        const std::filesystem::path path(image_path);
        const bool exists = std::filesystem::exists(path, ec);
        if (ec) {
            throw std::runtime_error(std::format("Failed to access image file '{}': {}", image_path, ec.message()));
        }

        if (!exists) {
            file_status = "Image file was already missing.";
        } else {
            file_deleted = std::filesystem::remove(path, ec);
            if (ec) {
                throw std::runtime_error(std::format("Failed to delete image file '{}': {}", image_path, ec.message()));
            }
            if (!file_deleted) {
                throw std::runtime_error(std::format("Failed to delete image file '{}'", image_path));
            }
        }
    } else {
        file_status = "Image path was empty.";
    }

    txn.commit();
    return {
        {"id", id},
        {"deleted", true},
        {"image_path", image_path},
        {"file_deleted", file_deleted},
        {"file_status", file_status},
        {"error", ""}
    };
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

int PostgresApi::update_rows(const std::string &table, const std::string &key_column, const json &rows)
{
    if (!is_identifier_safe(table) || !is_identifier_safe(key_column) || !rows.is_array()) {
        spdlog::error("[update_rows] Invalid table/key/rows");
        return 0;
    }

    pqxx::connection conn(conninfo_);
    pqxx::work txn(conn);
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

int PostgresApi::delete_rows(const std::string &table, const std::string &key_column, const json &keys)
{
    if (!is_identifier_safe(table) || !is_identifier_safe(key_column) || !keys.is_array()) {
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

    pqxx::connection conn(conninfo_);
    pqxx::work txn(conn);

    std::string placeholders;
    for (size_t i = 0; i < params.size(); ++i) {
        if (i) placeholders += ", ";
        placeholders += "$" + std::to_string(i + 1);
    }

    std::string sql = "DELETE FROM " + table + " WHERE " + key_column + " IN (" + placeholders + ") RETURNING id";

    pqxx::params pack;
    for (auto &p : params) {
        pack.append(p);
    }

    pqxx::result res = txn.exec_params(sql, pack);
    txn.commit();
    return static_cast<int>(res.size());
}

json PostgresApi::execute_sql(const std::string &sql)
{
    if (sql.empty()) {
        return json{
            {"rows", json::array()},
            {"row_count", 0},
            {"affected_rows", 0},
            {"command_tag", ""},
            {"error", "SQL string is empty"}
        };
    }

    try {
        pqxx::connection conn(conninfo_);
        pqxx::work txn(conn);

        pqxx::result res = txn.exec(sql);
        json payload = result_to_json(res);
        txn.commit();
        return payload;

    } catch (const std::exception &e) {
        spdlog::error("[execute_sql] {}", e.what());
        return json{
            {"rows", json::array()},
            {"row_count", 0},
            {"affected_rows", 0},
            {"command_tag", ""},
            {"error", e.what()}
        };
    }
}
