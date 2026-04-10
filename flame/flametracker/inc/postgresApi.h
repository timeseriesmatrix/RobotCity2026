#pragma once

#include <pqxx/pqxx>
#include <string>
#include <vector>
#include <unordered_map>
#include <nlohmann/json.hpp>

class PostgresApi
{
public:
    struct ShopConnection {
        int         shop_id{};
        std::string name;
        std::string conninfo;
    };

private:
    std::string                         conninfo_;
    std::vector<ShopConnection>         shop_connections_;
    std::unordered_map<int, std::size_t> shop_index_;

public:
    PostgresApi(std::string_view dbname = "flametrack", std::string_view user = "liam", std::string_view pass = "Knight@113078" 
            , std::string_view host = "localhost", std::string_view port = "5432"
            , std::string_view shop_config_path = "shop_databases.json");

    // Create tables in database FlameTrack
    void crt_tbs_ft();

    // Load external shop PostgreSQL connection info from JSON file.
    bool load_shop_connections(const std::string &path);
    const ShopConnection* get_shop_connection(int shop_id) const;
    const std::vector<ShopConnection>& shop_connections() const { return shop_connections_; }

    // Ingest FloreantPOS data (ticket, ticket_item, menu_item) into FlameTrack tables.
    nlohmann::json sync_floreant_shops(const std::vector<int> &shop_ids = {}, bool reset_pos = false);

    // Aggregate sales/order metrics for a shop and time window.
    nlohmann::json shop_summary(int shop_id, const std::string &start_time, const std::string &end_time);
    // Aggregate purchased metrics from flametrack purchase tables by time window.
    nlohmann::json purchased_summary(const std::string &start_time, const std::string &end_time);
    // Overview of flametrack schema for SQL helper UI.
    nlohmann::json db_schema_overview();

    bool is_image_scanned(const std::string &image_path);
    void insert_ocr_scan(const std::string &image_path, const std::string &extracted_text, const std::string &scan_type = ""); 
    
    // Ingest OCR scan JSON into purchase_orders/items. Returns summary stats.
    nlohmann::json ingest_from_ocr_scans(const std::string &product_type = "ingredient",
                                         const std::string &scan_type = "",
                                         const std::string &since = "",
                                         const std::string &end_time = "");
    int  ensure_supplier_exists(const int ocr_id, const nlohmann::json &supplier, pqxx::work &txn);
    int  ensure_product_exists(const int ocr_id, const std::string &name, int supplier_id, pqxx::work &txn, const std::string &product_type = "ingredient"); 
    int  insert_purchase_order(const int ocr_id, const nlohmann::json &order, int supplier_id, pqxx::transaction_base &txn); 
    void insert_purchase_item(const int ocr_id, int purchase_id, int product_id, const nlohmann::json &item, pqxx::work &txn); 

    // Execute arbitrary SQL and return result rows plus meta information.
    nlohmann::json execute_sql(const std::string &sql);

    // Batch update rows for a given table with a key column
    int update_rows(const std::string &table, const std::string &key_column, const nlohmann::json &rows);
    // Delete rows by key values
    int delete_rows(const std::string &table, const std::string &key_column, const nlohmann::json &keys);

    // Fetch image_path from ocr_scans by id
    std::string get_image_path(int ocr_id);
    // Navigate through OCR scans and return a single record
    nlohmann::json fetch_ocr_scan(int current_id, const std::string &direction,
                                  const std::string &start_time = "", const std::string &end_time = "");
    // Update extracted_text for an OCR scan
    bool update_ocr_scan_text(int id, const std::string &text);
    // Delete an OCR scan row and its image file path on disk.
    nlohmann::json delete_ocr_scan(int id);

private:
    nlohmann::json result_to_json(const pqxx::result &res);
    bool is_identifier_safe(const std::string &id);
    
    std::string clean_json(const std::string &raw_text);
    // Normalize OCR dates into ISO YYYY-MM-DD (accepts ISO or common D/M/Y forms).
    std::string fix_date(const std::string &raw_date);
    // Helper: validate that a string is a simple ISO date YYYY-MM-DD with
    // year >= 1900, month 1–12, day 1–31 (enough to catch "1900-01-00").
    bool is_valid_iso_date(const std::string &s);
    int ensure_shop_exists(int shop_id, const std::string &name, pqxx::work &txn);
    int ensure_pos_sync_scan(int shop_id, pqxx::work &txn);
};
