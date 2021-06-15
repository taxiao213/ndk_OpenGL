#include <jni.h>
#include <string>
#include "egl/TXEglHelp.h"
#include "egl/TXEglThread.h"
#include "shader/ShaderSource.h"
#include "shader/ShaderUtil.h"

JavaVM *jvm = NULL;
ANativeWindow *mANativeWindow = NULL;
TXEglHelp *mTxEglHelp = NULL;
TXEglThread *mTXEglThread = NULL;
int program;
GLint avPosition;
GLint afPosition;

// 获取 jvm
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *javaVm, void *reserved) {
    jvm = javaVm;
    JNIEnv *env;
    if (javaVm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    SDK_LOG_D("Jni_OnLoad");
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_taxiao_opengl_JniSdkImpl_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from c++";
    SDK_LOG_D("stringFromJNI");
    return env->NewStringUTF(hello.c_str());
}

// test
extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_setSurface(JNIEnv *env, jobject thiz, jobject surface) {
    // TODO: implement setSurface()
    SDK_LOG_D("setSurface");
    mANativeWindow = ANativeWindow_fromSurface(env, surface);
    mTxEglHelp = new TXEglHelp();
    mTxEglHelp->initEgl(mANativeWindow);

    // TODO: opengl
    glViewport(0, 0, 720, 100);
    glClearColor(0.0f, 0.0f, 1.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    mTxEglHelp->swapBuffers();
    SDK_LOG_D("setSurface success");
}

// -------------------------------------- surface ----------------------------------

// TODO: 回调函数 callBackSurfaceCreated
void callBackSurfaceCreated(void *ctx) {
    SDK_LOG_D("callBackSurfaceCreated");
    TXEglThread *txEglThread = (TXEglThread *) ctx;

    // TODO: 绘制三角形
    program = createProgram(vertexSource, fragmentSource);
    if (program > 0) {
        avPosition = glGetAttribLocation(program, "av_Position");
        afPosition = glGetUniformLocation(program, "af_Position");
    }
}

// TODO: 回调函数 callBackSurfaceChanged
void callBackSurfaceChanged(int width, int height, void *ctx) {
    SDK_LOG_D("callBackSurfaceChanged");
    TXEglThread *txEglThread = (TXEglThread *) ctx;
    glViewport(0.0f, 0.0f, txEglThread->surfaceWidth, txEglThread->surfaceHeight);
}

// TODO: 回调函数 callBackSurfaceDraw
void callBackSurfaceDraw(void *ctx) {
    SDK_LOG_D("callBackSurfaceDraw");
    TXEglThread *txEglThread = (TXEglThread *) ctx;
    glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    // TODO: 绘制三角形
    if (program > 0) {
        glUseProgram(program);
        glEnableVertexAttribArray(avPosition);
        glVertexAttribPointer(avPosition, 2, GL_FLOAT, false, 8, vertex);
        glUniform4f(afPosition, 1.0f, 0.0f, 0.0f, 0.0f);
        glDrawArrays(GL_TRIANGLES, 0, 3);
        SDK_LOG_D("callBackSurfaceDraw 绘制三角形")
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_surfaceCreated(JNIEnv *env, jobject thiz, jobject surface) {
    // TODO: implement surfaceCreated()
    mANativeWindow = ANativeWindow_fromSurface(env, surface);
    mTXEglThread = new TXEglThread();
    mTXEglThread->callBackOnSurfaceCreated(callBackSurfaceCreated, mTXEglThread);
    mTXEglThread->callBackOnSurfaceChanged(callBackSurfaceChanged, mTXEglThread);
    mTXEglThread->callBackOnSurfaceDraw(callBackSurfaceDraw, mTXEglThread);
    mTXEglThread->surfaceCreated(mANativeWindow);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_surfaceChanged(JNIEnv *env, jobject thiz, jint width,
                                                 jint height) {
    // TODO: implement surfaceChanged()
    if (mTXEglThread != NULL) {
        mTXEglThread->surfaceChanged(width, height);
    }
}