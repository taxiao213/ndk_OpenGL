/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#include "RtmpQueue.h"

RtmpQueue::RtmpQueue() {
    pthread_cond_init(&pthreadCond, NULL);
    pthread_mutex_init(&pthreadMutex, NULL);
}

RtmpQueue::~RtmpQueue() {
    clearQueue();
    pthread_cond_destroy(&pthreadCond);
    pthread_mutex_destroy(&pthreadMutex);
}

RTMPPacket *RtmpQueue::getRtmpPacket() {
    pthread_mutex_lock(&pthreadMutex);
    RTMPPacket *p = NULL;
    if (!queuePacket.empty()) {
        p = queuePacket.front();
        queuePacket.pop();
    } else {
        pthread_cond_wait(&pthreadCond, &pthreadMutex);
    }
    pthread_mutex_unlock(&pthreadMutex);
    return p;
}

int RtmpQueue::putRtmpPacket(RTMPPacket *rtmpPacket) {
    pthread_mutex_lock(&pthreadMutex);
    queuePacket.push(rtmpPacket);
    pthread_cond_signal(&pthreadCond);
    pthread_mutex_unlock(&pthreadMutex);
    return 0;
}

void RtmpQueue::notifyQueue() {
    pthread_mutex_lock(&pthreadMutex);
    pthread_cond_signal(&pthreadCond);
    pthread_mutex_unlock(&pthreadMutex);
}

void RtmpQueue::clearQueue() {
    pthread_mutex_lock(&pthreadMutex);
    while (true) {
        if (queuePacket.empty()) {
            break;
        }
        RTMPPacket *pPacket = queuePacket.front();
        queuePacket.pop();
        RTMPPacket_Free(pPacket);
        pPacket = NULL;
    }
    pthread_mutex_unlock(&pthreadMutex);
}
