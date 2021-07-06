package com.taxiao.opengl.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

/**
 * 动态申请权限
 * Created by hanqq on 2021/7/4
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class PermissionsUtils {
    public static final String TAG = PermissionsUtils.class.getSimpleName();
    private static volatile PermissionsUtils mPermissionsUtils;
    private static int REQUEST_CODE = 0X101;
    private String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private PermissionsUtils() {
    }

    public static PermissionsUtils getInstance() {
        if (mPermissionsUtils == null) {
            synchronized (PermissionsUtils.class) {
                if (mPermissionsUtils == null) {
                    mPermissionsUtils = new PermissionsUtils();
                }
            }
        }
        return mPermissionsUtils;
    }

    public void requestCameraPermissions(Activity context) {
        if (context == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : CAMERA_PERMISSION) {
                if (!TextUtils.isEmpty(permission)) {
                    if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                        context.requestPermissions(CAMERA_PERMISSION, REQUEST_CODE);
                    }
                }
            }
        }
    }
}
