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

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_stopPush(JNIEnv *env, jobject thiz) {
    if (rtmp != NULL) {
        rtmp->stop();
        delete (rtmp);
        rtmp = NULL;
    }
    if (txCallBack != NULL) {
        delete (txCallBack);
        txCallBack = NULL;
    }
}


#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>
#include <ft2build.h>
#include <freetype/freetype.h>
#include <glm.hpp>
#include <string>
#include <map>
#include <GLES3/gl3.h>
#include <ext.hpp>
#include <freetype/ftglyph.h>

static FT_Face face;

struct Character {
    GLuint textureID;   // ID handle of the glyph texture
    glm::ivec2 size;    // Size of glyph
    glm::ivec2 bearing;  // Offset from baseline to left/top of glyph
    GLuint advance;    // Horizontal offset to advance to next glyph
};

std::map<GLchar, Character> m_Characters;
GLuint m_VaoId = GL_NONE;
GLuint m_VboId = GL_NONE;
int m_SurfaceWidth = 0;
int m_SurfaceHeight = 0;
GLuint m_TextureId;
GLint m_SamplerLoc;
GLint m_MVPMatLoc;
glm::mat4 m_MVPMatrix;
float m_ScaleX = 1.0f;
float m_ScaleY = 1.0f;
#define MATH_PI 3.1415926535897932384626433832802
GLuint vShader;
GLuint fShader;
static const int MAX_SHORT_VALUE = 65536;

static const wchar_t BYTE_FLOW[] = L"微信公众号字节流动，欢迎关注交流学习。";

void LoadFacesByUnicode(const wchar_t *text, int size) {

    // Set size to load glyphs as
    FT_Set_Pixel_Sizes(face, 96, 96);
    FT_Select_Charmap(face, ft_encoding_unicode);

    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

    for (int i = 0; i < size; ++i) {
        //int index =  FT_Get_Char_Index(face,unicodeArr[i]);
        if (FT_Load_Glyph(face, FT_Get_Char_Index(face, text[i]), FT_LOAD_DEFAULT)) {
            SDK_LOG_D("TextRenderSample::LoadFacesByUnicode FREETYTPE: Failed to load Glyph");
            continue;
        }

        FT_Glyph glyph;
        FT_Get_Glyph(face->glyph, &glyph);

        //Convert the glyph to a bitmap.
        FT_Glyph_To_Bitmap(&glyph, ft_render_mode_normal, 0, 1);
        FT_BitmapGlyph bitmap_glyph = (FT_BitmapGlyph) glyph;

        //This reference will make accessing the bitmap easier
        FT_Bitmap &bitmap = bitmap_glyph->bitmap;

        // Generate texture
        GLuint texture;
        glGenTextures(1, &texture);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_LUMINANCE,
                bitmap.width,
                bitmap.rows,
                0,
                GL_LUMINANCE,
                GL_UNSIGNED_BYTE,
                bitmap.buffer
        );

        // Set texture options
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Now store character for later use
        Character character = {
                texture,
                glm::ivec2(face->glyph->bitmap.width, face->glyph->bitmap.rows),
                glm::ivec2(face->glyph->bitmap_left, face->glyph->bitmap_top),
                static_cast<GLuint>((glyph->advance.x / MAX_SHORT_VALUE) << 6)
        };
        m_Characters.insert(std::pair<GLint, Character>(text[i], character));

    }
    glBindTexture(GL_TEXTURE_2D, 0);

}

void LoadFacesByASCII() {
    FT_Set_Pixel_Sizes(face, 0, 96);

    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

    // Load first 128 characters of ASCII set
    for (unsigned char c = 0; c < 128; c++) {
        // Load character glyph
        if (FT_Load_Char(face, c, FT_LOAD_RENDER)) {
            continue;
        }
        // Generate texture
        GLuint texture;
        glGenTextures(1, &texture);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_LUMINANCE,
                face->glyph->bitmap.width,//点阵宽度
                face->glyph->bitmap.rows,//点阵高度
                0,
                GL_LUMINANCE,
                GL_UNSIGNED_BYTE,
                face->glyph->bitmap.buffer
        );
//		NativeImage image;
//		image.width = face->glyph->bitmap.width;
//		image.height = face->glyph->bitmap.rows;
//		image.format = 8;
//		image.ppPlane[0] = face->glyph->bitmap.buffer;
//		NativeImageUtil::DumpNativeImage(&image, "/sdcard/DCIM", "TextRenderSample");
//        SDK_LOG_D(
//                "TextRenderSample::LoadFacesByASCII [w,h,buffer]=[%d, %d, %p], ch.advance >> 6 = %ld",
//                face->glyph->bitmap.width, face->glyph->bitmap.rows, face->glyph->bitmap.buffer,
//                face->glyph->advance.x >> 6);
        // Set texture options
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Now store character for later use
        Character character = {
                texture,
                glm::ivec2(face->glyph->bitmap.width, face->glyph->bitmap.rows),
                glm::ivec2(face->glyph->bitmap_left, face->glyph->bitmap_top),
                static_cast<GLuint>(face->glyph->advance.x)
        };
        m_Characters.insert(std::pair<GLint, Character>(c, character));
    }
    glBindTexture(GL_TEXTURE_2D, 0);
}


void RenderText(std::string text, GLfloat x, GLfloat y, GLfloat scale,
                glm::vec3 color, glm::vec2 viewport) {
    SDK_LOG_D("RenderText");
    // 激活合适的渲染状态
    glUseProgram(program);
    glUniform3f(glGetUniformLocation(program, "u_textColor"), color.x, color.y, color.z);
    glBindVertexArray(m_VaoId);

    // 对文本中的所有字符迭代
    std::string::const_iterator c;
    x *= viewport.x;
    y *= viewport.y;
    for (c = text.begin(); c != text.end(); c++)
    {
        Character ch = m_Characters[*c];

        GLfloat xpos = x + ch.bearing.x * scale;
        GLfloat ypos = y - (ch.size.y - ch.bearing.y) * scale;

        xpos /= viewport.x;
        ypos /= viewport.y;

        GLfloat w = ch.size.x * scale;
        GLfloat h = ch.size.y * scale;

        w /= viewport.x;
        h /= viewport.y;

//        SDK_LOG_D("TextRenderSample::RenderText [xpos,ypos,w,h]=[%f, %f, %f, %f], ch.advance >> 6 = %d", xpos, ypos, w, h, ch.advance >> 6);

        // 当前字符的VBO
        GLfloat vertices[6][4] = {
                { xpos,     ypos + h,   0.0, 0.0 },
                { xpos,     ypos,       0.0, 1.0 },
                { xpos + w, ypos,       1.0, 1.0 },

                { xpos,     ypos + h,   0.0, 0.0 },
                { xpos + w, ypos,       1.0, 1.0 },
                { xpos + w, ypos + h,   1.0, 0.0 }
        };

        // 在方块上绘制字形纹理
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, ch.textureID);
        glUniform1i(m_SamplerLoc, 0);
        // 更新当前字符的VBO
        glBindBuffer(GL_ARRAY_BUFFER, m_VboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(vertices), vertices);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        // 绘制方块
        glDrawArrays(GL_TRIANGLES, 0, 6);
        // 更新位置到下一个字形的原点，注意单位是1/64像素
        x += (ch.advance >> 6) * scale; //(2^6 = 64)
    }
    glBindVertexArray(0);
    glBindTexture(GL_TEXTURE_2D, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_initAsserts(JNIEnv *env, jobject thiz, jobject assert_manager) {
    SDK_LOG_D("initAsserts");
    AAssetManager *g_pAssetManager = AAssetManager_fromJava(env, assert_manager);
    FT_Library ft;
    if (FT_Init_FreeType(&ft))
        SDK_LOG_D("ERROR::FREETYPE: Could not init FreeType Library");

    if (g_pAssetManager) {
        AAsset *fontAsset = AAssetManager_open(g_pAssetManager, "Arialn.ttf", AASSET_MODE_UNKNOWN);
        if (fontAsset) {
            size_t assetLength = AAsset_getLength(fontAsset);
            char *buffer = (char *) malloc(assetLength);
            AAsset_read(fontAsset, buffer, assetLength);
            AAsset_close(fontAsset);
//            FT_New_Memory_Face(ft, (const FT_Byte *) buffer, assetLength, 0, &face);

            FT_New_Face(ft, "/sdcard/opengl_font/arial.ttf",   0, &face);
        }
    }
    LoadFacesByASCII();
//    LoadFacesByUnicode(BYTE_FLOW, sizeof(BYTE_FLOW) / sizeof(BYTE_FLOW[0]) - 1);
    // Destroy FreeType once we're finished
    FT_Done_Face(face);
    FT_Done_FreeType(ft);
}


void freeTypeSurfaceCreated(void *ctx) {
    SDK_LOG_D("freeTypeSurfaceCreated");

    //create RGBA texture
    glGenTextures(1, &m_TextureId);
    glBindTexture(GL_TEXTURE_2D, m_TextureId);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glBindTexture(GL_TEXTURE_2D, GL_NONE);

    program = createProgram(vShaderStr, fShaderStr, &vShader, &fShader);
    if (program > 0) {
        // 2.获取顶点坐标和纹理坐标属性
        m_SamplerLoc = glGetUniformLocation(program, "s_textTexture");
        m_MVPMatLoc = glGetUniformLocation(program, "u_MVPMatrix");

        // 3.创建纹理
        glGenVertexArrays(1, &m_VaoId);
        // Generate VBO Ids and load the VBOs with data
        glGenBuffers(1, &m_VboId);
        glBindVertexArray(m_VaoId);
        glBindBuffer(GL_ARRAY_BUFFER, m_VboId);
        glBufferData(GL_ARRAY_BUFFER, sizeof(GLfloat) * 6 * 4, nullptr, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 4, GL_FLOAT, GL_FALSE, 4 * sizeof(GLfloat), 0);
        glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);
        glBindVertexArray(GL_NONE);

        //upload RGBA image data
        glBindTexture(GL_TEXTURE_2D, m_TextureId);
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, m_RenderImage.width, m_RenderImage.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, m_RenderImage.ppPlane[0]);
        glBindTexture(GL_TEXTURE_2D, GL_NONE);
    }
}

// TODO: 回调函数 callBackSurfaceChanged
void freeTypeSurfaceChanged(int width, int height, void *ctx) {
    SDK_LOG_D("freeTypeSurfaceChanged");
//    TXEglThread *txEglThread = (TXEglThread *) ctx;
//    glViewport(0.0f, 0.0f, txEglThread->surfaceWidth, txEglThread->surfaceHeight);
//    float screen = 1.0f * width / height;
//    float image = 1.0f * imageWidth / imageHeight;
//    if (screen > image) {
//        // 宽度缩放
//        float scale = width / (1.0f * height / imageHeight * imageWidth);
//        reflectionMatrix(-scale, scale, -1, 1, matrix);
//    } else {
//        // 高度缩放
//        float scale = height / (1.0f * width / imageWidth * imageHeight);
//        reflectionMatrix(-1, 1, -scale, scale, matrix);
//    }
//    if (txEglThread != NULL) {
//        txEglThread->notifyThread();
//    }
}

void UpdateMVPMatrix(glm::mat4 &mvpMatrix, int angleX, int angleY, float ratio) {
    SDK_LOG_D("TextRenderSample::UpdateMVPMatrix angleX = %d, angleY = %d, ratio = %f", angleX,
              angleY, ratio);
    angleX = angleX % 360;
    angleY = angleY % 360;

    //转化为弧度角
    float radiansX = static_cast<float>(MATH_PI / 180.0f * angleX);
    float radiansY = static_cast<float>(MATH_PI / 180.0f * angleY);


    // Projection matrix
    glm::mat4 Projection = glm::ortho(-1.0f, 1.0f, -1.0f, 1.0f, 0.1f, 100.0f);
    //glm::mat4 Projection = glm::frustum(-ratio, ratio, -1.0f, 1.0f, 4.0f, 100.0f);
    //glm::mat4 Projection = glm::perspective(45.0f,ratio, 0.1f,100.f);

    // View matrix
    glm::mat4 View = glm::lookAt(
            glm::vec3(0, 0, 4), // Camera is at (0,0,1), in World Space
            glm::vec3(0, 0, 0), // and looks at the origin
            glm::vec3(0, 1, 0)  // Head is up (set to 0,-1,0 to look upside-down)
    );

    // Model matrix
    glm::mat4 Model = glm::mat4(1.0f);
    Model = glm::scale(Model, glm::vec3(m_ScaleX, m_ScaleY, 1.0f));
    Model = glm::rotate(Model, radiansX, glm::vec3(1.0f, 0.0f, 0.0f));
    Model = glm::rotate(Model, radiansY, glm::vec3(0.0f, 1.0f, 0.0f));
    Model = glm::translate(Model, glm::vec3(0.0f, 0.0f, 0.0f));

    mvpMatrix = Projection * View * Model;

}

// TODO: 回调函数 callBackSurfaceDraw
void freeTypeSurfaceDraw(void *ctx) {
    SDK_LOG_D("freeTypeSurfaceDraw m_SurfaceWidth:%d,m_SurfaceHeight:%d",m_SurfaceWidth,m_SurfaceHeight);
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    glPixelStorei(GL_UNPACK_ALIGNMENT, 1); //禁用byte-alignment限制
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    if (program > 0) {

        glm::vec2 viewport(m_SurfaceWidth, m_SurfaceHeight);

//        UpdateMVPMatrix(m_MVPMatrix, 20, 10, viewport.x / viewport.y);
        glUniformMatrix4fv(m_MVPMatLoc, 1, GL_FALSE, &m_MVPMatrix[0][0]);

        // (x,y)为屏幕坐标系的位置，即原点位于屏幕中心，x(-1.0,1.0), y(-1.0,1.0)
        RenderText("My WeChat ID is Byte-Flow.", -0.9f, 0.2f, 1.0f, glm::vec3(0.8, 0.1f, 0.1f),
                   viewport);
//        RenderText("Welcome to add my WeChat.", -0.9f, 0.0f, 2.0f, glm::vec3(0.2, 0.4f, 0.7f),
//                   viewport);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_freeSurfaceCreated(JNIEnv *env, jobject thiz,
                                                     jobject surface) {
    SDK_LOG_D("freeSurfaceCreated");
    mANativeWindow = ANativeWindow_fromSurface(env, surface);
    mTXEglThread = new TXEglThread();
    mTXEglThread->callBackOnSurfaceCreated(freeTypeSurfaceCreated, mTXEglThread);
    mTXEglThread->callBackOnSurfaceChanged(freeTypeSurfaceChanged, mTXEglThread);
    mTXEglThread->callBackOnSurfaceDraw(freeTypeSurfaceDraw, mTXEglThread);
    mTXEglThread->setRenderType(RENDER_AUTO);
    mTXEglThread->surfaceCreated(mANativeWindow);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_taxiao_opengl_JniSdkImpl_freeSurfaceChanged(JNIEnv *env, jobject thiz, jint width,
                                                     jint height) {
    m_SurfaceWidth = width;
    m_SurfaceHeight = height;
}