/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/

#include "TXEglThread.h"


TXEglThread::TXEglThread() {
    pthread_mutex_init(&mutex, NULL);
    pthread_cond_init(&mcond, NULL);
    // TODO:渲染模式
//    mRenderType = RENDER_AUTO;
    mRenderType = RENDER_MANUAL;
}

TXEglThread::~TXEglThread() {
    pthread_mutex_destroy(&mutex);
    pthread_cond_destroy(&mcond);
}

void *eglThreadImpl(void *data) {
    TXEglThread *txEglThread = (TXEglThread *) data;
    if (txEglThread != NULL) {
        if (txEglThread->mANativeWindow != NULL) {
            TXEglHelp *txEglHelp = new TXEglHelp();
            txEglHelp->initEgl(txEglThread->mANativeWindow);
            txEglThread->isExit = false;
            while (true) {
                if (txEglThread->isCreate) {
                    SDK_LOG_D("eglThreadImpl call surfaceCreate");
                    txEglThread->isCreate = false;
                    // TODO: 回调到外层调用
                    txEglThread->onSurfaceCreated(txEglThread->onSurfaceCreatedCtx);
                }
                if (txEglThread->isChange) {
                    SDK_LOG_D("eglThreadImpl call surfaceChanged");
                    txEglThread->isChange = false;
//                    glViewport(0.0f, 0.0f, txEglThread->surfaceWidth, txEglThread->surfaceHeight);
                    // TODO: 回调到外层调用
                    txEglThread->onSurfaceChanged(txEglThread->surfaceWidth,
                                                  txEglThread->surfaceHeight,
                                                  txEglThread->onSurfaceChangedCtx);
                    txEglThread->isStart = true;
                }
                if (txEglThread->isChangeFilter) {
                    SDK_LOG_D("eglThreadImpl call onSurfaceChangedFilter");
                    txEglThread->isChangeFilter = false;
                    // TODO: 回调到外层调用
                    txEglThread->onSurfaceChangedFilter(txEglThread->surfaceWidth,
                                                        txEglThread->surfaceHeight,
                                                        txEglThread->onSurfaceChangedFilterCtx);
                }
                if (txEglThread->isStart) {
                    SDK_LOG_D("eglThreadImpl call surfaceDraw");
//                    glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
//                    glClear(GL_COLOR_BUFFER_BIT);
                    // TODO: 回调到外层调用
                    txEglThread->onSurfaceDraw(txEglThread->onSurfaceDrawCtx);
                    txEglHelp->swapBuffers();
                }
                if (txEglThread->mRenderType == RENDER_AUTO) {
                    // 单位微妙 60帧 每秒60帧
                    SDK_LOG_D("eglThreadImpl RENDER_AUTO");
                    usleep(1000000 / 60);
                } else {
                    SDK_LOG_D("eglThreadImpl RENDER_MANUAL");
                    pthread_mutex_lock(&txEglThread->mutex);
                    pthread_cond_wait(&txEglThread->mcond, &txEglThread->mutex);
                    pthread_mutex_unlock(&txEglThread->mutex);
                }
                SDK_LOG_D("eglThreadImpl draw");
                if (txEglThread->isExit) {
                    SDK_LOG_D("eglThreadImpl call surfaceExit");
                    txEglThread->onSurfaceDsetroy(txEglThread->onSurfaceDsetroyCtx);
                    break;
                }
            }
        }
    }
//    pthread_exit(&txEglThread->mEglPthread);
    return 0;
}

void TXEglThread::surfaceCreated(EGLNativeWindowType nativeWindow) {
    SDK_LOG_D("surfaceCreated");
    if (mEglPthread == -1) {
        mANativeWindow = nativeWindow;
        isCreate = true;
        pthread_create(&mEglPthread, NULL, eglThreadImpl, this);
        SDK_LOG_D("surfaceCreated success");
    }
}

void TXEglThread::surfaceChanged(int width, int height) {
    this->isChange = true;
    this->surfaceWidth = width;
    this->surfaceHeight = height;
    notifyThread();
}

void TXEglThread::setRenderType(int renderType) {
    this->mRenderType = renderType;
}

void TXEglThread::notifyThread() {
    pthread_mutex_lock(&mutex);
    pthread_cond_signal(&mcond);
    pthread_mutex_unlock(&mutex);
}

void TXEglThread::surfaceChangedFilter() {
    isChangeFilter = true;
    notifyThread();
}

void TXEglThread::surfaceDestroy() {
    isExit = true;
    notifyThread();
    pthread_join(mEglPthread, NULL);
    mANativeWindow = NULL;
    mEglPthread = -1;
}

void
TXEglThread::callBackOnSurfaceCreated(TXEglThread::OnSurfaceCreated onSurfaceCreated, void *ctx) {
    this->onSurfaceCreated = onSurfaceCreated;
    this->onSurfaceCreatedCtx = ctx;
}

void
TXEglThread::callBackOnSurfaceChanged(TXEglThread::OnSurfaceChanged onSurfaceChanged, void *ctx) {
    this->onSurfaceChanged = onSurfaceChanged;
    this->onSurfaceChangedCtx = ctx;
}

void TXEglThread::callBackOnSurfaceDraw(TXEglThread::OnSurfaceDraw onSurfaceDraw, void *ctx) {
    this->onSurfaceDraw = onSurfaceDraw;
    this->onSurfaceDrawCtx = ctx;
}


void TXEglThread::callBackOnSurfaceChangedFilter(
        TXEglThread::OnSurfaceChangedFilter onSurfaceChangedFilter, void *ctx) {
    this->onSurfaceChangedFilter = onSurfaceChangedFilter;
    this->onSurfaceChangedFilterCtx = ctx;
}


void
TXEglThread::callBackOnSurfaceDestroy(TXEglThread::OnSurfaceDsetroy onSurfaceDsetroy, void *ctx) {
    this->onSurfaceDsetroy = onSurfaceDsetroy;
    this->onSurfaceDsetroyCtx = ctx;
}

