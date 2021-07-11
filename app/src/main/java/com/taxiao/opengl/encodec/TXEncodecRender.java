package com.taxiao.opengl.encodec;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.taxiao.opengl.R;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.ShaderUtils;
import com.taxiao.opengl.util.egl.TXEglRender;
import com.taxiao.opengl.view.TXImageRender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by hanqq on 2021/7/10
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXEncodecRender extends TXEglRender {
    private String TAG = TXEncodecRender.this.getClass().getSimpleName();

    // 顶点坐标
    private final float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
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
    private int s_texture;
    private int mtextureID;
    private int[] vbo;

    public TXEncodecRender(Context context, int textureID) {
        this.mContext = context;
        this.mtextureID = textureID;
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

    @Override
    public void onSurfaceCreated() {
        LogUtils.d(TAG, "onSurfaceCreated");
        super.onSurfaceCreated();
        initOpenGLES();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        LogUtils.d(TAG, "onSurfaceChanged");
        super.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        LogUtils.d(TAG, "onDrawFrame");
        super.onDrawFrame();
        renderFrame();
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
            s_texture = GLES20.glGetUniformLocation(program, "s_texture");

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
        }
    }

    private void renderFrame() {
        if (program > 0) {
            LogUtils.d(TAG, "renderFrame");
            // 10.使用渲染器
            GLES20.glUseProgram(program);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mtextureID);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

            // 11.使顶点坐标和纹理坐标属性数组有效
            GLES20.glEnableVertexAttribArray(av_position);
            // 使用VBO 缓存时最后一个参数传0，不使用VBO ,最后一个参数传vertexBuffer
            GLES20.glVertexAttribPointer(av_position, 2, GLES20.GL_FLOAT, false, 8, 0);

            GLES20.glEnableVertexAttribArray(af_position);
            GLES20.glVertexAttribPointer(af_position, 2, GLES20.GL_FLOAT, false, 8, vertexData.length * 4);
            // 12.绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }
    }
}
