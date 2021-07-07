package com.taxiao.opengl;

import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.taxiao.opengl.util.Camera1Utils;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.PermissionsUtils;
import com.taxiao.opengl.view.camera.OnRenderCameraListener;
import com.taxiao.opengl.view.camera.TXMutiGLSurfaceCamera;
import com.taxiao.opengl.view.camera.TXMutiCameraRender;


/**
 * 渲染camera
 * Created by hanqq on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class EGLCameraActivity extends AppCompatActivity {
    private String TAG = EGLCameraActivity.this.getClass().getSimpleName();
    private TXMutiGLSurfaceCamera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl4);
        camera = findViewById(R.id.camera);

        // 渲染camera
        camera.setOnCreate(new OnRenderCameraListener() {
            @Override
            public void onCreate(int textid) {

            }

            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {

            }

            @Override
            public void onCreateSurfaceTexture(final SurfaceTexture surfaceTexture) {
                if (surfaceTexture != null) {
                    LogUtils.d(TAG, "onFrameAvailable Timestamp " + surfaceTexture.getTimestamp() + " cameraSurfaceTexture: " + surfaceTexture.hashCode());
                    PermissionsUtils.getInstance().requestCameraPermissions(EGLCameraActivity.this);
                    Camera1Utils.getInstance().initSurfaceTexture(EGLCameraActivity.this, surfaceTexture, false, new Point(1920, 1080));
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (camera != null) {
            camera.previewAngle(this);
        }
    }
}
