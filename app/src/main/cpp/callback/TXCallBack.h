/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#ifndef OPENGL_TXCALLBACK_H
#define OPENGL_TXCALLBACK_H


#include <jni.h>

#define THREAD_MAIN 1
#define THREAD_CHILD 2

class TXCallBack {

public:
    JavaVM *_javaVm;
    JNIEnv *_jniEnv;
    jobject _job;

    jmethodID jmid_connecting;
    jmethodID jmid_connectsuccess;
    jmethodID jmid_connectfail;
public:
    TXCallBack(JavaVM *javaVm, JNIEnv *jniEnv, jobject *job);

    ~TXCallBack();

    void onConecting(int type);

    void onSuccess(int type);

    void onFail(int type, char *msg);

};


#endif //OPENGL_TXCALLBACK_H
