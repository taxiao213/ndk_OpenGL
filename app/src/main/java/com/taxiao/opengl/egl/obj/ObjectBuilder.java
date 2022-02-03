package com.taxiao.opengl.egl.obj;

import android.opengl.GLES20;
import android.util.FloatMath;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by hanqq on 2022/1/11
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class ObjectBuilder {
    private final int FLOATS_PER_VERTEX = 3;
    private final float[] vertexData;
    private int offset = 0;
    private List<DrawCommand> drawList = new ArrayList<>();

    public ObjectBuilder(int sizeInVertices) {
        this.vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    // 创建冰球
    public static GeneratedData createPuck(Cylinder puck, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints)
                + sizeOfOpenCylinderInVertices(numPoints);
        ObjectBuilder builder = new ObjectBuilder(size);

        Circle puckTop = new Circle(
                puck.center.transLateY(puck.height / 2f),
                puck.radius);

        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCylinder(puck, numPoints);
        return builder.build();
    }

    // 创建木槌 创建两个
    public static GeneratedData createMallet(Point center, float radius, float height, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) * 2 + sizeOfOpenCylinderInVertices(numPoints) * 2;
        ObjectBuilder builder = new ObjectBuilder(size);
        float baseHeight = height * 0.25f;
        Circle baseCircle = new Circle(center.transLateY(-baseHeight), radius);
        Cylinder baseCylinder = new Cylinder(
                baseCircle.center.transLateY(-baseHeight / 2f),
                radius, baseHeight);
        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(baseCylinder, numPoints);

        // 生成手柄
        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;
        Circle handleCircle = new Circle(center.transLateY(height * 0.5f), handleRadius);
        Cylinder handleCylinder = new Cylinder(handleCircle.center.transLateY(-handleHeight / 2f), handleRadius, handleHeight);
        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCylinder(handleCylinder, numPoints);
        return builder.build();
    }

    // 顶部用三角形扇构造的圆，有一个顶点在圆心，围着圆的每个点都有一个顶点，并且围着圆的第一个顶点要重复2次才能使圆闭合
    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    // 侧面是一个卷起来的长方形，三角形带构造，顶部圆的顶点都需要2个顶点，并且前2顶点需要重复2次才能闭合
    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return 2 * (numPoints + 1);
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }

    // 圆柱体，绘制三角形带 循环的个数是 numPoints+1
    private void appendOpenCylinder(Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        float yStart = cylinder.center.y - (cylinder.height / 2f);
        float yEnd = cylinder.center.y + (cylinder.height / 2f);
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i /numPoints)
                            * ((float) Math.PI * 2f);
            float xPosition = cylinder.center.x + cylinder.radius * (float) Math.cos(angleInRadians);
            float zPosition = cylinder.center.z + cylinder.radius * (float) Math.sin(angleInRadians);

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    // 顶部圆形 绘制三角形扇
    // float 问题切记
    private void appendCircle(Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);

            vertexData[offset++] =
                    (float) (circle.center.x
                            + circle.radius * Math.cos(angleInRadians));
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] =
                    (float) (circle.center.z
                            + circle.radius * Math.sin(angleInRadians));
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }


    public interface DrawCommand {
        void draw();
    }
}
