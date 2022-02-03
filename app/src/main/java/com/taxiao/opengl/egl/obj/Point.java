package com.taxiao.opengl.egl.obj;

/**
 * 三维场景的点
 * Created by hanqq on 2022/1/11
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Point {
    public float x, y, z;

    public Point(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point transLateY(float distance) {
        return new Point(x, y + distance, z);
    }

    public Point transLate(Vector vector) {
        return new Point(
                x + vector.x,
                y + vector.y,
                z + vector.z);
    }
}
