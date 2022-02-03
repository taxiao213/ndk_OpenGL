package com.taxiao.opengl.egl.obj;

/**
 * 圆柱体
 * Created by hanqq on 2022/1/11
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Cylinder {
    public Point center;
    public float radius;
    public float height;

    public Cylinder(Point center, float radius, float height) {
        this.center = center;
        this.radius = radius;
        this.height = height;
    }
}
