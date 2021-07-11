package com.taxiao.opengl;

import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.taxiao.opengl.encodec.TXBaseMediaCodecEncoder;
import com.taxiao.opengl.encodec.TXMediaCodecEncoder;
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
import java.util.function.ToDoubleBiFunction;


/**
 * 渲染camera
 * Created by hanqq on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class EGLVideoCameraActivity extends AppCompatActivity {
    private String TAG = EGLVideoCameraActivity.this.getClass().getSimpleName();
    private TXMutiGLSurfaceCamera camera;
    private String path;
    private TXMediaCodecEncoder txMediaCodecEncoder;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl5);
        camera = findViewById(R.id.camera);
        Button bt_start = findViewById(R.id.bt_start);
        Button bt_pause = findViewById(R.id.bt_pause);
        Button bt_stop = findViewById(R.id.bt_stop);
        File cacheDir = getCacheDir();
        path = new File(cacheDir, "cache.mp4").getAbsolutePath();

        executorService = Executors.newSingleThreadExecutor();
        // 渲染camera
        camera.setOnCreate(new OnRenderCameraListener() {
            @Override
            public void onCreate(int textid) {

            }

            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {

            }

            @Override
            public void onCreateSurfaceTexture(final SurfaceTexture surfaceTexture) {
                if (surfaceTexture != null) {
                    LogUtils.d(TAG, "onFrameAvailable Timestamp " + surfaceTexture.getTimestamp() + " cameraSurfaceTexture: " + surfaceTexture.hashCode());
                    PermissionsUtils.getInstance().requestCameraPermissions(EGLVideoCameraActivity.this);
                    Camera1Utils.getInstance().initSurfaceTexture(EGLVideoCameraActivity.this, surfaceTexture, false, new Point(1920, 1080));
                }
            }
        });

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txMediaCodecEncoder = new TXMediaCodecEncoder(EGLVideoCameraActivity.this, camera.getTextureID());
                // 采样率和声道写死
                txMediaCodecEncoder.initEncoder(camera.getEglContext(), path, 1080, 1920, 44100, 2);
                txMediaCodecEncoder.setOnMediaInfoListener(new TXBaseMediaCodecEncoder.OnMediaInfoListener() {
                    @Override
                    public void onMediaTime(int times) {
//                        LogUtils.d(TAG, "time is : " + times);
                    }
                });
                txMediaCodecEncoder.startRecord();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // TODO: 2021/7/11 读取PCM数据 需要设置休眠
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // 读取PCM数据
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
                if (txMediaCodecEncoder != null) {
                    txMediaCodecEncoder.stopRecord();
                    LogUtils.d(TAG, "psth: " + path);
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (camera != null) {
            camera.previewAngle(this);
        }
    }
}
