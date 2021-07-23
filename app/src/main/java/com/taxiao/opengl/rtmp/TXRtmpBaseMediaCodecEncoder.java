package com.taxiao.opengl.rtmp;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.view.Surface;

import com.taxiao.opengl.encodec.TXEncodecAudioThread;
import com.taxiao.opengl.encodec.TXEncodecEglThread;
import com.taxiao.opengl.encodec.TXEncodecVideoThread;
import com.taxiao.opengl.util.Constant;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.egl.TXEglRender;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLContext;

/**
 * mediaCodec 编解码
 * Created by hanqq on 2021/7/10
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public abstract class TXRtmpBaseMediaCodecEncoder {

    private static final String TAG = TXRtmpBaseMediaCodecEncoder.class.getSimpleName();
    public Surface mSurface;
    public EGLContext mEglContext;
    public TXEglRender mTXEglRender;
    public MediaMuxer mMediaMuxer;
    public MediaCodec.BufferInfo mVideoBufferInfo;
    public MediaFormat mVideoMediaFormat;
    public MediaCodec mVideoEncoder;
    public MediaCodec.BufferInfo mAudioBufferInfo;
    public MediaFormat mAudioMediaFormat;
    public MediaCodec mAudioEncoder;
    public TXRtmpEncodecEglThread mTXEncodecEglThread;
    public TXRtmpEncodecVideoThread mTXEncodecVideoThread;
    public TXRtmpEncodecAudioThread mTXEncodecAudioThread;
    public OnMediaInfoListener mOnMediaInfoListener;
    public int mWidth;
    public int mHeight;
    public int mSampleRate;
    public int mRenderMode = Constant.RENDERMODE_CONTINUOUSLY;
    public boolean mAudioExit = false;
    public boolean mVideoExit = false;
    public boolean mEncodecStart = false;

    public void initEncoder(EGLContext eglContext, String filePath, int width, int height, int sampleRate, int channelCount) {
        this.mEglContext = eglContext;
        this.mWidth = width;
        this.mHeight = height;
        this.mSampleRate = sampleRate;
        try {
            mMediaMuxer = new MediaMuxer(filePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            initVideoMediaCodec(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
            initAudioMediaCodec(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
        } catch (IOException e) {
            e.printStackTrace();
            mMediaMuxer = null;
            mVideoBufferInfo = null;
            mVideoMediaFormat = null;
            mVideoEncoder = null;
            mAudioBufferInfo = null;
            mAudioMediaFormat = null;
            mAudioEncoder = null;
        }
    }

    // video 编码
    private void initVideoMediaCodec(String mimeType, int width, int height) throws IOException {
        mVideoBufferInfo = new MediaCodec.BufferInfo();
        mVideoMediaFormat = MediaFormat.createVideoFormat(mimeType, width, height);
        mVideoMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mVideoMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 4);
        mVideoMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        mVideoMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        mVideoEncoder = MediaCodec.createEncoderByType(mimeType);
        mVideoEncoder.configure(mVideoMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mVideoEncoder.createInputSurface();
    }

    // audio 编码
    private void initAudioMediaCodec(String mimeType, int sampleRate, int channelCount) throws IOException {
        mAudioBufferInfo = new MediaCodec.BufferInfo();
        mAudioMediaFormat = MediaFormat.createAudioFormat(mimeType, sampleRate, channelCount);
        mAudioMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
        mAudioMediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLD);
        mAudioMediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096);
        mAudioEncoder = MediaCodec.createEncoderByType(mimeType);
        mAudioEncoder.configure(mAudioMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    // 设置音频数据
    public void putPCMData(byte[] buffer, int size) {
        if (mTXEncodecAudioThread != null && !mTXEncodecAudioThread.mIsExit && mAudioEncoder != null && mEncodecStart && buffer != null && size > 0) {
            int inputBufferindex = mAudioEncoder.dequeueInputBuffer(0);
            if (inputBufferindex >= 0) {
                ByteBuffer byteBuffer = mAudioEncoder.getInputBuffers()[inputBufferindex];
                byteBuffer.clear();
                byteBuffer.put(buffer);
                long pts = mTXEncodecAudioThread.getAudioPts(size, mSampleRate);
                LogUtils.d(TAG, "pts :" + pts);
                mAudioEncoder.queueInputBuffer(inputBufferindex, 0, size, pts, 0);
            }
        }
    }

    public void startRecord() {
        if (mSurface != null && mEglContext != null) {
            mTXEncodecEglThread = new TXRtmpEncodecEglThread(new WeakReference<TXRtmpBaseMediaCodecEncoder>(this));
            mTXEncodecVideoThread = new TXRtmpEncodecVideoThread(new WeakReference<TXRtmpBaseMediaCodecEncoder>(this));
            mTXEncodecAudioThread = new TXRtmpEncodecAudioThread(new WeakReference<TXRtmpBaseMediaCodecEncoder>(this));
            mTXEncodecEglThread.mIsCreate = true;
            mTXEncodecEglThread.mIsChange = true;
            mTXEncodecEglThread.start();
            mTXEncodecVideoThread.start();
            mTXEncodecAudioThread.start();
        }
    }

    public void stopRecord() {
        if (mTXEncodecEglThread != null && mTXEncodecVideoThread != null) {
            mTXEncodecEglThread.onDestory();
            mTXEncodecVideoThread.exit();
            mTXEncodecAudioThread.exit();
            mTXEncodecEglThread = null;
            mTXEncodecVideoThread = null;
            mTXEncodecAudioThread = null;
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

        void onSPSPPSInfo(byte[] sps, byte[] pps);

        void onVideoInfo(byte[] data, boolean keyframe);

        void onAudioInfo(byte[] data);
    }

}
