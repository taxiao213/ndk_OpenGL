package com.taxiao.opengl.view.camera;

import android.content.Context;
import android.util.AttributeSet;

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
    private OnRenderCameraListener onRenderCameraListener;

    public TXMutiGLSurfaceCamera(Context context) {
        this(context, null);
    }

    public TXMutiGLSurfaceCamera(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TXMutiGLSurfaceCamera(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LogUtils.d(TAG, "create");
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        LogUtils.d(TAG, String.format("size: width:%d , height:%d ", width, height));
        setRenderMode(TXEglThread.RENDERMODE_CONTINUOUSLY);
        txEglRender = new TXMutiCameraRender(getContext(), width, height);
        setRender(txEglRender);
        LogUtils.d(TAG, "onSizeChanged: ");
        if (onRenderCameraListener != null) {
            txEglRender.setOnRenderCreateListener(onRenderCameraListener);
        }
    }

    public void setOnCreate(OnRenderCameraListener onRenderCreateListener) {
        LogUtils.d(TAG, "setOnCreate OnRenderCameraListener : ");
        this.onRenderCameraListener = onRenderCreateListener;
    }

}
