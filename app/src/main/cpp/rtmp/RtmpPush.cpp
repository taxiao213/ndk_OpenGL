/**
 * Created by yin13 on 2021/6/12
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
*/

#include "RtmpPush.h"

RtmpPush::RtmpPush(const char *urlPath, TXCallBack *txCallBack) {
    this->txCallBack = txCallBack;
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
    txCallBack = NULL;
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
        if (rtmpPush->txCallBack != NULL) {
            rtmpPush->txCallBack->onFail(THREAD_CHILD, "can not connect the url");
        }
        goto end;
    }
    if (!RTMP_ConnectStream(rtmpPush->rtmp, 0)) {
        SDK_LOG_D("can not connect the stream of service");
        if (rtmpPush->txCallBack != NULL) {
            rtmpPush->txCallBack->onFail(THREAD_CHILD, "can not connect the stream of service");
        }
        goto end;
    }
    SDK_LOG_D("链接成功， 开始推流");
    if (rtmpPush->txCallBack != NULL) {
        rtmpPush->txCallBack->onSuccess(THREAD_CHILD);
    }
    rtmpPush->startPushing = true;
    rtmpPush->startTime = RTMP_GetTime();
    while (true) {
        if (!rtmpPush->startPushing) {
            break;
        }
        RTMPPacket *packet = NULL;
        packet = rtmpPush->rtmpQueue->getRtmpPacket();
        if (packet != NULL) {
            int result = RTMP_SendPacket(rtmpPush->rtmp, packet, 1);
            SDK_LOG_D("RTMP_SendPacket result is %d", result);
            RTMPPacket_Free(packet);
            free(packet);
            packet = NULL;
        }
    }

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

void RtmpPush::pushSPSPPS(char *sps, int sps_len, char *pps, int pps_len) {
    // 根据FLV header 头需要的信息写入
    int bodysize = sps_len + pps_len + 16;
    RTMPPacket *rtmpPacket = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(rtmpPacket, bodysize);
    RTMPPacket_Reset(rtmpPacket);
    char *body = rtmpPacket->m_body;
    // body就是帧数据
    int i = 0;
    // frame type : 1关键帧、2非关键帧 (4 bit)
    // CodecID : 7表示AVC (4 bit)		0x17 和frametype组合成一个字节
    body[i++] = 0x17;
    // fixed : 0x00（AVCDecoderConfigurationRecord） 0x00 0x00 0x00 (4 byte)
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    // sps + pps数据
    // configurationVersion (1 byte)		0x01 版本
    body[i++] = 0x01;
    // AVCProfileIndication (1 byte)		sps[1] Profile
    body[i++] = sps[1];
    // profile_compatibility (1 byte)		sps[2] 兼容性
    body[i++] = sps[2];
    // AVCLevelIndication (1 byte)		sps[3
    // ] Profile level
    body[i++] = sps[3];
    // lengthSizeMinusOne (1 byte)		0xff 包长数据所使用的字节数
    body[i++] = 0xFF;
    // sps number (1 byte)			0xe1 sps个数
    body[i++] = 0xE1;
    // sps data length (2 byte)			sps长度
    // 16 位 放在 2 byte ,先位移将前8位放进去，&(都为1取1)
    body[i++] = (sps_len >> 8) & 0xFF;
    body[i++] = sps_len & 0xFF;
    // sps data					sps实际内容
    memcpy(&body[i], sps, sps_len);
    i += sps_len;
    // pps number (1 byte)			0x01 pps的个数
    body[i++] = 0x01;
    // pps data length (2 byte)			pps长度
    body[i++] = (pps_len >> 8) & 0xFF;
    body[i++] = pps_len & 0xFF;
    // pps data					pps实际内容
    memcpy(&body[i], pps, pps_len);

    rtmpPacket->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    rtmpPacket->m_nBodySize = bodysize;
    rtmpPacket->m_nTimeStamp = 0;
    rtmpPacket->m_hasAbsTimestamp = 0;
    rtmpPacket->m_nChannel = 0x04;//audio 或者 video
    rtmpPacket->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    rtmpPacket->m_nInfoField2 = rtmp->m_stream_id;
    rtmpQueue->putRtmpPacket(rtmpPacket);
}

void RtmpPush::pushVideoData(char *data, int data_len, bool keyframe) {
    SDK_LOG_D("pushVideoData %d", rtmp->m_stream_id);
    RTMPPacket *rtmpPacket = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    int bodysize = data_len + 9;
    RTMPPacket_Alloc(rtmpPacket, bodysize);
    RTMPPacket_Reset(rtmpPacket);
    char *body = rtmpPacket->m_body;
    // frame type : 1关键帧、2非关键帧 (4 bit)
    //CodecID : 7表示AVC (4 bit)	0x17/0x27 和frametype组合成一个字节
    //
    //fixed : 0x01（NALU） 0x00 0x00 0x00 (4 byte)
    //data length : 长度信息（4 byte）
    //data : h264裸数据
    int i = 0;
    if (keyframe) {
        body[i++] = 0x17;
    } else {
        body[i++] = 0x27;
    }
    body[i++] = 0x01;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = 0x00;
    body[i++] = (data_len >> 24) & 0xFF;
    body[i++] = (data_len >> 16) & 0xFF;
    body[i++] = (data_len >> 8) & 0xFF;
    body[i++] = data_len & 0xFF;
    memcpy(&body[i], data, data_len);
    rtmpPacket->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    rtmpPacket->m_nBodySize = bodysize;
    rtmpPacket->m_nTimeStamp = RTMP_GetTime() - startTime;
    rtmpPacket->m_hasAbsTimestamp = 0;
    rtmpPacket->m_nChannel = 0x04;
    rtmpPacket->m_headerType = RTMP_PACKET_SIZE_LARGE;
    rtmpPacket->m_nInfoField2 = rtmp->m_stream_id;
    rtmpQueue->putRtmpPacket(rtmpPacket);
}

void RtmpPush::pushAudioData(char *data, int data_len) {
    SDK_LOG_D("pushAudioData %d", rtmp->m_stream_id);
    RTMPPacket *rtmpPacket = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    int bodysize = data_len +7;
    RTMPPacket_Alloc(rtmpPacket, bodysize);
    RTMPPacket_Reset(rtmpPacket);
    char *body = rtmpPacket->m_body;

    int profile = 2; // AAC LC
    int freqIdx = 4; // 44.1KHz
    int chanCfg = 2; // CPE
    int i=0;
    // fill in ADTS data
//    body[i++]=0xFF;// 11111111     = syncword
//    body[i++]=0xF9; // 1111 1 00 1  = syncword MPEG-2 Layer CRC
//    body[2] = (char)(((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
//    body[6] = (char)0xFC;
//    body[1] =   0xF9;
//    body[2] =   (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
//    body[3] =   (((chanCfg & 3) << 6) + (bodysize >> 11));
//    body[4] =   ((bodysize & 0x7FF) >> 3);
//    body[5] =   (((bodysize & 7) << 5) + 0x1F);
//    body[6] =   0xFC;


    body[i++] = 0xff;                                    //syncword  (0xfff, high_8bits)
    body[i] = 0xf0;                                      //syncword  (0xfff, low_4bits)
    body[i] |= (0 << 3);                                 //ID (0, 1bit)
    body[i] |= (0 << 1);                                 //layer (0, 2bits)
    body[i]|= 1;                                        //protection_absent (1, 1bit)
    i++;
    body[i] = (unsigned char) ((profile & 0x3) << 6);  //profile (profile, 2bits)
    body[i] |= ((freqIdx & 0xf) << 2);         //sampling_frequency_index (sam_idx, 4bits)
    body[i] |= (0 << 1);                                 //private_bit (0, 1bit)
    body[i] |= ((chanCfg & 0x4) >> 2);                 //channel_configuration (channel, high_1bit)
    i++;
    body[i] = ((chanCfg & 0x3) << 6);                  //channel_configuration (channel, low_2bits)
    body[i] |= (0 << 5);                                 //original/copy (0, 1bit)
    body[i] |= (0 << 4);                                 //home  (0, 1bit);
    body[i]|= (0 << 3);                                 //copyright_identification_bit (0, 1bit)
    body[i]|= (0 << 2);                                 //copyright_identification_start (0, 1bit)
    body[i] |= ((bodysize & 0x1800) >> 11);             //frame_length (value, high_2bits)
    i++;
    body[i++] = (unsigned char) ((bodysize & 0x7f8) >> 3);  //frame_length (value, middle_8bits)
    body[i] = (unsigned char) ((bodysize & 0x7) << 5);      //frame_length (value, low_3bits)
    body[i] |= 0x1f;                                         //adts_buffer_fullness (0x7ff, high_5bits)
    i++;
    body[i] = 0xfc;                                          //adts_buffer_fullness (0x7ff, low_6bits)
    body[i] |= 0;                                            //number_of_raw_data_blocks_in_frame (0, 2bits);
    i++;



    //总共2字节：
    //
    //第一个字节表示AAC数据参数信息：
    //前4位的数值表示了音频数据格式 ---- 10(A)表示 AAC第5-6位的数值表示采样率，0 = 5.5 kHz，1 = 11 kHz，2 = 22 kHz，3(11) = 44 kHz。第7位表示采样精度，0 = 8bits，1 = 16bits。第8位表示音频类型，0 = mono，1 = stereo
    //
    //=>0xAF = AF
    //
    //第二个字节：
    //0x00 aac头信息  
    //0x01 aac 原始数据
//    body[i] = 0xAF;
//    i++;
//    body[i] = 0x01;
//    i++;
    memcpy(&body[i], data, data_len);
    rtmpPacket->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    rtmpPacket->m_nBodySize = bodysize;
    rtmpPacket->m_nTimeStamp = RTMP_GetTime() - startTime;
    rtmpPacket->m_hasAbsTimestamp = 0;
    rtmpPacket->m_nChannel = 0x04;
    rtmpPacket->m_headerType = RTMP_PACKET_SIZE_LARGE;
    rtmpPacket->m_nInfoField2 = rtmp->m_stream_id;
    rtmpQueue->putRtmpPacket(rtmpPacket);
}

void RtmpPush::stop() {
    startPushing = false;
    rtmpQueue->notifyQueue();
    pthread_join(pushThread, NULL);
}
