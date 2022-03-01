package com.taxiao.opengl.egl.obj;

import androidx.annotation.FloatRange;

/**
 * Created by hanqq on 2022/2/2
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Vector {
    public float x, y, z;

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // 计算两个向量的交叉乘积
    public Vector crossProduct(Vector other) {
        return new Vector(
                (y * other.z) -( z * other.y),
                (z * other.x) - (x * other.z),
                (x * other.y) - (y * other.x)
        );
    }

    public Vector normalize() {
        return scale(1f / length());
    }

    // 勾股定理返回向量的长度
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    // 计算两个向量之间的点积
    public float dotProduct(Vector other) {
        return x * other.x
                + y * other.y
                + z * other.z;
    }

    // 缩放量均匀的缩放向量的每个分量
    public Vector scale(float f) {
        return new Vector(
                x * f,
                y * f,
                z * f
        );
    }
}
