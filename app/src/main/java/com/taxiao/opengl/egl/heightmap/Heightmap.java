package com.taxiao.opengl.egl.heightmap;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;

/**
 * 顶点数据
 * Created by hanqq on 2022/2/21
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Heightmap {
    private static final int POSITION_COMPONENT_COUNT = 3;

    private final int width;
    private final int height;
    private final int numElements;
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    public Heightmap(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        if (width * height > 65536) {
            throw new RuntimeException("Heightmap is too large for the index buffer.");
        }
        numElements = calculateNumElements();
        vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));
        indexBuffer = new IndexBuffer(createIndexData());
    }

    /**
     * Copy the heightmap data into a vertex buffer object.
     * 以x-z平面的位置（0，0）为中心通过循环，建位图的左上角映射到（-0.5，-0.5），右下角（0.5，0.5）
     */
    private float[] loadBitmapData(Bitmap bitmap) {
        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        final float[] heightmapVertices =
                new float[width * height * POSITION_COMPONENT_COUNT];
        int offset = 0;
        // 内存的布局方式是 先读取行，再读取列，从左向右，cpu 按顺序缓存和移动数据时，效果更高
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // The heightmap will lie flat on the XZ plane and centered
                // around (0, 0), with the bitmap width mapped to X and the
                // bitmap height mapped to Z, and Y representing the height. We
                // assume the heightmap is grayscale, and use the value of the
                // red color to determine the height.
                final float xPosition = ((float)col / (float)(width - 1)) - 0.5f;
                // 读入像素公式，像素偏移值=当前行*高度+当前列
                final float yPosition =
                        (float) Color.red(pixels[(row * height) + col]) / (float)255;
                final float zPosition = ((float)row / (float)(height - 1)) - 0.5f;

                heightmapVertices[offset++] = xPosition;
                heightmapVertices[offset++] = yPosition;
                heightmapVertices[offset++] = zPosition;
            }
        }
        return heightmapVertices;
    }

    // 生成索引数据，高度图中每四个顶点构成的组，生成2个三角形，每个三角形3个索引，总共需要6个索引，
    // 通过(width - 1) * (height - 1) 算出需要多少组
    private int calculateNumElements() {
        // There should be 2 triangles for every group of 4 vertices, so a
        // heightmap of, say, 10x10 pixels would have 9x9 groups, with 2
        // triangles per group and 3 vertices per triangle for a total of (9 x 9
        // x 2 x 3) indices.
        return (width - 1) * (height - 1) * 2 * 3;
    }

    /**
     * Create an index buffer object for the vertices to wrap them together into
     * triangles, creating indices based on the width and height of the
     * heightmap.
     */
    private short[] createIndexData() {
        final short[] indexData = new short[numElements];
        int offset = 0;

        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width - 1; col++) {
                // Note: The (short) cast will end up underflowing the number
                // into the negative range if it doesn't fit, which gives us the
                // right unsigned number for OpenGL due to two's complement.
                // This will work so long as the heightmap contains 65536 pixels
                // or less.
                short topLeftIndexNum = (short) (row * width + col);
                short topRightIndexNum = (short) (row * width + col + 1);
                short bottomLeftIndexNum = (short) ((row + 1) * width + col);
                short bottomRightIndexNum = (short) ((row + 1) * width + col + 1);

                // Write out two triangles.
                indexData[offset++] = topLeftIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = topRightIndexNum;

                indexData[offset++] = topRightIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = bottomRightIndexNum;
            }
        }

        return indexData;
    }
    public void bindData(HeightmapShaderProgram heightmapProgram) {
        vertexBuffer.setVertexAttribPointer(0,
                heightmapProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numElements, GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
