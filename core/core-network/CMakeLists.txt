cmake_minimum_required(VERSION 3.22.1)
project("corenetwork")

add_library(corenetwork SHARED src/main/cpp/native-lib.cpp)

find_library(log-lib log)
target_link_libraries(corenetwork ${log-lib})

# Required for 16 KB page size support (Google Play requirement from Nov 2025)
target_link_options(corenetwork PRIVATE -Wl,-z,max-page-size=16384)
