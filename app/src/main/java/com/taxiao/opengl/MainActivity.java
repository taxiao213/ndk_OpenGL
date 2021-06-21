package com.taxiao.opengl;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.ByteBuffer;

/**
 *
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TXSurfaceView surface = findViewById(R.id.surface);
        TXSurfaceView2 surface2 = findViewById(R.id.surface2);
        Button button1 = findViewById(R.id.bt1);
        final JniSdkImpl jniSdk = new JniSdkImpl();
//        surface.setJniSdkImpl(jniSdk);
        surface2.setJniSdkImpl(jniSdk);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jniSdk.onSurfaceChangedFilter();
            }
        });
    }
}
