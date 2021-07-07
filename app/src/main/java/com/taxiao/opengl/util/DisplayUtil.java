package com.taxiao.opengl.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 获取屏幕的大小
 * Created by hanqq on 2021/7/7
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class DisplayUtil {
    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        return metric.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        return metric.heightPixels;
    }
}
