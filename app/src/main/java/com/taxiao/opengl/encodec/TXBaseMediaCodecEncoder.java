package com.taxiao.opengl.encodec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.view.Surface;

import com.taxiao.opengl.util.Constant;
import com.taxiao.opengl.util.egl.TXEglRender;

import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLContext;

/**
 * mediaCodec 编解码
 * Created by hanqq on 2021/7/10
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public abstract class TXBaseMediaCodecEncoder {

    public int mWidth;
    public int mHeight;
    public Surface mSurface;
    public EGLContext mEglContext;
    public MediaCodec.BufferInfo mBufferInfo;
    public MediaMuxer mMediaMuxer;
    public MediaFormat mMediaFormat;
    public MediaCodec mEncoder;
    public TXEglRender mTXEglRender;
    public int mRenderMode = Constant.RENDERMODE_CONTINUOUSLY;
    public OnMediaInfoListener mOnMediaInfoListener;
    public TXEncodecEglThread mTXEncodecEglThread;
    public TXEncodecMediaThread mTXEncodecMediaThread;

    public void initEncoder(EGLContext eglContext, String filePath, String mimeType, int width, int height) {
        this.mEglContext = eglContext;
        this.mWidth = width;
        this.mHeight = height;
        initMediaCodec(filePath, mimeType, width, height);
    }

    private void initMediaCodec(String filePath, String mimeType, int width, int height) {
        try {
            mMediaMuxer = new MediaMuxer(filePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mBufferInfo = new MediaCodec.BufferInfo();
            mMediaFormat = MediaFormat.createVideoFormat(mimeType, width, height);
            mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 4);
            mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            mEncoder = MediaCodec.createEncoderByType(mimeType);
            mEncoder.configure(mMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mSurface = mEncoder.createInputSurface();
        } catch (IOException e) {
            e.printStackTrace();
            mMediaMuxer = null;
            mBufferInfo = null;
            mMediaFormat = null;
            mEncoder = null;
        }
    }

    public void startRecord() {
        if (mSurface != null && mEglContext != null) {
            mTXEncodecEglThread = new TXEncodecEglThread(new WeakReference<TXBaseMediaCodecEncoder>(this));
            mTXEncodecMediaThread = new TXEncodecMediaThread(new WeakReference<TXBaseMediaCodecEncoder>(this));
            mTXEncodecEglThread.mIsCreate = true;
            mTXEncodecEglThread.mIsChange = true;
            mTXEncodecEglThread.start();
            mTXEncodecMediaThread.start();
        }
    }

    public void stopRecord() {
        if (mTXEncodecEglThread != null && mTXEncodecMediaThread != null) {
            mTXEncodecEglThread.onDestory();
            mTXEncodecMediaThread.exit();
            mTXEncodecEglThread = null;
            mTXEncodecMediaThread = null;
        }
    }

    public void setRender(TXEglRender eGLRender) {
        this.mTXEglRender = eGLRender;
    }

    public void setmRenderMode(int mRenderMode) {
        if (mTXEglRender == null) {
            throw new RuntimeException("must set render before");
        }
        this.mRenderMode = mRenderMode;
    }

    public void setOnMediaInfoListener(OnMediaInfoListener onMediaInfoListener) {
        this.mOnMediaInfoListener = onMediaInfoListener;
    }

    public interface OnMediaInfoListener {
        void onMediaTime(int times);
    }

}
