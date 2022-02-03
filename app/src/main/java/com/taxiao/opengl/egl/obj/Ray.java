package com.taxiao.opengl.egl.obj;


/**
 * 射线
 * Created by hanqq on 2022/2/2
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Ray {
    public Point point;
    public Vector vector;

    public Ray(Point point, Vector vector) {
        this.point = point;
        this.vector = vector;
    }
}
