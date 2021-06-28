package com.taxiao.opengl.util.egl;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.taxiao.opengl.util.LogUtils;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLContext;

/**
 * 自定义 GLSurfaceView
 * Created by hanqq on 2021/6/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public abstract class TXEglSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static String TAG = TXEglSurfaceView.class.getName();

    public Surface mSurface;
    public EGLContext mEGLContext;
    public TXEglRender mTXEglRender;
    public int mRenderMode = TXEglThread.RENDERMODE_CONTINUOUSLY;
    private TXEglThread mTxEglThread;

    public TXEglSurfaceView(Context context) {
        this(context, null);
    }

    public TXEglSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TXEglSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    public void setRender(TXEglRender txEglRender) {
        this.mTXEglRender = txEglRender;
    }

    public void setRenderMode(int renderMode) {
        this.mRenderMode = renderMode;
    }

    /**
     * Get the current rendering mode. May be called
     * from any thread. Must not be called before a renderer has been set.
     *
     * @return the current rendering mode.
     */
    public int getRenderMode() {
        return mRenderMode;
    }

    // 外部传入 Surface
    public void setSurface(Surface mSurface) {
        this.mSurface = mSurface;
    }

    public void requestRender() {
        if (mTxEglThread != null) {
            mTxEglThread.requestRender();
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        if (mSurface == null) {
            mSurface = holder.getSurface();
        }
        mTxEglThread = new TXEglThread(new WeakReference<TXEglSurfaceView>(this));
        mTxEglThread.mIsCreate = true;
        mTxEglThread.start();
        LogUtils.d("time_egl", "time1: " + System.currentTimeMillis());
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (mTxEglThread != null) {
            Log.d(TAG, "surfaceChanged");
            mTxEglThread.setWidth(width);
            mTxEglThread.setHeight(height);
            mTxEglThread.mIsChange = true;
        }
        LogUtils.d("time_egl", "time2: " + System.currentTimeMillis());
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (mTxEglThread != null) {
            Log.d(TAG, "surfaceDestroyed");
            mTxEglThread.onDestory();
            mTxEglThread = null;
            mSurface = null;
            mEGLContext = null;
        }
    }

    public interface EglRender {
        void onSurfaceCreated();

        void onSurfaceChanged(int width, int height);

        void onDrawFrame();
    }
}
