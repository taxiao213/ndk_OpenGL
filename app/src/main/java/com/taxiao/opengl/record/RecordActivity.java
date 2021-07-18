package com.taxiao.opengl.record;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.taxiao.opengl.JniSdkImpl;
import com.taxiao.opengl.R;

import java.io.File;

/**
 * Created by hanqq on 2021/7/16
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class RecordActivity extends AppCompatActivity {

    private JniSdkImpl jniSdk;
    private File recordFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        jniSdk = new JniSdkImpl();
        recordFile = new File(getCacheDir(), "record.pcm");
        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开始录音
                jniSdk.startRecord(recordFile.getAbsolutePath());
            }
        });

        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 暂停录音
                jniSdk.pauseRecord();
            }
        });

        findViewById(R.id.bt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 恢复录音
                jniSdk.resumeRecord();
            }
        });

        findViewById(R.id.bt4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 停止录音
                jniSdk.stopRecord();
            }
        });
    }
}
