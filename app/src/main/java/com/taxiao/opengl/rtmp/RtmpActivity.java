package com.taxiao.opengl.rtmp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.taxiao.opengl.JniSdkImpl;
import com.taxiao.opengl.R;
import com.taxiao.opengl.util.LogUtils;

import java.io.File;

/**
 * Created by hanqq on 2021/7/16
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class RtmpActivity extends AppCompatActivity {

    private static final String TAG = RtmpActivity.class.getSimpleName();

    private JniSdkImpl jniSdk;
    private String url = "rtmp://172.21.0.158/myapp/mystream";
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtmp);
        textView = findViewById(R.id.tv_callback);

        jniSdk = new JniSdkImpl();

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开始录音
                jniSdk.initRtmp(url);
            }
        });

        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 暂停录音

            }
        });

        findViewById(R.id.bt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 恢复录音

            }
        });

        findViewById(R.id.bt4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 停止录音

            }
        });

        jniSdk.setTxConnectListenr(new TXConnectListenr() {
            @Override
            public void onConnecting() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d(TAG, "onConnecting");
                        textView.setText("onConnecting");
                    }
                });
            }

            @Override
            public void onConnectSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d(TAG, "onConnectSuccess");
                        textView.setText("onConnectSuccess");
                    }
                });
            }

            @Override
            public void onConnectFail(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d(TAG, "onConnectFail");
                        textView.setText("onConnectFail");
                    }
                });
            }
        });
    }
}
