package com.taxiao.opengl.view;

import android.content.Context;
import android.util.AttributeSet;

import com.taxiao.opengl.util.Constant;
import com.taxiao.opengl.util.egl.TXEglRender;
import com.taxiao.opengl.util.egl.TXEglSurfaceView;
import com.taxiao.opengl.util.egl.TXEglThread;

/**
 * Created by hanqq on 2021/6/26
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class MyEglSurfaceView extends TXEglSurfaceView {

    public MyEglSurfaceView(Context context) {
        this(context, null);
    }

    public MyEglSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyEglSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setRender(new TXEglRender());
        setRenderMode(Constant.RENDERMODE_WHEN_DIRTY);
    }
}
