package com.taxiao.opengl.view;

import android.content.Context;
import android.util.AttributeSet;

import com.taxiao.opengl.util.Constant;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.egl.TXEglSurfaceView;

/**
 * 渲染图片 多Surface渲染多个纹理
 * ffmpeg decode
 * <p>
 * Created by hanqq on 2021/5/31
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXMutiGLSurfaceImageView3 extends TXEglSurfaceView {
    private TXMutiImageRender3 txEglRender;

    public TXMutiGLSurfaceImageView3(Context context) {
        this(context, null);
    }

    public TXMutiGLSurfaceImageView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TXMutiGLSurfaceImageView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TXMutiImageRender3 getTXEglRender() {
        return txEglRender;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        txEglRender = new TXMutiImageRender3(getContext(), width, height);
        setRender(txEglRender);
        setRenderMode(Constant.RENDERMODE_CONTINUOUSLY);
    }
}
