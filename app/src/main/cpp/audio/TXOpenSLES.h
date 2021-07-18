/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_TXOPENSLES_H
#define OPENGL_TXOPENSLES_H

#include "../android_log.h"
#include <cstdio>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <assert.h>
#include "TXRecordBuffer.h"

class TXOpenSLES {

public:
    FILE *file = NULL;
    SLObjectItf slObjectItf;
    SLEngineItf engineEngine;
    SLObjectItf recorderObject = NULL;
    SLAndroidSimpleBufferQueueItf recorderBufferQueue;
    SLRecordItf recorderRecord;
    TXRecordBuffer *recorderBuffer;
    bool onPause = false;

public :
    TXOpenSLES();

    ~TXOpenSLES();

    void createEngine();

    void start();

    void pause();

    void resume();

    void stop();

};


#endif //OPENGL_TXOPENSLES_H
