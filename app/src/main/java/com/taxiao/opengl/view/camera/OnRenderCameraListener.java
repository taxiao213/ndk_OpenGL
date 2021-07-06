package com.taxiao.opengl.view.camera;

import android.graphics.SurfaceTexture;

import com.taxiao.opengl.view.OnRenderCreateListener;

/**
 * camera 回调
 * Created by hanqq on 2021/7/3
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public interface OnRenderCameraListener extends OnRenderCreateListener {

    void onFrameAvailable(SurfaceTexture surfaceTexture);

    void onCreateSurfaceTexture(SurfaceTexture surfaceTexture);
}
