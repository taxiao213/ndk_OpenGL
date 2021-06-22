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
#include "TXOpenglFilterTwo.h"
#include "TXOpenglFilterYUV.h"
#include "../egl/TXEglThread.h"

#define FILTER_IMAGE 1
#define FILTER_YUV 2

class TXOpengl {
public:
    ANativeWindow *mANativeWindow;
    TXEglThread *mTXEglThread;
    TXBaseOpengl *mTXBaseOpengl;
    void *mData = NULL;
    int mPicWidth;
    int mPicHeight;
    int filterType;
public:
    TXOpengl();

    ~TXOpengl();

    void onSurfaceCreate(JNIEnv *env, jobject surface);

    void onSurfaceChange(int width, int height);

    void onSurfaceDestroy();

    void setImage(void *data, int size, int imageWidth, int imageHeight);

    void setRenderType(int type);

    void onSurfaceChangedFilter();// 切换滤镜

    void setYUVData(void *yuv_y, void *yuv_u, void *yuv_v, int width, int height);

    void setFilterType(int type);
};


#endif //OPENGL_TXOPENGL_H
