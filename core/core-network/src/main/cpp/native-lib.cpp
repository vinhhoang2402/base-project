#include <jni.h>
#include <string>

extern "C" {

JNIEXPORT jstring JNICALL
Java_com_demo_projectbase_core_network_NativeLib_getApiKey(JNIEnv *env, jobject /* thiz */) {
    std::string apiKey = "b3405f3b38c2bc0d92e82084f682eb64";
    return env->NewStringUTF(apiKey.c_str());
}

JNIEXPORT jstring JNICALL
Java_com_demo_projectbase_core_network_NativeLib_getReadAccessToken(JNIEnv *env, jobject /* thiz */) {
    std::string token = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiMzQwNWYzYjM4YzJiYzBkOTJlODIwODRmNjgyZWI2NCIsIm5iZiI6MTc4MjM3MTcyNy44NTEsInN1YiI6IjZhM2NkNThmMjY0NzJiYzZiODRjNGNmYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.FLXlu_CMLIVHOmmw4DaAMNsST4l_XrnhjoW2y2IQum8";
    return env->NewStringUTF(token.c_str());
}

} // extern "C"
