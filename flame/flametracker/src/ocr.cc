#include "ocr.h"
#include "utilities.hxx"

#include <poppler-document.h>
#include <poppler-page.h>
#include <cairo.h>
#include <cairo-pdf.h>
#include <cairo-svg.h>
#include <poppler-page-renderer.h>
#include <tesseract/baseapi.h>
#include <leptonica/allheaders.h>

#include <boost/beast/core.hpp>
#include <boost/beast/http.hpp>
#include <boost/beast/version.hpp>
#include <boost/asio/connect.hpp>
#include <boost/asio/ip/tcp.hpp>
#include <boost/asio/ssl.hpp>
#include <nlohmann/json.hpp>
#include <spdlog/spdlog.h>

#include <sstream>
#include <fstream>
#include <filesystem>
#include <cstdlib>
#include <thread>
#include <chrono>
// === namespaces ===
namespace fs = std::filesystem;
namespace beast = boost::beast;
namespace http  = beast::http;
namespace net   = boost::asio;
using tcp       = net::ip::tcp;
using json      = nlohmann::json;

// === Improved Receipt OCR and OpenAI GPT-4o Integration ===

namespace {
std::string trim_copy(std::string s)
{
    const auto first = s.find_first_not_of(" \t\n\r");
    if (first == std::string::npos) return {};
    const auto last = s.find_last_not_of(" \t\n\r");
    return s.substr(first, last - first + 1);
}

bool is_regular_file_path(const fs::path &path)
{
    if (path.empty()) return false;
    std::error_code ec;
    return fs::exists(path, ec) && fs::is_regular_file(path, ec);
}

fs::path absolute_normalized_path(const fs::path &path)
{
    std::error_code ec;
    const auto absolute = fs::absolute(path, ec);
    return ec ? path.lexically_normal() : absolute.lexically_normal();
}

fs::path executable_dir_path()
{
    std::error_code ec;
    const auto exe_path = fs::read_symlink("/proc/self/exe", ec);
    if (ec || exe_path.empty()) {
        return {};
    }
    return absolute_normalized_path(exe_path).parent_path();
}

fs::path search_ancestor_paths_for_file(fs::path start, const fs::path &relative)
{
    if (relative.empty()) return {};

    start = absolute_normalized_path(start);
    std::error_code ec;

    while (!start.empty()) {
        const auto candidate = start / relative;
        if (is_regular_file_path(candidate)) {
            return absolute_normalized_path(candidate);
        }

        const auto parent = start.parent_path();
        if (parent.empty() || parent == start) {
            break;
        }
        start = parent;
    }

    return {};
}

std::string read_text_file(const fs::path &path)
{
    std::ifstream in(path);
    if (!in.is_open()) {
        throw std::runtime_error("Failed to open prompt template: " + path.string());
    }
    std::ostringstream buffer;
    buffer << in.rdbuf();
    return buffer.str();
}

std::string default_receipt_prompt_template()
{
    return
        "You extract structured purchase receipt data for the Flame ERP expense tracker.\n\n"
        "Target database and table mapping:\n"
        "- `tracker.ocr_scans`: raw OCR scan record. Keep your output faithful to the visible receipt input.\n"
        "- `tracker.purchase_drafts`: one staged purchase document per detected receipt.\n"
        "- `tracker.purchase_draft_items`: staged line items for each detected receipt.\n"
        "- `tracker.suppliers`: canonical supplier master data. The dynamic historical context primarily comes from this table. `supplier.name`, `supplier.tin`, and `supplier.site` should be normalized so they can later populate this table.\n"
        "- `tracker.products`: canonical product master data. The dynamic historical context primarily comes from this table. Use stable item names and stable product categories that match known shop product history when the image strongly supports that correction.\n"
        "- `tracker.purchase_orders`: final approved purchase header. Map from `purchase_order.invoice_id`, `purchase_order.purchase_date`, `purchase_order.subtotal_amount`, `purchase_order.tax_amount`, `purchase_order.discount_amount`, `purchase_order.rounding_amount`, `purchase_order.grand_total`, and `purchase_order.line_total_basis`.\n"
        "- `tracker.purchase_items`: final approved purchase lines. Map from each `purchase_items[]` entry.\n\n"
        "Core extraction task:\n"
        "- Return one JSON object for this receipt input.\n"
        "- This workflow guarantees exactly one logical receipt per input image.\n"
        "- The input may be a normal single-page receipt image or a vertically stitched long image made from several receipt pages/photos.\n"
        "- If the image is vertically stitched, read it from top to bottom as one receipt and merge all visible sections into one receipt object.\n"
        "- The returned receipt object must contain `supplier`, `purchase_order`, and `purchase_items`.\n"
        "- You may also include optional `warnings` on the receipt object and optional `warnings` on individual purchase items.\n"
        "- Warnings should explain uncertainty, historical mismatches, OCR corrections, or values that differ from this shop's past records.\n\n"
        "Inference rules:\n"
        "- Use visible text first.\n"
        "- Use the appended supplier/product references and optional shop description only as conservative hints when text is degraded, truncated, blurry, or partially hidden.\n"
        "- If a supplier or item is clearly the same as a historical record except for a small OCR drift, normalize it to the historical value and add a warning describing that correction.\n"
        "- If the receipt clearly shows a new or different supplier/item, preserve what is visible and add a warning rather than forcing a historical match.\n"
        "- If the printed purchase date is missing, blurred, or incomplete, you may use the source filename date from the shop context as a strong fallback hint.\n"
        "- If the source filename contains a date plus suffix, such as `2026-03-02_a.pdf`, `2026-03-02-b.pdf`, or `2026-03-02_1.jpg`, infer the receipt date from the date part only. Treat suffixes like `_a`, `_b`, `-1`, or page/bundle labels as receipt bundle identifiers, not date information.\n"
        "- If the receipt clearly shows a different date from the source filename date, keep the visible receipt date and add a warning about the conflict.\n"
        "- Do not invent suppliers, items, TINs, dates, or prices that are not supported by the image and surrounding context.\n\n"
        "Confidence and missing data rules:\n"
        "- If a field cannot be confidently read or conservatively inferred, use an empty string `\"\"` for text and `0` for numeric values.\n"
        "- Never emit placeholder text like `Unknown`, `N/A`, or `Not available`.\n\n"
        "Supplier fields:\n"
        "- `supplier.name`\n"
        "- `supplier.tin`\n"
        "- `supplier.site`\n"
        "- `supplier.contact_info`\n\n"
        "TIN rules:\n"
        "- `supplier.tin` must be digits only.\n"
        "- If no reliable TIN exists, output `\"\"`.\n\n"
        "Purchase order fields:\n"
        "- `purchase_order.invoice_id`\n"
        "- `purchase_order.purchase_date`\n"
        "- `purchase_order.subtotal_amount`\n"
        "- `purchase_order.tax_amount`\n"
        "- `purchase_order.discount_amount`\n"
        "- `purchase_order.rounding_amount`\n"
        "- `purchase_order.grand_total`\n"
        "- `purchase_order.line_total_basis` with one of `inclusive`, `exclusive`, or `unknown`\n\n"
        "Purchase item fields:\n"
        "- `name`\n"
        "- `category`\n"
        "- `quantity`\n"
        "- `unit_price`\n"
        "- Optional `line_discount_percent`\n"
        "- Optional `line_discount_amount`\n"
        "- Optional `line_subtotal_amount`\n"
        "- Optional `line_tax_amount`\n"
        "- `total_price`\n"
        "- Optional `warnings`\n\n"
        "Non-itemized service-bill rules:\n"
        "- Some receipts are utility or service invoices rather than merchandise receipts.\n"
        "- If the document shows no itemized purchase rows but clearly shows one current invoice charge, still return one synthesized `purchase_items[]` entry summarizing that bill.\n"
        "- Use a visible service label when possible, such as `Electricity bill`, `Water bill`, `Telecom bill`, `Internet bill`, `Rent`, or `Service charge`.\n"
        "- For synthesized summary items, set `quantity = 1`, `unit_price = grand_total`, and `total_price = grand_total`.\n"
        "- If the bill shows both a current invoice amount and older balances such as `previous balance`, `other invoices payable`, `arrears`, or `amount due`, use only the current invoice/current period charge as this receipt's expense.\n"
        "- Do not use carried-forward balances or cumulative amount due as `grand_total` for this receipt.\n"
        "- If an older balance or cumulative amount due is visible, mention that in `warnings` instead of adding it to the expense.\n"
        "- For utility/service bills, use the invoice issue date or tax invoice date as `purchase_order.purchase_date`, not the due date and not the service-period label.\n\n"
        "Tabular receipt rules:\n"
        "- If the receipt is printed as a table or grid, identify the item-name, quantity, unit-price, and amount columns first, then read each item row horizontally from left to right.\n"
        "- Keep all values from the same printed row together. Do not attach a quantity, unit price, amount, spec, or pack size from an adjacent row.\n"
        "- In Chinese or POS-style sales tables, the last money column is often the line amount, while a separate quantity column may appear earlier in the row. Treat each printed row as one record and keep the rightmost visible money amount on that same row with that row's item only.\n"
        "- If a spec, pack size, Chinese suffix, or barcode fragment appears on the same row as the item name, keep it with that item row. Do not let that extra text shift the quantity or amount onto the next row.\n"
        "- Summary rows such as subtotal, total, grand total, 小计, 合计, tax, discount, and rounding are header/footer totals, not purchase items.\n"
        "- If the table shows quantity and line amount but does not show a printed unit-price column, compute `unit_price` from the same row only.\n"
        "- If the receipt shows a printed line unit price before discount, keep that printed price in `unit_price` exactly as shown.\n"
        "- If the receipt shows a per-line discount column, extract it into `line_discount_percent` or `line_discount_amount` instead of silently baking that discount into `unit_price`.\n"
        "- If the receipt shows a pre-tax line subtotal column such as `Price ex Tax`, extract that value into `line_subtotal_amount`.\n"
        "- If the receipt shows a per-line tax column, extract that value into `line_tax_amount`.\n"
        "- When discount or tax columns exist, do not force `unit_price × quantity` to equal `total_price`; keep the printed line columns separately.\n"
        "- Never use subtotal, 合计, 小计, tax, or grand total values as any item's `unit_price` or `total_price`.\n"
        "- Before returning JSON, verify that each item's quantity/price/amount still matches the same printed row as the item name.\n"
        "- If a row is partially unreadable, preserve only the values you can align to that same row confidently and add a warning.\n\n"
        "Numeric rules:\n"
        "- `quantity`, `unit_price`, `line_discount_percent`, `line_discount_amount`, `line_subtotal_amount`, `line_tax_amount`, `total_price`, `subtotal_amount`, `tax_amount`, `discount_amount`, `rounding_amount`, and `grand_total` must be plain numbers only.\n"
        "- No formulas, units, or text in numeric fields.\n"
        "- Determine numeric separator usage from the printed receipt before converting values to JSON numbers.\n"
        "- Never mistake a thousands separator for a decimal point in money fields.\n"
        "- Receipts may mix decimal quantities such as `0.64` with whole-currency money amounts such as `822`, `8,240`, `1,286`, or `12,044`.\n"
        "- If the printed money amount is `12,044`, output `12044`.\n"
        "- If the printed money amount is `8,240`, output `8240`.\n"
        "- If the printed money amount is `1,286`, output `1286`.\n"
        "- If the printed amount is `11,64`, output `11.64`.\n"
        "- If the printed quantity is `0.64`, output `0.64`.\n"
        "- If the printed discount is `3%`, output `line_discount_percent: 3`.\n"
        "- Prefer the separator interpretation that makes the receipt internally consistent across line totals, subtotal, tax, and grand total.\n"
        "- If two interpretations are still plausible, keep the one best supported by the printed receipt and add a warning.\n"
        "- `total_price` should match the receipt exactly whenever visible.\n"
        "- If `quantity` and `total_price` are clearly known, you may compute `unit_price = total_price / quantity`.\n"
        "- Do not infer pack-size math unless the receipt itself supports it.\n\n"
        "Tax and total rules:\n"
        "- Use `grand_total` for the final payable amount on the receipt.\n"
        "- For utility/service bills that show both a current invoice amount and a cumulative balance due, `grand_total` must be the current invoice amount for this billing period only.\n"
        "- Use `subtotal_amount` for the merchandise subtotal before tax when the receipt shows it.\n"
        "- Use `tax_amount`, `discount_amount`, and `rounding_amount` only when they are shown or strongly implied by printed totals.\n"
        "- If line totals already include tax, set `line_total_basis` to `inclusive`.\n"
        "- If line totals sum to the subtotal before tax, set `line_total_basis` to `exclusive`.\n"
        "- If you cannot determine whether line totals are tax-inclusive or tax-exclusive, set `line_total_basis` to `unknown`.\n\n"
        "Category rules:\n"
        "- `category` must be exactly one value from the allowed product categories provided in the shop context.\n"
        "- Reuse allowed historical category names exactly when the item clearly matches known shop product history.\n"
        "- Keep category naming consistent across receipts for the same kind of product.\n"
        "- If no allowed category can be confidently inferred, output `Others`.\n\n"
        "Formatting rules:\n"
        "- Dates must be ISO `YYYY-MM-DD`.\n"
        "- Output strict RFC 8259 JSON only.\n"
        "- No comments, no markdown fences, no extra narration.\n"
        "- No trailing commas.\n\n"
        "{{SHOP_CONTEXT_BLOCK}}"
        "Return only the JSON value.\n";
}

fs::path resolve_receipt_prompt_template_path()
{
    if (const char *env_path = std::getenv("FLAME_RECEIPT_PROMPT_TEMPLATE_PATH")) {
        const auto trimmed = trim_copy(env_path);
        if (!trimmed.empty()) {
            const fs::path env_candidate(trimmed);
            if (env_candidate.is_absolute()) {
                if (is_regular_file_path(env_candidate)) {
                    return absolute_normalized_path(env_candidate);
                }
            } else {
                if (is_regular_file_path(env_candidate)) {
                    return absolute_normalized_path(env_candidate);
                }
                if (const auto from_cwd = search_ancestor_paths_for_file(fs::current_path(), env_candidate); !from_cwd.empty()) {
                    return from_cwd;
                }
                if (const auto exe_dir = executable_dir_path(); !exe_dir.empty()) {
                    if (const auto from_exe = search_ancestor_paths_for_file(exe_dir, env_candidate); !from_exe.empty()) {
                        return from_exe;
                    }
                }
            }
        }
    }

    const fs::path relative("prompts/receipt_vision_system_prompt.txt");
    if (is_regular_file_path(relative)) {
        return absolute_normalized_path(relative);
    }
    if (const auto from_cwd = search_ancestor_paths_for_file(fs::current_path(), relative); !from_cwd.empty()) {
        return from_cwd;
    }
    if (const auto exe_dir = executable_dir_path(); !exe_dir.empty()) {
        if (const auto from_exe = search_ancestor_paths_for_file(exe_dir, relative); !from_exe.empty()) {
            return from_exe;
        }
    }
    return {};
}

std::string replace_all(std::string text, const std::string &needle, const std::string &replacement)
{
    if (needle.empty()) return text;
    std::size_t pos = 0;
    while ((pos = text.find(needle, pos)) != std::string::npos) {
        text.replace(pos, needle.size(), replacement);
        pos += replacement.size();
    }
    return text;
}

std::string build_shop_context_block(const std::string &prompt_context)
{
    const std::string trimmed = trim_copy(prompt_context);
    if (trimmed.empty()) return {};
    return "Shop-specific historical reference context:\n" + trimmed + "\n\n";
}

std::string load_receipt_prompt_template()
{
    static bool missing_template_warned = false;
    static bool loaded_template_logged = false;

    const auto path = resolve_receipt_prompt_template_path();
    if (path.empty()) {
        if (!missing_template_warned) {
            spdlog::warn("[Ocr] receipt prompt template not found; using built-in fallback");
            missing_template_warned = true;
        }
        return default_receipt_prompt_template();
    }

    if (!loaded_template_logged) {
        spdlog::info("[Ocr] Using receipt prompt template '{}'", path.string());
        loaded_template_logged = true;
    }
    return read_text_file(path);
}

std::string build_receipt_vision_system_prompt(const std::string &prompt_context)
{
    std::string prompt = load_receipt_prompt_template();
    const std::string shop_context_block = build_shop_context_block(prompt_context);
    const std::string marker = "{{SHOP_CONTEXT_BLOCK}}";
    if (prompt.find(marker) != std::string::npos) {
        return replace_all(std::move(prompt), marker, shop_context_block);
    }

    if (!shop_context_block.empty()) {
        const std::string trimmed = trim_copy(prompt);
        if (!trimmed.empty()) {
            prompt += "\n\n";
        }
        prompt += shop_context_block;
    }
    return prompt;
}

std::string post_openai_chat_completion(const std::string &openai_key,
                                        const json &req_body,
                                        bool content_only)
{
    const std::string host = "api.openai.com";
    const std::string port = "443";
    const std::string target = "/v1/chat/completions";

    for (int attempt = 1; attempt <= 3; ++attempt) {
        try {
            net::io_context ioc;
            net::ssl::context ctx(net::ssl::context::tls_client);
            ctx.set_default_verify_paths();
            ctx.set_verify_mode(net::ssl::verify_peer);

            tcp::resolver resolver(ioc);
            net::ssl::stream<beast::tcp_stream> stream(ioc, ctx);
            auto results = resolver.resolve(host, port);
            beast::get_lowest_layer(stream).connect(results);

            if (!SSL_set_tlsext_host_name(stream.native_handle(), host.c_str())) {
                throw std::runtime_error("SNI setup failed");
            }

            stream.handshake(net::ssl::stream_base::client);

            std::string body = req_body.dump();
            http::request<http::string_body> req(http::verb::post, target, 11);
            req.set(http::field::host, host);
            req.set(http::field::authorization, "Bearer " + openai_key);
            req.set(http::field::content_type, "application/json");
            req.set(http::field::user_agent, BOOST_BEAST_VERSION_STRING);
            req.body() = body;
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
                json err_json;
                try {
                    err_json = json::parse(res.body());
                    spdlog::error("OpenAI error: {}", err_json.dump(2));
                } catch (...) {
                    spdlog::error("HTTP {}: {}", res.result_int(), res.body());
                }
                if (res.result_int() == 500 && attempt < 3) {
                    std::this_thread::sleep_for(std::chrono::seconds(1));
                    continue;
                }
            }

            if (!content_only) {
                return res.body();
            }

            const json response_json = json::parse(res.body());
            if (!response_json.contains("choices") ||
                !response_json["choices"].is_array() ||
                response_json["choices"].empty() ||
                !response_json["choices"][0].contains("message") ||
                !response_json["choices"][0]["message"].is_object() ||
                !response_json["choices"][0]["message"].contains("content")) {
                throw std::runtime_error("OpenAI response did not contain message content");
            }
            return response_json["choices"][0]["message"]["content"].get<std::string>();
        } catch (const std::exception &e) {
            spdlog::error("OpenAI request error: {}", e.what());
            if (attempt < 3) {
                std::this_thread::sleep_for(std::chrono::seconds(1));
            }
        }
    }

    throw std::runtime_error("OpenAI request failed after 3 attempts.");
}
} // namespace

std::string Ocr::send_receipt_to_openai(const std::string &base64_image,
                                        const std::string &prompt_context,
                                        const std::string &line_items_focus_base64) 
{
    json user_content = json::array({
        {
            {"type", "text"},
            {"text",
             line_items_focus_base64.empty()
                 ? "Parse this receipt image. There is exactly one logical receipt in this image. The image may be a normal single-page receipt or a vertically stitched long image made from several pages/photos. Read it top to bottom as one receipt and return one receipt object only. Return strict JSON only."
                 : "Parse this receipt image set. There is exactly one logical receipt in this input. Image 1 is the full receipt page for overall context, header fields, and totals. Image 2 is a focused crop of the line-item table and should be used to align item rows, quantities, unit prices, and amounts. Keep values from the same printed row together. Return one receipt object only. Return strict JSON only."}
        },
        {
            {"type", "image_url"},
            {"image_url", {{"url", "data:image/png;base64," + base64_image}, {"detail", "high"}}}
        }
    });
    if (!line_items_focus_base64.empty()) {
        user_content.push_back({
            {"type", "image_url"},
            {"image_url", {{"url", "data:image/png;base64," + line_items_focus_base64}, {"detail", "high"}}}
        });
    }

    const json req_body = {
        {"model", "gpt-4o"},
        {"max_tokens", 2024},
        {"messages", json::array({
            {
                {"role", "system"},
                {"content", build_receipt_vision_system_prompt(prompt_context)}
            },
            {
                {"role", "user"},
                {"content", user_content}
            }
        })}
    };
    return post_openai_chat_completion(openai_key_, req_body, false);
}

std::string Ocr::send_structured_prompt_to_openai(const std::string &system_prompt,
                                                  const std::string &user_prompt,
                                                  int max_tokens,
                                                  const std::string &model)
{
    const json req_body = {
        {"model", model},
        {"max_tokens", max_tokens},
        {"messages", json::array({
            {
                {"role", "system"},
                {"content", system_prompt}
            },
            {
                {"role", "user"},
                {"content", user_prompt}
            }
        })}
    };
    return post_openai_chat_completion(openai_key_, req_body, true);
}

void Ocr::tsr_ocr_pdfs()
{
    fs::path input_dir(source_dir_);
    if (!fs::exists(input_dir) || !fs::is_directory(input_dir)) {
        throw std::runtime_error("invalid directory: " + source_dir_);
    }

    for (const auto &entry : fs::directory_iterator(input_dir)) {
        if (!entry.is_regular_file() || entry.path().extension() != ".pdf")
            continue;

        std::string pdf_path    =   entry.path().string();
        std::string output_dir  =   input_dir.string() + "/images";

        spdlog::info("processing: {}", pdf_path);

        // step 1: convert pdf to pngs
        convert_pdf_to_png(pdf_path, output_dir);

        // step 2: process each generated image (usually _page1.png, _page2.png, ...)
        std::string stem = entry.path().stem().string();
        for (int i = 1;; ++i) {
            fs::path image_path = fs::path(output_dir) / (stem + "_page" + std::to_string(i) + ".png");
            if (!fs::exists(image_path))
                break;

            spdlog::info("preprocess picture {} ... ", image_path.string());
            cv::Mat img = cv::imread(image_path.string(), cv::IMREAD_COLOR);

            if (img.empty()) {
                std::cerr << "  failed to load image: " << image_path << std::endl;
                continue;
            }

            // step 3: preprocess
            cv::Mat preprocessed = preprocess_picture(img);

            // step 4: enhance for ocr (optional)
            cv::Mat enhanced = enhance_for_ocr(preprocessed);  // you need to define this if not already

            // step 5: extract text
            std::string text = extract_text_from_image(enhanced);
            std::cout << "[ocr result]\n" << text << "\n";
            
        }
    }
}

int Ocr::convert_pdf_to_png_incremental(const std::string& pdf_path,
                                        const std::string& output_dir,
                                        const std::function<void(int, int, const std::string&)> &on_page_saved)
{
    fs::path images_dir     =   output_dir;
    // create the directory if it doesn't exist
    std::error_code ec;
    if (!fs::exists(images_dir)) {
        fs::create_directories(images_dir, ec);  // creates all missing parent dirs
        if (ec) {
            throw std::runtime_error(
                "failed to create directory '" + images_dir.string() + "': " + ec.message()
            );
        }
    }

    std::string pdf_name    =   fs::path{pdf_path}.stem().string();
    fs::path output_path    =   images_dir / pdf_name;

    auto doc = poppler::document::load_from_file(pdf_path);
    if (!doc) {
        std::cerr << "failed to load pdf: " << pdf_path << std::endl;
        return 0;
    }

    poppler::page_renderer renderer;
    renderer.set_render_hint(poppler::page_renderer::antialiasing, true);
    renderer.set_render_hint(poppler::page_renderer::text_antialiasing, true);

    const int total_pages = doc->pages();
    for (int i = 0; i < total_pages; ++i) {
        std::unique_ptr<poppler::page> page(doc->create_page(i));
        if (!page) continue;

        const double dpi    =   300.0;
        auto image          =   renderer.render_page(page.get(), dpi, dpi);

        if (!image.is_valid()) {
            std::cerr << "failed to render page " << i << std::endl;
            continue;
        }

        std::string filename = output_path.string() + "_page" + std::to_string(i + 1) + ".png";
        if (!image.save(filename, "png")) {
            std::cerr << "failed to save image: " << filename << std::endl;
        } else {
            std::cout << "saved: " << filename << std::endl;
            if (on_page_saved) {
                on_page_saved(i + 1, total_pages, filename);
            }
        }
    }

    return total_pages;
}

void Ocr::convert_pdf_to_png(const std::string& pdf_path, const std::string& output_dir) 
{
    (void)convert_pdf_to_png_incremental(pdf_path, output_dir, {});
}

cv::Mat Ocr::preprocess_picture(const cv::Mat &input)
{
    cv::Mat gray, blur, claheimg, binary;

    // 1. Convert to grayscale
    cv::cvtColor(input, gray, cv::COLOR_BGR2GRAY);

    // 2. Light denoise (Gaussian blur keeps edges)
    cv::GaussianBlur(gray, blur, cv::Size(3, 3), 0);

    // 3. Enhance local contrast (CLAHE)
    cv::Ptr<cv::CLAHE> clahe = cv::createCLAHE(2.5, cv::Size(8, 8));
    clahe->apply(blur, claheimg);

    // 4. Global binarization (Otsu threshold)
    cv::threshold(claheimg, binary, 0, 255,
                  cv::THRESH_BINARY | cv::THRESH_OTSU);

    // 5. Clean specks / small dots
    cv::Mat kernel = cv::getStructuringElement(cv::MORPH_RECT, cv::Size(2, 2));
    cv::morphologyEx(binary, binary, cv::MORPH_OPEN, kernel);

    // 6. Crop large white borders
    std::vector<std::vector<cv::Point>> contours;
    cv::findContours(binary, contours, cv::RETR_EXTERNAL, cv::CHAIN_APPROX_SIMPLE);
    if (!contours.empty()) {
        cv::Rect bbox = cv::boundingRect(contours[0]);
        for (size_t i = 1; i < contours.size(); ++i)
            bbox |= cv::boundingRect(contours[i]);
        binary = binary(bbox);
    }

    // 7. Upscale small receipts to improve text clarity
    if (binary.cols < 1000)
        cv::resize(binary, binary, cv::Size(), 2.0, 2.0, cv::INTER_CUBIC);

    return binary;
}

cv::Mat Ocr::enhance_for_ocr(const cv::Mat &bin)
{
    cv::Mat dil, erd, sharp;

    // 1. Close gaps slightly (dilate + erode)
    cv::Mat k = cv::getStructuringElement(cv::MORPH_RECT, cv::Size(2, 2));
    cv::dilate(bin, dil, k);
    cv::erode(dil, erd, k);

    // 2. Apply unsharp mask for edge clarity
    cv::Mat blur;
    cv::GaussianBlur(erd, blur, cv::Size(0, 0), 1.0);
    cv::addWeighted(erd, 1.8, blur, -0.8, 0, sharp);

    // 3. Normalize lighting and invert if needed
    double meanVal = cv::mean(sharp)[0];
    if (meanVal > 127)
        cv::bitwise_not(sharp, sharp);

    // 4. Optional resize to target width 1024 for consistent payloads
    if (sharp.cols > 1200)
        cv::resize(sharp, sharp, cv::Size(1024, sharp.rows * 1024 / sharp.cols));

    return sharp;
}

std::string Ocr::extract_text_from_image(const cv::Mat &img_in)
{
    if (img_in.empty())
        throw std::runtime_error("image is empty");

    // --- 1. grayscale and resize ---
    cv::Mat gray;
    if (img_in.channels() == 3)
        cv::cvtColor(img_in, gray, cv::COLOR_BGR2GRAY);
    else
        gray = img_in.clone();

    cv::Mat big;
    cv::resize(gray, big, cv::Size(), 3.0, 3.0, cv::INTER_CUBIC);  // bigger scale helps

    cv::Mat rgb;
    cv::cvtColor(big, rgb, cv::COLOR_GRAY2BGR);

    // --- 2. initialize tesseract ---
    tesseract::TessBaseAPI tess;
    if (tess.Init(nullptr, "eng", tesseract::OEM_LSTM_ONLY) != 0)
        throw std::runtime_error("could not initialize tesseract");

    //tess.setpagesegmode(tesseract::psm_single_block);
    //tess.setpagesegmode(tesseract::psm_auto);
    tess.SetPageSegMode(tesseract::PSM_AUTO_OSD);
    tess.SetImage(rgb.data, rgb.cols, rgb.rows, 3, static_cast<int>(rgb.step));
    tess.SetSourceResolution(300);

    // --- 3. ocr engine tweaks ---
    tess.SetVariable("preserve_interword_spaces", "1");
    tess.SetVariable("load_system_dawg", "1");
    tess.SetVariable("load_freq_dawg", "1");

    // (optional) light whitelist to avoid noise:
    tess.SetVariable("tessedit_char_whitelist",
        "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz0123456789.,:/-()%& vt");

    // --- 4. extract text ---
    char *out = tess.GetUTF8Text();
    std::string result = out ? std::string(out) : std::string();
    delete[] out;
    tess.End();

    return result;
}

std::string Ocr::read_file(const std::string& path)
{
    std::ifstream in(path, std::ios::binary);
    std::ostringstream ss;
    ss << in.rdbuf();
    return ss.str();
}
