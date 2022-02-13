package com.taxiao.opengl.egl.skybox;

import android.opengl.GLES20;

import com.taxiao.opengl.egl.image.VertexArray;

import java.nio.ByteBuffer;

/**
 * 立方体天空盒
 * Created by hanqq on 2022/2/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Skybox {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private final VertexArray vertexArray;
    private final ByteBuffer indexArray;

    public Skybox() {
        // Create a unit cube 立方体的顶点
        vertexArray = new VertexArray(new float[] {
                -1,  1,  1,     // (0) Top-left near
                1,  1,  1,     // (1) Top-right near
                -1, -1,  1,     // (2) Bottom-left near
                1, -1,  1,     // (3) Bottom-right near
                -1,  1, -1,     // (4) Top-left far
                1,  1, -1,     // (5) Top-right far
                -1, -1, -1,     // (6) Bottom-left far
                1, -1, -1      // (7) Bottom-right far
        });

        // 6 indices per cube side 立方体6个面，每个面需要2个三角形，总共36个顶点
        indexArray =  ByteBuffer.allocateDirect(6 * 6)
                .put(new byte[] {
                        // Front
                        1, 3, 0,
                        0, 3, 2,

                        // Back
                        4, 6, 5,
                        5, 6, 7,

                        // Left
                        0, 2, 4,
                        4, 2, 6,

                        // Right
                        5, 7, 1,
                        1, 7, 3,

                        // Top
                        5, 1, 4,
                        4, 1, 0,

                        // Bottom
                        6, 2, 7,
                        7, 2, 3
                });
        indexArray.position(0);
    }

    public void bindData(SkyboxShaderProgram skyboxProgram) {
        vertexArray.setVertexAttribPointer(0,
                skyboxProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, indexArray);
    }
}
