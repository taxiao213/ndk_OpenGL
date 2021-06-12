/**
 * egl 初始化
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/

#include "EGL/egl.h"
#include "../android_log.h"
#ifndef OPENGL_TXEGLHELP_H
#define OPENGL_TXEGLHELP_H

class TXEglHelp {
public:
    EGLDisplay mEGLDisplay; // 显示
    EGLConfig mEGLConfig; // config
    EGLContext mEGLContext; // 上下文
    EGLSurface mEGLSurface;// 缓冲数据
public:
    TXEglHelp();

    ~TXEglHelp();

    // 初始化 egl
    // EGLNativeWindowType ANativeWindow*
    // windowType 是 JAVA 传的 surface，显示窗口
    int initEgl(EGLNativeWindowType windowType);

    // 刷新数据
    int swapBuffers();

    // 销毁 egl
    int destroyEgl();
};


#endif //OPENGL_TXEGLHELP_H
