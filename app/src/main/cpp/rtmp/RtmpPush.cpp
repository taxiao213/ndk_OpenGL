/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/

#include "RtmpPush.h"

RtmpPush::RtmpPush(const char *urlPath) {
    this->url = static_cast<char *>(malloc(512));
    strcpy(this->url, urlPath);
    this->rtmpQueue = new RtmpQueue();
}

RtmpPush::~RtmpPush() {
    if (rtmpQueue != NULL) {
        rtmpQueue->notifyQueue();
        rtmpQueue->clearQueue();
    }
    free(this->url);
}

void *callBackPush(void *data) {
    RtmpPush *rtmpPush = (RtmpPush *) data;
    // 分配空间
    rtmpPush->rtmp = RTMP_Alloc();
    // 初始化
    RTMP_Init(rtmpPush->rtmp);
    rtmpPush->rtmp->Link.timeout = 10;
    rtmpPush->rtmp->Link.lFlags |= RTMP_LF_LIVE;
    // 设置推流URL
    RTMP_SetupURL(rtmpPush->rtmp, rtmpPush->url);
    // 设置可写状态
    RTMP_EnableWrite(rtmpPush->rtmp);
    // 链接服务器
    if (!RTMP_Connect(rtmpPush->rtmp, NULL)) {
        SDK_LOG_D("can not connect the url");
        goto end;
    }
    if (!RTMP_ConnectStream(rtmpPush->rtmp, 0)) {
        SDK_LOG_D("can not connect the stream of service");
        goto end;
    }
    SDK_LOG_D("链接成功， 开始推流");
    end:
    RTMP_Close(rtmpPush->rtmp);
    RTMP_Free(rtmpPush->rtmp);
    rtmpPush->rtmp = NULL;
    pthread_exit(&rtmpPush->pushThread);
}

void RtmpPush::create() {
    pthread_create(&pushThread, NULL, callBackPush, this);
}

void RtmpPush::destroy() {

}
