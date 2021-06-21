/**
 * shader 工具类
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_SHADERUTIL_H
#define OPENGL_SHADERUTIL_H

#include "../android_log.h"
#include <GLES2/gl2.h>

/**
 * 加载shader
 * @param shaderType
 * @param source
 * @return
 */
static int loadShader(int shaderType, const char *source) {
    GLuint shader = glCreateShader(shaderType);
    if (shader <= 0) {
        SDK_LOG_E("loadShader glCreateShader error");
        return 0;
    }
    glShaderSource(shader, 1, &source, 0);
    glCompileShader(shader);
    SDK_LOG_D("loadShader success");
    return shader;
}

/**
 * 创建program
 * @param vertexSource 顶点坐标
 * @param fragmentSource 纹理坐标
 * @return
 */
static GLuint createProgram(const char *vertexSource, const char *fragmentSource, GLuint *vShader,
                            GLuint *fShader) {
    int vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource);
    int fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource);
    GLuint program = glCreateProgram();
    if (program <= 0) {
        SDK_LOG_E("createProgram glCreateProgram error");
        return 0;
    }
    glAttachShader(program, vertexShader);
    glAttachShader(program, fragmentShader);
    glLinkProgram(program);
    *vShader = vertexShader;
    *fShader = fragmentShader;
    SDK_LOG_D("createProgram success");
    return program;
}

#endif //OPENGL_SHADERUTIL_H