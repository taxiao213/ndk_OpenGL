package com.taxiao.opengl;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.taxiao.opengl.util.egl.TXEglHelp;

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
        surface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull final SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(@NonNull final SurfaceHolder holder, int format, int width, int height) {
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        TXEglHelp txeglHelp = new TXEglHelp();
                        txeglHelp.init(holder.getSurface(),null);
                        while (true){
                            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                            GLES20.glClearColor(1f,0f,0f,0f);
                            txeglHelp.swapBuffers();

                        }
                    }
                });
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });
    }
}
