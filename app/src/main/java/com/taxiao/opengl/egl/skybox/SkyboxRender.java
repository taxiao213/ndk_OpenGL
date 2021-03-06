package com.taxiao.opengl.egl.skybox;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.taxiao.opengl.R;
import com.taxiao.opengl.egl.BaseRenderImp;
import com.taxiao.opengl.egl.obj.Point;
import com.taxiao.opengl.egl.obj.Vector;
import com.taxiao.opengl.egl.particle.ParticleShaderProgram;
import com.taxiao.opengl.egl.particle.ParticleShooter;
import com.taxiao.opengl.egl.particle.ParticleSystem;
import com.taxiao.opengl.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glClearColor;
import static android.opengl.Matrix.setIdentityM;

/**
 * 粒子发射器 增加天空盒
 * Created by hanqq on 2022/2/7
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class SkyboxRender extends BaseRenderImp {

    private Context context;
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

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

    private SkyboxShaderProgram skyboxProgram;
    private Skybox skybox;
    private int skyboxTexture;
    private float xRotation, yRotation;

    public SkyboxRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0f, 0f, 0.0f);

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
                / (float) height, 1f, 10f);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        drawSkybox();
        drawParticles();
    }

    // 因为只有粒子才有混合技术，绘制完成后再关闭
    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0,
                viewMatrix, 0);

        // Enable additive blending 粒子混合，越多越亮，累加混合技术
        GLES20.glEnable(GLES20.GL_BLEND);
        // 混合公式：输出=（源因子*源片段）+（目标因子*目标片段），源因子和目标因子 通过glBlendFunc配置，
        // 设置源因子和目标因子都为GLES20.GL_ONE
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);

        particleProgram.useProgram();
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, texture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    // 以（0，0，0）为中心绘制天空盒
    private void drawSkybox() {
        setIdentityM(viewMatrix, 0);
        // 首先应用于y轴，再应用于x轴旋转矩阵，这叫“FPS样式”旋转（FPS代表First People Shooter 第一人称发射者）,
        // 向上向下看围绕X轴，向左向右围绕Y轴
        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(viewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
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
    }
}
