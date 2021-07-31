package com.taxiao.opengl;

import android.view.Surface;

import com.taxiao.opengl.rtmp.TXConnectListenr;

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

    private TXConnectListenr txConnectListenr;

    public void setTxConnectListenr(TXConnectListenr txConnectListenr) {
        this.txConnectListenr = txConnectListenr;
    }

    private void onConnecting() {
        if (txConnectListenr != null) {
            txConnectListenr.onConnecting();
        }
    }

    private void onConnectSuccess() {
        if (txConnectListenr != null) {
            txConnectListenr.onConnectSuccess();
        }
    }

    private void onConnectFail(String msg) {
        if (txConnectListenr != null) {
            txConnectListenr.onConnectFail(msg);
        }
    }

    public void pushSPSPPS(byte[] sps, byte[] pps) {
        if (sps != null && pps != null) {
            pushSPSPPS(sps, sps.length, pps, pps.length);
        }
    }

    public void pushVideoData(byte[] data, boolean keyframe) {
        if (data != null) {
            pushVideoData(data, data.length, keyframe);
        }
    }

    public void pushAudioData(byte[] data) {
        if (data != null) {
            pushAudioData(data, data.length);
        }
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

    private native void pushSPSPPS(byte[] sps, int sps_len, byte[] pps, int pps_len);

    private native void pushVideoData(byte[] data, int data_len, boolean keyframe);

    private native void pushAudioData(byte[] data, int data_len);

    public native void stopPush();
}
