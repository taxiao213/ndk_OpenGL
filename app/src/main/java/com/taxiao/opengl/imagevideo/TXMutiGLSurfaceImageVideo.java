package com.taxiao.opengl.imagevideo;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.WindowManager;

import com.taxiao.opengl.util.Camera1Utils;
import com.taxiao.opengl.util.Constant;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.egl.TXEglSurfaceView;
import com.taxiao.opengl.view.camera.OnRenderCameraListener;
import com.taxiao.opengl.view.camera.TXMutiCameraRender;

/**
 * 渲染多图片 生成视频
 * ffmpeg decode
 * <p>
 * Created by hanqq on 2021/5/31
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXMutiGLSurfaceImageVideo extends TXEglSurfaceView {
    private String TAG = TXMutiGLSurfaceImageVideo.this.getClass().getSimpleName();
    private TXMutiImageRender txEglRender;

    public TXMutiGLSurfaceImageVideo(Context context) {
        this(context, null);
    }

    public TXMutiGLSurfaceImageVideo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TXMutiGLSurfaceImageVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setRenderMode(Constant.RENDERMODE_WHEN_DIRTY);
        txEglRender = new TXMutiImageRender(getContext());
        setRender(txEglRender);
        requestRender();
    }

    public int getTextureID() {
        if (txEglRender != null) {
            return txEglRender.getTextureID();
        }
        return -1;
    }

    public void setCurrentImg(int imgsr) {
        if (txEglRender != null) {
            txEglRender.setCurrentImgSrc(imgsr);
            requestRender();
        }
    }
}
