package com.taxiao.opengl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

/**
 * SurfaceView
 * Created by hanqq on 2021/6/14
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private JniSdkImpl jniSdk;

    public TXSurfaceView(Context context) {
        this(context, null);
    }

    public TXSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TXSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Surface surface = holder.getSurface();
        if (jniSdk != null) {
            jniSdk.surfaceCreated(surface);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (jniSdk != null) {
            jniSdk.surfaceChanged(width, height);
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    public void setJniSdkImpl(JniSdkImpl jniSdk) {
        this.jniSdk = jniSdk;
    }
}
