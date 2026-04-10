#include "ocr.h"
#include "utilities.hxx"
#include "postgresApi.h"

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
// === namespaces ===
namespace fs = std::filesystem;
namespace beast = boost::beast;
namespace http  = beast::http;
namespace net   = boost::asio;
using tcp       = net::ip::tcp;
using json      = nlohmann::json;

// === Improved Receipt OCR and OpenAI GPT-4o Integration ===

std::string Ocr::send_receipt_to_openai(const std::string &base64_image) 
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

            json req_body = {
                {"model", "gpt-4o"},
                {"max_tokens", 2024},
                {"messages", json::array({{
                    {"role", "user"},
                    {"content", json::array({
                        {
                            {"type", "text"},
                            
                            {
                                "text",
                                "You are an intelligent system that extracts structured data from scanned purchase receipts.\n\n"
                                "The images may be low-resolution, partially obscured, or contain OCR-invisible or degraded text. "
                                "You are allowed to infer and reconstruct missing or unclear characters based on:\n"
                                "- Known supplier formats and item names\n"
                                "- Familiar layouts and common receipt structures\n"
                                "- Partial numbers (e.g. infer a date or price if digits are partially visible)\n"
                                "- Contextual clues (e.g. use formatting, column alignment, total lines, etc.)\n\n"
                                "Only infer characters or digits that are likely present but degraded. Do NOT invent new suppliers, items, or text that are not supported by the receipt.\n\n"
                                "Confidence and missing data rules:\n"
                                "- If any field cannot be confidently read or inferred from the visible content and layout, do NOT guess or invent a value.\n"
                                "- In that case, set the field to an empty string \"\" for text fields, or 0 for numeric fields (as specified below).\n"
                                "- Do NOT output placeholder words such as \"Unknown\", \"Unknown supplier\", \"N/A\", or \"Not available\" for any field; use an empty string \"\" instead.\n\n"
                                "There may be one or more receipts in the image. For each receipt, extract the following information:\n\n"
                                "1. `supplier`:\n"
                                "    - `name`\n"
                                "    - `tin`\n"
                                "    - `site`\n"
                                "    - `contact_info` (address, phone, email)\n\n"
                                "TIN rules (VERY IMPORTANT, Vanuatu only):\n"
                                "- `supplier.tin` must be digits only (0-9), no letters, no spaces, no punctuation.\n"
                                "- Typical length is 6 digits (e.g. \"100687\", \"437611\").\n"
                                "- If no TIN can be reliably found, set \"tin\": \"\" (empty string). Do NOT guess or invent a TIN.\n\n"
                                "2. `purchase_order`:\n"
                                "    - `invoice_id`\n"
                                "    - `purchase_date`\n"
                                "    - `total_cost` (integer in VT)\n\n"
                                "3. `purchase_items`:\n"
                                "    - `name`\n"
                                "    - `quantity`\n"
                                "    - `unit_price` (integer in VT)\n"
                                "    - `total_price` (integer in VT)\n\n"
                                "4. Output JSON array where each receipt has: `supplier`, `purchase_order`, `purchase_items`\n\n"
                                "Important: Money values (before 'vt') must be exact.\n"
                                "- Fix common OCR errors (O->0, I->1, etc.)\n"
                                "- Never guess or round missing digits.\n"
                                "- Remove commas, return as integer (e.g. \"1,800vt\" -> 1800)\n"
                                "- Use layout alignment to infer unclear numbers when possible, but only if the result is unambiguous.\n\n"
                                "Numeric rules (VERY IMPORTANT):\n"
                                "- The fields `quantity`, `unit_price`, `total_price`, and `total_cost` MUST be plain numbers "
                                "  (e.g. 3, 12, 4325, 0.75). They must NOT contain words, units, or expressions.\n"
                                "- NEVER output formulas or text in these fields. These are all FORBIDDEN examples:\n"
                                "    \"unit_price\": 5472 / (3 * 24)\n"
                                "    \"unit_price\": \"5472 / 3\"\n"
                                "    \"total_price\": \"25950 VT\"\n"
                                "- Always extract `total_price` exactly as it appears on the receipt. This value is always present and most reliable.\n"
                                "- If `unit_price` and `quantity` are both present and reliable, they must satisfy: "
                                "  total_price = unit_price * quantity.\n"
                                "- You may compute `unit_price = total_price / quantity` ONLY when both `total_price` and `quantity` "
                                "  are clearly known. Do not use pack sizes such as 6, 12, or 24 in this calculation.\n"
                                "- If `unit_price` or `quantity` is absent or unclear, do not guess or infer from typical pack sizes; "
                                "  set that field to 0.\n\n"
                                "Formatting rules (IMPORTANT):\n"
                                "- Dates MUST be ISO 8601 format `YYYY-MM-DD` with zero padding (e.g. 2025-02-09).\n"
                                "- When you infer or normalize a date from partial text, always output it in ISO format.\n"
                                "- If a date is missing or ambiguous, return an empty string \"\" for `purchase_date`.\n"
                                "- Output MUST be valid strict JSON according to RFC 8259.\n"
                                "- Do NOT include any comments (no `// ...`, no `/* ... */`, no `# ...`).\n"
                                "- Do NOT wrap the JSON in markdown fences (no ``` or ```json).\n"
                                "- Do NOT include any explanation, narration, or text outside the JSON.\n"
                                "- Do NOT add extra fields that are not specified.\n"
                                "- Do NOT include trailing commas at the end of arrays or objects.\n\n"
                                "If you need to make assumptions or inferences, make them minimal and conservative, and encode them directly in the JSON fields as final values. "
                                "When in doubt, leave text fields as \"\" and numeric fields as 0 instead of guessing. "
                                "Do not explain your reasoning in text or comments.\n"
                                "Return ONLY the JSON value.\n"
                            }
                            
                        },
                        {
                            {"type", "image_url"},
                            {"image_url", {{"url", "data:image/png;base64," + base64_image}}}
                        }
                    })}
                }})}
            };

            std::string body = req_body.dump();
            http::request<http::string_body> req(http::verb::post, target, 11);
            req.set(http::field::host, host);
            req.set(http::field::authorization, "Bearer " + openai_key_);
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

            return res.body();

        } catch (const std::exception &e) {
            spdlog::error("GPT OCR error: {}", e.what());
            if (attempt < 3) std::this_thread::sleep_for(std::chrono::seconds(1));
        }
    }

    throw std::runtime_error("OpenAI request failed after 3 attempts.");
}

void Ocr::gpt_ocr_pdfs() 
{
    fs::path input_dir(source_dir_);
    if (!fs::exists(input_dir) || !fs::is_directory(input_dir)) {
        throw std::runtime_error("invalid directory: " + source_dir_);
    }

    PostgresApi psq_api;

    for (const auto &entry : fs::directory_iterator(input_dir)) {
        if (!entry.is_regular_file() || entry.path().extension() != ".pdf") continue;

        std::string pdf_path = entry.path().string();
        std::string output_dir = input_dir.string() + "/images";
        convert_pdf_to_png(pdf_path, output_dir);

        std::string stem = entry.path().stem().string();
        for (int i = 1;; ++i) {
            fs::path image_path = fs::path(output_dir) / (stem + "_page" + std::to_string(i) + ".png");
            if (!fs::exists(image_path)) break;
            
            std::string image_path_str = image_path.string();
            if (psq_api.is_image_scanned(image_path_str)) {
                spdlog::info("Already scanned, skipping: {}", image_path_str);
                continue;
            }

            cv::Mat raw = cv::imread(image_path_str);
            //cv::Mat bin = preprocess_picture(raw);
            //cv::Mat enhanced = enhance_for_ocr(bin);
            cv::Mat resized;
            if (raw.cols > 1024)
                cv::resize(raw, resized, cv::Size(1024, raw.rows * 1024 / raw.cols));
            else
                resized = raw.clone();

            std::vector<uchar> buf;
            cv::imencode(".png", resized, buf);
            std::string png_bytes(reinterpret_cast<const char*>(buf.data()), buf.size());
            std::string base64_image = base64_encode(png_bytes);

            const std::string &raw_res = send_receipt_to_openai(base64_image);
            json response_json = json::parse(raw_res);
            std::string extracted_text = response_json["choices"][0]["message"]["content"];

            //try {
            //    json pretty = json::parse(extracted_text);
            //    std::cout << pretty.dump(4) << std::endl;
            //} catch (...) {
            //    std::cout << extracted_text << std::endl;
            //}

            // Insert extracted text into ocr_scan table
            spdlog::info("Insert extracted string into orc_scan table in database from: {}", image_path_str);
            psq_api.insert_ocr_scan(image_path_str, extracted_text);
        }
    }
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

void Ocr::convert_pdf_to_png(const std::string& pdf_path, const std::string& output_dir) 
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
        return;
    }

    poppler::page_renderer renderer;
    renderer.set_render_hint(poppler::page_renderer::antialiasing, true);
    renderer.set_render_hint(poppler::page_renderer::text_antialiasing, true);

    for (int i = 0; i < doc->pages(); ++i) {
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
        }
    }
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
