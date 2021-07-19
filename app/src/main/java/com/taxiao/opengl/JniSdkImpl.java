package com.taxiao.opengl;

import android.view.Surface;

/**
 * jni 交互
 * Created by hanqq on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class JniSdkImpl {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native void surfaceCreated(Surface surface);

    public native void surfaceChanged(int width, int height);

    public native void drawImage(int width, int height, int size, byte[] bytes);

    public native void onSurfaceCreated(Surface surface);

    public native void onSurfaceChanged(int width, int height);

    public native void onSurfaceDestroy();

    public native void onDrawImage(int width, int height, int size, byte[] bytes);

    public native void setRenderType(int type);

    public native void onSurfaceChangedFilter();

    public native void setYUVData(byte[] yuv_y, byte[] yuv_u, byte[] yuv_v, int width, int height);

    public native void setFilterType(int type);

    public native void startRecord(String absolutePath);

    public native void pauseRecord();

    public native void resumeRecord();

    public native void stopRecord();

    public native void initRtmp(String url);

}
