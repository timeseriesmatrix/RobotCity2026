#pragma once

#include <string>
#include <nlohmann/json.hpp>

/**
 * Lightweight helper that sends natural language or audio instructions
 * to OpenAI and expects back a single SQL statement (or an error) for
 * the flametrack PostgreSQL database.
 */
class OpenAiSqlAgent
{
    std::string api_key_;
    std::string host_   = "api.openai.com";
    std::string port_   = "443";
    std::string model_  = "gpt-4o";
    bool        has_key_{false};

public:
    explicit OpenAiSqlAgent(std::string api_key = "");

    // Translate raw text into SQL (or return {sql:"", error:"..."} on failure).
    auto natural_language_to_sql(const std::string &input_text) -> nlohmann::json;

    // Same as above but accepts base64 encoded audio and optional text hint.
    auto audio_to_sql(const std::string &audio_base64, const std::string &format = "wav",
                      const std::string &hint_text = "") -> nlohmann::json;

private:
    std::string chat_completion(const nlohmann::json &body);
    nlohmann::json base_messages() const;
};
