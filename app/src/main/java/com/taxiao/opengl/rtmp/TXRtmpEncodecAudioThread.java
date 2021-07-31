package com.taxiao.opengl.rtmp;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import com.taxiao.opengl.encodec.TXBaseMediaCodecEncoder;
import com.taxiao.opengl.util.LogUtils;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * mediacodec audio 编码
 * Created by hanqq on 2021/7/10
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXRtmpEncodecAudioThread extends Thread {
    private static String TAG = TXRtmpEncodecAudioThread.class.getName();

    private WeakReference<TXRtmpBaseMediaCodecEncoder> mWeakReference;
    private MediaCodec mEncoder;
    private MediaCodec.BufferInfo mBufferInfo;
    public int mAudioTrackIndex;
    private long mPts;// 帧数
    public boolean mIsExit;// 退出

    public TXRtmpEncodecAudioThread(WeakReference<TXRtmpBaseMediaCodecEncoder> weakReference) {
        mWeakReference = weakReference;
    }

    @Override
    public void run() {
        super.run();
        mEncoder = mWeakReference.get().mAudioEncoder;
        mBufferInfo = mWeakReference.get().mAudioBufferInfo;
        mPts = 0;
        mAudioTrackIndex = -1;
        mIsExit = false;
        mEncoder.start();
        while (true) {
            if (mIsExit) {
                release();
                break;
            }
            encoder();
        }
    }

    private void encoder() {
        if (mEncoder != null) {
            LogUtils.d(TAG, "audio encoder");
            int dequeueOutputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
            if (dequeueOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // 添加视频轨道
                mWeakReference.get().mEncodecStart = true;
            } else {
                while (dequeueOutputBufferIndex >= 0) {
                    if (mWeakReference.get().mEncodecStart) {
                        ByteBuffer outputBuffer = mEncoder.getOutputBuffers()[dequeueOutputBufferIndex];
                        outputBuffer.position(mBufferInfo.offset);
                        outputBuffer.limit(mBufferInfo.offset + mBufferInfo.size);
                        if (mPts == 0) {
                            mPts = mBufferInfo.presentationTimeUs;
                        }
                        mBufferInfo.presentationTimeUs = mBufferInfo.presentationTimeUs - mPts;

                        byte[] data = new byte[outputBuffer.remaining()];
                        outputBuffer.get(data, 0, data.length);
                        if (mWeakReference.get().mOnMediaInfoListener != null) {
                            mWeakReference.get().mOnMediaInfoListener.onAudioInfo(data);
                        }
                    }
                    mEncoder.releaseOutputBuffer(dequeueOutputBufferIndex, false);
                    dequeueOutputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
                }
            }
        }
    }

    public void exit() {
        mIsExit = true;
    }

    private void release() {
        try {
            if (mEncoder != null) {
                mEncoder.stop();
                mEncoder.release();
                mEncoder = null;
            }
            mWeakReference.get().mAudioExit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "release");
    }

    // 单位是微秒
    public long getAudioPts(int size, int sampleRate) {
        mPts += (long) (1.0 * size / (sampleRate * 2 * 2) * 1000000.0);
        return mPts;
    }
}
