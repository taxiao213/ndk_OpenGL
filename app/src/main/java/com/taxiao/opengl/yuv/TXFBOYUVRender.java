package com.taxiao.opengl.yuv;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.taxiao.opengl.R;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 离屏渲染
 * Created by hanqq on 2021/6/27
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXFBOYUVRender {
    private String TAG = TXFBOYUVRender.this.getClass().getSimpleName();
    private boolean drawPic = true;
    // 顶点坐标
    private final float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,

            0f, 0f,
            0f, 0f,
            0f, 0f,
            0f, 0f
    };

    // 纹理坐标
    private final float[] textureData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    private Context mContext;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int program;
    private int av_position;
    private int af_position;
    private int[] vbo;
    private Bitmap bitmap;
    private int bitmapTexture;

    public TXFBOYUVRender(Context context) {
        this.mContext = context;
        bitmap = ShaderUtils.createTextImage("图片合成：他晓", 50, "#ff0000", "#00000000", 10);

        float r = 1.0f * bitmap.getWidth() / bitmap.getHeight();
        float w = r * 0.1f;

        vertexData[8] = 0.8f - w;
        vertexData[9] = -0.8f;

        vertexData[10] = 0.8f;
        vertexData[11] = -0.8f;

        vertexData[12] = 0.8f - w;
        vertexData[13] = -0.7f;

        vertexData[14] = 0.8f;
        vertexData[15] = -0.7f;

        // 1.创建顶点 和 纹理 buffer
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);
    }

    public void onSurfaceCreated() {
        LogUtils.d(TAG, "onSurfaceCreated");
        initOpenGLES();
    }

    public void onSurfaceChanged(int width, int height) {
        LogUtils.d(TAG, "onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
    }

    public void onDrawFrame(int imageTexure) {
        LogUtils.d(TAG, "onDrawFrame");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 1f, 0f);
        renderFrame(imageTexure);
    }

    private void initOpenGLES() {
        LogUtils.d(TAG, "initOpenGLES");
        // 2.加载 shader
        String vertex = ShaderUtils.readRawTxt(mContext, R.raw.vertex_image_shader);
        String texture = ShaderUtils.readRawTxt(mContext, R.raw.fragment_image_shader);
        // 3.创建渲染程序
        program = ShaderUtils.createProgram(vertex, texture);
        if (program > 0) {
            // 4.得到着色器中的属性
            av_position = GLES20.glGetAttribLocation(program, "av_Position");
            af_position = GLES20.glGetAttribLocation(program, "af_Position");

            // 使用VBO
            // 4.1 创建VBO
            vbo = new int[1];
            GLES20.glGenBuffers(1, vbo, 0);
            // 4.2 绑定VBO
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
            // 4.3 分配VBO需要的缓存大小，静态绘制
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + textureData.length * 4, null, GLES20.GL_STATIC_DRAW);
            // 4.4 为VBO设置顶点数据的值
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, textureData.length * 4, textureBuffer);
            // 4.5 解绑
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

            bitmapTexture = ShaderUtils.loadBitmapTexture(bitmap);
        }
    }

    private void renderFrame(int imageTexure) {
        if (program > 0) {
            LogUtils.d(TAG, "renderFrame");
            // 10.使用渲染器
            GLES20.glUseProgram(program);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexure);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
            // 11.使顶点坐标和纹理坐标属性数组有效
            GLES20.glEnableVertexAttribArray(av_position);
            GLES20.glVertexAttribPointer(av_position, 2, GLES20.GL_FLOAT, false, 8, 0);

            GLES20.glEnableVertexAttribArray(af_position);
            GLES20.glVertexAttribPointer(af_position, 2, GLES20.GL_FLOAT, false, 8, vertexData.length * 4);
            // 12.绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            // bitmap
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bitmapTexture);
            GLES20.glEnableVertexAttribArray(av_position);
            GLES20.glVertexAttribPointer(av_position, 2, GLES20.GL_FLOAT, false, 8, 32);
            GLES20.glEnableVertexAttribArray(af_position);
            GLES20.glVertexAttribPointer(af_position, 2, GLES20.GL_FLOAT, false, 8, vertexData.length * 4);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            // 解绑
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }
    }
}
