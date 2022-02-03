package com.taxiao.opengl.egl.obj;

/**
 * 圆
 * Created by hanqq on 2022/1/11
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Circle {
    public Point center;
    public float radius;

    public Circle(Point center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public Circle scale(float scale) {
        return new Circle(center, radius * scale);
    }
}
