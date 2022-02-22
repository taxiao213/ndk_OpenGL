package com.taxiao.opengl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.taxiao.opengl.egl.EGLProjectActivity;

import java.io.File;
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
        Button button2 = findViewById(R.id.bt2);
        Button button3 = findViewById(R.id.bt3);
        Button button4 = findViewById(R.id.bt4);
        Button button5 = findViewById(R.id.bt5);
        Button button6 = findViewById(R.id.bt6);
        Button button7 = findViewById(R.id.bt7);
        Button button8 = findViewById(R.id.bt8);
        Button button9 = findViewById(R.id.bt9);
        Button button10 = findViewById(R.id.bt10);
        Button button11 = findViewById(R.id.bt11);
        Button button12 = findViewById(R.id.bt12);
        Button button13 = findViewById(R.id.bt13);
        Button button14 = findViewById(R.id.bt14);
        Button button15 = findViewById(R.id.bt15);
        final JniSdkImpl jniSdk = new JniSdkImpl();
//        surface.setJniSdkImpl(jniSdk);
//        surface2.setJniSdkImpl(jniSdk);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jniSdk.onSurfaceChangedFilter();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EGLActivity.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 棒球-三角形
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 1);
                startActivity(intent);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 棒球-三角形扇
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 2);
                startActivity(intent);
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 棒球-三角形扇-混色
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 3);
                startActivity(intent);
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 棒球-三角形扇-混色-正交投影
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 4);
                startActivity(intent);
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 棒球-三角形扇-混色-正交投影-三维
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 5);
                startActivity(intent);
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 棒球-三角形扇-混色-投影矩阵
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 6);
                startActivity(intent);
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 棒球-三角形扇-混色-投影矩阵-旋转
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 7);
                startActivity(intent);
            }
        });

        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 图片纹理
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 8);
                startActivity(intent);
            }
        });

        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 图片纹理2
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 9);
                startActivity(intent);
            }
        });

        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 图片纹理2，增加手势互动
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 10);
                startActivity(intent);
            }
        });

        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 图粒子系统
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 11);
                startActivity(intent);
            }
        });
        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 粒子系统，增加天空盒
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 12);
                startActivity(intent);
            }
        });
        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 粒子系统，增加天空盒，绘制高度图
                Intent intent = new Intent(MainActivity.this, EGLProjectActivity.class);
                intent.putExtra("action", 13);
                startActivity(intent);
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
                        File file = new File(filePath);
                        if (!file.exists()) return;
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
