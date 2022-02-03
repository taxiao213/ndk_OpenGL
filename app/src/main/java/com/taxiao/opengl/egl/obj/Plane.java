package com.taxiao.opengl.egl.obj;

/**
 * 计算向量的点 积
 * Created by hanqq on 2022/2/2
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Plane {
    public Point point;
    public Vector normal;

    public Plane(Point point, Vector normal) {
        this.point = point;
        this.normal = normal;
    }
}
