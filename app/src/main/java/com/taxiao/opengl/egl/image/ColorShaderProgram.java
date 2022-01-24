package com.taxiao.opengl.egl.image;

import android.content.Context;
import android.opengl.GLES20;

import com.taxiao.opengl.R;

/**
 * Created by hanqq on 2022/1/5
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class ColorShaderProgram extends ShaderProgram {
    private int aPositionLocation;
    private int aColorLocation;
    private int uColorLocation;
    private int uMatrixLocation;

    public ColorShaderProgram(Context context, int vetexShaderResourceId, int fragmentShaderResourceId) {
        super(context, vetexShaderResourceId, fragmentShaderResourceId);
    }

    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_image_vertex_shader2, R.raw.simple_image_fragment_shader2);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
    }

    public void setUniform(float[] matrix) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }

    public int getPositionAttributeLocation() {

        return aPositionLocation;
    }

    public int getColorAttributeLocation() {

        return aColorLocation;
    }

    public int getColorUniformLocation() {

        return uColorLocation;
    }
}
