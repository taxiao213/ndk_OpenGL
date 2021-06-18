/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#include "TXOpenglFilterOne.h"

void TXOpenglFilterOne::onSurfaceCreate() {
    SDK_LOG_D("onSurfaceCreate");
    mProgram = createProgram(vertexMatrixSource, fragmentMatrixSource);
    if (mProgram > 0) {
        // 2.获取顶点坐标和纹理坐标属性
        avPosition = glGetAttribLocation(mProgram, "av_Position");
        afPosition = glGetAttribLocation(mProgram, "af_Position");
        s_texture = glGetUniformLocation(mProgram, "s_texture");
        u_Matrix = glGetUniformLocation(mProgram, "u_Matrix");
        initMatrix(mMatrix);
//        rotateMatrixForZ(45, matrix);
//        translationMatrix(0.5, 0.5, matrix);

        // 3.创建纹理
        glGenTextures(1, &textureid);
        // 4.绑定纹理
        glBindTexture(GL_TEXTURE_2D, textureid);
        // 5.设置环绕和过滤方式 环绕（超出纹理坐标范围）：（s==x t==y GL_REPEAT 重复）
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // 6.过滤（纹理像素映射到坐标点）：（缩小、放大：GL_LINEAR线性）边角无锯齿
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // 解绑纹理
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}

void TXOpenglFilterOne::onSurfaceChange(int width, int height) {
    SDK_LOG_D("onSurfaceChange");
    this->mSurfaceWidth = width;
    this->mSurfaceHeight = height;
    setMatrix(width, height);
}

void TXOpenglFilterOne::onSurfaceDraw() {
    SDK_LOG_D("onSurfaceDraw");
    glClearColor(0.0f, 1.0f, 1.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    // TODO: 矩阵
    if (mProgram > 0) {
        glBindTexture(GL_TEXTURE_2D, textureid);
        // 7.绑定图片
        if (image != NULL) {
            SDK_LOG_D("onSurfaceDraw image != NULL");
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, imageWidth, imageHeight, 0, GL_RGBA,
                         GL_UNSIGNED_BYTE, image);
        }
        // 8.使用渲染器
        glUseProgram(mProgram);
        glUniformMatrix4fv(u_Matrix, 1, GL_FALSE, mMatrix);
        // 动态绑定纹理
//        glActiveTexture(GL_TEXTURE0);
//        glUniform1i(s_texture, 0);

        // 9.使顶点坐标和纹理坐标属性数组有效
        glEnableVertexAttribArray(avPosition);
        glVertexAttribPointer(avPosition, 2, GL_FLOAT, false, 8, vertexMatrixData);
        glEnableVertexAttribArray(afPosition);
        glVertexAttribPointer(afPosition, 2, GL_FLOAT, false, 8, textureMatrixData);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        // 解绑纹理
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}

void TXOpenglFilterOne::onSurfaceDestroy() {
    SDK_LOG_D("onSurfaceDestroy");
    if (image != NULL) {
        free(image);
        image = NULL;
    }
}

void TXOpenglFilterOne::setImage(void *data, int size, int imageWidth, int imageHeight) {
    SDK_LOG_D("setImage");
    this->imageWidth = imageWidth;
    this->imageHeight = imageHeight;
    this->image = data;
    if (mSurfaceWidth > 0 && mSurfaceHeight > 0) {
        setMatrix(mSurfaceWidth, mSurfaceHeight);
    }
}

void TXOpenglFilterOne::setMatrix(int width, int height) {
    SDK_LOG_D("setMatrix");
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
