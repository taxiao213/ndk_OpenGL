package com.taxiao.opengl.egl.obj;

import android.content.Context;
import android.opengl.GLES20;

import com.taxiao.opengl.util.ShaderUtils;

/**
 * Created by hanqq on 2022/1/5
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class ShaderProgram2 {
    // Uniform
    public static final String U_MATRIX = "u_Matrix";
    public static final String U_COLOR = "u_Color";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // Attribute constants
    public static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    public final int program;


    public ShaderProgram2(Context context, int vetexShaderResourceId, int fragmentShaderResourceId) {
        String vertexSource = ShaderUtils.readRawTxt(context, vetexShaderResourceId);
        String fragmentSource = ShaderUtils.readRawTxt(context, fragmentShaderResourceId);
        program = ShaderUtils.createProgram(vertexSource, fragmentSource);
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }
}
