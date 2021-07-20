/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_RTMPPUSH_H
#define OPENGL_RTMPPUSH_H

#include "../android_log.h"
#include "RtmpQueue.h"
#include "pthread.h"
#include "../callback/TXCallBack.h"

extern "C" {
#include "rtmp.h"
};

class RtmpPush {

public:
    RTMP *rtmp = NULL;
    char *url = NULL;
    RtmpQueue *rtmpQueue = NULL;
    pthread_t pushThread;
    TXCallBack *txCallBack;
public:
    RtmpPush(const char *urlPath, TXCallBack *txCallBack);

    ~RtmpPush();

    void create();

    void destroy();
};


#endif //OPENGL_RTMPPUSH_H
