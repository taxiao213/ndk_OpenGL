/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/



#include "TXBaseOpengl.h"

TXBaseOpengl::TXBaseOpengl() {
    mVertex = new float[8];
    float vertex[8] = {
            -1, -1,
            1, -1,
            -1, 1,
            1, 1,
    };
    memcpy(mVertex, vertex, sizeof(vertex));

    mFragment = new float[8];
    float fragment[8] = {
            0, 1,
            1, 1,
            0, 0,
            1, 0
    };
    memcpy(mFragment, fragment, sizeof(fragment));

}

TXBaseOpengl::~TXBaseOpengl() {
    delete[]mVertex;
    delete[]mFragment;
}

void TXBaseOpengl::onSurfaceCreate() {

}

void TXBaseOpengl::onSurfaceChange(int width, int height) {

}

void TXBaseOpengl::onSurfaceDraw() {

}

void TXBaseOpengl::onSurfaceDestroy() {

}

void TXBaseOpengl::setImage(void *data, int size, int imageWidth, int imageHeight) {

}

void TXBaseOpengl::setYUVData(void *yuv_y, void *yuv_u, void *yuv_v, int width, int height) {

}

void TXBaseOpengl::setMatrix(int width, int height, int imageWidth, int imageHeight) {
    SDK_LOG_D("setMatrix width:%d, height:%d, imageWidth:%d, imageHeight:%d ", width, height,
              imageWidth, imageHeight);
    glViewport(0, 0, width, height);
    float screen = 1.0f * width / height;
    float image = 1.0f * imageWidth / imageHeight;
    if (screen > image) {
        // 宽度缩放
        float scale = width / (1.0f * height / imageHeight * imageWidth);
        reflectionMatrix(-scale, scale, -1, 1, mMatrix);
    } else {
        // 高度缩放
        float scale = height / (1.0f * width / imageWidth * imageHeight);
        reflectionMatrix(-1, 1, -scale, scale, mMatrix);
    }
}
