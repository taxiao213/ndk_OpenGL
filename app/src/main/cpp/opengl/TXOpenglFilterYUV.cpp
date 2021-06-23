/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#include "TXOpenglFilterYUV.h"

void TXOpenglFilterYUV::onSurfaceCreate() {
    SDK_LOG_D("onSurfaceCreate");
    // 1.创建program
    mProgram = createProgram(vertexMatrixSource3, fragmentMatrixSource3, &vShader, &fShader);
    if (mProgram > 0) {
        // 2.获取顶点坐标和纹理坐标属性
        avPosition = glGetAttribLocation(mProgram, "av_Position");
        afPosition = glGetAttribLocation(mProgram, "af_Position");
        s_texture = glGetUniformLocation(mProgram, "s_texture");
        u_Matrix = glGetUniformLocation(mProgram, "u_Matrix");
        sampler_y = glGetUniformLocation(mProgram, "sampler_y");
        sampler_u = glGetUniformLocation(mProgram, "sampler_u");
        sampler_v = glGetUniformLocation(mProgram, "sampler_v");

        initMatrix(mMatrix);
//        rotateMatrixForZ(45, matrix);
//        translationMatrix(0.5, 0.5, matrix);

        // 3.创建绑定纹理
        glGenTextures(3, textureid);
        for (int i = 0; i < 3; i++) {
            // 4.绑定纹理
            glBindTexture(GL_TEXTURE_2D, textureid[i]);
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

}

void TXOpenglFilterYUV::onSurfaceChange(int width, int height) {
    SDK_LOG_D("onSurfaceChange");
    this->mSurfaceWidth = width;
    this->mSurfaceHeight = height;
    setMatrix(width, height, yuv_wdith, yuv_height);
}

void TXOpenglFilterYUV::onSurfaceDraw() {
    SDK_LOG_D("onSurfaceDraw");
    glClearColor(0.0f, 1.0f, 1.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    // TODO: 矩阵
    if (mProgram > 0) {
        // 8.使用渲染器
        glUseProgram(mProgram);
        glUniformMatrix4fv(u_Matrix, 1, GL_FALSE, mMatrix);

        // 9.使顶点坐标和纹理坐标属性数组有效
        glEnableVertexAttribArray(avPosition);
        glVertexAttribPointer(avPosition, 2, GL_FLOAT, false, 8, vertexMatrixData3);
        glEnableVertexAttribArray(afPosition);
        glVertexAttribPointer(afPosition, 2, GL_FLOAT, false, 8, textureMatrixData3);
        if (yuv_wdith > 0 && yuv_height > 0) {
            // 动态绑定纹理
            if (y != NULL) {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, textureid[0]);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, yuv_wdith, yuv_height, 0, GL_LUMINANCE,
                             GL_UNSIGNED_BYTE, y);
                glUniform1i(sampler_y, 0);
            }
            if (u != NULL) {
                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D, textureid[1]);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, yuv_wdith / 2, yuv_height / 2, 0,
                             GL_LUMINANCE,
                             GL_UNSIGNED_BYTE, u);
                glUniform1i(sampler_u, 1);
            }
            if (v != NULL) {
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, textureid[2]);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, yuv_wdith / 2, yuv_height / 2, 0,
                             GL_LUMINANCE,
                             GL_UNSIGNED_BYTE, v);
                glUniform1i(sampler_v, 2);
            }

            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            // 解绑纹理
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }
}

void TXOpenglFilterYUV::onSurfaceDestroy() {
    SDK_LOG_D("onSurfaceDestroy");
    if (y != NULL) {
        free(y);
        y = NULL;
    }
    if (u != NULL) {
        free(u);
        u = NULL;
    }
    if (v != NULL) {
        free(v);
        v = NULL;
    }
    glDeleteTextures(3, textureid);
    glDetachShader(mProgram, vShader);
    glDetachShader(mProgram, fShader);
    glDeleteShader(vShader);
    glDeleteShader(fShader);
    glDeleteProgram(mProgram);
}

void TXOpenglFilterYUV::setYUVData(void *yuv_y, void *yuv_u, void *yuv_v, int width, int height) {
    if (width > 0 && height > 0) {
        yuv_wdith = width;
        yuv_height = height;
        if (y != NULL) {
            free(y);
            y = NULL;
        }
        if (u != NULL) {
            free(u);
            u = NULL;
        }
        if (v != NULL) {
            free(v);
            v = NULL;
        }
        y = malloc(width * height);
        u = malloc(width * height / 4);
        v = malloc(width * height / 4);

        memcpy(y, yuv_y, width * height);
        memcpy(u, yuv_u, width * height / 4);
        memcpy(v, yuv_v, width * height / 4);
    }
}

