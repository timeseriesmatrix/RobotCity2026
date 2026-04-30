#include "apis.hxx"

#include <boost/asio/co_spawn.hpp>
#include <boost/asio/detached.hpp>
#include <boost/asio/io_context.hpp>
#include <boost/asio/signal_set.hpp>
#include <boost/asio/ip/tcp.hpp>

#include <cstdlib>
#include <filesystem>
#include <fstream>
#include <limits>
#include <sstream>
#include <string>
#include <thread>
#include <vector>

#include <spdlog/spdlog.h>
#include <spdlog/sinks/stdout_color_sinks.h>
#include <spdlog/sinks/daily_file_sink.h>

using boost::asio::awaitable;
using boost::asio::co_spawn;
using boost::asio::detached;
using boost::asio::use_awaitable;

namespace this_coro =   boost::asio::this_coro;

#if defined(BOOST_ASIO_ENABLE_HANDLER_TRACKING)
#   define use_awaitable \
    boost::asio::use_awaitable_t(__FILE__, __LINE__, __PRETTY_FUNCTION__)
#endif

namespace {
std::string trim_env_line(std::string s)
{
    const auto first = s.find_first_not_of(" \t\r\n");
    if (first == std::string::npos) return {};
    const auto last = s.find_last_not_of(" \t\r\n");
    return s.substr(first, last - first + 1);
}

std::string unquote_env_value(std::string value)
{
    value = trim_env_line(std::move(value));
    if (value.size() >= 2) {
        const char first = value.front();
        const char last = value.back();
        if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
            return value.substr(1, value.size() - 2);
        }
    }
    return value;
}

bool env_path_seen(const std::vector<std::filesystem::path> &paths, const std::filesystem::path &candidate)
{
    std::error_code ec;
    const auto canonical_candidate = std::filesystem::weakly_canonical(candidate, ec);
    const auto normalized_candidate = ec ? candidate.lexically_normal() : canonical_candidate;
    for (const auto &path : paths) {
        std::error_code path_ec;
        const auto canonical_path = std::filesystem::weakly_canonical(path, path_ec);
        const auto normalized_path = path_ec ? path.lexically_normal() : canonical_path;
        if (normalized_path == normalized_candidate) return true;
    }
    return false;
}

void add_env_path(std::vector<std::filesystem::path> &paths, const std::filesystem::path &path)
{
    if (path.empty() || env_path_seen(paths, path)) return;
    paths.push_back(path);
}

bool load_dotenv_file(const std::filesystem::path &path)
{
    std::error_code ec;
    if (!std::filesystem::is_regular_file(path, ec)) return false;

    std::ifstream in(path);
    if (!in) return false;

    std::string line;
    while (std::getline(in, line)) {
        line = trim_env_line(std::move(line));
        if (line.empty() || line.front() == '#') continue;
        if (line.starts_with("export ")) {
            line = trim_env_line(line.substr(7));
        }

        const auto equals = line.find('=');
        if (equals == std::string::npos) continue;

        const std::string key = trim_env_line(line.substr(0, equals));
        std::string value = unquote_env_value(line.substr(equals + 1));
        if (key.empty() || key.find_first_of(" \t\r\n") != std::string::npos) continue;
        if (std::getenv(key.c_str()) != nullptr) continue;

        setenv(key.c_str(), value.c_str(), 0);
    }

    return true;
}

std::size_t load_dotenv_files(int argc, char *argv[])
{
    std::vector<std::filesystem::path> candidates;
    if (const char *env_file = std::getenv("FLAME_ENV_FILE"); env_file && *env_file) {
        add_env_path(candidates, env_file);
    }

    std::error_code ec;
    const auto cwd = std::filesystem::current_path(ec);
    if (!ec) {
        add_env_path(candidates, cwd / ".env");
        add_env_path(candidates, cwd.parent_path() / ".env");
        add_env_path(candidates, cwd.parent_path().parent_path() / ".env");
    }

    if (argc > 0 && argv && argv[0] && *argv[0]) {
        std::filesystem::path exe_path(argv[0]);
        if (exe_path.is_relative() && !ec) {
            exe_path = cwd / exe_path;
        }
        add_env_path(candidates, exe_path.parent_path() / ".env");
        add_env_path(candidates, exe_path.parent_path().parent_path() / ".env");
    }

    std::size_t loaded = 0;
    for (const auto &path : candidates) {
        if (load_dotenv_file(path)) {
            ++loaded;
            spdlog::info("[main] Loaded environment from {}", path.string());
        }
    }
    return loaded;
}

std::size_t request_body_limit_bytes()
{
    constexpr std::size_t default_limit = 64ULL * 1024ULL * 1024ULL;
    const char *raw = std::getenv("FLAME_HTTP_BODY_LIMIT_MB");
    if (!raw || !*raw) {
        return default_limit;
    }

    try {
        unsigned long long mb = std::stoull(raw);
        if (mb == 0) {
            return default_limit;
        }
        if (mb > (std::numeric_limits<std::size_t>::max)() / (1024ULL * 1024ULL)) {
            return default_limit;
        }
        return static_cast<std::size_t>(mb * 1024ULL * 1024ULL);
    } catch (...) {
        return default_limit;
    }
}
} // namespace

awaitable<void> do_session(tcp::socket socket)
{
    spdlog::info("\n>>>>>> The session start <<<<<<");

    auto executor = co_await this_coro::executor;
 
    boost::beast::flat_buffer buffer{};
    for (;;)
    try {    
        http::request_parser<http::string_body> parser;
        parser.body_limit(request_body_limit_bytes());
        co_await http::async_read(socket, buffer, parser, use_awaitable);
        auto req = parser.release();
        spdlog::info("Request that read of target: {}", req.target());

        // auto msg = apis(move(req), executor);
        auto msg = co_await async_apis(move(req), executor);

        co_await http::async_write(socket, msg, use_awaitable);

        malloc_trim(0);

        if (!msg.keep_alive()) {
            break;   
        }
    } catch (std::exception &e) {
        spdlog::error("[main::do_session] Exception: {}", e.what());
        break;
    }

    spdlog::info("\n++++++ The session end ++++++");
}

awaitable<void> do_listen()
{
    auto executor = co_await this_coro::executor;
    tcp::acceptor acceptor(executor, {tcp::v4(), _server_port_});

    spdlog::info("\n>>> {} start to be listening on {} <<<", _server_name_, _server_port_);

    for (;;) {
       tcp::socket socket = co_await acceptor.async_accept(use_awaitable);
       co_spawn(executor, do_session(std::move(socket)), detached); 
    }
}

int main(int argc, char *argv[])
{

    try {
        // Create a color console sink
        auto console_sink   =   std::make_shared<spdlog::sinks::stdout_color_sink_mt>();
        console_sink->set_level(spdlog::level::trace);
        console_sink->set_pattern("%H:%M:%S.%e [%^%l%$] %v"); 
        // Example pattern explanation:
        //   %^ => start color range
        //   %l => log level (e.g., INFO, ERROR)
        //   %$ => end color range
        //   %v => the actual log message
        // Create a daily file logger
        auto daily_sink     =   std::make_shared<spdlog::sinks::daily_file_sink_mt>("logs/daily.log", 0, 0);
        // Available levels : trace, debug, info, warn, err (or error), critical, off.
        daily_sink->set_level(spdlog::level::info);
        daily_sink->set_pattern("[%Y-%m-%d %H:%M:%S.%e] [thread %t] [%l] %v");
        // Combine them into a multi-sink logger
        std::vector<spdlog::sink_ptr> sinks{console_sink, daily_sink};
        auto multi_logger = std::make_shared<spdlog::logger>("multi_sink", sinks.begin(), sinks.end());
        // Optionally, make this multi-sink logger the default so SPDLOG_INFO, etc. go here
        // Set the logger level to the *lowest* threshold you need
        multi_logger->set_level(spdlog::level::trace);
        // Flush on every log call (trace is the lowest level)
        multi_logger->flush_on(spdlog::level::info);
        spdlog::set_default_logger(multi_logger);
        load_dotenv_files(argc, argv);
        
        // Create boost::asio and spawn a thread pool.
        boost::asio::io_context io_context;
        boost::asio::signal_set signals(io_context, SIGINT, SIGTERM);
        signals.async_wait([&](auto, auto){
            io_context.stop(); 
            spdlog::info("[main] Stopping io_context ...");
        });

        co_spawn(io_context, do_listen(), detached);

        auto num_threads = std::thread::hardware_concurrency();
        if (num_threads < 2) {
            throw std::runtime_error("Number of threads is less than 2. The system is not supported.");
        }

        std::vector<std::thread> threads;
        if (argc > 1 && std::stoi(argv[1]) > 1) {
            num_threads = std::stoi(argv[1]);
        }
        
        spdlog::info("[main] Number of threads: {}", num_threads);
        for (size_t i = 0; i < num_threads; ++i) {
            threads.emplace_back([&io_context](){
                io_context.run();
            });
        }


        for (auto &t : threads) {
            t.join();
        }

    } catch (std::exception &e) {
        spdlog::critical("[main] Exception: {}.", e.what());
    }       

    return 0;
}
