package com.taxiao.opengl.util.egl;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by hanqq on 2021/6/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXEglRender implements TXEglSurfaceView.EglRender {
    private static String TAG = TXEglRender.class.getName();

    @Override
    public void onSurfaceCreated() {
        Log.d(TAG, "onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.d(TAG, String.format("onSurfaceChanged width:%d , height:%d", width, height));
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame() {
        Log.d(TAG, "onDrawFrame");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0f, 1f, 1f, 0f);
    }
}
