package com.taxiao.opengl.egl.obj;

import com.taxiao.opengl.egl.image.VertexArray;

import java.util.List;

/**
 * 木槌数据
 * Created by hanqq on 2022/1/5
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Mallet2 {
    // 一个顶点3个分量，代表x,y,z值
    private final int POSITION_COMPONENT_COUNT = 3;
    public float radius, height;
    private VertexArray vertexArray;
    private List<ObjectBuilder.DrawCommand> drawList;

    public Mallet2(float radius, float height, int numPointsAroundMallet) {
        GeneratedData generatedData = ObjectBuilder.createMallet(new Point(0f, 0f, 0f), radius, height, numPointsAroundMallet);
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
