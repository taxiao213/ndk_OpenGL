package com.taxiao.opengl.yuv;

import android.content.Context;
import android.util.AttributeSet;

import com.taxiao.opengl.imagevideo.TXMutiImageRender;
import com.taxiao.opengl.util.Constant;
import com.taxiao.opengl.util.egl.TXEglSurfaceView;

/**
 * 渲染多图片 生成视频
 * ffmpeg decode
 * <p>
 * Created by hanqq on 2021/5/31
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXMutiGLSurfaceYUVVideo extends TXEglSurfaceView {
    private String TAG = TXMutiGLSurfaceYUVVideo.this.getClass().getSimpleName();
    private TXMutiYUVRender txEglRender;

    public TXMutiGLSurfaceYUVVideo(Context context) {
        this(context, null);
    }

    public TXMutiGLSurfaceYUVVideo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TXMutiGLSurfaceYUVVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setRenderMode(Constant.RENDERMODE_WHEN_DIRTY);
        txEglRender = new TXMutiYUVRender(getContext());
        setRender(txEglRender);
        requestRender();
    }

    public int getTextureID() {
        if (txEglRender != null) {
            return txEglRender.getTextureID();
        }
        return -1;
    }

    public void setFrameData(int w, int h, byte[] y, byte[] u, byte[] v) {
        if (txEglRender != null) {
            txEglRender.setFrameData(w, h, y, u, v);
            requestRender();
        }
    }
}
