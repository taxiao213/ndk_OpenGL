/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#include "TXOpengl.h"

TXOpengl::TXOpengl() {

}

TXOpengl::~TXOpengl() {

}

void callBackSurfaceCreated2(void *data) {
    SDK_LOG_D("callBackSurfaceCreated");
    TXOpengl *opengl = (TXOpengl *) data;
    if (opengl != NULL) {
        if (opengl->mTXBaseOpengl != NULL) {
            opengl->mTXBaseOpengl->onSurfaceCreate();
        }
    }
}

void callBackSurfaceChanged2(int width, int height, void *data) {
    SDK_LOG_D("callBackSurfaceChanged");
    TXOpengl *opengl = (TXOpengl *) data;
    if (opengl != NULL) {
        if (opengl->mTXBaseOpengl != NULL) {
            opengl->mTXBaseOpengl->onSurfaceChange(width, height);
        }
    }
}

void callBackSurfaceDraw2(void *data) {
    SDK_LOG_D("callBackSurfaceDraw");
    TXOpengl *opengl = (TXOpengl *) data;
    if (opengl != NULL) {
        if (opengl->mTXBaseOpengl != NULL) {
            opengl->mTXBaseOpengl->onSurfaceDraw();
        }
    }
}

void callBackOnSurfaceChangedFilter2(int width, int height, void *data) {
    SDK_LOG_D("callBackOnSurfaceChangedFilter2");
    TXOpengl *opengl = (TXOpengl *) data;
    if (opengl != NULL) {
        if (opengl->mTXBaseOpengl != NULL) {
            opengl->mTXBaseOpengl->onSurfaceDestroy();
            delete opengl->mTXBaseOpengl;
            opengl->mTXBaseOpengl = NULL;
        }
        SDK_LOG_D("callBackOnSurfaceChangedFilter2 创建过滤器");
        opengl->mTXBaseOpengl = new TXOpenglFilterTwo();
        opengl->mTXBaseOpengl->onSurfaceCreate();
        opengl->mTXBaseOpengl->onSurfaceChange(width, height);
        opengl->mTXBaseOpengl->setImage(opengl->mData, 0, opengl->mPicWidth, opengl->mPicHeight);
        opengl->mTXEglThread->notifyThread();
    }
}

void callBackOnSurfaceDestroy2(void *data) {
    SDK_LOG_D("callBackOnSurfaceDestroy2");
    TXOpengl *opengl = (TXOpengl *) data;
    if (opengl != NULL) {
        if (opengl->mTXBaseOpengl != NULL) {
            opengl->mTXBaseOpengl->onSurfaceDestroy();
            delete opengl->mTXBaseOpengl;
            opengl->mTXBaseOpengl = NULL;
        }
    }
}

void TXOpengl::onSurfaceCreate(JNIEnv *env, jobject surface) {
    SDK_LOG_D("onSurfaceCreate");
    mANativeWindow = ANativeWindow_fromSurface(env, surface);
    mTXEglThread = new TXEglThread();
    mTXEglThread->callBackOnSurfaceCreated(callBackSurfaceCreated2, this);
    mTXEglThread->callBackOnSurfaceChanged(callBackSurfaceChanged2, this);
    mTXEglThread->callBackOnSurfaceDraw(callBackSurfaceDraw2, this);
    mTXEglThread->callBackOnSurfaceChangedFilter(callBackOnSurfaceChangedFilter2, this);
    mTXEglThread->callBackOnSurfaceDestroy(callBackOnSurfaceDestroy2, this);
    mTXBaseOpengl = new TXOpenglFilterOne();
    mTXEglThread->surfaceCreated(mANativeWindow);
}

void TXOpengl::onSurfaceChange(int width, int height) {
    SDK_LOG_D("onSurfaceChange");
    if (mTXEglThread != NULL) {
        mTXEglThread->surfaceChanged(width, height);
    }
    if (mTXBaseOpengl != NULL) {
        mTXBaseOpengl->onSurfaceChange(width, height);
    }
}

void TXOpengl::onSurfaceDestroy() {
    SDK_LOG_D("onSurfaceDestroy");
    if (mTXEglThread != NULL) {
        mTXEglThread->surfaceDestroy();
        mTXEglThread = NULL;
    }
    if (mTXBaseOpengl != NULL) {
        mTXBaseOpengl->onSurfaceDestroy();
        delete mTXBaseOpengl;
        mTXBaseOpengl = NULL;
    }
    if (mANativeWindow != NULL) {
        ANativeWindow_release(mANativeWindow);
        mANativeWindow = NULL;
    }
    if (mData != NULL) {
        free(mData);
        mData = NULL;
    }
}

void TXOpengl::setImage(void *data, int size, int imageWidth, int imageHeight) {
    SDK_LOG_D("setImage");
    if (mTXBaseOpengl != NULL) {
        if (mData != NULL) {
            free(mData);
            mData = NULL;
        }
        mData = malloc(size);
        memcpy(mData, data, size);
        mPicWidth = imageWidth;
        mPicHeight = imageHeight;
        mTXBaseOpengl->setImage(mData, size, imageWidth, imageHeight);
        if (mTXEglThread != NULL) {
            mTXEglThread->notifyThread();
        }
    }
}

void TXOpengl::setRenderType(int type) {
    if (mTXEglThread != NULL) {
        mTXEglThread->setRenderType(type);
    }
}

void TXOpengl::onSurfaceChangedFilter() {
    if (mTXEglThread != NULL) {
        mTXEglThread->surfaceChangedFilter();
    }
}
