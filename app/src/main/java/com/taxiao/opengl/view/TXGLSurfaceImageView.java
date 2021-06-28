package com.taxiao.opengl.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.taxiao.opengl.util.egl.TXEglRender;
import com.taxiao.opengl.util.egl.TXEglSurfaceView;
import com.taxiao.opengl.util.egl.TXEglThread;

/**
 * 渲染图片
 * ffmpeg decode
 * <p>
 * Created by hanqq on 2021/5/31
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXGLSurfaceImageView extends TXEglSurfaceView {

    public TXGLSurfaceImageView(Context context) {
        this(context, null);
    }

    public TXGLSurfaceImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TXGLSurfaceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setRender(new TXImageRender1(context));
        setRenderMode(TXEglThread.RENDERMODE_WHEN_DIRTY);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
    }

}
