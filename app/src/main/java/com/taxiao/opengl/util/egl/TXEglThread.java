package com.taxiao.opengl.util.egl;

import android.util.Log;

import com.taxiao.opengl.util.LogUtils;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLContext;

/**
 * egl thread
 * Created by hanqq on 2021/6/26
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXEglThread extends Thread {

    private static String TAG = TXEglThread.class.getName();
    private WeakReference<TXEglSurfaceView> mEglSurfaceViewWeakReference;
    private TXEglHelp mTXEglHelp;
    public boolean mIsStart;// 开始
    public boolean mIsCreate;// 创建
    public boolean mIsChange;// 改变
    public boolean mIsExit;// 退出
    private int mWidth;
    private int mHeight;
    private Object mObject;

    /**
     * The renderer only renders
     * when the surface is created, or when {@link #requestRender} is called.
     * 手动渲染
     */
    public final static int RENDERMODE_WHEN_DIRTY = 0;

    /**
     * The renderer is called
     * continuously to re-render the scene.
     * 自动渲染
     */
    public final static int RENDERMODE_CONTINUOUSLY = 1;

    public TXEglThread(WeakReference<TXEglSurfaceView> eglSurfaceViewWeakReference) {
        this.mEglSurfaceViewWeakReference = eglSurfaceViewWeakReference;
    }

    @Override
    public void run() {
        super.run();
        LogUtils.d("time_egl", "time3: " + System.currentTimeMillis());
        if (mEglSurfaceViewWeakReference == null || mEglSurfaceViewWeakReference.get() == null) {
            Log.d(TAG, "mEglSurfaceViewWeakReference == null || mEglSurfaceViewWeakReference.get() == null");
            return;
        }
        mIsStart = false;
        mIsExit = false;
        mObject = new Object();
        mTXEglHelp = new TXEglHelp();
        mTXEglHelp.init(mEglSurfaceViewWeakReference.get().mSurface, mEglSurfaceViewWeakReference.get().mEGLContext);
        while (true) {
            Log.d(TAG, String.format("mIsStart: %b , mIsCreate: %b , mIsChange: %b  mIsExit: %b ", mIsStart, mIsCreate, mIsChange, mIsExit));
            onStart();

            onCreate();

            onChange();

            onDraw();

            if (mIsExit) {
                release();
                break;
            }

            mIsStart = true;
        }
    }

    private void onStart() {
        if (mIsStart) {
            Log.d(TAG, "onStart: ");
            if (mEglSurfaceViewWeakReference == null || mEglSurfaceViewWeakReference.get() == null) {
                Log.d(TAG, " mIsStart mEglSurfaceViewWeakReference == null || mEglSurfaceViewWeakReference.get() == null");
            }
            int mRenderMode = mEglSurfaceViewWeakReference.get().mRenderMode;
            Log.d(TAG, "mRenderMode: " + mRenderMode);
            if (mRenderMode == RENDERMODE_WHEN_DIRTY) {
                synchronized (mObject) {
                    try {
                        mObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else if (mRenderMode == RENDERMODE_CONTINUOUSLY) {
                try {
                    Thread.sleep(1000 / 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, " mIsStart mRenderMode == null");
            }
        }
    }

    private void onCreate() {
        if (mIsCreate && mEglSurfaceViewWeakReference != null && mEglSurfaceViewWeakReference.get() != null && mEglSurfaceViewWeakReference.get().mTXEglRender != null) {
            Log.d(TAG, "onCreate");
            mEglSurfaceViewWeakReference.get().mTXEglRender.onSurfaceCreated();
            mIsCreate = false;
        }
    }

    private void onChange() {
        if (mIsChange && mEglSurfaceViewWeakReference != null && mEglSurfaceViewWeakReference.get() != null && mEglSurfaceViewWeakReference.get().mTXEglRender != null) {
            Log.d(TAG, "onChange");
            mEglSurfaceViewWeakReference.get().mTXEglRender.onSurfaceChanged(mWidth, mHeight);
            mIsChange = false;
        }
    }

    private void onDraw() {
        if (mEglSurfaceViewWeakReference != null && mEglSurfaceViewWeakReference.get() != null && mEglSurfaceViewWeakReference.get().mTXEglRender != null && mTXEglHelp != null) {
            Log.d(TAG, "onDraw");
            mEglSurfaceViewWeakReference.get().mTXEglRender.onDrawFrame();
            // 解决手动绘制时无法渲染
            if (!mIsStart) {
                mEglSurfaceViewWeakReference.get().mTXEglRender.onDrawFrame();
            }
            mTXEglHelp.swapBuffers();
        }
    }

    private void release() {
        Log.d(TAG, "release");
        if (mTXEglHelp != null) {
            mTXEglHelp.destoryEgl();
            mTXEglHelp = null;
            mObject = null;
            mEglSurfaceViewWeakReference.clear();
            mEglSurfaceViewWeakReference = null;
        }
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    // 渲染
    public void requestRender() {
        if (mObject != null) {
            Log.d(TAG, "requestRender");
            synchronized (mObject) {
                mObject.notifyAll();
            }
        }
    }

    public void onDestory() {
        mIsExit = true;
        requestRender();
    }

    public EGLContext getEglContext() {
        if (mTXEglHelp != null) {
            return mTXEglHelp.getmEglContext();
        }
        return null;
    }
}
