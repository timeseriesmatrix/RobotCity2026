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

#include <spdlog/spdlog.h>
#include <nlohmann/json.hpp>

#include "utilities.hxx"
#include "postgresApi.h"
#include "ocr.h"
#include "openaiSqlAgent.h"

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
    res.set(http::field::access_control_allow_headers, "Content-Type");
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

    // Fetch OCR image by id
    if (req.method() == http::verb::post && target == "/ocr_image") {
        try {
            json body_json = json::parse(req.body());
            int ocr_id = body_json.value("ocr_id", 0);
            if (ocr_id <= 0) {
                auto res = bad_request("ocr_id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            std::string image_path = db.get_image_path(ocr_id);
            if (image_path.empty()) {
                json resp = {{"image_path", ""}, {"image_base64", ""}, {"error", "Not found"}};
                auto res = json_response(resp, http::status::not_found, req.keep_alive(), req.version());
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            std::ifstream file(image_path, std::ios::binary);
            if (!file) {
                json resp = {{"image_path", image_path}, {"image_base64", ""}, {"error", "Cannot open file"}};
                auto res = json_response(resp, http::status::bad_request, req.keep_alive(), req.version());
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }
            std::ostringstream buffer;
            buffer << file.rdbuf();
            std::string bytes = buffer.str();
            std::string b64   = base64_encode(bytes);

            json resp = {{"image_path", image_path}, {"image_base64", b64}, {"error", ""}};
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

    // Serve shop_databases.json for clients (frontend nodes view)
    if (req.method() == http::verb::get && (target == "/shop_databases.json" || target == "/shop_databases")) {
        try {
            json shops = load_shop_config_json("shop_databases.json");
            json body  = {{"shops", shops}};
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
            std::string start = body_json.value("start_time", "");
            std::string end   = body_json.value("end_time", "");

            PostgresApi db;
            json summary = db.purchased_summary(start, end);
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

    if (req.method() == http::verb::get && target == "/db_schema_overview") {
        try {
            PostgresApi db;
            json overview = db.db_schema_overview();
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

    if (req.method() == http::verb::post && target == "/sync_pos_shops") {
        try {
            std::vector<int> shop_ids;
            bool reset_pos = false;
            if (!req.body().empty()) {
                json body_json = json::parse(req.body());
                if (body_json.contains("shop_ids") && body_json["shop_ids"].is_array()) {
                    for (const auto &sid : body_json["shop_ids"]) {
                        if (sid.is_number_integer()) {
                            shop_ids.push_back(sid.get<int>());
                        }
                    }
                }
                if (body_json.contains("reset_pos") && body_json["reset_pos"].is_boolean()) {
                    reset_pos = body_json["reset_pos"].get<bool>();
                }
            }

            PostgresApi db;
            json stats = db.sync_floreant_shops(shop_ids, reset_pos);
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
            std::string table      = body_json.value("table", "");
            std::string key_column = body_json.value("key_column", "id");
            json rows              = body_json.value("rows", json::array());

            if (table.empty() || !rows.is_array()) {
                auto res = bad_request("table and rows[] are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            int updated = db.update_rows(table, key_column, rows);
            json resp = {{"updated", updated}, {"error", ""}};
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
            std::string table      = body_json.value("table", "");
            std::string key_column = body_json.value("key_column", "id");
            json keys              = body_json.value("keys", json::array());

            if (table.empty() || !keys.is_array()) {
                auto res = bad_request("table and keys[] are required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            int deleted = db.delete_rows(table, key_column, keys);
            json resp = {{"deleted", deleted}, {"error", ""}};
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

    // Navigate OCR scans (prev/next/current) and return text + image
    if (req.method() == http::verb::post && target == "/scan_nav") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int current_id = body_json.value("current_id", 0);
            std::string direction = body_json.value("direction", "next");
            std::string start_time = body_json.value("start_time", "");
            std::string end_time   = body_json.value("end_time", "");

            PostgresApi db;
            json scan = db.fetch_ocr_scan(current_id, direction, start_time, end_time);
            if (scan.contains("error") && scan["error"].is_string() && !scan["error"].get<std::string>().empty()) {
                auto res = json_response(scan, http::status::not_found, req.keep_alive(), req.version());
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            std::string image_path = scan.value("image_path", "");
            std::string image_b64;
            std::string error;
            if (!image_path.empty()) {
                std::ifstream file(image_path, std::ios::binary);
                if (file) {
                    std::ostringstream buffer;
                    buffer << file.rdbuf();
                    image_b64 = base64_encode(buffer.str());
                } else {
                    error = "Cannot open image file";
                }
            } else {
                error = "Image path is empty";
            }

            json resp = {{"scan", scan}, {"image_base64", image_b64}, {"error", error}};
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

    // Update extracted_text for a specific OCR scan
    if (req.method() == http::verb::post && target == "/scan_update") {
        try {
            json body_json = json::parse(req.body());
            int id = body_json.value("id", 0);
            std::string text = body_json.value("extracted_text", "");

            if (id <= 0) {
                auto res = bad_request("id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            bool ok = db.update_ocr_scan_text(id, text);
            json resp = {{"id", id}, {"updated", ok}, {"error", ok ? "" : "Update failed"}};

            auto res = json_response(resp, ok ? http::status::ok : http::status::not_found,
                                     req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {{"id", 0}, {"updated", false}, {"error", e.what()}};
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    // Delete an OCR scan row and remove its image file from disk.
    if (req.method() == http::verb::post && target == "/scan_delete") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            int id = body_json.value("id", 0);

            if (id <= 0) {
                auto res = bad_request("id is required");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json resp = db.delete_ocr_scan(id);
            const bool ok = resp.value("deleted", false) && resp.value("error", "").empty();

            auto res = json_response(resp, ok ? http::status::ok : http::status::not_found,
                                     req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        } catch (const std::exception &e) {
            json err = {
                {"id", 0},
                {"deleted", false},
                {"image_path", ""},
                {"file_deleted", false},
                {"file_status", ""},
                {"error", e.what()}
            };
            auto res = json_response(err, http::status::bad_request, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
    }

    // Ingest OCR scans into purchase tables
    if (req.method() == http::verb::post && target == "/ingest_from_ocr") {
        try {
            json body_json = req.body().empty() ? json::object() : json::parse(req.body());
            std::string since        = body_json.value("since", "");
            std::string end_time     = body_json.value("end_time", "");
            std::string scan_type    = body_json.value("scan_type", "");
            std::string product_type = body_json.value("product_type", "ingredient");

            PostgresApi psq_api;
            json result = psq_api.ingest_from_ocr_scans(product_type, scan_type, since, end_time);

            auto res = json_response(result, http::status::ok, req.keep_alive(), req.version());
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

    if (req.method() == http::verb::post && target.starts_with("/gpt_ocr_pdfs")) {
        try {
            json body_json   = req.body().empty() ? json::object() : json::parse(req.body());
            std::string dir  = body_json.value("dir", "/home/liam/Data/wokandflame/ocr");
            const char *api_key = std::getenv("OPENAI_OCR_KEY");

            if (!api_key) {
                json err = {{"error", "Missing OPENAI_OCR_KEY env var."}};
                auto res = json_response(err, http::status::unauthorized, req.keep_alive(), req.version());
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            if (dir.empty()) {
                dir = "/home/liam/Data/wokandflame/ocr";
            }

            Ocr ocr(dir, api_key);
            ocr.gpt_ocr_pdfs();

            json payload = {{"message", "gpt_ocr_pdfs has completed!"}, {"dir", dir}};
            auto res = json_response(payload, http::status::ok, req.keep_alive(), req.version());
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

    // CORS preflight support
    if (req.method() == http::verb::options) {
        http::response<http::string_body> res{http::status::no_content, req.version()};
        res.set(http::field::server, _server_name_);
        res.keep_alive(false);
        res.content_length(0);
        add_cors(res);
        co_return res;
    }

    if (req.method() == http::verb::post && target == "/openai_sql") {
        std::string source = "text";
        std::string model  = "gpt-4o";
        try {
            auto body_json           =  json::parse(req.body());
            std::string input_text   =  body_json.value("input_text", "");
            std::string audio_base64 =  body_json.value("audio_base64", "");
            std::string audio_format =  body_json.value("audio_format", "wav");
            source                   =  audio_base64.empty() ? "text" : "audio";

            if (input_text.empty() && audio_base64.empty()) {
                auto res = bad_request("input_text or audio_base64 must be provided");
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            std::string sql;
            std::string agent_error;

            if (!input_text.empty() && input_text.front() == '!') {
                sql   = input_text.substr(1); // strip leading sentinel
                model = "direct-sql-bang";
            } else {
                OpenAiSqlAgent agent{};
                json sql_result = audio_base64.empty()
                    ? agent.natural_language_to_sql(input_text)
                    : agent.audio_to_sql(audio_base64, audio_format, input_text);

                sql         = sql_result.value("sql", "");
                agent_error = sql_result.value("error", "");
            }

            if (sql.empty() || !agent_error.empty()) {
                json response = {
                    {"sql", sql},
                    {"error", agent_error.empty() ? "SQL generation failed" : agent_error},
                    {"source", source},
                    {"model", model},
                    {"result", json::object()}
                };

                auto res = json_response(response, http::status::ok, req.keep_alive(), req.version());
                add_cors(res);
                res.keep_alive(false);
                co_return res;
            }

            PostgresApi db;
            json exec_result = db.execute_sql(sql);
            std::string db_error = exec_result.value("error", "");

            json response = {
                {"sql", sql},
                {"error", db_error},
                {"source", source},
                {"model", model},
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
                {"source", source},
                {"model", model},
                {"result", json::object()}
            };
            auto res = json_response(err, http::status::ok, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }
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
            /crt_tbs_ft:
            
            /test:
            
            /gpt_ocr_pdfs:
            /ingest_from_ocr;

            /tsr_ocr_pdfs:
            /cvt_pdf_png:

            /ext_txt_img:

            /openai_sql:
                POST body json: {"input_text": "..."} or {"audio_base64": "...", "audio_format": "wav"}
                If input_text starts with '!' it is executed directly as SQL (bang is stripped).
                returns executed SQL result (rows/affected_rows)

            /table_update:
                POST body: {"table": "...", "key_column": "id", "rows": [{"key": ..., "changes": {...}}]}
                applies updates to Postgres

            /ocr_image:
                POST body: {"ocr_id": <int>}
                returns image_path and base64 image content for that OCR scan

            /shop_summary:
                POST body: {"shop_id": <int>, "start_time": "...", "end_time": "..."}
                returns revenue/orders/product rollups for a shop within the window

            /purchased_summary:
                POST body: {"start_time": "...", "end_time": "..."}
                returns purchased summary from flametrack purchase tables

            /db_schema_overview:
                GET returns current database tables/columns/row counts

            /scan_nav:
                POST body: {"current_id": <int>, "direction": "next|prev|current", "start_time": "...", "end_time": "..."}
                returns one ocr_scan row plus base64 image

            /scan_update:
                POST body: {"id": <int>, "extracted_text": "<raw JSON/text>"}
                updates extracted_text for that ocr_scan row

            /scan_delete:
                POST body: {"id": <int>}
                deletes that ocr_scan row and removes its image file
        )";

        co_return get_response(msg);
    
    } else if (target == "/test") {
        ScopedTimer timer("test");
         
        co_return get_response("test running has completed!");
    
    } else if (target == "/ingest_from_ocr") {

        PostgresApi psq_api;
        json result = psq_api.ingest_from_ocr_scans();
        
        auto res = json_response(result);
        add_cors(res);
        res.keep_alive(false);
        co_return res;

    } else if (target.starts_with("/gpt_ocr_pdfs")) {

        const char *api_key = std::getenv("OPENAI_OCR_KEY");
        if (!api_key) {
            json err = {{"error", "Missing OPENAI_OCR_KEY env var."}};
            auto res = json_response(err, http::status::unauthorized, req.keep_alive(), req.version());
            add_cors(res);
            res.keep_alive(false);
            co_return res;
        }

        std::string dir = "/home/liam/Data/wokandflame/ocr";
        auto parsed = urls::parse_uri_reference(std::string(target));
        if (parsed) {
            for (auto const &param : parsed->params()) {
                if (param.key == "dir" && param.has_value) {
                    dir = std::string(param.value);
                    break;
                }
            }
        }

        if (dir.empty()) {
            dir = "/home/liam/Data/wokandflame/ocr";
        }

        try {
            Ocr ocr(dir, api_key);
            ocr.gpt_ocr_pdfs();
            
            json payload = {{"message", "gpt_ocr_pdfs has completed!"}, {"dir", dir}};
            auto res = json_response(payload, http::status::ok, req.keep_alive(), req.version());
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

    } else if (target == "/tsr_ocr_pdfs") {
        
        Ocr ocr("/home/liam/Data/wokandflame/ocr");
        ocr.tsr_ocr_pdfs();

        co_return get_response("ocr_pdfs has completed!");

    } else if (target == "/crt_tbs_ft") {

        PostgresApi psq_api;
        psq_api.crt_tbs_ft();

        co_return get_response("Creating tables in database flametrack has completed!");

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
