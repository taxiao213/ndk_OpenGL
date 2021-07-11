package com.taxiao.opengl.encodec;

import android.util.Log;

import com.taxiao.opengl.util.Constant;
import com.taxiao.opengl.util.egl.TXEglHelp;

import java.lang.ref.WeakReference;


/**
 * Created by hanqq on 2021/7/10
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXEncodecEglThread extends Thread {
    private static String TAG = TXEncodecEglThread.class.getName();

    private WeakReference<TXBaseMediaCodecEncoder> mWeakReference;
    private TXEglHelp mTXEglHelp;
    private Object mObject;
    public boolean mIsStart;// 开始
    public boolean mIsCreate;// 创建
    public boolean mIsChange;// 改变
    public boolean mIsExit;// 退出

    public TXEncodecEglThread(WeakReference<TXBaseMediaCodecEncoder> weakReference) {
        mWeakReference = weakReference;
    }

    @Override
    public void run() {
        super.run();
        mIsExit = false;
        mIsStart = false;
        mObject = new Object();
        mTXEglHelp = new TXEglHelp();
        mTXEglHelp.init(mWeakReference.get().mSurface, mWeakReference.get().mEglContext);

        while (true) {
            Log.d(TAG, String.format("mIsStart: %b , mIsCreate: %b , mIsChange: %b  mIsExit: %b ", mIsStart, mIsCreate, mIsChange, mIsExit));
            onStart();

            onCreate();

            onChange(mWeakReference.get().mWidth, mWeakReference.get().mHeight);

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
            if (mWeakReference == null || mWeakReference.get() == null) {
                Log.d(TAG, " mIsStart mEglSurfaceViewWeakReference == null || mEglSurfaceViewWeakReference.get() == null");
            }
            int mRenderMode = mWeakReference.get().mRenderMode;
            Log.d(TAG, "mRenderMode: " + mRenderMode);
            if (mRenderMode == Constant.RENDERMODE_WHEN_DIRTY) {
                synchronized (mObject) {
                    try {
                        mObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else if (mRenderMode == Constant.RENDERMODE_CONTINUOUSLY) {
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
        if (mIsCreate && mWeakReference != null && mWeakReference.get() != null && mWeakReference.get().mTXEglRender != null) {
            Log.d(TAG, "onCreate");
            mWeakReference.get().mTXEglRender.onSurfaceCreated();
            mIsCreate = false;
        }
    }

    private void onChange(int width, int height) {
        if (mIsChange && mWeakReference != null && mWeakReference.get() != null && mWeakReference.get().mTXEglRender != null) {
            Log.d(TAG, "onChange");
            mWeakReference.get().mTXEglRender.onSurfaceChanged(width, height);
            mIsChange = false;
        }
    }

    private void onDraw() {
        if (mWeakReference != null && mWeakReference.get() != null && mWeakReference.get().mTXEglRender != null && mTXEglHelp != null) {
            Log.d(TAG, "onDraw");
            mWeakReference.get().mTXEglRender.onDrawFrame();
            // 解决手动绘制时无法渲染
            if (!mIsStart) {
                mWeakReference.get().mTXEglRender.onDrawFrame();
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
            mWeakReference.clear();
            mWeakReference = null;
        }
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
}
