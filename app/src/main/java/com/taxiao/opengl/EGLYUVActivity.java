package com.taxiao.opengl;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.taxiao.opengl.encodec.TXBaseMediaCodecEncoder;
import com.taxiao.opengl.encodec.TXMediaCodecEncoder;
import com.taxiao.opengl.imagevideo.TXMutiGLSurfaceImageVideo;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.yuv.TXMutiGLSurfaceYUVVideo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;


/**
 * 渲染YUV
 * Created by hanqq on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class EGLYUVActivity extends AppCompatActivity {
    private String TAG = EGLYUVActivity.this.getClass().getSimpleName();
    private TXMutiGLSurfaceYUVVideo image;
    private String path;
    private TXMediaCodecEncoder txMediaCodecEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl7);
        image = findViewById(R.id.image);
        Button bt_start = findViewById(R.id.bt_start);
        Button bt_pause = findViewById(R.id.bt_pause);
        Button bt_stop = findViewById(R.id.bt_stop);
        File cacheDir = getCacheDir();
        path = new File(cacheDir, "cache_yuv.mp4").getAbsolutePath();

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int w = 640;
                    int h = 360;
                    FileInputStream fis = new FileInputStream(new File(getCacheDir(),"sintel_640_360.yuv"));

                    byte[] y = new byte[w * h];
                    byte[] u = new byte[w * h / 4];
                    byte[] v = new byte[w * h / 4];

                    while (true) {
                        int ry = fis.read(y);
                        int ru = fis.read(u);
                        int rv = fis.read(v);
                        if (ry > 0 && ru > 0 && rv > 0) {
                            image.setFrameData(w, h, y, u, v);
                            Thread.sleep(40);
                        } else {
                            LogUtils.d(TAG, "完成");
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txMediaCodecEncoder = new TXMediaCodecEncoder(EGLYUVActivity.this, image.getTextureID());
                // 采样率和声道写死  mydream.pcm 采样率是44100 声道2
                txMediaCodecEncoder.initEncoder(image.getEglContext(), path, 1920, 1080, 44100, 2);
                txMediaCodecEncoder.setOnMediaInfoListener(new TXBaseMediaCodecEncoder.OnMediaInfoListener() {
                    @Override
                    public void onMediaTime(int times) {
                        LogUtils.d(TAG, "time is : " + times);
                    }
                });
                txMediaCodecEncoder.startRecord();
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // TODO: 2021/7/11 读取PCM数据 需要设置休眠
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // 读取PCM数据 mydream.pcm 采样率是44100 声道2
                            InputStream stream = getResources().getAssets().open("mydream.pcm");
                            byte[] bytes = new byte[4096];
                            int size;
                            int count = 0;
                            while ((size = stream.read(bytes)) != -1) {
                                count++;
                                try {
                                    Thread.sleep(1000 / 30);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                txMediaCodecEncoder.putPCMData(bytes, 4096);
                            }
                            LogUtils.d(TAG, "count : " + count);
//                            stopRecord();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
            }
        });
    }

    private void stopRecord() {
        if (txMediaCodecEncoder != null) {
            txMediaCodecEncoder.stopRecord();
            LogUtils.d(TAG, "psth: " + path);
        }
    }
}
