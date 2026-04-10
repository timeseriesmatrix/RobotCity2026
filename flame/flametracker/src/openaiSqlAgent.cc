#include "openaiSqlAgent.h"

#include <boost/beast/core.hpp>
#include <boost/beast/http.hpp>
#include <boost/beast/version.hpp>
#include <boost/asio/connect.hpp>
#include <boost/asio/ip/tcp.hpp>
#include <boost/asio/ssl.hpp>

#include <spdlog/spdlog.h>

#include <cstdlib>
#include <stdexcept>

namespace beast = boost::beast;
namespace http  = beast::http;
namespace net   = boost::asio;
using tcp       = net::ip::tcp;
using json      = nlohmann::json;

OpenAiSqlAgent::OpenAiSqlAgent(std::string api_key) : api_key_(std::move(api_key))
{
    if (api_key_.empty()) {
        if (const char *env = std::getenv("OPENAI_OCR_KEY")) {
            api_key_ = env;
        }
    }

    has_key_ = !api_key_.empty();
    if (!has_key_) {
        spdlog::error("[OpenAiSqlAgent] Missing OPENAI_API_KEY for SQL generation.");
    }
}

json OpenAiSqlAgent::base_messages() const
{
    static const std::string system_prompt = R"(
You are a PostgreSQL assistant for the flametrack database. Convert a single user request
into exactly one SQL statement or return an error. Database tables you can use:
- shops(id, name, pos_info, contact_info, location)
- suppliers(id, shop_id, tin, ocr_id, name, contact_info, site)
- products(id, shop_id, sku, ocr_id, name, product_type, class_name, type_name, supplier_id, default_unit_price, is_active, stock_qty)
- purchase_orders(id, shop_id, invoice_id, ocr_id, supplier_id, purchase_date, total_cost, notes)
- purchase_items(id, shop_id, purchase_id, product_id, ocr_id, quantity, unit_price, total_price)
- customer_orders(id, shop_id, order_time, total_price, payment_method, order_type)
- order_items(id, shop_id, order_id, product_id, quantity, unit_price, total_price)
- ocr_scans(id, shop_id, image_path, scan_type, extracted_text, scanned_at)

Rules:
- shop_id columns default to 0 (an unassigned shop) and reference shops(id).
- Always return a JSON object with fields: {"sql": "<statement or empty>", "error": "<message or empty>"}.
- If the request is ambiguous or unsafe, leave "sql" empty and explain in "error".
- Use PostgreSQL syntax. Avoid multiple statements; produce one clear command.
- Only use existing columns. Prefer SELECT unless the user clearly asks to write.
)";

    json messages = json::array();
    messages.push_back({
        {"role", "system"},
        {"content", system_prompt}
    });
    return messages;
}

std::string OpenAiSqlAgent::chat_completion(const json &body)
{
    net::io_context ioc;
    net::ssl::context ctx(net::ssl::context::tls_client);
    ctx.set_default_verify_paths();
    ctx.set_verify_mode(net::ssl::verify_peer);

    tcp::resolver resolver(ioc);
    net::ssl::stream<beast::tcp_stream> stream(ioc, ctx);
    auto results = resolver.resolve(host_, port_);
    beast::get_lowest_layer(stream).connect(results);

    if (!SSL_set_tlsext_host_name(stream.native_handle(), host_.c_str())) {
        throw std::runtime_error("Failed to set SNI for OpenAI request");
    }

    stream.handshake(net::ssl::stream_base::client);

    http::request<http::string_body> req{http::verb::post, "/v1/chat/completions", 11};
    req.set(http::field::host, host_);
    req.set(http::field::authorization, "Bearer " + api_key_);
    req.set(http::field::user_agent, BOOST_BEAST_VERSION_STRING);
    req.set(http::field::content_type, "application/json");
    req.body() = body.dump();
    req.prepare_payload();

    http::write(stream, req);

    beast::flat_buffer buffer;
    http::response<http::string_body> res;
    http::read(stream, buffer, res);

    beast::error_code ec;
    stream.shutdown(ec);
    if (ec && ec != net::error::eof && ec != net::ssl::error::stream_truncated) {
        throw beast::system_error{ec};
    }

    if (res.result_int() >= 400) {
        spdlog::error("[OpenAiSqlAgent] HTTP {} {}", res.result_int(), res.reason());
        throw std::runtime_error("OpenAI request failed: " + std::to_string(res.result_int()));
    }

    return res.body();
}

json OpenAiSqlAgent::natural_language_to_sql(const std::string &input_text)
{
    if (input_text.empty()) return json{{"sql", ""}, {"error", "input_text is empty"}};
    if (!has_key_)          return json{{"sql", ""}, {"error", "OPENAI_API_KEY is not set"}};

    json messages = base_messages();
    messages.push_back({
        {"role", "user"},
        {"content", json::array({
            {{"type", "text"}, {"text", input_text}}
        })}
    });

    json req_body = {
        {"model", model_},
        {"messages", messages},
        {"temperature", 0.2},
        {"max_tokens", 512},
        {"response_format", {{"type", "json_object"}}}
    };

    try {
        auto raw = chat_completion(req_body);
        auto parsed = json::parse(raw);
        std::string content = parsed["choices"][0]["message"]["content"];

        try {
            return json::parse(content);
        } catch (const std::exception &e) {
            spdlog::error("[OpenAiSqlAgent] Failed to parse content as JSON: {}", e.what());
            return json{{"sql", ""}, {"error", "Model returned non-JSON response"}};
        }
    } catch (const std::exception &e) {
        spdlog::error("[OpenAiSqlAgent] {}", e.what());
        return json{{"sql", ""}, {"error", e.what()}};
    }
}

json OpenAiSqlAgent::audio_to_sql(const std::string &audio_base64, const std::string &format,
                                  const std::string &hint_text)
{
    if (audio_base64.empty()) return json{{"sql", ""}, {"error", "audio_base64 is empty"}};
    if (!has_key_)            return json{{"sql", ""}, {"error", "OPENAI_API_KEY is not set"}};

    json messages = base_messages();
    json user_content = json::array();

    if (!hint_text.empty()) {
        user_content.push_back({{"type", "text"}, {"text", hint_text}});
    }

    user_content.push_back({
        {"type", "input_audio"},
        {"input_audio", {
            {"data", audio_base64},
            {"format", format.empty() ? "wav" : format}
        }}
    });

    messages.push_back({{"role", "user"}, {"content", user_content}});

    json req_body = {
        {"model", model_},
        {"messages", messages},
        {"temperature", 0.2},
        {"max_tokens", 512},
        {"response_format", {{"type", "json_object"}}}
    };

    try {
        auto raw = chat_completion(req_body);
        auto parsed = json::parse(raw);
        std::string content = parsed["choices"][0]["message"]["content"];

        try {
            return json::parse(content);
        } catch (const std::exception &e) {
            spdlog::error("[OpenAiSqlAgent] Failed to parse content as JSON: {}", e.what());
            return json{{"sql", ""}, {"error", "Model returned non-JSON response"}};
        }
    } catch (const std::exception &e) {
        spdlog::error("[OpenAiSqlAgent] {}", e.what());
        return json{{"sql", ""}, {"error", e.what()}};
    }
}
