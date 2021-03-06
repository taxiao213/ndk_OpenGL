/**
 * 资源
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_SHADERSOURCE_H
#define OPENGL_SHADERSOURCE_H


// ----------------绘制三角形 和 四边形----------------
static const char *vertexSource = "attribute vec4 av_Position;\n"
                                  "void main(){\n"
                                  "    gl_Position = av_Position;\n"
                                  "}";

static const char *fragmentSource = "precision mediump float;\n"
                                    "uniform vec4 af_Position;\n"
                                    "void main(){\n"
                                    "    gl_FragColor = af_Position;\n"
                                    "}";

// 绘制三角形
static float vertex[] = {
        -1, -1,
        1, -1,
        0, 1,
};

// 绘制四边形
static float vertex2[] = {
        -1, -1,
        1, -1,
        -1, 1,
        1, 1,
};

// ----------------绘制纹理----------------
static const char *vertexSource2 = "attribute vec4 av_Position;\n"
                                   "attribute vec2 af_Position;\n"
                                   "varying vec2 v_texPosition;\n"
                                   "void main() {\n"
                                   "    v_texPosition = af_Position;\n"
                                   "    gl_Position = av_Position;\n"
                                   "}";

static const char *fragmentSource2 = "precision mediump float;\n"
                                     "varying vec2 v_texPosition;\n"
                                     "uniform sampler2D s_texture;\n"
                                     "void main() {\n"
                                     "    gl_FragColor = texture2D(s_texture, v_texPosition);\n"
                                     "}";

// 绘制四边形
static float vertexData[] = {
        -1, -1,
        1, -1,
        -1, 1,
        1, 1,
};

// 纹理坐标
static float textureData[] = {
        0, 1,
        1, 1,
        0, 0,
        1, 0
};

// ----------------矩阵----------------
static const char *vertexMatrixSource = "attribute vec4 av_Position;\n"
                                        "attribute vec2 af_Position;\n"
                                        "varying vec2 v_texPosition;\n"
                                        "uniform mat4 u_Matrix;\n"
                                        "void main() {\n"
                                        "    v_texPosition = af_Position;\n"
                                        "    gl_Position = av_Position * u_Matrix;\n"
                                        "}";

static const char *fragmentMatrixSource = "precision mediump float;\n"
                                          "varying vec2 v_texPosition;\n"
                                          "uniform sampler2D s_texture;\n"
                                          "void main() {\n"
                                          "    gl_FragColor = texture2D(s_texture, v_texPosition);\n"
                                          "}";
// 绘制四边形
static float vertexMatrixData[] = {
        -1, -1,
        1, -1,
        -1, 1,
        1, 1,
};

// 纹理坐标
static float textureMatrixData[] = {
        0, 1,
        1, 1,
        0, 0,
        1, 0
};

// 滤镜
static const char *vertexMatrixSource2 = "attribute vec4 av_Position;\n"
                                         "attribute vec2 af_Position;\n"
                                         "varying vec2 v_texPosition;\n"
                                         "uniform mat4 u_Matrix;\n"
                                         "void main() {\n"
                                         "    v_texPosition = af_Position;\n"
                                         "    gl_Position = av_Position * u_Matrix;\n"
                                         "}";

static const char *fragmentMatrixSource2 = "precision mediump float;\n"
                                           "varying vec2 v_texPosition;\n"
                                           "uniform sampler2D s_texture;\n"
                                           "void main() {\n"
                                           "    lowp vec4 textureColor = texture2D(s_texture, v_texPosition);\n"
                                           "    float gray = textureColor.r * 0.2125 + textureColor.g * 0.7154 + textureColor.b * 0.0721;\n"
                                           "    gl_FragColor = vec4(gray, gray, gray, textureColor.w);\n"
                                           "}";
// 绘制四边形
static float vertexMatrixData2[] = {
        -1, -1,
        1, -1,
        -1, 1,
        1, 1,
};

// 纹理坐标
static float textureMatrixData2[] = {
        0, 1,
        1, 1,
        0, 0,
        1, 0
};

// 渲染YUV数据
static const char *vertexMatrixSource3 = "attribute vec4 av_Position;\n"
                                         "attribute vec2 af_Position;\n"
                                         "varying vec2 v_texPosition;\n"
                                         "uniform mat4 u_Matrix;\n"
                                         "void main() {\n"
                                         "    v_texPosition = af_Position;\n"
                                         "    gl_Position = av_Position * u_Matrix;\n"
                                         "}";

static const char *fragmentMatrixSource3 = "precision mediump float;\n"
                                           "varying vec2 v_texPosition;\n"
                                           "uniform sampler2D sampler_y;\n"
                                           "uniform sampler2D sampler_u;\n"
                                           "uniform sampler2D sampler_v;\n"
                                           "void main() {\n"
                                           "    float y, u, v;\n"
                                           "    y = texture2D(sampler_y, v_texPosition).r;\n"
                                           "    u = texture2D(sampler_u, v_texPosition).r - 0.5;\n"
                                           "    v = texture2D(sampler_v, v_texPosition).r - 0.5;\n"
                                           "\n"
                                           "    vec3 rgb;\n"
                                           "    rgb.r = y + 1.403 * v;\n"
                                           "    rgb.g = y - 0.344 * u - 0.714 * v;\n"
                                           "    rgb.b = y + 1.770 * u;\n"
                                           "    gl_FragColor = vec4(rgb, 1);\n"
                                           "}";
// 绘制四边形
static float vertexMatrixData3[] = {
        -1, -1,
        1, -1,
        -1, 1,
        1, 1,
};

// 纹理坐标
static float textureMatrixData3[] = {
        0, 1,
        1, 1,
        0, 0,
        1, 0
};


static const char *vShaderStr = "#version 300 es\n"
                                        "layout(location = 0) in vec4 a_position;// <vec2 pos, vec2 tex>\n"
                                        "uniform mat4 u_MVPMatrix;\n"
                                        "out vec2 v_texCoord;\n"
                                        "void main()\n"
                                        "{\n"
                                        "    gl_Position = u_MVPMatrix * vec4(a_position.xy, 0.0, 1.0);;\n"
                                        "    v_texCoord = a_position.zw;\n"
                                        "}";

static const char *fShaderStr = "#version 300 es\n"
                                "precision mediump float;\n"
                                "in vec2 v_texCoord;\n"
                                "layout(location = 0) out vec4 outColor;\n"
                                "uniform sampler2D s_textTexture;\n"
                                "uniform vec3 u_textColor;\n"
                                "\n"
                                "void main()\n"
                                "{\n"
                                "    vec4 color = vec4(1.0, 1.0, 1.0, texture(s_textTexture, v_texCoord).r);\n"
                                "    outColor = vec4(u_textColor, 1.0) * color;\n"
                                "}";

#endif //OPENGL_SHADERSOURCE_H