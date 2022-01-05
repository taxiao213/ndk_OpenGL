package com.taxiao.opengl.egl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.taxiao.opengl.R;
import com.taxiao.opengl.egl.image.ColorShaderProgram;
import com.taxiao.opengl.egl.image.Mallet;
import com.taxiao.opengl.egl.image.Table;
import com.taxiao.opengl.egl.image.TextureShaderProgram;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.ShaderUtils;
import com.taxiao.opengl.util.TextureHelper;

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
 * 使用三角形扇绘制,增加颜色属性,加入正交投影,加入w分量,创建三维图,创建投影矩阵，利用平移矩阵将桌子移到可视界面,
 * 纹理增加图片
 * 利用右手坐标系，加入旋转矩阵，逆时针旋转60度
 * Created by hanqq on 2021/12/22
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class ImageOpenGLRender implements GLSurfaceView.Renderer {
    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private float[] projectMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private Table table;
    private Mallet mallet;
    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;
    private int texture;

    public ImageOpenGLRender(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0f, 0f, 0.0f);
        table = new Table();
        mallet = new Mallet();
        textureShaderProgram = new TextureShaderProgram(mContext);
        colorShaderProgram = new ColorShaderProgram(mContext);
        texture = TextureHelper.loadTexture(mContext, R.mipmap.img_111);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.d(TAG, " onSurfaceChanged,width: " + width + " ,height: " + height);
        glViewport(0, 0, width, height);
        // 创建投影矩阵
        Matrix.perspectiveM(projectMatrix, 0, 45, (float) width / height, 1f, 10f);
        // 默认处于z=0的位置，因为这个视锥体是从z值为-1开始的，除非移动到距离内，否则无法看到桌子
        Matrix.setIdentityM(modelMatrix, 0);
        // 沿着z轴负方向移动4个单位
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -4f);
        // 旋转矩阵，沿着x轴旋转-60度
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
        // 投影矩阵在左侧*模型矩阵在右侧*顶点坐标
        float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        LogUtils.d(TAG, " onDrawFrame ");
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniform(projectMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniform(projectMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

    }

    ;
}
