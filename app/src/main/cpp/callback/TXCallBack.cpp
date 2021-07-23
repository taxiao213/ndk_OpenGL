/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/


#include "TXCallBack.h"

TXCallBack::TXCallBack(JavaVM *javaVm, JNIEnv *jniEnv, jobject *job) {
    this->_javaVm = javaVm;
    this->_jniEnv = jniEnv;
    this->_job = jniEnv->NewGlobalRef(*job);
    jclass pJclass = jniEnv->GetObjectClass(_job);
    jmid_connecting = _jniEnv->GetMethodID(pJclass, "onConnecting", "()V");
    jmid_connectsuccess = _jniEnv->GetMethodID(pJclass, "onConnectSuccess", "()V");
    jmid_connectfail = _jniEnv->GetMethodID(pJclass, "onConnectFail", "(Ljava/lang/String;)V");
}

TXCallBack::~TXCallBack() {
    _jniEnv->DeleteGlobalRef(_job);
    _job = NULL;
    _jniEnv = NULL;
    _javaVm = NULL;
}

void TXCallBack::onConecting(int type) {
    if (type == THREAD_CHILD) {
        JNIEnv *jniEnv;
        if (_javaVm->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
            return;
        }
        jniEnv->CallVoidMethod(_job, jmid_connecting);
        _javaVm->DetachCurrentThread();
    } else {
        _jniEnv->CallVoidMethod(_job, jmid_connecting);
    }
}

void TXCallBack::onSuccess(int type) {
    if (type == THREAD_CHILD) {
        JNIEnv *jniEnv;
        if (_javaVm->AttachCurrentThread(&jniEnv, 0) != JNI_OK) { return; }
        jniEnv->CallVoidMethod(_job, jmid_connectsuccess);
        _javaVm->DetachCurrentThread();
    } else {
        _jniEnv->CallVoidMethod(_job, jmid_connectsuccess);
    }
}

void TXCallBack::onFail(int type, char *msg) {
    if (type == THREAD_CHILD) {
        JNIEnv *jniEnv;
        if (_javaVm->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
            return;
        }
        jstring pJstring = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(_job, jmid_connectfail, pJstring);
        jniEnv->DeleteLocalRef(pJstring);
        _javaVm->DetachCurrentThread();
    } else {
        jstring pJstring = _jniEnv->NewStringUTF(msg);
        _jniEnv->CallVoidMethod(_job, jmid_connectfail, pJstring);
        _jniEnv->DeleteLocalRef(pJstring);
    }
}
