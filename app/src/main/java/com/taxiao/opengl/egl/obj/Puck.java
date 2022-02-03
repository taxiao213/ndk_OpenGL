package com.taxiao.opengl.egl.obj;

import com.taxiao.opengl.egl.image.ColorShaderProgram;
import com.taxiao.opengl.egl.image.VertexArray;

import java.util.List;

/**
 * 冰球
 * Created by hanqq on 2022/1/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Puck {
    // 一个顶点3个分量，代表x,y,z值
    private final int POSITION_COMPONENT_COUNT = 3;
    public float radius, height;
    private VertexArray vertexArray;
    private List<ObjectBuilder.DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        GeneratedData generatedData = ObjectBuilder.createPuck(new Cylinder(new Point(0f, 0f, 0f), radius, height), numPointsAroundPuck);
        this.radius = radius;
        this.height = height;
        vertexArray = new VertexArray(generatedData.vertexData);
        this.drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram2 colorShaderProgram) {
        vertexArray.setVertexAttribPointer(0, colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
