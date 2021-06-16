/**
 * 资源
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_SHADERSOURCE_H
#define OPENGL_SHADERSOURCE_H

#endif //OPENGL_SHADERSOURCE_H

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
