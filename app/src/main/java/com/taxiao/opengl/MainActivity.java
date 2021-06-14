package com.taxiao.opengl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

/**
 *
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TXSurfaceView surface = findViewById(R.id.surface);
        JniSdkImpl jniSdk = new JniSdkImpl();
        surface.setJniSdkImpl(jniSdk);
    }


}
