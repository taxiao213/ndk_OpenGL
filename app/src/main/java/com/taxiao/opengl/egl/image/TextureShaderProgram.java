package com.taxiao.opengl.egl.image;

import android.content.Context;
import android.opengl.GLES20;

import com.taxiao.opengl.R;

/**
 * 顶点坐标
 * Created by hanqq on 2022/1/5
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TextureShaderProgram extends ShaderProgram {
    private int aPositionLoaction;
    private int aTextureCoodinateLocation;

    private int uMatrixLocation;
    private int uTextureUnitLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.simple_image_vertex_shader, R.raw.simple_image_fragment_shader);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPositionLoaction = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoodinateLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDTINATES);
    }

    public void setUniform(float[] matrix, int textureId) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {

        return aPositionLoaction;
    }

    public int getTextureCoordinateAttributeLocation() {

        return aTextureCoodinateLocation;
    }
}
