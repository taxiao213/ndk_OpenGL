package com.taxiao.opengl.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;


import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;

/**
 * Created by hanqq on 2022/1/5
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TextureHelper {
    private static String TAG = "TextureHelper";

    public static int loadTexture(Context context, int resourceId) {
        int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            LogUtils.d(TAG, "glGenTextures error");
            return 0;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 获取原始数据
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            LogUtils.d(TAG, "decodeResource error");
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        // 绑定
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        // 纹理过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // 加载位图数据到OpenGL
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        // 生成MIP贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        bitmap.recycle();
        // 解绑
        glBindTexture(GL_TEXTURE_2D, 0);
        return textureObjectIds[0];
    }
}
