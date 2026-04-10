#pragma once

#include <string>
#include <chrono>
#include <sys/wait.h>

#include <atomic>
#include <thread>
#include <unordered_map>
#include <mutex>
#include <torch/torch.h>

#include <boost/date_time/posix_time/posix_time.hpp>
#include <boost/interprocess/mapped_region.hpp>
#include <nlohmann/json.hpp>

#include "meminf.hxx"

using namespace boost::posix_time;
using namespace boost::gregorian;

using json = nlohmann::json;

const double _epsilon_  =   1e-10;

/**
 * Comparison operator for std::map
*/
class s2d_less
{
protected:
    double epsilon_;
public:
    s2d_less(double arg = 1e-8) : epsilon_(arg) {}
    bool operator()(const std::string &left, const std::string &right) const
    {
        double d_l = std::stod(left);
        double d_r = std::stod(right);

        return (std::abs(d_l - d_r) > epsilon_) && (d_l < d_r);
    }
};

class s2d_greater : public s2d_less
{
public:
    bool operator()(const std::string &left, const std::string &right) const
    {
        double d_l = std::stod(left);
        double d_r = std::stod(right);

        return (std::abs(d_l - d_r) > epsilon_) && (d_l > d_r);
    }
};

inline bool is_db_equel(double d1, double d2)
{
    return std::abs(d1 - d2) < _epsilon_;
}

/**
 * Convert iso time string into ptime
 */
inline ptime from_iso_to_ptime(std::string iso_str)
{
    // Remove trailing 'Z'
    if (!iso_str.empty() && iso_str.back() == 'Z') {
        iso_str.pop_back();
    }

    return from_iso_extended_string(iso_str);
}
inline std::string from_ptime_to_iso(ptime pti)
{
    return to_iso_extended_string(pti) + "Z";
}
/**
 * Convert long long into ptime
*/
inline ptime from_millisec(unsigned long long milliseconds)
{
    time_t secs     =   static_cast<time_t>(milliseconds / 1000ULL);
    const long part =   static_cast<long>(milliseconds % 1000ULL);

    return from_time_t(secs) + millisec(part);
}
inline ptime from_microsec(unsigned long long micros)
{
    const time_t secs   =   static_cast<time_t>(micros / 1'000'000ULL);
    const long   part   =   static_cast<long>(micros % 1'000'000ULL); 
                                                                      
    return from_time_t(secs) + microseconds(part);
}
// Convert ptime to long long
inline unsigned long long to_nanosec(ptime ptm)
{
    static const ptime epoch(date(1970, 1, 1));
    return  (ptm - epoch).total_nanoseconds();
}
inline unsigned long long to_millisec(ptime ptm)
{
    const ptime epoch(date(1970, 1, 1));
    return  (ptm - epoch).total_milliseconds();
}
inline unsigned long get_millisec_of_day(ptime t)
{
    time_duration tod   =   t.time_of_day();
    return tod.total_milliseconds();
}
/**
 * Time period in boost::date_time::posix_time
*/
class day_period : public time_period
{
public:
    day_period(date d) : time_period(ptime(d), ptime(d, hours(24))) {}
};

/**
 * Instrumentation profilers for measuring performance
 * A scoped timer
*/
using ClockType = std::chrono::steady_clock;
class ScopedTimer
{
    const char *function_name_{};
    const ClockType::time_point start_{};
public:
    
    ScopedTimer(const char *func) :
        function_name_{func}, start_{ClockType::now()} {}
    
    ScopedTimer(const ScopedTimer &) = delete;
    ScopedTimer(ScopedTimer &&) = delete;
    auto operator=(const ScopedTimer &) -> ScopedTimer & = delete;
    auto operator=(ScopedTimer &&) -> ScopedTimer & = delete;

    ~ScopedTimer()
    {

        auto stop = ClockType::now();
        auto duration = (stop - start_);
        auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(duration).count();
        std::cout << ms << " ms " << function_name_ << std::endl;
    }
 };

/**
 * Display memory usage information
*/
inline void print_pages() {
    static size_t pagesize = sysconf(_SC_PAGESIZE);
    int64_t bytes = getCurrentRSS();
    assert((bytes % pagesize) == 0);
    size_t pages = bytes / pagesize;
    std::cout << "page size: " << pagesize << "\t";
    std::cout << "bytes: " << bytes << "\t";
    std::cout << "pages: " << pages << std::endl;
}

/**
 * Memory align
*/
inline auto align_up(std::size_t n, std::size_t alignment) noexcept -> std::size_t
{
    return (n + (alignment-1)) & ~(alignment-1);    
}
inline auto get_page_size() -> std::size_t
{
    return boost::interprocess::mapped_region::get_page_size();
}

/**
 * Calculate medain of elements in range
*/
template<class RANGE>
double rng_median(RANGE &r)
{
    std::size_t sz = r.size();
    if (sz % 2 == 0) {
        auto it = r.begin() + sz / 2 - 1;
        std::nth_element(r.begin(), it, r.end());
        auto val1 = *it;
        
        it = r.begin() + sz / 2;
        std::nth_element(r.begin(), it, r.end());
        auto val2 = *it;

        return (val1 + val2) / 2.0;
    } else {
        auto it = r.begin() + sz / 2;
        std::nth_element(r.begin(), it, r.end());
        return *it;  
    }
}

/**
 * Iterate std::tuple
*/
template<std::size_t Index, class Tuple, class Func>
void tuple_at(const Tuple &t, Func f)
{
    const auto &v = std::get<Index>(t);
    std::invoke(f, v);
}
template<class Tuple, class Func, std::size_t Index = 0>
void tuple_for_each(const Tuple &t, const Func& f)
{
    constexpr auto n = std::tuple_size_v<Tuple>;
    if constexpr(Index < n) {
        tuple_at<Index>(t, f);
        tuple_for_each<Tuple, Func, Index+1>(t, f);
    }
}

template<std::size_t Index, class Tuple, class Func>
void tuple_at_ix(const Tuple &t, Func f)
{
    const auto &v = std::get<Index>(t);
    std::invoke(f, v, Index);
}
template<class Tuple, class Func, std::size_t Index = 0>
void tuple_for_index(const Tuple &t, const Func& f)
{
    constexpr auto n = std::tuple_size_v<Tuple>;
    if constexpr(Index < n) {
        tuple_at_ix<Index>(t, f);
        tuple_for_index<Tuple, Func, Index+1>(t, f);
    }
}

/**
 * Rotate a vector of two dimension revese it axis.
*/
inline auto rotate_vector(const std::vector<std::vector<double> > &vec) -> std::vector<std::vector<double> >
{
    auto cols   =   std::size_t{vec[0].size()};
    auto da     =   std::vector<std::vector<double> >(cols);
    
    std::ranges::for_each(vec, [&da](auto &&row){
        std::size_t ix{0};
        for (auto elem : row) {
            da[ix++].emplace_back(elem);
        }
    });

    return da;
}

/**
 * To test whether a std::vector<double> contains NAN 
*/
inline auto hasNAN(const std::vector<double> &vec) -> bool
{
    for (const auto &val : vec) {
        if (std::isnan(val)) {
            return true;
        }
    }
    return false;
}

/**
 * Parse & Print exit status of child process
*/
inline void pr_exit(int status, pid_t pid = 0)
{
    if (WIFEXITED(status)) {
        printf("normal termination, pid = %d, exit status = %d\n", pid, WEXITSTATUS(status));
    } else if (WIFSIGNALED(status)) {
        printf("abnormal termination, pid = %d, signal number = %d%s\n",
            pid,
            WTERMSIG(status),
#ifdef WCOREDUMP
            WCOREDUMP(status) ? " (core file generated)" : "");
#else
            "");
#endif
    } else if (WIFSTOPPED(status)) {
        printf("child stopped, pid = %d, signal number = %d\n", pid, WSTOPSIG(status));
    }
}

/**
 * Whenever a thread calls it, the thread receives a unique integer ID from 0 up to \
 * however many threads have called it so far minus one
 */
inline int getThreadIndex()
{
    // Thread-local storage for this thread’s index
    thread_local static int threadIndex = -1;

    // We keep a global (shared) atomic counter to assign IDs
    static std::atomic<int> g_nextIndex{0};

    // If the thread has no index assigned yet, get one
    if (threadIndex == -1)
    {
        threadIndex = g_nextIndex.fetch_add(1, std::memory_order_relaxed);
    }

    return threadIndex;
}

//------------------------------------------------------------------------------
// Trim whitespace (spaces, tabs, CR, LF) from both ends of a string
//------------------------------------------------------------------------------
inline static void trim_ws(std::string &s) {
    static const char* ws = " \t\r\n";
    auto start = s.find_first_not_of(ws);
    if (start == std::string::npos) {
        s.clear();
        return;
    }
    auto end = s.find_last_not_of(ws);
    s = s.substr(start, end - start + 1);
}

/**
 * Dealing with torch::Tensor
 */
// Percentile computation (in-place, no gradient)
inline double percentile(const torch::Tensor &x, double q) {
    auto sorted = std::get<0>(x.flatten().sort());
    auto idx    = static_cast<int64_t>(q * (sorted.size(0) - 1));
    return sorted[idx].item<double>();
}

// Base64 encoding function
static const std::string base64_chars =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    "abcdefghijklmnopqrstuvwxyz"
    "0123456789+/";

inline std::string base64_encode(const std::string& input) 
{
    std::string encoded;
    int val = 0, valb = -6;
    for (uint8_t c : input) {
        val = (val << 8) + c;
        valb += 8;
        while (valb >= 0) {
            encoded.push_back(base64_chars[(val >> valb) & 0x3F]);
            valb -= 6;
        }
    }
    if (valb > -6) encoded.push_back(base64_chars[((val << 8) >> (valb + 8)) & 0x3F]);
    while (encoded.size() % 4) encoded.push_back('=');
    return encoded;
}

/**
 * For extract values from json item
 */

 inline std::string json_to_str(const json &j, const char *key, const std::string &def = "")
{
    if (!j.contains(key) || j[key].is_null()) return def;

    const auto &val = j.at(key);

    if (val.is_string()) {
        return val.get<std::string>();
    }

    if (val.is_object()) {
        std::string flat;

        for (auto it = val.begin(); it != val.end(); ++it) {
            if (!it.value().is_null()) {
                if (!flat.empty()) flat += ", ";
                flat += it.key() + ": " + it.value().dump();  // or `.get<std::string>()` if safe
            }
        }

        return flat;
    }

    // fallback for number, bool, array, etc.
    return val.dump();
}

inline double json_to_double(const json& j, const char* key, double def = 0.0) 
{
    if (!j.contains(key) || j[key].is_null()) return def;

    const auto& v = j[key];
    try {
        if (v.is_number()) return v.get<double>();
        if (v.is_string()) return std::stod(v.get<std::string>());
    } catch (...) {
        return def;
    }
    return def;
}

inline int json_to_int(const json& j, const char* key, int def = 0) 
{
    if (!j.contains(key) || j[key].is_null()) return def;

    const auto& v = j[key];
    try {
        if (v.is_number_integer()) return v.get<int>();
        if (v.is_number_float()) return static_cast<int>(v.get<double>());
        if (v.is_string()) return std::stoi(v.get<std::string>());
    } catch (...) {
        return def;
    }
    return def;
}
