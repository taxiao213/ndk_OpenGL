/**
 * 过滤器
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/

#ifndef OPENGL_TXOPENGLFILTERONE_H
#define OPENGL_TXOPENGLFILTERONE_H

#include "TXBaseOpengl.h"
#include "../shader/ShaderSource.h"
#include "../shader/ShaderUtil.h"

class TXOpenglFilterOne : public TXBaseOpengl {
public:
    GLint avPosition;
    GLint afPosition;
    GLint s_texture;
    GLint u_Matrix;
    GLuint textureid;
    int imageWidth;
    int imageHeight;
    void *image;
public:
    void onSurfaceCreate();

    void onSurfaceChange(int width, int height);

    void onSurfaceDraw();

    void onSurfaceDestroy();

    void setImage(void *data, int size, int imageWidth, int imageHeight);
};


#endif //OPENGL_TXOPENGLFILTERONE_H
