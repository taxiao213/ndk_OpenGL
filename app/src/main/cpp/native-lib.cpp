#include <jni.h>
#include <string>
#include "egl/TXEglHelp.h"
#include "egl/TXEglThread.h"
#include "shader/ShaderSource.h"
#include "shader/ShaderUtil.h"
#include "matrix/TXMatrix.h"

JavaVM *jvm = NULL;
ANativeWindow *mANativeWindow = NULL;
TXEglHelp *mTxEglHelp = NULL;
TXEglThread *mTXEglThread = NULL;
int program;
GLint avPosition;
GLint afPosition;
GLint s_texture;
GLint u_Matrix;
GLuint textureid;
void *image = NULL;
int imageWidth;
int imageHeight;
float matrix[16];

// 获取 jvm
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *javaVm, void *reserved) {
    jvm = javaVm;
    JNIEnv *env;
    if (javaVm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    SDK_LOG_D("Jni_OnLoad");
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_taxiao_opengl_JniSdkImpl_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from c++";
    SDK_LOG_D("stringFromJNI");
    return env->NewStringUTF(hello.c_str());
}

// test
extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_setSurface(JNIEnv *env, jobject thiz, jobject surface) {
    // TODO: implement setSurface()
    SDK_LOG_D("setSurface");
    mANativeWindow = ANativeWindow_fromSurface(env, surface);
    mTxEglHelp = new TXEglHelp();
    mTxEglHelp->initEgl(mANativeWindow);

    // TODO: opengl
    glViewport(0, 0, 720, 100);
    glClearColor(0.0f, 0.0f, 1.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    mTxEglHelp->swapBuffers();
    SDK_LOG_D("setSurface success");
}

// -------------------------------------- surface ----------------------------------

// TODO: 回调函数 callBackSurfaceCreated
void callBackSurfaceCreated(void *ctx) {
    SDK_LOG_D("callBackSurfaceCreated");
    TXEglThread *txEglThread = (TXEglThread *) ctx;

    // TODO: 绘制三角形
    // 1.创建渲染程序
//    program = createProgram(vertexSource, fragmentSource);
//    if (program > 0) {
//        // 2.获取顶点坐标属性
//        avPosition = glGetAttribLocation(program, "av_Position");
//        afPosition = glGetUniformLocation(program, "af_Position");
//    }

    // TODO: 绘制纹理
//    program = createProgram(vertexSource2, fragmentSource2);
//    if (program > 0) {
//        // 2.获取顶点坐标和纹理坐标属性
//        avPosition = glGetAttribLocation(program, "av_Position");
//        afPosition = glGetAttribLocation(program, "af_Position");
//        s_texture = glGetUniformLocation(program, "s_texture");
//        // 3.创建纹理
//        glGenTextures(1, &textureid);
//        // 4.绑定纹理
//        glBindTexture(GL_TEXTURE_2D, textureid);
//        // 5.设置环绕和过滤方式 环绕（超出纹理坐标范围）：（s==x t==y GL_REPEAT 重复）
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
//        // 6.过滤（纹理像素映射到坐标点）：（缩小、放大：GL_LINEAR线性）边角无锯齿
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//        // 7.绑定图片
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, imageWidth, imageHeight, 0, GL_RGBA,
//                     GL_UNSIGNED_BYTE, image);
//        // 解绑纹理
//        glBindTexture(GL_TEXTURE_2D, 0);
//    }

    // TODO: 矩阵
    program = createProgram(vertexMatrixSource, fragmentMatrixSource);
    if (program > 0) {
        // 2.获取顶点坐标和纹理坐标属性
        avPosition = glGetAttribLocation(program, "av_Position");
        afPosition = glGetAttribLocation(program, "af_Position");
        s_texture = glGetUniformLocation(program, "s_texture");
        u_Matrix = glGetUniformLocation(program, "u_Matrix");
        initMatrix(matrix);
        for (int i = 0; i < 16; i++) {
            SDK_LOG_D("matrix: %d: %f", i, matrix[i]);
        }
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
        // 7.绑定图片
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, imageWidth, imageHeight, 0, GL_RGBA,
                     GL_UNSIGNED_BYTE, image);
        // 解绑纹理
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}

// TODO: 回调函数 callBackSurfaceChanged
void callBackSurfaceChanged(int width, int height, void *ctx) {
    SDK_LOG_D("callBackSurfaceChanged");
    TXEglThread *txEglThread = (TXEglThread *) ctx;
    glViewport(0.0f, 0.0f, txEglThread->surfaceWidth, txEglThread->surfaceHeight);
}

// TODO: 回调函数 callBackSurfaceDraw
void callBackSurfaceDraw(void *ctx) {
    SDK_LOG_D("callBackSurfaceDraw");
    TXEglThread *txEglThread = (TXEglThread *) ctx;
    glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    // TODO: 绘制三角形
//    if (program > 0) {
//        glUseProgram(program);
//        glEnableVertexAttribArray(avPosition);
//        glUniform4f(afPosition, 1.0f, 0.0f, 0.0f, 0.0f);
//        SDK_LOG_D("callBackSurfaceDraw 绘制三角形");
//        glVertexAttribPointer(avPosition, 2, GL_FLOAT, false, 8, vertex);
//        glDrawArrays(GL_TRIANGLES, 0, 3);
//    }

    // TODO: 绘制四边形
//    if (program > 0) {
//        glUseProgram(program);
//        glEnableVertexAttribArray(avPosition);
//        glUniform4f(afPosition, 1.0f, 0.0f, 0.0f, 0.0f);
//        glVertexAttribPointer(avPosition, 2, GL_FLOAT, false, 8, vertex2);
//        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
//        SDK_LOG_D("callBackSurfaceDraw 绘制四边形");
//    }

    // TODO: 绘制纹理
//    if (program > 0) {
//        glBindTexture(GL_TEXTURE_2D, textureid);
//        // 8.使用渲染器
//        glUseProgram(program);
//        // 9.使顶点坐标和纹理坐标属性数组有效
//        glEnableVertexAttribArray(avPosition);
//        glVertexAttribPointer(avPosition, 2, GL_FLOAT, false, 8, vertexData);
//        glEnableVertexAttribArray(afPosition);
//        glVertexAttribPointer(afPosition, 2, GL_FLOAT, false, 8, textureData);
//        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
//        // 解绑纹理
//        glBindTexture(GL_TEXTURE_2D, 0);
//    }

    // TODO: 矩阵
    if (program > 0) {
        glBindTexture(GL_TEXTURE_2D, textureid);
        // 8.使用渲染器
        glUseProgram(program);
        glUniformMatrix4fv(u_Matrix, 1, GL_FALSE, matrix);
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

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_surfaceCreated(JNIEnv *env, jobject thiz, jobject surface) {
    // TODO: implement surfaceCreated()
    mANativeWindow = ANativeWindow_fromSurface(env, surface);
    mTXEglThread = new TXEglThread();
    mTXEglThread->callBackOnSurfaceCreated(callBackSurfaceCreated, mTXEglThread);
    mTXEglThread->callBackOnSurfaceChanged(callBackSurfaceChanged, mTXEglThread);
    mTXEglThread->callBackOnSurfaceDraw(callBackSurfaceDraw, mTXEglThread);
    mTXEglThread->surfaceCreated(mANativeWindow);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_surfaceChanged(JNIEnv *env, jobject thiz, jint width,
                                                 jint height) {
    // TODO: implement surfaceChanged()
    if (mTXEglThread != NULL) {
        mTXEglThread->surfaceChanged(width, height);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_onDrawImage(JNIEnv *env, jobject thiz, jint width, jint height,
                                              jint size,
                                              jbyteArray bytes) {
    // TODO: implement onDrawImage()
    imageWidth = width;
    imageHeight = height;
    jbyte *elements = env->GetByteArrayElements(bytes, NULL);
    // 申请空间赋值
    image = malloc(size);
    memcpy(image, elements, size);
    env->ReleaseByteArrayElements(bytes, elements, 0);
}