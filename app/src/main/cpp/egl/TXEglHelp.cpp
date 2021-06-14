/**
 * egl 初始化
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/

#include "TXEglHelp.h"

TXEglHelp::TXEglHelp() {
    this->mEGLDisplay = EGL_NO_DISPLAY;
    this->mEGLContext = EGL_NO_CONTEXT;
    this->mEGLSurface = EGL_NO_SURFACE;
    this->mEGLConfig = NULL;
}

TXEglHelp::~TXEglHelp() {


}

int TXEglHelp::initEgl(EGLNativeWindowType windowType) {
    // 1.初始化 EGLDisplay
    SDK_LOG_D("初始化 EGLDisplay");
    mEGLDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (mEGLDisplay == EGL_NO_DISPLAY) {
        SDK_LOG_E("初始化失败 EGLDisplay");
        return -1;
    }

    // 2.初始化 eglInitialize
    SDK_LOG_D("初始化 eglInitialize");
    EGLint *egLint = new EGLint(2);
    if (!eglInitialize(mEGLDisplay, &egLint[0], &egLint[1])) {
        SDK_LOG_E("初始化失败 eglInitialize");
        return -1;
    }

    // 3.设置显示的相关属性
    // 占用一个字节 8 bit, 以 EGL_NONE 结尾, 每次获取 2 位数值
    // 颜色 R G B ，depth 深度, stencil 模板等级
    SDK_LOG_D("初始化 设置显示的相关属性");
    const EGLint attrib[] = {
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_ALPHA_SIZE, 8,
            EGL_DEPTH_SIZE, 8,
            EGL_STENCIL_SIZE, 8,
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL_NONE
    };

    // 4.初始化 eglChooseConfig
    SDK_LOG_D("初始化 eglChooseConfig");
    EGLint num_config;
    // 传 1 只获取一个 config，传 null 在某些设备可能获取不到值
    if (!eglChooseConfig(mEGLDisplay, attrib, NULL, 1, &num_config)) {
        SDK_LOG_E("初始化失败 eglChooseConfig1");
        return -1;
    }
    // 获取到的 num_config 传进去，获取到 EGLConfig
    if (!eglChooseConfig(mEGLDisplay, attrib, &mEGLConfig, num_config, &num_config)) {
        SDK_LOG_E("初始化失败 eglChooseConfig2");
        return -1;
    }

    // 5.初始化 eglCreateContext
    // CLIENT_VERSION egl版本 ，这里用的是 2 版本
    SDK_LOG_D("初始化 eglCreateContext");
    EGLint attrib_list[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };
    mEGLContext = eglCreateContext(mEGLDisplay, mEGLConfig, EGL_NO_CONTEXT, attrib_list);
    if (mEGLContext == NULL) {
        SDK_LOG_E("初始化失败 eglCreateContext");
        return -1;
    }

    // 6.初始化 eglCreateWindowSurface
    SDK_LOG_D("初始化 eglCreateWindowSurface");
    mEGLSurface = eglCreateWindowSurface(mEGLDisplay, mEGLConfig, windowType, NULL);
    if (mEGLSurface == NULL) {
        SDK_LOG_E("初始化失败 eglCreateWindowSurface");
        return -1;
    }

    // 7.初始化 eglMakeCurrent 绑定EglContext和Surface到显示设备中
    SDK_LOG_D("初始化 eglMakeCurrent");
    if (!eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
        SDK_LOG_E("初始化失败 eglMakeCurrent");
        return -1;
    }

    SDK_LOG_D("egl init success! ");
    return 0;
}

int TXEglHelp::swapBuffers() {
    if (mEGLDisplay == NULL || mEGLSurface == NULL || mEGLDisplay == EGL_NO_DISPLAY ||
        mEGLSurface == EGL_NO_SURFACE) {
        SDK_LOG_E("mEGLDisplay 为 null , mEGLSurface  为 null ");
        return -1;
    }
    // 8. 初始化 eglSwapBuffers 刷新数据 显示渲染场景
    if (!eglSwapBuffers(mEGLDisplay, mEGLSurface)) {
        SDK_LOG_E("初始化失败 eglSwapBuffers");
        return -1;
    }
    return 0;
}

int TXEglHelp::destroyEgl() {

    if (mEGLDisplay == NULL || mEGLSurface == NULL || mEGLDisplay == EGL_NO_DISPLAY ||
        mEGLSurface == EGL_NO_SURFACE) {
        SDK_LOG_E("mEGLDisplay 为 null , mEGLSurface  为 null ");
        return -1;
    }
    if (mEGLDisplay != EGL_NO_DISPLAY) {
        SDK_LOG_E("destroyEgl eglMakeCurrent ");
        eglMakeCurrent(mEGLDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    }

    if (mEGLDisplay != EGL_NO_DISPLAY && mEGLSurface != EGL_NO_SURFACE) {
        SDK_LOG_E("destroyEgl eglDestroySurface ");
        eglDestroySurface(mEGLDisplay, mEGLSurface);
        mEGLSurface = EGL_NO_SURFACE;
    }

    if (mEGLDisplay != EGL_NO_DISPLAY && mEGLContext != EGL_NO_CONTEXT) {
        SDK_LOG_E("destroyEgl eglDestroyContext ");
        eglDestroyContext(mEGLDisplay, mEGLContext);
        mEGLContext = EGL_NO_CONTEXT;
    }

    if (mEGLDisplay != EGL_NO_DISPLAY) {
        SDK_LOG_E("destroyEgl eglTerminate ");
        eglTerminate(mEGLDisplay);
        mEGLDisplay = EGL_NO_DISPLAY;
    }
    return 0;
}
