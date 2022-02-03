package com.taxiao.opengl.egl.obj;

import android.content.Context;
import android.opengl.GLES20;

import com.taxiao.opengl.R;
import com.taxiao.opengl.egl.image.ShaderProgram;

/**
 * Created by hanqq on 2022/1/5
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class ColorShaderProgram2 extends ShaderProgram2 {
    private int aPositionLocation;
    private int uMatrixLocation;
    private int uColorLocation;

    public ColorShaderProgram2(Context context) {
        super(context, R.raw.simple_image_vertex_shader3, R.raw.simple_image_fragment_shader3);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
    }

    public void setUniform(float[] matrix, float r, float g, float b) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        GLES20.glUniform4f(uColorLocation, r, g, b, 1f);
    }

    public int getPositionAttributeLocation() {

        return aPositionLocation;
    }

}
