#include "apis.hxx"

#include <boost/asio/co_spawn.hpp>
#include <boost/asio/detached.hpp>
#include <boost/asio/io_context.hpp>
#include <boost/asio/signal_set.hpp>
#include <boost/asio/ip/tcp.hpp>

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

awaitable<void> do_session(tcp::socket socket)
{
    spdlog::info("\n>>>>>> The session start <<<<<<");

    auto executor = co_await this_coro::executor;
 
    boost::beast::flat_buffer buffer{};
    http::request<http::string_body> req{};
    for (;;)
    try {    
        co_await http::async_read(socket, buffer, req, use_awaitable);
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
