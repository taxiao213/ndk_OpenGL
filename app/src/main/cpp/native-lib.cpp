#include <jni.h>
#include <string>
#include "android_log.h"
#include "EGL/egl.h"
#include "GLES2/gl2.h"
#include "android/native_window.h"

JavaVM *jvm;
// 获取 jvm
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *javaVm, void *reserved) {
    jvm = javaVm;
    JNIEnv *env;
    if (javaVm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    SDK_LOG_E("Jni_OnLoad");
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_taxiao_opengl_JniSdkImpl_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from c++";
    SDK_LOG_E("stringFromJNI");
    return env->NewStringUTF(hello.c_str());
}