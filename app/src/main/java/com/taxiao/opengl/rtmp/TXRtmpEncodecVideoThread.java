package com.taxiao.opengl.rtmp;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import com.taxiao.opengl.encodec.TXBaseMediaCodecEncoder;
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
public class TXRtmpEncodecVideoThread extends Thread {
    private static String TAG = TXRtmpEncodecVideoThread.class.getName();

    private WeakReference<TXRtmpBaseMediaCodecEncoder> mWeakReference;
    private MediaCodec mEncoder;
    private MediaFormat mMediaFormat;
    private MediaCodec.BufferInfo mBufferInfo;
    private MediaMuxer mMediaMuxer;
    private long mPts;// 帧数
    public boolean mIsExit;// 退出
    private byte[] sps;
    private byte[] pps;

    public TXRtmpEncodecVideoThread(WeakReference<TXRtmpBaseMediaCodecEncoder> weakReference) {
        mWeakReference = weakReference;
    }

    @Override
    public void run() {
        super.run();
        mEncoder = mWeakReference.get().mVideoEncoder;
        mMediaFormat = mWeakReference.get().mVideoMediaFormat;
        mBufferInfo = mWeakReference.get().mVideoBufferInfo;
        mPts = 0;
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
                if (mWeakReference.get().mTXEncodecAudioThread.mAudioTrackIndex != -1) {
                    mMediaMuxer.start();
                    mWeakReference.get().mEncodecStart = true;
                }
                ByteBuffer spsd = mEncoder.getOutputFormat().getByteBuffer("csd-0");
                sps = new byte[spsd.remaining()];
                spsd.get(sps, 0, sps.length);

                ByteBuffer ppsd = mEncoder.getOutputFormat().getByteBuffer("csd-1");
                pps = new byte[ppsd.remaining()];
                ppsd.get(pps, 0, pps.length);
                LogUtils.d(TAG, "sps:" + byteToHex(sps));
                LogUtils.d(TAG, "pps:" + byteToHex(pps));

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
                        LogUtils.d(TAG, "data:" + byteToHex(data));
                        // 将数据发送
                        boolean isKeyFrame = false;
                        if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                            // 关键帧的时候发送 sps pps
                            isKeyFrame = true;
                            mWeakReference.get().mOnMediaInfoListener.onSPSPPSInfo(sps, pps);
                        }

                        if (mWeakReference.get().mOnMediaInfoListener != null) {
                            mWeakReference.get().mOnMediaInfoListener.onMediaTime((int) (mBufferInfo.presentationTimeUs / 1000000));
                            // 发送数据
                            mWeakReference.get().mOnMediaInfoListener.onVideoInfo(data, isKeyFrame);
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
            mWeakReference.get().mVideoExit = true;
            if (mWeakReference.get().mAudioExit) {
                if (mMediaMuxer != null) {
                    mMediaMuxer.stop();
                    mMediaMuxer.release();
                    mMediaMuxer = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG, "release");
    }

    public static String byteToHex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i]);
            if (hex.length() == 1) {
                stringBuffer.append("0" + hex);
            } else {
                stringBuffer.append(hex);
            }
            if (i > 20) {
                break;
            }
        }
        return stringBuffer.toString();
    }

}
