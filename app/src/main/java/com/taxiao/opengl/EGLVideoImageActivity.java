package com.taxiao.opengl;

import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.taxiao.opengl.encodec.TXBaseMediaCodecEncoder;
import com.taxiao.opengl.encodec.TXMediaCodecEncoder;
import com.taxiao.opengl.imagevideo.TXMutiGLSurfaceImageVideo;
import com.taxiao.opengl.util.Camera1Utils;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.PermissionsUtils;
import com.taxiao.opengl.view.camera.OnRenderCameraListener;
import com.taxiao.opengl.view.camera.TXMutiGLSurfaceCamera;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 图片合成视频
 * Created by hanqq on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class EGLVideoImageActivity extends AppCompatActivity {
    private String TAG = EGLVideoImageActivity.this.getClass().getSimpleName();
    private TXMutiGLSurfaceImageVideo image;
    private String path;
    private TXMediaCodecEncoder txMediaCodecEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl6);
        image = findViewById(R.id.image);
        Button bt_start = findViewById(R.id.bt_start);
        Button bt_pause = findViewById(R.id.bt_pause);
        Button bt_stop = findViewById(R.id.bt_stop);
        File cacheDir = getCacheDir();
        path = new File(cacheDir, "cache_image.mp4").getAbsolutePath();

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    try {
                        if (count % 3 == 0) {
                            image.setCurrentImg(R.mipmap.img_111);
                        } else if (count % 3 == 1) {
                            image.setCurrentImg(R.mipmap.img_222);
                        } else if (count % 3 == 2) {
                            image.setCurrentImg(R.mipmap.img_333);
                        } else {
                            image.setCurrentImg(R.mipmap.img_444);
                        }
                        Thread.sleep(1000/15);
                        count++;
                        LogUtils.d(TAG, "count : " + count);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txMediaCodecEncoder = new TXMediaCodecEncoder(EGLVideoImageActivity.this, image.getTextureID());
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
