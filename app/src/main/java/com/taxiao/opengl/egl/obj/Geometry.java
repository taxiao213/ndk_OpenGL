package com.taxiao.opengl.egl.obj;

/**
 * Created by hanqq on 2022/2/2
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Geometry {

    // 创建一个方向向量
    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(to.x - from.x, to.y - from.y, to.z - from.z);
    }

    // 相交测试
    public static boolean intersects(Sphere sphere, Ray ray) {
        // 计算球体与射线之间的距离，射线的起始点和结束点，和球体的球心，如果这个距离比半径小，那么射线就和球体相交
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    // 向量计算距离
    public static float distanceBetween(Point point, Ray ray) {
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(ray.point.transLate(ray.vector), point);
        float areaOfTriangleTimeTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();
        float distanceFromPointToRay = areaOfTriangleTimeTwo / lengthOfBase;
        return distanceFromPointToRay;
    }

    // 计算缩放因子
    public static Point intersectionPoint(Ray ray, Plane plane) {
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);
        // 缩放因子
        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal) / ray.vector.dotProduct(plane.normal);
        Point intersectionPoint = ray.point.transLate(ray.vector.scale(scaleFactor));
        return intersectionPoint;
    }
}
