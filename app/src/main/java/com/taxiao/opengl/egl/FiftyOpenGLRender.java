package com.taxiao.opengl.egl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.taxiao.opengl.R;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * 使用三角形扇绘制,增加颜色属性,加入正交投影,加入w分量,创建三维图
 * Created by hanqq on 2021/12/22
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class FiftyOpenGLRender implements GLSurfaceView.Renderer {
    private String TAG = this.getClass().getSimpleName();
    // 坐标归一化 数据类型是x,y,z,w,r,g,b
    float[] tableVertices = {

            // 三角形扇
            -0.5f, 0.8f, 0f, 2f, 1f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0f, 1f, 1f, 0.7f, 0.7f,
            0.5f, -0.8f, 0f, 1f, 1f, 0.7f, 0.7f,
            -0.5f, 0.8f, 0f, 2f, 1f, 0.7f, 0.7f,
            0.5f, -0.8f, 0f, 1f, 1f, 0.7f, 0.7f,
            0.5f, 0.8f, 0f, 2f, 1f, 0.7f, 0.7f,

            // line1
            -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
            0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,

            // mallets
            0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
            0f, 0.4f, 0f, 1.75f, 1f, 0f, 0f,
    };
    private final FloatBuffer vertexData;
    private final int BYTES_PER_FLOAT = 4;
    // 一个顶点2个分量，代表x,y值
    private final int POSITION_COMPONENT_COUNT = 4;
    // r,g,b 代表颜色
    private final int COLOR_COMPONENT_COUNT = 3;
    private final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private Context mContext;
    private String vertex;
    private String fragment;
    private int program;
    private int aColorLocation;
    private int aPositionLocation;
    private int uMatrixLocation;
    private float[] projectMatrix = new float[16];

    public FiftyOpenGLRender(Context context) {
        this.mContext = context;
        vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(tableVertices);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0f, 0f, 0.0f);
        vertex = ShaderUtils.readRawTxt(mContext, R.raw.simple_vertex_shader2);
        fragment = ShaderUtils.readRawTxt(mContext, R.raw.simple_fragment_shader2);
        program = ShaderUtils.createProgram(vertex, fragment);
        if (program > 0) {
            LogUtils.d(TAG, " onSurfaceCreated,program: " + program);
            glUseProgram(program);
            aColorLocation = glGetAttribLocation(program, "a_Color");
            aPositionLocation = glGetAttribLocation(program, "a_Position");
            uMatrixLocation = glGetUniformLocation(program, "u_Matrix");
            // 找到属性 a_Position 对应的数据
            vertexData.position(0);
            //  int indx, 属性位置
            //  int size, 每个属性数据的计数，有多少个分量与每一个顶点相关联，每个顶点使用两个浮点数，用来表达x,y值，这就需要2个分量
            //  int type, 数据类型
            //  boolean normalized, 是整数的时候，才有意义
            //  int stride, 一个数组存储多于一个属性时，才有意义
            //  java.nio.Buffer 读取数据，从缓存区读取，如果不调用 vertexData.position(0); 可能会尝试读取缓冲区结尾后面的内容，导致程序崩溃
            glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
            glEnableVertexAttribArray(aPositionLocation);

            vertexData.position(POSITION_COMPONENT_COUNT);
            glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
            glEnableVertexAttribArray(aColorLocation);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.d(TAG, " onSurfaceChanged,width: " + width + " ,height: " + height);
        glViewport(0, 0, width, height);
        // 创建正交投影
        float aspectRatio = width > height ? (float) width / height : (float) height / width;
        if (width > height) {
            Matrix.orthoM(projectMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            Matrix.orthoM(projectMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        LogUtils.d(TAG, " onDrawFrame ");
        if (program > 0) {
            // 空气曲棍球桌子
            glClear(GL_COLOR_BUFFER_BIT);
            glUniformMatrix4fv(uMatrixLocation, 1, false, projectMatrix, 0);
            // 绘制桌子 四个三角形拼成四边形桌子
            // int mode, 绘制三角形扇
            // int first, 从开头开始读取
            // int count， 读入6个顶点，每个三角形三个顶点，最终画出两个三角形
            glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
            // 画红线，需要两个点
            // (-0.5f, 0f,) (0.5f, 0f,)
            glDrawArrays(GL_LINES, 6, 2);
            // 画木槌 1个点 blue
            // (0f, -0.25f,)
            glDrawArrays(GL_POINTS, 8, 1);
            // 画木槌 1个点 red
            // (0f, 0.25f)
            glDrawArrays(GL_POINTS, 9, 1);
        }
    }
}
