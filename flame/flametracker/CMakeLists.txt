cmake_minimum_required(VERSION 3.28)

project(flametracker LANGUAGES CXX)

# Limit glibc to a single malloc arena
#set(ENV{MALLOC_ARENA_MAX} "1")

# Define BOOST_URL_HEADER_ONLY globally
add_compile_definitions(BOOST_URL_HEADER_ONLY)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(LIB_DIR "/home/liam/libs")

set(CMAKE_PREFIX_PATH /home/liam/libs/libtorch)
set(TORCH_CUDA_ARCH_LIST "8.6")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${TORCH_CXX_FLAGS} -Wall -O2 -Wno-mismatched-new-delete")

find_package(Torch REQUIRED)
find_package(OpenSSL REQUIRED)
find_package(Arrow REQUIRED)
find_package(Parquet REQUIRED)
# Specifical for flametracker
find_package(OpenCV REQUIRED)
find_package(PkgConfig REQUIRED)

function(require_pkg_config_module prefix module apt_package)
    pkg_check_modules(${prefix} QUIET IMPORTED_TARGET ${module})
    if(NOT ${prefix}_FOUND)
        message(FATAL_ERROR
            "Missing pkg-config module '${module}'. Install '${apt_package}' and rerun CMake.\n"
            "Ubuntu/Debian example: sudo apt install ${apt_package}"
        )
    endif()
endfunction()

require_pkg_config_module(TESSERACT tesseract libtesseract-dev)
require_pkg_config_module(PQXX libpqxx libpqxx-dev)
require_pkg_config_module(POPPLER poppler-cpp libpoppler-cpp-dev)
require_pkg_config_module(CAIRO cairo libcairo2-dev)

# Include directories
include_directories(
    "${CMAKE_SOURCE_DIR}/inc"
    "${LIB_DIR}/boost/include"
    "${LIB_DIR}/nlohmannjson/include"
    "${LIB_DIR}/rangev3/include"
    "${LIB_DIR}/eigen"
    "${LIB_DIR}"
    "${OpenCV_INCLUDE_DIRS}"
)

# Link directories
link_directories(
    "${LIB_DIR}/boost/lib"
)

# Source files
file(GLOB SOURCES CONFIGURE_DEPENDS
    "main.cc"
    "src/*.cc"
)
# Header files
file(GLOB HEADERS CONFIGURE_DEPENDS
    "inc/*.h"
    "inc/*.hxx"
)

# Add executable
add_executable(${PROJECT_NAME} ${SOURCES})

# Increase template depth to support large instantiations
# target_compile_options(${PROJECT_NAME} PRIVATE -ftemplate-depth=2048)

# Add BOOST as a system include directory
target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE "${LIB_DIR}/boost/include")

# Link libraries
target_link_libraries(${PROJECT_NAME} PRIVATE
    "${TORCH_LIBRARIES}"
    OpenSSL::SSL
    OpenSSL::Crypto
    boost_url
    Arrow::arrow_shared
    Parquet::parquet_shared
    "${OpenCV_LIBS}"
    PkgConfig::TESSERACT
    PkgConfig::PQXX
    PkgConfig::POPPLER
    PkgConfig::CAIRO
)
