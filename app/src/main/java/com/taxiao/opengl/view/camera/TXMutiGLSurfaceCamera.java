package com.taxiao.opengl.view.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.WindowManager;

import com.taxiao.opengl.util.Camera1Utils;
import com.taxiao.opengl.util.Constant;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.egl.TXEglSurfaceView;
import com.taxiao.opengl.util.egl.TXEglThread;

/**
 * 渲染图片 多Surface渲染多个纹理
 * ffmpeg decode
 * <p>
 * Created by hanqq on 2021/5/31
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXMutiGLSurfaceCamera extends TXEglSurfaceView {
    private String TAG = TXMutiGLSurfaceCamera.this.getClass().getSimpleName();
    private TXMutiCameraRender txEglRender;
    private int cameraId = Camera1Utils.getInstance().getCameraId();

    public TXMutiGLSurfaceCamera(Context context) {
        this(context, null);
    }

    public TXMutiGLSurfaceCamera(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TXMutiGLSurfaceCamera(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setRenderMode(Constant.RENDERMODE_CONTINUOUSLY);
        txEglRender = new TXMutiCameraRender(getContext());
        setRender(txEglRender);
        previewAngle(getContext());
    }

    public void setOnCreate(OnRenderCameraListener onRenderCreateListener) {
        if (onRenderCreateListener != null) {
            txEglRender.setOnRenderCreateListener(onRenderCreateListener);
        }
    }

    public void previewAngle(Context context) {
        if (txEglRender == null) return;
        int angle = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        txEglRender.resetMatrix();
        switch (angle) {
            case Surface.ROTATION_0:
                LogUtils.d(TAG, "0");
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    txEglRender.setAngle(90, 0, 0, 1);
                    txEglRender.setAngle(180, 1, 0, 0);
                } else {
                    txEglRender.setAngle(90f, 0f, 0f, 1f);
                }

                break;
            case Surface.ROTATION_90:
                LogUtils.d(TAG, "90");
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    txEglRender.setAngle(180, 0, 0, 1);
                    txEglRender.setAngle(180, 0, 1, 0);
                } else {
                    txEglRender.setAngle(90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_180:
                LogUtils.d(TAG, "180");
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    txEglRender.setAngle(90f, 0.0f, 0f, 1f);
                    txEglRender.setAngle(180f, 0.0f, 1f, 0f);
                } else {
                    txEglRender.setAngle(-90, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_270:
                LogUtils.d(TAG, "270");
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    txEglRender.setAngle(180f, 0.0f, 1f, 0f);
                } else {
                    txEglRender.setAngle(0f, 0f, 0f, 1f);
                }
                break;
        }
    }

    public int getTextureID() {
        if (txEglRender != null) {
            return txEglRender.getTextureID();
        }
        return -1;
    }
}
