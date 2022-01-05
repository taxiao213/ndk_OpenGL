package com.taxiao.opengl.egl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Build;

/**
 * Created by hanqq on 2021/12/22
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class EGLUtils {
    private static EGLUtils eglUtils;
    private Context context;

    public EGLUtils(Context context) {
        this.context = context;
    }

    public static EGLUtils getInstance(Context context) {
        if (eglUtils == null) {
            synchronized (EGLUtils.class) {
                if (eglUtils == null) {
                    eglUtils = new EGLUtils(context);
                }
            }
        }
        return eglUtils;
    }

    /**
     * 是否支持 OpenGL ES 2.0
     * 检测当前设备是不是模拟器，如果是，假定支持
     * @return true 支持
     */
    public boolean isEnableEs2() {
        boolean isEnable = false;
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
                if (configurationInfo != null) {
                    isEnable = configurationInfo.reqGlEsVersion >= 0x2000 ||
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                                    && Build.FINGERPRINT.startsWith("generic")
                                    || Build.FINGERPRINT.startsWith("unknown")
                                    || Build.MODEL.contains("google_sdk")
                                    || Build.MODEL.contains("Emulator")
                                    || Build.MODEL.contains("Android SDK built for x86"));
                }
            }
        }
        return isEnable;
    }
}
