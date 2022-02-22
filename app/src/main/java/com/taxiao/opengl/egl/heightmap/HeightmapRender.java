package com.taxiao.opengl.egl.heightmap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.taxiao.opengl.R;
import com.taxiao.opengl.egl.BaseRenderImp;
import com.taxiao.opengl.egl.obj.Point;
import com.taxiao.opengl.egl.obj.Vector;
import com.taxiao.opengl.egl.particle.ParticleShaderProgram;
import com.taxiao.opengl.egl.particle.ParticleShooter;
import com.taxiao.opengl.egl.particle.ParticleSystem;
import com.taxiao.opengl.egl.skybox.Skybox;
import com.taxiao.opengl.egl.skybox.SkyboxShaderProgram;
import com.taxiao.opengl.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.Matrix.setIdentityM;

/**
 * 渲染高度图
 * Created by hanqq on 2022/2/7
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class HeightmapRender extends BaseRenderImp {

    private Context context;
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewMatrixForSkyBox = new float[16];
    private final float[] projectionMatrix = new float[16];


    private final float[] tempMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private ParticleShaderProgram particleProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    /*private ParticleFireworksExplosion particleFireworksExplosion;
    private Random random;*/
    // 全局的初始时间 单位是纳秒 ，除以10亿 是秒
    private long globalStartTime;
    private int texture;

    private HeightmapShaderProgram heightmapProgram;
    private Heightmap heightmap;

    private SkyboxShaderProgram skyboxProgram;
    private Skybox skybox;
    private int skyboxTexture;
    private float xRotation, yRotation;

    public HeightmapRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0f, 0f, 0.0f);
        // 深度缓冲区功能
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // 关闭两面绘制，消减绘制开销
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        heightmapProgram = new HeightmapShaderProgram(context);
        heightmap = new Heightmap(((BitmapDrawable) context.getResources().getDrawable(R.mipmap.heightmap)).getBitmap());

        skyboxProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox();

        particleProgram = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.nanoTime();

        final Vector particleDirection = new Vector(0f, 0.5f, 0f);

        final float angleVarianceInDegrees = 5f;
        final float speedVariance = 1f;

        redParticleShooter = new ParticleShooter(
                new Point(-0.7f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance);

        greenParticleShooter = new ParticleShooter(
                new Point(0f, 0f, 0f),
                particleDirection,
                Color.rgb(25, 255, 25),
                angleVarianceInDegrees,
                speedVariance);

        blueParticleShooter = new ParticleShooter(
                new Point(0.7f, 0f, 0f),
                particleDirection,
                Color.rgb(5, 50, 255),
                angleVarianceInDegrees,
                speedVariance);
        texture = TextureHelper.loadTexture(context, R.mipmap.particle_texture);

        skyboxTexture = TextureHelper.loadCubeMap(context,
                new int[]{R.mipmap.left, R.mipmap.right,
                        R.mipmap.bottom, R.mipmap.top,
                        R.mipmap.front, R.mipmap.back});
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        // 运用透视投影和视图矩阵将图放在正确的空间
        Matrix.perspectiveM(projectionMatrix, 0, 45, (float) width
                / (float) height, 1f, 100f);
        updateViewMatrices();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        drawHeightmap();
        drawSkybox();
        drawParticles();
    }

    private void drawHeightmap() {
        setIdentityM(modelMatrix, 0);
        // Expand the heightmap's dimensions, but don't expand the height as
        // much so that we don't get insanely tall mountains.
        // x 和 z 方向放大100倍
        Matrix.scaleM(modelMatrix, 0, 100f, 10f, 100f);
        updateMvpMatrix();
        heightmapProgram.useProgram();
        heightmapProgram.setUniforms(modelViewProjectionMatrix);
        heightmap.bindData(heightmapProgram);
        heightmap.draw();
    }

    private void drawSkybox() {
        setIdentityM(modelMatrix, 0);
        updateMvpMatrixForSkybox();
        // 深度测试默认是 GL_LESS
        GLES20.glDepthFunc(GLES20.GL_LEQUAL); // This avoids problems with the skybox itself getting clipped.
        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(modelViewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
        GLES20.glDepthFunc(GLES20.GL_LESS);
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        setIdentityM(modelMatrix, 0);
        updateMvpMatrix();

        // 保持测试功能开启的同时，禁用深度更新
        GLES20.glDepthMask(false);
        // Enable additive blending 粒子混合，越多越亮，累加混合技术
        GLES20.glEnable(GLES20.GL_BLEND);
        // 混合公式：输出=（源因子*源片段）+（目标因子*目标片段），源因子和目标因子 通过glBlendFunc配置，
        // 设置源因子和目标因子都为GLES20.GL_ONE
        glBlendFunc(GLES20.GL_ONE,GLES20.GL_ONE);

        particleProgram.useProgram();
        particleProgram.setUniforms(modelViewProjectionMatrix, currentTime, texture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();
        glDisable(GLES20.GL_BLEND);
        GLES20.glDepthMask(true);
    }

    private void updateViewMatrices() {
        // 重置
        setIdentityM(viewMatrix, 0);
        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        System.arraycopy(viewMatrix, 0, viewMatrixForSkyBox, 0, viewMatrix.length);

        // We want the translation to apply to the regular view matrix, and not
        // the skybox.
        Matrix.translateM(viewMatrix, 0, 0, -1.5f, -15f);
    }

    private void updateMvpMatrix() {
        // 合并为模型 视图投影矩阵
        Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }

    private void updateMvpMatrixForSkybox() {
        Matrix.multiplyMM(tempMatrix, 0, viewMatrixForSkyBox, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }

    @Override
    public void handleTouchDrag(float deltaX, float deltaY) {
        super.handleTouchDrag(deltaX, deltaY);
        // 不需要过渡灵敏，所以除以16，缩减拖动效果，不希望上下旋转过大，把y限制在+90到-90度之间
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;

        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }
        updateViewMatrices();
    }
}
