/**
 * 渲染YUV数据
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_TXOPENGLFILTERYUV_H
#define OPENGL_TXOPENGLFILTERYUV_H

#include "TXBaseOpengl.h"
#include "../shader/ShaderSource.h"
#include "../shader/ShaderUtil.h"
#include "../matrix/TXMatrix.h"

class TXOpenglFilterYUV : public TXBaseOpengl {
public:
    GLint avPosition;
    GLint afPosition;
    GLint s_texture;
    GLint u_Matrix;
    GLint sampler_y;
    GLint sampler_u;
    GLint sampler_v;
    GLuint textureid[3];
    void *y = NULL;
    void *u = NULL;
    void *v = NULL;
    int yuv_wdith = 0;
    int yuv_height = 0;
public:
    void onSurfaceCreate();

    void onSurfaceChange(int width, int height);

    void onSurfaceDraw();

    void onSurfaceDestroy();

    void setYUVData(void *yuv_y, void *yuv_u, void *yuv_v, int width, int height);
};


#endif //OPENGL_TXOPENGLFILTERYUV_H
