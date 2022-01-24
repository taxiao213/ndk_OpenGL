package com.taxiao.opengl.egl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.taxiao.opengl.R;
import com.taxiao.opengl.egl.image.Table;
import com.taxiao.opengl.egl.image.TextureShaderProgram;
import com.taxiao.opengl.egl.obj.ColorShaderProgram2;
import com.taxiao.opengl.egl.obj.Mallet2;
import com.taxiao.opengl.egl.obj.Puck;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * 使用三角形扇绘制,增加颜色属性,加入正交投影,加入w分量,创建三维图,创建投影矩阵，利用平移矩阵将桌子移到可视界面,
 * 纹理增加图片
 * 利用右手坐标系，加入旋转矩阵，逆时针旋转60度
 * 构建简单的物体
 * Created by hanqq on 2021/12/22
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class ImageOpenGLRender2 implements GLSurfaceView.Renderer {
    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private float[] projectMatrix = new float[16];
    private float[] modelMatrix = new float[16];

    private float[] viewMatrix = new float[16];
    private float[] viewProjectMatrix = new float[16];
    private float[] modelViewProjectMatrix = new float[16];
    private Table table;
    private Mallet2 mallet;
    private Puck puck;
    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram2 colorShaderProgram;
    private int texture;

    public ImageOpenGLRender2(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0f, 0f, 0.0f);
        table = new Table();
        mallet = new Mallet2(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);
        textureShaderProgram = new TextureShaderProgram(mContext);
        colorShaderProgram = new ColorShaderProgram2(mContext);
        texture = TextureHelper.loadTexture(mContext, R.mipmap.img_444);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.d(TAG, " onSurfaceChanged,width: " + width + " ,height: " + height);
        glViewport(0, 0, width, height);
        // 创建投影矩阵
        Matrix.perspectiveM(projectMatrix, 0, 45, (float) width / height, 1f, 10f);
        // 设置视口 把眼睛eye 设为（0,1.2,2.2），意味着眼睛的位置在x-z平面上方1.2个单位，并向后2.2个单位，换句话说，场景的所有东西都在你下面1.2个单位和你前面2.2个单位地方
        // 中心设为（0,0,0）意味着你将向下看你前面的原点，并把指向up设为（0,1,0），意味着你的头是笔直指向上面的
        Matrix.setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
//        // 默认处于z=0的位置，因为这个视锥体是从z值为-1开始的，除非移动到距离内，否则无法看到桌子
//        Matrix.setIdentityM(modelMatrix, 0);
//        // 沿着z轴负方向移动4个单位
//        Matrix.translateM(modelMatrix, 0, 0f, 0f, -4f);
//        // 旋转矩阵，沿着x轴旋转-60度
//        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
//        // 投影矩阵在左侧*模型矩阵在右侧*顶点坐标
//        float[] temp = new float[16];
//        Matrix.multiplyMM(temp, 0, projectMatrix, 0, modelMatrix, 0);
//        System.arraycopy(temp, 0, projectMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        LogUtils.d(TAG, " onDrawFrame ");
        Matrix.multiplyMM(viewProjectMatrix, 0, projectMatrix, 0, viewMatrix, 0);
        positionTableInScene();
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniform(modelViewProjectMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

        // draw mallet
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniform(modelViewProjectMatrix, 1f, 0f, 0f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorShaderProgram.setUniform(modelViewProjectMatrix, 0f, 0f, 1f);
        mallet.draw();

        // draw puck
        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorShaderProgram.setUniform(modelViewProjectMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorShaderProgram);
        puck.draw();
    }

    private void positionObjectInScene(float x, float y, float z) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, 0f, x, y, z);
        Matrix.multiplyMM(modelViewProjectMatrix, 0, viewProjectMatrix, 0, modelMatrix, 0);
    }

    private void positionTableInScene() {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        Matrix.multiplyMM(modelViewProjectMatrix, 0, viewProjectMatrix, 0, modelMatrix, 0);
    }

}
