/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#include "TXRecordBuffer.h"

TXRecordBuffer::TXRecordBuffer(int buffersize) {
    buffer = new short *[2];
    for (int i = 0; i < 2; i++) {
        buffer[i] = new short[buffersize];
    }
}

TXRecordBuffer::~TXRecordBuffer() {
    for (int i = 0; i < 2; i++) {
        delete buffer[i];
    }
    delete buffer;
}

short *TXRecordBuffer::getRecordBuffer() {
    index++;
    if (index > 1) {
        index = 1;
    }
    SDK_LOG_D("recordbuffer %d", index);
    return buffer[index];
}

short *TXRecordBuffer::getNowBuffer() {
    SDK_LOG_D("recordbuffer %d", index);
    return buffer[index];
}
