package com.taxiao.opengl.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;


import com.taxiao.opengl.R;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.ShaderUtils;
import com.taxiao.opengl.util.egl.TXEglRender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 渲染图片
 * Created by hanqq on 2021/6/1
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXImageRender extends TXEglRender {
    private String TAG = TXImageRender.this.getClass().getSimpleName();

    // 顶点坐标
    private final float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f

//            -0.5f, -0.5f,
//            0.5f, -0.5f,
//            -0.5f, 0.5f,
//            0.5f, 0.5f
    };

    // 纹理坐标
    private final float[] textureData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f

//            0f, 0.5f,
//            0.5f, 0.5f,
//            0f, 0f,
//            0.5f, 0f
    };

    private Context mContext;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int program;
    private int av_position;
    private int af_position;
    private int s_texture;

    public TXImageRender(Context context) {
        this.mContext = context;
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
//        initOpenGLES();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        LogUtils.d(TAG, "onSurfaceChanged");
//        super.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        LogUtils.d(TAG, "onDrawFrame");
//        super.onDrawFrame();
//        renderFrame();
    }

    public void onDrawFrame(int program,int imageTexure) {
        LogUtils.d(TAG, "onDrawFrame");
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//        GLES20.glClearColor(1f, 0f, 0f, 0f);
        renderFrame(program,imageTexure);
    }

    private void renderFrame(int mProgram,int imageTexure) {
        if (mProgram > 0) {
            LogUtils.d(TAG, "renderFrame");
            // 10.使用渲染器
            GLES20.glUseProgram(mProgram);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexure);
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.img_111);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//            GLES20.glUniform1i(s_texture, 0);
            // 11.使顶点坐标和纹理坐标属性数组有效
//            GLES20.glEnableVertexAttribArray(av_position);
//            GLES20.glVertexAttribPointer(av_position, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);
//            GLES20.glEnableVertexAttribArray(af_position);
//            GLES20.glVertexAttribPointer(af_position, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);
            // 12.绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
    }


    private void initOpenGLES() {
        LogUtils.d(TAG, "initOpenGLES");
        // 2.加载 shader
//        String vertex = ShaderUtils.readRawTxt(mContext, R.raw.vertex_image_shader);
//        String texture = ShaderUtils.readRawTxt(mContext, R.raw.fragment_image_shader);
        // 3.创建渲染程序
//        program = ShaderUtils.createProgram(vertex, texture);
        if (program > 0) {
            // 4.得到着色器中的属性
//            av_position = GLES20.glGetAttribLocation(program, "av_Position");
//            af_position = GLES20.glGetAttribLocation(program, "af_Position");
//            s_texture = GLES20.glGetUniformLocation(program, "s_texture");
//            int[] textureid = new int[1];
//            // 5.创建纹理
//            GLES20.glGenTextures(1, textureid, 0);
//            if (textureid[0] == 0) {
//                LogUtils.d(TAG, " textureid[0] == 0");
//                return;
//            }
            // 6.绑定纹理
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid[0]);
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);


            // 7.设置环绕和过滤方式 环绕（超出纹理坐标范围）：（s==x t==y GL_REPEAT 重复）
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
//            // 8.过滤（纹理像素映射到坐标点）：（缩小、放大：GL_LINEAR线性）
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // 9.绑定图片
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
//            if (bitmap != null) {
//                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//                bitmap.recycle();
//                bitmap = null;
//            }
            LogUtils.d(TAG, "bind Bitmap");
        }
    }

    private void renderFrame() {
        if (program > 0) {
            LogUtils.d(TAG, "renderFrame");
            // 10.使用渲染器
            GLES20.glUseProgram(program);
            // 11.使顶点坐标和纹理坐标属性数组有效
            GLES20.glEnableVertexAttribArray(av_position);
            GLES20.glVertexAttribPointer(av_position, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);

            GLES20.glEnableVertexAttribArray(af_position);
            GLES20.glVertexAttribPointer(af_position, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);
            // 12.绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }
}
