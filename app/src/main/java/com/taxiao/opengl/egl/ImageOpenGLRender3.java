package com.taxiao.opengl.egl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.taxiao.opengl.R;
import com.taxiao.opengl.egl.obj.ColorShaderProgram2;
import com.taxiao.opengl.egl.obj.Geometry;
import com.taxiao.opengl.egl.obj.Mallet2;
import com.taxiao.opengl.egl.obj.Plane;
import com.taxiao.opengl.egl.obj.Point;
import com.taxiao.opengl.egl.obj.Puck;
import com.taxiao.opengl.egl.obj.Ray;
import com.taxiao.opengl.egl.obj.Sphere;
import com.taxiao.opengl.egl.obj.Table2;
import com.taxiao.opengl.egl.obj.TextureShaderProgram2;
import com.taxiao.opengl.egl.obj.Vector;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glViewport;

/**
 * 使用三角形扇绘制,增加颜色属性,加入正交投影,加入w分量,创建三维图,创建投影矩阵，利用平移矩阵将桌子移到可视界面,
 * 纹理增加图片
 * 利用右手坐标系，加入旋转矩阵，逆时针旋转60度
 * 构建简单的物体
 * 增加手势互动
 * Created by hanqq on 2021/12/22
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class ImageOpenGLRender3 extends BaseRenderImp {
    private String TAG = this.getClass().getSimpleName();
    private Context mContext;

    private float[] projectionMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] viewProjectMatrix = new float[16];
    private float[] modelViewProjectMatrix = new float[16];
    private float[] invertedViewProjectMatrix = new float[16];

    private Table2 table;
    private Mallet2 mallet;
    private Puck puck;
    private TextureShaderProgram2 textureProgram;
    private ColorShaderProgram2 colorProgram;
    private int texture;
    private boolean malletPressed = false;

    // 木槌的位置记录
    private Point blueMalletPosition;
    // 边界限定
    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;
    // 上次木槌的位置
    private Point previousBlueMalletPosition;
    // 冰球的位置
    private Point puckPosition;
    // 冰球向量 用向量存储速度和方向
    private Vector puckVector;

    public ImageOpenGLRender3(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        table = new Table2();
        mallet = new Mallet2(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);

        blueMalletPosition = new Point(0f, mallet.height / 2f, 0.4f);
        puckPosition = new Point(0f, puck.height / 2f, 0f);
        puckVector = new Vector(0f, 0f, 0f);

        textureProgram = new TextureShaderProgram2(mContext);
        colorProgram = new ColorShaderProgram2(mContext);
        texture = TextureHelper.loadTexture(mContext, R.mipmap.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.d(TAG, " onSurfaceChanged,width: " + width + " ,height: " + height);
        glViewport(0, 0, width, height);
        // 创建投影矩阵
        Matrix.perspectiveM(projectionMatrix, 0, 45, (float) width / height, 1f, 10f);
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
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // ==== 冰球的移动,冰球增加边界检查，撞到桌子边缘时，都要把它从桌子边缘弹开 ====
        puckPosition = puckPosition.transLate(puckVector);
        if (puckPosition.x < leftBound + puck.radius || puckPosition.x > rightBound - puck.radius) {
            puckVector = new Vector(-puckVector.x, puckVector.y, puckVector.z);
        }
        if (puckPosition.z < farBound + puck.radius || puckPosition.z > nearBound - puck.radius) {
            puckVector = new Vector(puckVector.x, puckVector.y, -puckVector.z);
        }
        puckPosition = new Point(
                clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y,
                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
        );
        // ==== end ====

        // 增加摩擦，当冰球从桌子边缘弹开时，我们将看到它变得更慢
        puckVector = puckVector.scale(0.99f);
        puckVector = puckVector.scale(0.90f);

        Matrix.multiplyMM(viewProjectMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        // 取消视图矩阵和投影矩阵
        Matrix.invertM(invertedViewProjectMatrix, 0, viewProjectMatrix, 0);

        // draw table
        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniform(modelViewProjectMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        // draw mallet 绘制木槌，动态绘制
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniform(modelViewProjectMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

//        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        colorProgram.setUniform(modelViewProjectMatrix, 0f, 0f, 1f);
        mallet.draw();

        // draw puck
//        positionObjectInScene(0f, puck.height / 2f, 0f);
        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
        colorProgram.setUniform(modelViewProjectMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    private void positionTableInScene() {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        Matrix.multiplyMM(modelViewProjectMatrix, 0, viewProjectMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, x, y, z);
        Matrix.multiplyMM(modelViewProjectMatrix, 0, viewProjectMatrix, 0, modelMatrix, 0);
    }

    @Override
    public void handleTouchPress(float normalizedX, float normalizedY) {
        super.handleTouchPress(normalizedX, normalizedY);
        LogUtils.d(TAG, "===== handleTouchPress ===== ");
        // 将触控的点转成一个三维射线
        // 通常我们把一个三维场景投递到二维屏幕的时候，我们使用透视投影和透视除法把顶点坐标变换为归一化设备坐标
        // 二维点转换成三维，我们取消透视投影和透视除法，我们要实现这个转换，需要一个反转的矩阵，它会取消视图矩阵和投影矩阵
        // 反转透视投影和透视除法
        Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        Sphere malletBoundingSphere = new Sphere(new Point(blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z),
                mallet.height / 2f);
        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
    }

    // 将点转成射线
    private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
        float[] nearPointWorld = new float[4];
        float[] farPointWorld = new float[4];
        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectMatrix, 0, farPointNdc, 0);
        divideByW(nearPointWorld);
        divideByW(farPointWorld);
        Point nearPointRay = new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Point farPointRay = new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    // 把x,y,z 除以反转的w,就撤销了透视除法的影响，转换成世界空间中的两个点
    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    @Override
    public void handleTouchDrag(float normalizedX, float normalizedY) {
        super.handleTouchDrag(normalizedX, normalizedY);
        if (malletPressed) {
            LogUtils.d(TAG, "===== handleTouchDrag ===== ");
            Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
            Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 1, 0));
            Point touchedPoint = Geometry.intersectionPoint(ray, plane);
            previousBlueMalletPosition = blueMalletPosition;
            blueMalletPosition = new Point(
                    clamp(touchedPoint.x,
                            leftBound + mallet.radius,
                            rightBound - mallet.radius),
                    mallet.height / 2f,
                    clamp(touchedPoint.z,
                            farBound + mallet.radius,
                            nearBound - mallet.radius));

            // Now test if mallet has struck the puck.
            float distance =
                    Geometry.vectorBetween(blueMalletPosition, puckPosition).length();

            if (distance < (puck.radius + mallet.radius)) {
                // 检查木槌和冰球之间的距离，如果距离小于它们的半径之和，就说明木槌撞击了冰球，并且我们用木槌的位置
                // 和当前木槌的位置给冰球创建一个方向向量，木槌移动的越快，向量就会越大，冰球也会移动的越快
                // The mallet has struck the puck. Now send the puck flying
                // based on the mallet velocity.
                puckVector = Geometry.vectorBetween(
                        previousBlueMalletPosition, blueMalletPosition);
            }
        }
    }

    // 边界定义
    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }
}
