/**
 * 初始化矩阵
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_TXMATRIX_H
#define OPENGL_TXMATRIX_H

#endif //OPENGL_TXMATRIX_H

static void initMatrix(float *matrix) {
    for (int i = 0; i < 16; i++) {
        if (i % 5 == 0) {
            matrix[i] = 1;
        } else {
            matrix[i] = 0;
        }
    }
}