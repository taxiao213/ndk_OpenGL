package com.taxiao.opengl.rtmp;

/**
 * Created by hanqq on 2021/7/20
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public interface TXConnectListenr {
    void onConnecting();

    void onConnectSuccess();

    void onConnectFail(String msg);
}
