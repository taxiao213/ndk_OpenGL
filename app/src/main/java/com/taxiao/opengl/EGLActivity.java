package com.taxiao.opengl;

import android.content.Intent;
import android.opengl.GLES20;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.taxiao.opengl.util.egl.TXEglHelp;
import com.taxiao.opengl.view.MyEglSurfaceView;
import com.taxiao.opengl.view.TXGLSurfaceImageView;

import java.util.concurrent.Executors;

/**
 * Created by hanqq on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class EGLActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl);
        SurfaceView surface = findViewById(R.id.surface);
        MyEglSurfaceView surface1 = findViewById(R.id.surface1);
        TXGLSurfaceImageView surface2 = findViewById(R.id.surface2);
        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 多 Surface 渲染同一纹理
                startActivity(new Intent(EGLActivity.this, EGLActivity2.class));
            }
        });
        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 多 Surface 渲染多个纹理
                startActivity(new Intent(EGLActivity.this, EGLActivity3.class));
            }
        });
        findViewById(R.id.bt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 渲染camera
                startActivity(new Intent(EGLActivity.this, EGLCameraActivity.class));
            }
        });

        surface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull final SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(@NonNull final SurfaceHolder holder, int format, int width, int height) {
// TODO: 2021/7/5 暂时屏蔽

//                Executors.newSingleThreadExecutor().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        TXEglHelp txeglHelp = new TXEglHelp();
//                        txeglHelp.init(holder.getSurface(), null);
//                        while (true) {
//                            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//                            GLES20.glClearColor(1f, 0f, 0f, 0f);
//                            txeglHelp.swapBuffers();
//                        }
//                    }
//                });
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });
    }
}
