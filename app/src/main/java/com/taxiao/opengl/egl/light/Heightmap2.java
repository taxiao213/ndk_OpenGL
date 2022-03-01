package com.taxiao.opengl.egl.light;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;

import androidx.core.math.MathUtils;

import com.taxiao.opengl.egl.heightmap.HeightmapShaderProgram;
import com.taxiao.opengl.egl.heightmap.IndexBuffer;
import com.taxiao.opengl.egl.heightmap.VertexBuffer;
import com.taxiao.opengl.egl.obj.Geometry;
import com.taxiao.opengl.egl.obj.Point;
import com.taxiao.opengl.egl.obj.Vector;

/**
 * 顶点数据
 * Created by hanqq on 2022/2/21
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Heightmap2 {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;
    public static final int BYTES_PER_FLOAT = 4;
    // 存储位置和法线
    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT;
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private final int width;
    private final int height;
    private final int numElements;
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    public Heightmap2(Bitmap bitmap) {
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
                new float[width * height * TOTAL_COMPONENT_COUNT];
        int offset = 0;
        // 内存的布局方式是 先读取行，再读取列，从左向右，cpu 按顺序缓存和移动数据时，效果更高
        for (int row = 0; row < height ; row++) {
            for (int col = 0; col < width ; col++) {
                final Point point = getPoint(pixels, row, col);

                heightmapVertices[offset++] = point.x;
                heightmapVertices[offset++] = point.y;
                heightmapVertices[offset++] = point.z;

                final Point top = getPoint(pixels, row - 1, col);
                final Point left = getPoint(pixels, row, col - 1);
                final Point right = getPoint(pixels, row, col + 1);
                final Point bottom = getPoint(pixels, row + 1, col);

                final Vector rightToLeft = Geometry.vectorBetween(right, left);
                final Vector topToBottom = Geometry.vectorBetween(top, bottom);
                final Vector normal = rightToLeft.crossProduct(topToBottom).normalize();

                heightmapVertices[offset++] = normal.x;
                heightmapVertices[offset++] = normal.y;
                heightmapVertices[offset++] = normal.z;

            }
        }
        return heightmapVertices;
    }

    /**
     * Returns a point at the expected position given by row and col, but if the
     * position is out of bounds, then it clamps the position and uses the
     * clamped position to read the height. For example, calling with row = -1
     * and col = 5 will set the position as if the point really was at -1 and 5,
     * but the height will be set to the heightmap height at (0, 5), since (-1,
     * 5) is out of bounds. This is useful when we're generating normals, and we
     * need to read the heights of neighbouring points.
     */
    private Point getPoint(int[] pixels, int row, int col) {
        float x = ((float) col / (float) (width - 1)) - 0.5f;
        float z = ((float) row / (float) (height - 1)) - 0.5f;

        row = MathUtils.clamp(row, 0, width - 1);
        col = MathUtils.clamp(col, 0, height - 1);

        float y = (float) Color.red(pixels[(row * height) + col]) / (float) 255;

        return new Point(x, y, z);
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

    public void bindData(HeightmapShaderProgram2 heightmapProgram) {
        vertexBuffer.setVertexAttribPointer(0,
                heightmapProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);

        vertexBuffer.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT,
                heightmapProgram.getNormalAttributeLocation(),
                NORMAL_COMPONENT_COUNT, STRIDE);

    }

    public void draw() {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numElements, GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
