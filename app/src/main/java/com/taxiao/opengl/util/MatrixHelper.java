package com.taxiao.opengl.util;

import android.opengl.Matrix;

/**
 * Created by hanqq on 2022/1/4
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class MatrixHelper {
    /**
     * Matrix.perspectiveM(); 在Android 的ICS（4.0） 版本开始引入
     * Matrix.frustumM(); 会影响某些类型的投影
     *
     * @param m      数组
     * @param fovy   焦距
     * @param aspect 宽高比
     * @param zNear  近距离
     * @param zFar   远距离
     */
    public static void perspectiveM(float[] m, float fovy, float aspect, float zNear, float zFar) {

        float angleInRadians = (float) (fovy * Math.PI / 180.0);
        float a = (float) (1.0f / Math.tan(angleInRadians / 2.0));
        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((zFar + zNear) / (zFar - zNear));
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2.0f * zFar * zNear) / (zFar - zNear));
        m[15] = 0f;

    }
}
