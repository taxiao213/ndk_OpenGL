package com.taxiao.opengl;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
        JniSdkImpl jniSdk = new JniSdkImpl();
        surface.setJniSdkImpl(jniSdk);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        int byteCount = bitmap.getByteCount();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(byteCount);
        bitmap.copyPixelsToBuffer(byteBuffer);
        jniSdk.onDrawImage(bitmap.getWidth(), bitmap.getHeight(), byteCount, byteBuffer.array());
    }


}
