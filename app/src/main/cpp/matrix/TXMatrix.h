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

#include "math.h"

// 初始化矩阵
static void initMatrix(float *matrix) {
    for (int i = 0; i < 16; i++) {
        if (i % 5 == 0) {
            matrix[i] = 1;
        } else {
            matrix[i] = 0;
        }
    }
}

/**
 * 旋转矩阵 沿Z轴
 * @param angle 角度 正数是逆时针旋转，负数是顺时针旋转
 * @param matrix 矩阵
 */
static void rotateMatrixForZ(double angle, float *matrix) {
    angle = angle * (M_PI / 180.0f);
    matrix[0] = cos(angle);
    matrix[1] = -sin(angle);
    matrix[4] = sin(angle);
    matrix[5] = cos(angle);
}

/**
 * 旋转矩阵 沿X轴
 * @param angle 角度
 * @param matrix 矩阵
 */
static void rotateMatrixForX(double angle, float *matrix) {
    angle = angle * (M_PI / 180.0f);
    matrix[5] = cos(angle);
    matrix[6] = -sin(angle);
    matrix[9] = sin(angle);
    matrix[10] = cos(angle);
}

/**
 * 旋转矩阵 沿Y轴
 * @param angle 角度
 * @param matrix 矩阵
 */
static void rotateMatrixForY(double angle, float *matrix) {
    angle = angle * (M_PI / 180.0f);
    matrix[0] = cos(angle);
    matrix[2] = sin(angle);
    matrix[8] = -sin(angle);
    matrix[10] = cos(angle);
}

/**
 * 缩放矩阵 不均匀缩放时 x ,y 大小不一样
 * 传入的数值是占比 eg:0.5 缩放0.5倍
 * @param x
 * @param y
 * @param matrix 矩阵
 */
static void scaleMatrix(double x, double y, float *matrix) {
    matrix[0] = x;
    matrix[5] = y;
}

/**
 * 位移矩阵
 * 传入的数值是占比 eg:0.5 位移0.5倍
 * @param x
 * @param y
 * @param matrix 矩阵
 */
static void translationMatrix(double x, double y, float *matrix) {
    matrix[3] = x;
    matrix[7] = y;
}

/**
 * 投影矩阵 将图片缩放到手机屏幕大小
 * > 0 缩小 <0 放大
 * @param left
 * @param right
 * @param bottom
 * @param top
 * @param matrix
 */
static void reflectionMatrix(float left, float right, float bottom, float top, float *matrix) {
    matrix[0] = 2 / (right - left);
    matrix[3] = (right + left) / (right - left) * -1;
    matrix[5] = 2 / (top - bottom);
    matrix[7] = (top + bottom) / (top - bottom) * -1;
    matrix[10] = 1;
    matrix[11] = 1;
}