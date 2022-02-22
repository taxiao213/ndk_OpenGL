package com.taxiao.opengl.egl.heightmap;

import android.content.Context;
import android.opengl.GLES20;

import com.taxiao.opengl.R;
import com.taxiao.opengl.egl.skybox.ShaderProgram4;


/**
 * Created by hanqq on 2022/2/21
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class HeightmapShaderProgram extends ShaderProgram4 {
    private final int uMatrixLocation;
    private final int aPositionLocation;

    protected HeightmapShaderProgram(Context context) {
        super(context, R.raw.vertex_heightmap_shader, R.raw.fragment_heightmap_shader);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
    }

    public void setUniforms(float[] matrix) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}
