package com.taxiao.opengl.egl.obj;

import android.content.Context;
import android.opengl.GLES20;

import com.taxiao.opengl.R;
import com.taxiao.opengl.egl.image.ShaderProgram;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;

/**
 * 顶点坐标
 * Created by hanqq on 2022/1/5
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TextureShaderProgram2 extends ShaderProgram2 {
    private int aPositionLocation;
    private int aTextureCoordinatesLocation;

    private int uMatrixLocation;
    private int uTextureUnitLocation;

    public TextureShaderProgram2(Context context) {
        super(context, R.raw.simple_image_vertex_shader, R.raw.simple_image_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program,
                U_TEXTURE_UNIT);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniform(float[] matrix, int textureId) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {

        return aPositionLocation;
    }

    public int getTextureCoordinateAttributeLocation() {

        return aTextureCoordinatesLocation;
    }
}
