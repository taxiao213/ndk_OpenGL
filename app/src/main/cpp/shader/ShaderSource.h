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

static const char *vertexSource = "attribute vec4 av_Position;\n"
                                  "void main(){\n"
                                  "    gl_Position = av_Position;\n"
                                  "}";

static const char *fragmentSource = "precision mediump float;\n"
                                    "uniform vec4 af_Position;\n"
                                    "void main(){\n"
                                    "    gl_FragColor = af_Position;\n"
                                    "}";

static float vertex[] = {
        -1, -1,
        1, -1,
        0, 1,
};