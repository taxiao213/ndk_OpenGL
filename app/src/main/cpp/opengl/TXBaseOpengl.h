/**
 * opengl 基类
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_TXBASEOPENGL_H
#define OPENGL_TXBASEOPENGL_H

#include <GLES2/gl2.h>
#include "../android_log.h"
#include <cstring>
#include "../matrix/TXMatrix.h"

class TXBaseOpengl {
public:
    float *mVertex;
    float *mFragment;
    GLuint mProgram;
    GLuint vShader;
    GLuint fShader;
    int mSurfaceWidth;
    int mSurfaceHeight;
    float mMatrix[16];
public:
    TXBaseOpengl();

    ~TXBaseOpengl();

    virtual void onSurfaceCreate();

    virtual void onSurfaceChange(int width, int height);

    virtual void onSurfaceDraw();

    virtual void onSurfaceDestroy();

    virtual void setImage(void *data, int size, int imageWidth, int imageHeight);

    virtual void setYUVData(void *yuv_y, void *yuv_u, void *yuv_v, int width, int height);

    void setMatrix(int width, int height, int imageWidth, int imageHeight);

};


#endif //OPENGL_TXBASEOPENGL_H
