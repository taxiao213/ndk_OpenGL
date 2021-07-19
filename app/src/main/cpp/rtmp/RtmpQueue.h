/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_RTMPQUEUE_H
#define OPENGL_RTMPQUEUE_H

#include "queue"
#include "../android_log.h"
#include "pthread.h"
extern "C" {
#include "rtmp.h"
};

class RtmpQueue {
public:
    std::queue<RTMPPacket *> queuePacket;
    pthread_cond_t pthreadCond;
    pthread_mutex_t pthreadMutex;

public :
    RtmpQueue();

    ~RtmpQueue();

    RTMPPacket *getRtmpPacket();

    int putRtmpPacket(RTMPPacket *rtmpPacket);

    void notifyQueue();

    void clearQueue();
};


#endif //OPENGL_RTMPQUEUE_H
