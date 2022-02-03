package com.taxiao.opengl.egl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.taxiao.opengl.util.LogUtils;

/**
 * Created by hanqq on 2021/12/22
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class EGLProjectActivity extends Activity {
    public Context mContext;
    public boolean renderSet = false;
    private GLSurfaceView glSurfaceView;
    private IBaseRender renderer;
    private String TAG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int action = intent.getIntExtra("action", 1);
        mContext = this;
        glSurfaceView = new GLSurfaceView(mContext);
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {
                    final float normalizedX = (event.getX() / (float) v.getWidth()) * 2 - 1;
                    final float normalizedY = -((event.getY() / (float) v.getHeight()) * 2 - 1);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                renderer.handleTouchPress(normalizedX, normalizedY);
                            }
                        });
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                renderer.handleTouchDrag(normalizedX, normalizedY);
                            }
                        });
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        setContentView(glSurfaceView);
        boolean enableEs2 = EGLUtils.getInstance(mContext).isEnableEs2();
        if (enableEs2) {
            LogUtils.d(TAG, " render ");
            switch (action) {
                case 1:
                    renderer = new FirstOpenGLRender(mContext);
                    break;
                case 2:
                    renderer = new SecondOpenGLRender(mContext);
                    break;
                case 3:
                    renderer = new ThirdOpenGLRender(mContext);
                    break;
                case 4:
                    renderer = new FourthOpenGLRender(mContext);
                    break;
                case 5:
                    renderer = new FiftyOpenGLRender(mContext);
                    break;
                case 6:
                    renderer = new SixthdOpenGLRender(mContext);
                    break;
                case 7:
                    renderer = new SeventhOpenGLRender(mContext);
                    break;
                case 8:
                    renderer = new ImageOpenGLRender(mContext);
                    break;
                case 9:
                    renderer = new ImageOpenGLRender2(mContext);
                    break;
                case 10:
                    renderer = new ImageOpenGLRender3(mContext);
                    break;
                default:
                    renderer = new FirstOpenGLRender(mContext);
                    break;
            }
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(renderer);
            // 请求时才渲染
//            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            // 不间断渲染
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            renderSet = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (renderSet) {
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (renderSet) {
            glSurfaceView.onPause();
        }
    }
}
