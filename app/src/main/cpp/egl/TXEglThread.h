/**
 * EGL 线程
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_TXEGLTHREAD_H
#define OPENGL_TXEGLTHREAD_H

#include <EGL/eglplatform.h>
#include "android/native_window.h"
#include "android/native_window_jni.h"
#include "pthread.h"
#include "TXEglHelp.h"
#include <GLES2/gl2.h>
#include <unistd.h>

#define RENDER_AUTO 1 // 自动渲染
#define RENDER_MANUAL 2 // 手动渲染，阻塞线程

class TXEglThread {

public:
    pthread_t mEglPthread = -1;
    ANativeWindow *mANativeWindow = NULL;
    bool isCreate = false;
    bool isChange = false;
    bool isExit = false;
    bool isStart = false;
    int surfaceWidth = 0;
    int surfaceHeight = 0;

    // 回调函数 OnSurfaceCreated
    typedef void (*OnSurfaceCreated)(void *);

    OnSurfaceCreated onSurfaceCreated;
    void *onSurfaceCreatedCtx;

    // 回调函数 OnSurfaceChanged
    typedef void (*OnSurfaceChanged)(int width, int height, void *);

    OnSurfaceChanged onSurfaceChanged;
    void *onSurfaceChangedCtx;

    // 回调函数 OnSurfaceDraw
    typedef void (*OnSurfaceDraw)(void *);

    OnSurfaceDraw onSurfaceDraw;
    void *onSurfaceDrawCtx;

    // 线程锁
    pthread_mutex_t mutex;
    pthread_cond_t mcond;
    int mRenderType =0;

public :
    TXEglThread();

    ~TXEglThread();

    void surfaceCreated(EGLNativeWindowType nativeWindow);

    void surfaceChanged(int width, int height);

    void callBackOnSurfaceCreated(OnSurfaceCreated onSurfaceCreated, void *ctx);

    void callBackOnSurfaceChanged(OnSurfaceChanged onSurfaceChanged, void *ctx);

    void callBackOnSurfaceDraw(OnSurfaceDraw onSurfaceDraw, void *ctx);

    void setRenderType(int renderType);

    void notifyThread();

};


#endif //OPENGL_TXEGLTHREAD_H
