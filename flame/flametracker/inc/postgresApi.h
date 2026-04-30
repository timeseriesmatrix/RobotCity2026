#pragma once

#include <pqxx/pqxx>
#include <string>
#include <vector>
#include <unordered_map>
#include <nlohmann/json.hpp>

class PostgresApi
{
public:
    enum class SourceKind {
        Pos,
        Expense
    };

    struct DbSource {
        std::string host;
        std::string port;
        std::string dbname;
        std::string user;
        std::string password;
        std::string user_env;
        std::string pass_env;
        std::string conninfo;
    };

    struct ShopConnection {
        int         shop_id{};
        std::string name;
        std::string description;
        std::string timezone;
        std::vector<std::string> categories;
        DbSource    pos;
        DbSource    expense;
        std::string host;
        std::string port;
        std::string dbname;
        std::string user;
        std::string conninfo;
    };

private:
    std::vector<ShopConnection>         shop_connections_;
    std::unordered_map<int, std::size_t> shop_index_;
    int default_shop_id_{0};
    std::string shop_config_path_;

public:
    explicit PostgresApi(std::string_view shop_config_path = "shop_databases.json");

    // Load external shop PostgreSQL connection info from JSON file.
    bool load_shop_connections(const std::string &path);
    const ShopConnection* get_shop_connection(int shop_id) const;
    const DbSource* get_shop_source(int shop_id, SourceKind kind) const;
    const std::vector<ShopConnection>& shop_connections() const { return shop_connections_; }
    int default_shop_id() const { return default_shop_id_; }
    nlohmann::json shop_connections_json() const;
    nlohmann::json editable_shop_connections_json() const;
    nlohmann::json users_json(bool include_passwords = false) const;
    nlohmann::json settings_state_json() const;
    nlohmann::json save_settings(const nlohmann::json &shops, const nlohmann::json &users, int default_shop_id = 0);
    nlohmann::json settings_test_connection(const nlohmann::json &source, SourceKind kind) const;
    nlohmann::json settings_init_expense_db(const nlohmann::json &source) const;

    // Create the tracker schema/tables inside each selected shop expense DB.
    nlohmann::json init_expense_tracker_schemas(const std::vector<int> &shop_ids = {});

    // Aggregate sales/order metrics for a shop and time window.
    nlohmann::json shop_summary(int shop_id, const std::string &start_time, const std::string &end_time);
    // Aggregate purchased metrics from a shop expense tracker DB by time window.
    nlohmann::json purchased_summary(int shop_id, const std::string &start_time, const std::string &end_time);
    // Overview of a shop source schema for SQL helper UI.
    nlohmann::json db_schema_overview(int shop_id, SourceKind kind);

    nlohmann::json receipt_upload(int shop_id,
                                  const std::string &file_name,
                                  const std::string &mime_type,
                                  const std::string &content_base64);
    nlohmann::json receipt_queue(int shop_id, const std::string &ocr_status = "", int limit = 50);
    nlohmann::json receipt_detail(int shop_id, int ocr_id);
    nlohmann::json receipt_page_image(int shop_id, int page_id);
    nlohmann::json receipt_run_ocr(int shop_id, int ocr_id, const std::string &openai_api_key);
    nlohmann::json receipt_reprocess(int shop_id, int ocr_id, const std::string &openai_api_key);
    nlohmann::json receipt_reopen(int shop_id,
                                  int ocr_id,
                                  const std::string &reopened_by = "",
                                  const std::string &review_note = "");
    nlohmann::json receipt_delete(int shop_id, int ocr_id);
    nlohmann::json receipt_delete_page(int shop_id, int ocr_id, int page_id);
    nlohmann::json save_receipt_drafts(int shop_id,
                                       int ocr_id,
                                       const nlohmann::json &drafts,
                                       const std::string &review_note = "",
                                       const std::string &reviewed_by = "");
    nlohmann::json receipt_approve(int shop_id,
                                   int ocr_id,
                                   const std::string &approved_by = "",
                                   const std::string &review_note = "");
    nlohmann::json receipt_post(int shop_id,
                                int ocr_id,
                                const std::string &posted_by = "");
    int  ensure_supplier_exists(int shop_id, const int ocr_id, int ocr_page_id, const nlohmann::json &supplier, pqxx::work &txn);
    int  ensure_product_exists(int shop_id, const int ocr_id, int ocr_page_id, const std::string &name, int supplier_id, pqxx::work &txn, const std::string &product_type = "ingredient", const std::string &category = "Others"); 
    int  insert_purchase_order(int shop_id, const int ocr_id, int ocr_page_id, const nlohmann::json &order, int supplier_id, pqxx::transaction_base &txn); 
    void insert_purchase_item(int shop_id, const int ocr_id, int ocr_page_id, int purchase_id, int product_id, const nlohmann::json &item, pqxx::work &txn); 

    // Execute arbitrary SQL and return result rows plus meta information.
    nlohmann::json execute_sql(int shop_id, SourceKind kind, const std::string &sql);

    // Batch update rows for a given table with a key column
    int update_rows(int shop_id, SourceKind kind, const std::string &table, const std::string &key_column, const nlohmann::json &rows);
    // Delete rows by key values
    int delete_rows(int shop_id, SourceKind kind, const std::string &table, const std::string &key_column, const nlohmann::json &keys);

    // Fetch image_path from ocr_scans by id
    std::string get_image_path(int shop_id, int ocr_id);

private:
    void execute_receipt_ocr_job(int shop_id, int ocr_id, const std::string &openai_api_key, long long job_id);
    pqxx::connection open_shop_connection(int shop_id, SourceKind kind, bool write = false) const;
    void ensure_expense_tracker_schema(pqxx::transaction_base &txn) const;
    void require_write_allowed(const std::string &conninfo, SourceKind kind, std::string_view context) const;
    bool is_read_only_sql(const std::string &sql) const;
    nlohmann::json result_to_json(const pqxx::result &res);
    bool is_identifier_safe(const std::string &id);
    bool is_table_name_safe(const std::string &name) const;
    
    std::string clean_json(const std::string &raw_text);
    std::vector<std::string> category_options_for_shop(int shop_id) const;
    nlohmann::json sync_shop_categories_after_settings_save(int shop_id,
                                                            const std::vector<std::string> &previous_categories,
                                                            const std::vector<std::string> &next_categories,
                                                            const std::string &openai_api_key);
    nlohmann::json backfill_other_product_categories_with_ai(int shop_id,
                                                             const std::vector<std::string> &allowed_categories,
                                                             const std::string &openai_api_key);
    // Normalize OCR dates into ISO YYYY-MM-DD (accepts ISO or common D/M/Y forms).
    std::string fix_date(const std::string &raw_date);
    // Helper: validate that a string is a simple ISO date YYYY-MM-DD with
    // year >= 1900, month 1–12, day 1–31 (enough to catch "1900-01-00").
    bool is_valid_iso_date(const std::string &s);
};
