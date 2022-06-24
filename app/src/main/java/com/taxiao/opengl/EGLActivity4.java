package com.taxiao.opengl;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.taxiao.opengl.view.TXMutiGLSurfaceImageView3;


/**
 * freetype 字体显示
 * Created by hanqq on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class EGLActivity4 extends AppCompatActivity {

    private TXMutiGLSurfaceImageView3 camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl8);
        camera = findViewById(R.id.surface);

    }

}
