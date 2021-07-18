/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_TXRECORDBUFFER_H
#define OPENGL_TXRECORDBUFFER_H

#include "../android_log.h"

class TXRecordBuffer {
public:
    short **buffer;
    int index = -1;

public:
    TXRecordBuffer(int buffersize);

    ~TXRecordBuffer();

    short *getRecordBuffer();

    short *getNowBuffer();
};


#endif //OPENGL_TXRECORDBUFFER_H
