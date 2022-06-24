package com.taxiao.opengl;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.view.OnRenderCreateListener;
import com.taxiao.opengl.view.TXMutiGLSurfaceImageView2;
import com.taxiao.opengl.view.TXMutiImageRender2;


/**
 * freetype 字体显示
 * Created by hanqq on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class EGLFreeTypeActivity extends AppCompatActivity {

    private SurfaceView camera;
    private JniSdkImpl jniSdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freetype);
        camera = findViewById(R.id.surface);
        jniSdk = new JniSdkImpl();

        jniSdk.initAsserts(getApplicationContext().getAssets());

        camera.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                jniSdk.freeSurfaceCreated(holder.getSurface());
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                jniSdk.freeSurfaceChanged(width, height);
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });
    }

}
