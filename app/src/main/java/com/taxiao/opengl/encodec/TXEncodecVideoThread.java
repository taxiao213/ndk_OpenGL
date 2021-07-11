package com.taxiao.opengl.encodec;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import com.taxiao.opengl.util.LogUtils;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * mediacodec video 编码
 * Created by hanqq on 2021/7/10
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXEncodecVideoThread extends Thread {
    private static String TAG = TXEncodecVideoThread.class.getName();

    private WeakReference<TXBaseMediaCodecEncoder> mWeakReference;
    private MediaCodec mEncoder;
    private MediaFormat mMediaFormat;
    private MediaCodec.BufferInfo mBufferInfo;
    private MediaMuxer mMediaMuxer;
    public int mVideoTrackIndex;
    private long mPts;// 帧数
    public boolean mIsExit;// 退出

    public TXEncodecVideoThread(WeakReference<TXBaseMediaCodecEncoder> weakReference) {
        mWeakReference = weakReference;
    }

    @Override
    public void run() {
        super.run();
        mEncoder = mWeakReference.get().mVideoEncoder;
        mMediaFormat = mWeakReference.get().mVideoMediaFormat;
        mBufferInfo = mWeakReference.get().mVideoBufferInfo;
        mMediaMuxer = mWeakReference.get().mMediaMuxer;
        mPts = 0;
        mVideoTrackIndex = -1;
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
            int dequeueOutputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
            if (dequeueOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // 添加视频轨道
                mVideoTrackIndex = mMediaMuxer.addTrack(mEncoder.getOutputFormat());
                if (mWeakReference.get().mTXEncodecAudioThread.mAudioTrackIndex != -1) {
                    mMediaMuxer.start();
                    mWeakReference.get().mEncodecStart = true;
                }
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
                        mMediaMuxer.writeSampleData(mVideoTrackIndex, outputBuffer, mBufferInfo);
                        if (mWeakReference.get().mOnMediaInfoListener != null) {
                            mWeakReference.get().mOnMediaInfoListener.onMediaTime((int) (mBufferInfo.presentationTimeUs / 1000000));
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
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        mWeakReference.get().mVideoExit = true;
        if (mWeakReference.get().mAudioExit) {
            if (mMediaMuxer != null) {
                mMediaMuxer.stop();
                mMediaMuxer.release();
                mMediaMuxer = null;
            }
        }
        LogUtils.d(TAG, "release");
    }
}
