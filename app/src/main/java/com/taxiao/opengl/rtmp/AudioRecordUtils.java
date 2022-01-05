package com.taxiao.opengl.rtmp;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;

import com.taxiao.opengl.util.LogUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 录音工具类
 * Created by hanqq on 2020/7/20
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class AudioRecordUtils {
    private static final String TAG = AudioRecordUtils.class.getSimpleName();

    private static volatile AudioRecordUtils mHexAudioRecordUtils;
    private static final int DEFAULT_RECORDER_AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int DEFAULT_RECORDER_SAMPLERATE = 44100;
    private static final int DEFAULT_RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int DEFAULT_RECORDER_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord audioRecord = null;
    private AudioRunnable audioRunnable;
    private HandlerThread handlerThread;
    private boolean start = false;
    private FileOutputStream outputStream;

    private AudioRecordUtils() {
        try {
            outputStream = new FileOutputStream("/sdcard/cc.pcm");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static AudioRecordUtils getInstance() {
        if (mHexAudioRecordUtils == null) {
            synchronized (AudioRecordUtils.class) {
                if (mHexAudioRecordUtils == null) {
                    mHexAudioRecordUtils = new AudioRecordUtils();
                }
            }
        }
        return mHexAudioRecordUtils;
    }

    /**
     * 开始录音
     *
     * @param audioRecordInterface IAudioRecordInterface
     */
    public void startAudioRecord(IAudioRecordInterface audioRecordInterface) {
        handlerThread = new HandlerThread("audio_record");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        audioRunnable = new AudioRunnable(audioRecordInterface);
        handler.post(audioRunnable);
    }


    public class AudioRunnable implements Runnable {
        private IAudioRecordInterface mAudioRecordInterface;

        public AudioRunnable(IAudioRecordInterface audioRecordInterface) {
            mAudioRecordInterface = audioRecordInterface;
        }

        @Override
        public void run() {
            start = true;
            // 获取每一帧的字节流大小
            int minBufferSize = AudioRecord.getMinBufferSize(
                    DEFAULT_RECORDER_SAMPLERATE,
                    DEFAULT_RECORDER_CHANNELS,
                    DEFAULT_RECORDER_AUDIO_FORMAT);
            // 设置缓存的字节流大小
            byte[] cacheBuffer = new byte[minBufferSize];
            // 设置缓存字节流
            audioRecord = new AudioRecord(
                    DEFAULT_RECORDER_AUDIO_SOURCE,
                    DEFAULT_RECORDER_SAMPLERATE,
                    DEFAULT_RECORDER_CHANNELS,
                    DEFAULT_RECORDER_AUDIO_FORMAT,
                    minBufferSize);
            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                audioRecord.startRecording();
            }

            while (start) {
                int size = audioRecord.read(cacheBuffer, 0, minBufferSize);
                if (size > 0) {
                    if (mAudioRecordInterface != null) {
                        mAudioRecordInterface.putAudioData(cacheBuffer, size);
                    }
                    try {
                        LogUtils.d(TAG, "putaudio data");
                        outputStream.write(cacheBuffer, 0, size);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 释放资源
     */
    public void releaseAudioRecord() {
        if (audioRecord != null) {
            try {
                audioRecord.release();
                if (handlerThread != null) {
                    handlerThread.getLooper().quit();
                    handlerThread.quitSafely();
                    handlerThread = null;
                }
                if (audioRunnable != null) {
                    audioRunnable = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRecord() {
        start = false;
        releaseAudioRecord();
    }

    public boolean isStart() {
        return start;
    }

    public interface IAudioRecordInterface {

        void putAudioData(byte[] data, int readSize);

    }
}
