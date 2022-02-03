package com.taxiao.opengl.egl.obj;

import android.opengl.GLES20;

import com.taxiao.opengl.egl.image.TextureShaderProgram;
import com.taxiao.opengl.egl.image.VertexArray;

/**
 * 桌子数据
 * Created by hanqq on 2022/1/5
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Table2 {
    // 一个顶点2个分量，代表x,y值
    private final int POSITION_COMPONENT_COUNT = 2;
    // r,g,b 代表颜色
    private final int COLOR_COMPONENT_COUNT = 2;
    private final int BYTES_PER_FLOAT = 4;
    private final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            // x,y,s,t
            0F, 0F, 0.5F, 0.5F,
            -0.5F, -0.8F, 0F, 0.9F,
            0.5F, -0.8F, 1F, 0.9F,
            0.5F, 0.8F, 1F, 0.1F,
            -0.5F, 0.8F, 0F, 0.1F,
            -0.5F, -0.8F, 0F, 0.9F,
    };

    private VertexArray vertexArray;

    public Table2() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram2 textureShaderProgram) {
        vertexArray.setVertexAttribPointer(0,
                textureShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);

        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
                textureShaderProgram.getTextureCoordinateAttributeLocation(),
                COLOR_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
    }
}
