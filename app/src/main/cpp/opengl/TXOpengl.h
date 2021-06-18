/**
 *
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_TXOPENGL_H
#define OPENGL_TXOPENGL_H

#include "TXOpenglFilterOne.h"
#include "../egl/TXEglThread.h"

class TXOpengl {
public:
    ANativeWindow *mANativeWindow;
    TXEglThread *mTXEglThread;
    TXBaseOpengl *mTXBaseOpengl;
    void *mData = NULL;
public:
    TXOpengl();

    ~TXOpengl();

    void onSurfaceCreate(JNIEnv *env, jobject surface);

    void onSurfaceChange(int width, int height);

    void onSurfaceDestroy();

    void setImage(void *data, int size, int imageWidth, int imageHeight);

    void setRenderType(int type);
};


#endif //OPENGL_TXOPENGL_H
