#pragma once
#include <opencv2/opencv.hpp>
#include <functional>
#include <string>

struct OcrWord {
    std::string text;
    int conf;        // confidence score
    cv::Rect bbox;   // bounding box in pixels
};

class Ocr
{
    const std::string source_dir_;
    const std::string openai_key_;

public:
    Ocr(std::string_view dir = "", const std::string key = "") : source_dir_(dir), openai_key_(key)
    {}

    // ocr through tesseract
    void tsr_ocr_pdfs();

    void convert_pdf_to_png(const std::string& pdf_path, const std::string& output_dir = "./images"); 
    int convert_pdf_to_png_incremental(const std::string& pdf_path,
                                       const std::string& output_dir,
                                       const std::function<void(int, int, const std::string&)> &on_page_saved = {});
    auto send_receipt_to_openai(const std::string &base64_image,
                                const std::string &prompt_context = {},
                                const std::string &line_items_focus_base64 = {}) -> std::string;
    auto send_structured_prompt_to_openai(const std::string &system_prompt,
                                          const std::string &user_prompt,
                                          int max_tokens = 1024,
                                          const std::string &model = "gpt-4o") -> std::string;

    cv::Mat preprocess_picture(const cv::Mat &input);
    cv::Mat enhance_for_ocr(const cv::Mat& bin);
    std::string extract_text_from_image(const cv::Mat& bin_img);

private:
    // Read file into binary string
    std::string read_file(const std::string& path);
};
