package com.taxiao.opengl.egl;

import com.taxiao.opengl.util.LogUtils;

/**
 * Created by hanqq on 2022/2/1
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public abstract class BaseRenderImp extends IBaseRender {
    private String TAG = this.getClass().getSimpleName();

    @Override
    public void handleTouchPress(float normalizedX, float normalizedY) {
        LogUtils.d(TAG, "handleTouchPress normalizedX: " + normalizedX + " normalizedY: " + normalizedY);
    }

    @Override
    public void handleTouchDrag(float normalizedX, float normalizedY) {
        LogUtils.d(TAG, "handleTouchDrag normalizedX: " + normalizedX + " normalizedY: " + normalizedY);
    }
}
