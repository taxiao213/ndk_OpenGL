package com.taxiao.opengl.egl.particle;

import android.content.Context;
import android.opengl.GLES20;

import com.taxiao.opengl.util.ShaderUtils;

/**
 * 粒子爆炸
 * Created by hanqq on 2022/1/5
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class ShaderProgram3 {
    // Uniform
    public static final String U_MATRIX = "u_Matrix";
    protected static final String U_COLOR = "u_Color";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_TIME = "u_Time";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";

    // Shader program
    protected final int program;

    public ShaderProgram3(Context context, int vetexShaderResourceId, int fragmentShaderResourceId) {
        String vertexSource = ShaderUtils.readRawTxt(context, vetexShaderResourceId);
        String fragmentSource = ShaderUtils.readRawTxt(context, fragmentShaderResourceId);
        program = ShaderUtils.createProgram(vertexSource, fragmentSource);
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }
}
