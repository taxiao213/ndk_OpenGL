#include <jni.h>
#include <string>
//#include "egl/TXEglHelp.h"
//#include "egl/TXEglThread.h"
//#include "shader/ShaderSource.h"
//#include "shader/ShaderUtil.h"
//#include "matrix/TXMatrix.h"

#include "opengl/TXOpengl.h"
#include "audio/TXOpenSLES.h"
#include "rtmp/RtmpPush.h"
#include "callback/TXCallBack.h"

JavaVM *jvm = NULL;
ANativeWindow *mANativeWindow = NULL;
TXEglHelp *mTxEglHelp = NULL;
TXEglThread *mTXEglThread = NULL;
GLuint program;
GLint avPosition;
GLint afPosition;
GLint s_texture;
GLint u_Matrix;
GLuint textureid;
void *image = NULL;
int imageWidth;
int imageHeight;
float matrix[16];
TXOpengl *txOpengl = NULL;
int filterType;
TXCallBack *txCallBack = NULL;
RtmpPush *rtmp;

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

JNIEXPORT jint JNICALL JNI_OnUnLoad(JavaVM *javaVm, void *reserved) {
    jvm = NULL;
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
    program = createProgram(vertexMatrixSource, fragmentMatrixSource, 0, 0);
    if (program > 0) {
        // 2.获取顶点坐标和纹理坐标属性
        avPosition = glGetAttribLocation(program, "av_Position");
        afPosition = glGetAttribLocation(program, "af_Position");
        s_texture = glGetUniformLocation(program, "s_texture");
        u_Matrix = glGetUniformLocation(program, "u_Matrix");
        initMatrix(matrix);
//        rotateMatrixForZ(45, matrix);
//        translationMatrix(0.5, 0.5, matrix);

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
    float screen = 1.0f * width / height;
    float image = 1.0f * imageWidth / imageHeight;
    if (screen > image) {
        // 宽度缩放
        float scale = width / (1.0f * height / imageHeight * imageWidth);
        reflectionMatrix(-scale, scale, -1, 1, matrix);
    } else {
        // 高度缩放
        float scale = height / (1.0f * width / imageWidth * imageHeight);
        reflectionMatrix(-1, 1, -scale, scale, matrix);
    }
    if (txEglThread != NULL) {
        txEglThread->notifyThread();
    }
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
Java_com_taxiao_opengl_JniSdkImpl_drawImage(JNIEnv *env, jobject thiz, jint width, jint height,
                                            jint size, jbyteArray bytes) {
    // TODO: implement drawImage()
    imageWidth = width;
    imageHeight = height;
    jbyte *elements = env->GetByteArrayElements(bytes, NULL);
    // 申请空间赋值
    image = malloc(size);
    memcpy(image, elements, size);
    env->ReleaseByteArrayElements(bytes, elements, 0);
    if (mTXEglThread != NULL) {
        mTXEglThread->notifyThread();
    }
}

// -------------------------------------- surface  另一种写法 ----------------------------------

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_onSurfaceCreated(JNIEnv *env, jobject thiz, jobject surface) {
    // TODO: implement onSurfaceCreated()
    if (txOpengl == NULL) {
        txOpengl = new TXOpengl();
        txOpengl->setFilterType(filterType);
        txOpengl->onSurfaceCreate(env, surface);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_onSurfaceChanged(JNIEnv *env, jobject thiz, jint width,
                                                   jint height) {
    // TODO: implement onSurfaceChanged()
    if (txOpengl != NULL) {
        txOpengl->onSurfaceChange(width, height);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_onSurfaceDestroy(JNIEnv *env, jobject thiz) {
    // TODO: implement onSurfaceDestroy()
    if (txOpengl != NULL) {
        txOpengl->onSurfaceDestroy();
        delete txOpengl;
        txOpengl = NULL;
        filterType = -1;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_onSurfaceChangedFilter(JNIEnv *env, jobject thiz) {
    // TODO: implement onSurfaceChangedFilter()
    if (txOpengl != NULL) {
        txOpengl->onSurfaceChangedFilter();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_onDrawImage(JNIEnv *env, jobject thiz, jint width, jint height,
                                              jint size,
                                              jbyteArray bytes) {
    // TODO: implement onDrawImage()
    SDK_LOG_D("onDrawImage");
    if (txOpengl != NULL) {
        SDK_LOG_D("onDrawImage1");
        jbyte *data = env->GetByteArrayElements(bytes, NULL);
        txOpengl->setImage(data, size, width, height);
        env->ReleaseByteArrayElements(bytes, data, 0);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_setRenderType(JNIEnv *env, jobject thiz, jint type) {
    // TODO: implement setRenderType()
    if (txOpengl != NULL) {
        SDK_LOG_D("setRenderType");
        txOpengl->setRenderType(type);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_setYUVData(JNIEnv *env, jobject thiz, jbyteArray yuv_y,
                                             jbyteArray yuv_u, jbyteArray yuv_v, jint width,
                                             jint height) {
    // TODO: implement setYUVData()
    // 设置YUV数据
    if (txOpengl != NULL) {
        SDK_LOG_D("setYUVData");
        jbyte *y = env->GetByteArrayElements(yuv_y, NULL);
        jbyte *u = env->GetByteArrayElements(yuv_u, NULL);
        jbyte *v = env->GetByteArrayElements(yuv_v, NULL);
        txOpengl->setYUVData(y, u, v, width, height);
        env->ReleaseByteArrayElements(yuv_y, y, 0);
        env->ReleaseByteArrayElements(yuv_u, u, 0);
        env->ReleaseByteArrayElements(yuv_v, v, 0);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_setFilterType(JNIEnv *env, jobject thiz, jint type) {
    // TODO: implement setFilterType()
    filterType = type;
}


// -----------------------opensl es 录音-------------------------------

TXOpenSLES *txOpenSles;

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_startRecord(JNIEnv *env, jobject thiz, jstring path) {
    const char *filePath = env->GetStringUTFChars(path, 0);
    FILE *pcmFile = fopen(filePath, "w");
    txOpenSles = new TXOpenSLES();
    txOpenSles->file = pcmFile;
    txOpenSles->start();
    env->ReleaseStringUTFChars(path, filePath);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_pauseRecord(JNIEnv *env, jobject thiz) {
    if (txOpenSles != NULL) {
        txOpenSles->pause();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_resumeRecord(JNIEnv *env, jobject thiz) {
    if (txOpenSles != NULL) {
        txOpenSles->resume();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_stopRecord(JNIEnv *env, jobject thiz) {
    if (txOpenSles != NULL) {
        txOpenSles->stop();
        delete txOpenSles;
        txOpenSles = NULL;
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_initRtmp(JNIEnv *env, jobject thiz, jstring url) {
    const char *pushUrl = env->GetStringUTFChars(url, 0);
    txCallBack = new TXCallBack(jvm, env, &thiz);
    rtmp = new RtmpPush(pushUrl, txCallBack);
    rtmp->create();
    env->ReleaseStringUTFChars(url, pushUrl);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_pushSPSPPS(JNIEnv *env, jobject thiz, jbyteArray sps,
                                             jint sps_len, jbyteArray pps, jint pps_len) {
    jbyte *_sps = env->GetByteArrayElements(sps, 0);
    jbyte *_pps = env->GetByteArrayElements(pps, 0);
    if (rtmp != NULL) {
        rtmp->pushSPSPPS(reinterpret_cast<char *>(_sps), sps_len, reinterpret_cast<char *>(_pps),
                         pps_len);
    }
    env->ReleaseByteArrayElements(sps, _sps, 0);
    env->ReleaseByteArrayElements(pps, _pps, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_pushVideoData(JNIEnv *env, jobject thiz, jbyteArray data,
                                                jint data_len, jboolean keyframe) {
    jbyte *_data = env->GetByteArrayElements(data, 0);
    if (rtmp != NULL) {
        rtmp->pushVideoData(reinterpret_cast<char *>(_data), data_len, keyframe);
    }
    env->ReleaseByteArrayElements(data, _data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_pushAudioData(JNIEnv *env, jobject thiz, jbyteArray data,
                                                jint data_len) {
    jbyte *_data = env->GetByteArrayElements(data, 0);
    if (rtmp != NULL) {
        rtmp->pushAudioData(reinterpret_cast<char *>(_data), data_len);
    }
    env->ReleaseByteArrayElements(data, _data, 0);
}