package com.taxiao.opengl.util.egl;

import android.opengl.EGL14;
import android.util.Log;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;


/**
 * Egl 环境
 * Created by hanqq on 2021/6/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXEglHelp {
    private static String TAG = TXEglHelp.class.getName();
    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLContext mEglContext;
    private EGLSurface mEglSurface;

    public void init(Surface surface, EGLContext eglContext) {
        Log.d(TAG, "createEngine");
        // 1.Get an EGL instance
        mEgl = (EGL10) EGLContext.getEGL();

        // 2.Get to the default display.
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        // 3.We can now initialize EGL for that display
        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed");
        }

        // 4.创建属性
        int[] attrbutes = new int[]{
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 8,
                EGL10.EGL_STENCIL_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL10.EGL_NONE
        };

        // 5.获取 config
        int[] num_config = new int[1];
        if (!mEgl.eglChooseConfig(mEglDisplay, attrbutes, null, 0,
                num_config)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }

        int numConfigs = num_config[0];

        if (numConfigs <= 0) {
            throw new IllegalArgumentException(
                    "No configs match configSpec");
        }

        EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!mEgl.eglChooseConfig(mEglDisplay, attrbutes, configs, numConfigs,
                num_config)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }

        // 6.获取 EGLContext
        int[] attrib_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION,
                2,
                EGL10.EGL_NONE
        };

        if (eglContext != null) {
            Log.d(TAG, "eglContext != null");
            mEglContext = mEgl.eglCreateContext(mEglDisplay, configs[0], eglContext, attrib_list);
        } else {
            Log.d(TAG, "eglContext == null");
            mEglContext = mEgl.eglCreateContext(mEglDisplay, configs[0], EGL10.EGL_NO_CONTEXT, attrib_list);
        }

        // 7.CreateWindowSurface
        mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, configs[0], surface, null);

        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw new RuntimeException("eglMakeCurrent fail");
        }
    }

    public void swapBuffers() {
        if (mEglDisplay == null || mEglSurface == null || mEglDisplay == EGL10.EGL_NO_DISPLAY ||
                mEglSurface == EGL10.EGL_NO_SURFACE) {
            Log.d(TAG, "mEGLDisplay 为 null , mEGLSurface  为 null ");
            return;
        }
        // 8. 初始化 eglSwapBuffers 刷新数据 显示渲染场景
        if (!mEgl.eglSwapBuffers(mEglDisplay, mEglSurface)) {
            Log.d(TAG, "初始化失败 eglSwapBuffers");
            return;
        }
        Log.d(TAG, "success eglSwapBuffers");
    }

    public EGLContext getmEglContext() {
        return mEglContext;
    }

    public void destoryEgl() {
        if (mEgl != null) {
            mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_CONTEXT);

            mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
            mEglSurface = null;

            mEgl.eglDestroyContext(mEglDisplay, mEglContext);
            mEglContext = null;

            mEgl.eglTerminate(mEglDisplay);
            mEglDisplay = null;
            mEgl = null;
        }
    }
}
