package com.taxiao.opengl.rtmp;

import android.content.Context;

import com.taxiao.opengl.encodec.TXBaseMediaCodecEncoder;
import com.taxiao.opengl.encodec.TXEncodecRender;
import com.taxiao.opengl.util.Constant;

/**
 * Created by hanqq on 2021/7/10
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXRtmpMediaCodecEncoder extends TXRtmpBaseMediaCodecEncoder {

    public TXRtmpMediaCodecEncoder(Context context, int textureID) {
        TXEncodecRender wlEncodecRender = new TXEncodecRender(context, textureID);
        setRender(wlEncodecRender);
        setmRenderMode(Constant.RENDERMODE_CONTINUOUSLY);
    }
}
