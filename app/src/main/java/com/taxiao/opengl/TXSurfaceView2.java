package com.taxiao.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
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
public class TXSurfaceView2 extends SurfaceView implements SurfaceHolder.Callback {
    private JniSdkImpl jniSdk;

    public TXSurfaceView2(Context context) {
        this(context, null);
    }

    public TXSurfaceView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TXSurfaceView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Surface surface = holder.getSurface();
        if (jniSdk != null) {
            jniSdk.onSurfaceCreated(surface);
            jniSdk.setRenderType(2);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            int byteCount = bitmap.getByteCount();
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(byteCount);
            bitmap.copyPixelsToBuffer(byteBuffer);
            jniSdk.onDrawImage(bitmap.getWidth(), bitmap.getHeight(), byteCount, byteBuffer.array());
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (jniSdk != null) {
            jniSdk.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (jniSdk != null) {
            jniSdk.onSurfaceDestroy();
        }
    }

    public void setJniSdkImpl(JniSdkImpl jniSdk) {
        this.jniSdk = jniSdk;
    }
}
