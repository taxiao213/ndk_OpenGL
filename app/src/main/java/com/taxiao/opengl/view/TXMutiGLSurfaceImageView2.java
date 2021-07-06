package com.taxiao.opengl.view;

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
public class TXMutiGLSurfaceImageView2 extends TXEglSurfaceView {
    private String TAG = TXMutiGLSurfaceImageView2.this.getClass().getSimpleName();
    private TXMutiImageRender2 txEglRender;
    private OnRenderCreateListener onRenderCreateListener;

    public TXMutiGLSurfaceImageView2(Context context) {
        this(context, null);
    }

    public TXMutiGLSurfaceImageView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TXMutiGLSurfaceImageView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LogUtils.d(TAG,"create");
        post(new Runnable() {
            @Override
            public void run() {
                LogUtils.d(TAG, String.format("create: width:%d , height:%d ", getWidth(), getHeight()));
            }
        });
    }

    public TXMutiImageRender2 getTXEglRender() {
        return txEglRender;
    }

    public void setTextureId(int textureId, int index) {
        if (txEglRender != null) {
            txEglRender.setTextureId(textureId, index);
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        LogUtils.d(TAG, String.format("size: width:%d , height:%d ", width, height));
        txEglRender = new TXMutiImageRender2(getContext(), width, height);
        setRender(txEglRender);
        setRenderMode(TXEglThread.RENDERMODE_WHEN_DIRTY);
        LogUtils.d(TAG, "size: ");
        if (onRenderCreateListener != null) {
            onRenderCreateListener.onCreate(0);
        }
    }

    public void setOnCreate(OnRenderCreateListener onRenderCreateListener) {
        this.onRenderCreateListener = onRenderCreateListener;
    }

}
