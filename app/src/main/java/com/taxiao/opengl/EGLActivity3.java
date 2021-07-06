package com.taxiao.opengl;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.view.OnRenderCreateListener;
import com.taxiao.opengl.view.TXMutiGLSurfaceImageView;
import com.taxiao.opengl.view.TXMutiGLSurfaceImageView2;
import com.taxiao.opengl.view.TXMutiImageRender;
import com.taxiao.opengl.view.TXMutiImageRender2;


/**
 * 多 Surface 渲染多个纹理
 * Created by hanqq on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class EGLActivity3 extends AppCompatActivity {

    private LinearLayout ll_sur;
    private TXMutiGLSurfaceImageView2 camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl3);
        camera = findViewById(R.id.camera);
        ll_sur = findViewById(R.id.ll_sur);

        // 多 surface 渲染多个纹理
        camera.setOnCreate(new OnRenderCreateListener() {
            @Override
            public void onCreate(int textid) {
                createLayout();
            }
        });
    }

    private void createLayout() {
        // 多 Surface 渲染多个纹理
        TXMutiImageRender2 txEglRender = camera.getTXEglRender();
        if (txEglRender != null) {
            LogUtils.d("TXMutiGLSurfaceImageView txEglRender");
            txEglRender.setOnRenderCreateListener(new OnRenderCreateListener() {
                @Override
                public void onCreate(final int textid) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LogUtils.d("TXMutiGLSurfaceImageView", "回调");
                            // 返回纹理
                            if (ll_sur.getChildCount() > 0) {
                                ll_sur.removeAllViews();
                            }
                            for (int i = 0; i < 3; i++) {
                                final TXMutiGLSurfaceImageView2 txMutiGLSurfaceImageView = new TXMutiGLSurfaceImageView2(EGLActivity3.this);
                                final int id = i;
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                lp.width = 275;
                                lp.height = 442;
                                lp.rightMargin = 10;
                                txMutiGLSurfaceImageView.setLayoutParams(lp);
                                ll_sur.addView(txMutiGLSurfaceImageView);
                                txMutiGLSurfaceImageView.setOnCreate(new OnRenderCreateListener() {
                                    @Override
                                    public void onCreate(int textid) {
                                        txMutiGLSurfaceImageView.setTextureId(textid, id);
                                        txMutiGLSurfaceImageView.setSurfaceAndEglContext(null, camera.getEglContext());
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }
}
