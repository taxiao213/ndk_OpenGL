package com.taxiao.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

/**
 * SurfaceView
 * Created by hanqq on 2021/6/14
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXSurfaceView3 extends SurfaceView implements SurfaceHolder.Callback {
    private JniSdkImpl jniSdk;
    private ISurfaceInterface surfaceInterface;

    public TXSurfaceView3(Context context) {
        this(context, null);
    }

    public TXSurfaceView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TXSurfaceView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (jniSdk != null) {
            jniSdk.setFilterType(2);
            Surface surface = holder.getSurface();
            Log.d("TXSurfaceView3", "surfaceCreated ");
            jniSdk.onSurfaceCreated(surface);
            jniSdk.setRenderType(2);
            if (surfaceInterface != null) {
                surfaceInterface.init();
            }
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (jniSdk != null) {
            Log.d("TXSurfaceView3", "surfaceChanged ");
            jniSdk.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (jniSdk != null) {
            jniSdk.onSurfaceDestroy();
        }
    }

    public void setJniSdkImpl(JniSdkImpl jniSdk, ISurfaceInterface surfaceInterface) {
        this.jniSdk = jniSdk;
        this.surfaceInterface = surfaceInterface;
    }

    public interface ISurfaceInterface {
        void init();
    }
}
