package com.taxiao.opengl;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

/**
 * Created by hanqq on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TXSurfaceView surface = findViewById(R.id.surface);
        TXSurfaceView2 surface2 = findViewById(R.id.surface2);
        TXSurfaceView3 surface3 = findViewById(R.id.surface3);
        Button button1 = findViewById(R.id.bt1);
        final JniSdkImpl jniSdk = new JniSdkImpl();
//        surface.setJniSdkImpl(jniSdk);
//        surface2.setJniSdkImpl(jniSdk);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jniSdk.onSurfaceChangedFilter();
            }
        });

        surface3.setJniSdkImpl(jniSdk, new TXSurfaceView3.ISurfaceInterface() {
            @Override
            public void init() {
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean isExit = false;
                        int width = 544;
                        int height = 720;
                        // yuv 数据渲染
                        String filePath = "/storage/9016-4EF8/ble2.yuv";
                        try {
                            InputStream inputStream = new FileInputStream(filePath);
                            byte[] yuv_y = new byte[width * height];
                            byte[] yuv_u = new byte[width * height / 4];
                            byte[] yuv_v = new byte[width * height / 4];
                            while (true) {
                                if (isExit) {
                                    break;
                                }
                                int size_y = inputStream.read(yuv_y);
                                int size_u = inputStream.read(yuv_u);
                                int size_v = inputStream.read(yuv_v);
                                if (size_y > 0 && size_u > 0 && size_v > 0) {
                                    Log.d("MainActivity", "setYUVData");
                                    jniSdk.setYUVData(yuv_y, yuv_u, yuv_v, width, height);
                                    Thread.sleep(30);
                                } else {
                                    isExit = true;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
