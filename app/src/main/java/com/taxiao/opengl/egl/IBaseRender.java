package com.taxiao.opengl.egl;

import android.opengl.GLSurfaceView;

/**
 * Created by hanqq on 2021/12/10
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public abstract class IBaseRender implements GLSurfaceView.Renderer {

    public abstract void handleTouchPress(float normalizedX, float normalizedY);

    public abstract void handleTouchDrag(float normalizedX, float normalizedY);

}
