package com.taxiao.opengl.rtmp;

import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.taxiao.opengl.EGLVideoCameraActivity;
import com.taxiao.opengl.JniSdkImpl;
import com.taxiao.opengl.R;
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

/**
 * Created by hanqq on 2021/7/16
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class RtmpActivity extends AppCompatActivity {

    private static final String TAG = RtmpActivity.class.getSimpleName();

    private JniSdkImpl jniSdk;
    private String url = "rtmp://172.21.0.158/myapp/mystream";
    private TextView textView;
    private TXMutiGLSurfaceCamera camera;
    private TXRtmpMediaCodecEncoder txMediaCodecEncoder;
    private ExecutorService executorService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtmp);
        textView = findViewById(R.id.tv_callback);
        camera = findViewById(R.id.camera);
        jniSdk = new JniSdkImpl();
        executorService = Executors.newSingleThreadExecutor();

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开始录音
                jniSdk.initRtmp(url);

                txMediaCodecEncoder = new TXRtmpMediaCodecEncoder(RtmpActivity.this, camera.getTextureID());
                // 采样率和声道写死  mydream.pcm 采样率是44100 声道2
                txMediaCodecEncoder.initEncoder(camera.getEglContext(),  360, 640, 44100, 2);
                txMediaCodecEncoder.setOnMediaInfoListener(new TXRtmpBaseMediaCodecEncoder.OnMediaInfoListener() {
                    @Override
                    public void onMediaTime(int times) {
//                        LogUtils.d(TAG, "time is : " + times);
                    }

                    @Override
                    public void onSPSPPSInfo(byte[] sps, byte[] pps) {
                        jniSdk.pushSPSPPS(sps, pps);
                    }

                    @Override
                    public void onVideoInfo(byte[] data, boolean keyframe) {
                        jniSdk.pushVideoData(data, keyframe);
                    }

                    @Override
                    public void onAudioInfo(byte[] data) {
                        jniSdk.pushAudioData(data);
                    }
                });
                txMediaCodecEncoder.startRecord();
            }
        });

        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 暂停录音

            }
        });

        findViewById(R.id.bt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 恢复录音

            }
        });

        findViewById(R.id.bt4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 停止录音
                stopRecord();
                jniSdk.stopPush();
            }
        });

        jniSdk.setTxConnectListenr(new TXConnectListenr() {
            @Override
            public void onConnecting() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d(TAG, "onConnecting");
                        textView.setText("onConnecting");
                    }
                });
            }

            @Override
            public void onConnectSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d(TAG, "onConnectSuccess");
                        textView.setText("onConnectSuccess");
                    }
                });
            }

            @Override
            public void onConnectFail(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d(TAG, "onConnectFail");
                        textView.setText("onConnectFail");
                    }
                });
            }
        });

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
                    PermissionsUtils.getInstance().requestCameraPermissions(RtmpActivity.this);
                    Camera1Utils.getInstance().initSurfaceTexture(RtmpActivity.this, surfaceTexture, false, new Point(1920, 1080));
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

    private void stopRecord() {
        if (txMediaCodecEncoder != null) {
            txMediaCodecEncoder.stopRecord();
        }
    }
}
