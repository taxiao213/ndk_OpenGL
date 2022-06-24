package com.taxiao.opengl.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.taxiao.opengl.R;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.ShaderUtils;
import com.taxiao.opengl.util.egl.TXEglRender;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * 多Surface 渲染
 * VBO:Vertex Buffer object
 * 渲染图片 使用VBO
 * 不使用VBO时，我们每次绘制（ glDrawArrays ）图形时都是从本地内存处获取顶点数据然后传输给OpenGL来绘制，这样就会频繁的操作CPU->GPU增大开销，从而降低效率。
 * <p>
 * 使用VBO，我们就能把顶点数据缓存到GPU开辟的一段内存中，然后使用时不必再从本地获取，而是直接从显存中获取，这样就能提升绘制的效率。
 * <p>
 * FBO： Frame Buffer object
 * 为什么要用FBO?
 * 当我们需要对纹理进行多次渲染采样时，而这些渲染采样是不需要展示给用户看的，所以我们就可以用一个单独的缓冲对象（离屏渲染）来存储我们的这几次渲染采样的结果，等处理完后才显示到窗口上。
 * <p>
 * 优势
 * 提高渲染效率，避免闪屏，可以很方便的实现纹理共享等。
 * <p>
 * 渲染方式
 * 渲染到缓冲区（Render）- 深度测试和模板测试
 * 渲染到纹理（Texture）- 图像渲染
 * <p>
 * <p>
 * FBO离屏渲染的纹理坐标系以左下角为（0，0），左上角（0，1），右上角（1，1），右下角（1，0）
 * 手机正常的纹理坐标系以左下角为（0，1），左上角（0，0），右上角（0，1），右下角（1，1）
 * Created by hanqq on 2021/6/1
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class TXMutiImageRender3 extends TXEglRender {
    private String TAG = TXMutiImageRender3.this.getClass().getSimpleName();

    // 顶点坐标
    private final float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,

            -0.5f, -0.5f,
            0.5f, -0.5f,
            -0.5f, 0.5f,
            0.5f, 0.5f

            - 0.2f, -0.2f,
            0.2f, -0.2f,
            -0.2f, 0.2f,
            0.2f, 0.2f
    };

    // TODO 纹理坐标 FBO 离屏绘制和手机正常坐标不同
    // * FBO离屏渲染的纹理坐标系以左下角为（0，0），左上角（0，1），右上角（1，1），右下角（1，0）
    // * 手机正常的纹理坐标系以左下角为（0，1），左上角（0，0），右上角（1，0），右下角（1，1）
    private final float[] textureData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f

//            0f, 0f,
//            1f, 0f,
//            0f, 1f,
//            1f, 1f
    };

    private Context mContext;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int program;
    private int av_position;
    private int af_position;
    private int s_texture;
    private int[] vbo;
    private int[] textureid;
    private int[] fbo;
    private int imageTexure;
    private int imageTexure2;
    private int imageTexure3;
    private final TXFBORender fboRender;
    private int u_matrix;
    private float[] mMatrix;
    private float imageWidth;
    private float imageHeight;
    private int mTextureId;
    private int mIndex = -1;
    // view 大小
    private int mWidth;
    private int mHeight;
    private OnRenderCreateListener mOnRenderCreateListener;
    private int[] m_vaoId;
    private int[] m_vboId;

    public TXMutiImageRender3(Context context, int width, int height) {
        this.mContext = context;
        this.mWidth = width;
        this.mHeight = height;
        fboRender = new TXFBORender(context);
        mMatrix = new float[16];
        // 1.创建顶点 和 纹理 buffer
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated() {
        LogUtils.d(TAG, "onSurfaceCreated");
        if (fboRender != null) {
            fboRender.onSurfaceCreated();
        }
        initOpenGLES();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        LogUtils.d(TAG, "onSurfaceChanged");
        if (fboRender != null) {
            fboRender.onSurfaceChanged(width, height);
        }

        // 正交投影
        // {#link https://www.jianshu.com/p/044d521351ec}
        if (width > 0 && height > 0 && imageWidth > 0 && imageHeight > 0) {
            LogUtils.d(TAG, String.format("width:%d , height:%d , imageWidth:%f , imageHeight:%f, scaleWidth:%f , scaleHeight:%f",
                    width, height, imageWidth, imageHeight, width / ((height / imageHeight) * imageWidth), height / ((width / imageWidth) * imageHeight)));
            if (width > height) {
                Matrix.orthoM(mMatrix, 0, -width / ((height / imageHeight) * imageWidth), width / ((height / imageHeight) * imageWidth), -1f, 1f, -1f, 1f);
            } else {
                Matrix.orthoM(mMatrix, 0, -1, 1, -height / ((width * 1.0f / imageWidth) * imageHeight), height / ((width * 1.0f / imageWidth) * imageHeight), -1f, 1f);
            }
        }
        // 矩阵旋转
        // 纹理坐标 FBO 离屏绘制和手机正常坐标不同 围绕哪个坐标轴旋转就填1
        // 参数1: 旋转对象，参数2: 旋转角度，参数3:  x坐标，参数4:  y坐标，参数5:  z坐标
        Matrix.rotateM(mMatrix, 0, 180, 1, 0, 0);
    }

    @Override
    public void onDrawFrame() {
        LogUtils.d(TAG, "onDrawFrame");
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1); //禁用byte-alignment限制
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glViewport(0, 0, mWidth, mHeight);
//
        if (fboRender != null && textureid != null && textureid.length > 0) {
//            // 传递的是纹理
            fboRender.onDrawFrame(textureid[0]);
        }
        renderFrame();
    }

    private void initOpenGLES() {
        int[] m_TextureId = new int[]{0};
        GLES20.glGenTextures(1, m_TextureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_TextureId[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);

        m_vaoId = new int[]{0};
        m_vboId = new int[]{0};
        GLES30.glGenVertexArrays(1, m_vaoId, 0);
        GLES20.glGenBuffers(1, m_vboId, 0);

        GLES30.glBindVertexArray(m_vaoId[0]);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m_vboId[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + textureData.length * 4, null, GLES20.GL_STATIC_DRAW);
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, sizeof(GLfloat) * 6 * 4, nullptr, GLES20.GL_DYNAMIC_DRAW);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, textureData.length * 4, textureBuffer);
        // 4.5 解绑
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, GLES20.GL_NONE);
        GLES30.glBindVertexArray(GLES20.GL_NONE);







     /*   LogUtils.d(TAG, "initOpenGLES");
        // 2.加载 shader 注意需要使用不同的顶点shader
        String vertex = ShaderUtils.readRawTxt(mContext, R.raw.vertex_image_matrix_shader);
        String texture = ShaderUtils.readRawTxt(mContext, R.raw.fragment_image_shader);
        // 加载不同的纹理
        if (mIndex == 0) {
            texture = ShaderUtils.readRawTxt(mContext, R.raw.fragment_image_shader1);
        } else if (mIndex == 1) {
            texture = ShaderUtils.readRawTxt(mContext, R.raw.fragment_image_shader2);
        } else if (mIndex == 2) {
            texture = ShaderUtils.readRawTxt(mContext, R.raw.fragment_image_shader3);
        }

        // 3.创建渲染程序
        program = ShaderUtils.createProgram(vertex, texture);
        if (program > 0) {
            // 4.得到着色器中的属性
            av_position = GLES20.glGetAttribLocation(program, "av_Position");
            af_position = GLES20.glGetAttribLocation(program, "af_Position");
            s_texture = GLES20.glGetUniformLocation(program, "s_texture");
            u_matrix = GLES20.glGetUniformLocation(program, "u_Matrix");


            // 使用VBO
            // 4.1 创建VBO
            vbo = new int[1];
            GLES20.glGenBuffers(1, vbo, 0);
            // 4.2 绑定VBO
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
            // 4.3 分配VBO需要的缓存大小，静态绘制
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + textureData.length * 4, null, GLES20.GL_STATIC_DRAW);
            // 4.4 为VBO设置顶点数据的值
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, textureData.length * 4, textureBuffer);
            // 4.5 解绑
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

            // 使用FBO
            // 4.6 创建FBO
            fbo = new int[1];
            GLES20.glGenBuffers(1, fbo, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo[0]);


            // 5.创建纹理
            if (textureid == null) {
                textureid = new int[1];
            }
            GLES20.glGenTextures(1, textureid, 0);
            if (textureid[0] == 0) {
                LogUtils.d(TAG, " textureid[0] == 0");
                return;
            }
            // 6.绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid[0]);

            // 7.设置环绕和过滤方式 环绕（超出纹理坐标范围）：（s==x t==y GL_REPEAT 重复）
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            // 8.过滤（纹理像素映射到坐标点）：（缩小、放大：GL_LINEAR线性）
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);


            // 参数1 匹配模式
            // 参数2 纹理层级
            // 参数3 format
            // 参数4 离屏渲染的宽 和 VIEW 的宽相同
            // 参数5 离屏渲染的高 和 VIEW 的高相同
            // 参数6 历史遗留传0就行
            // 参数7 format
            // 参数8 无符号的byte
            // 参数9 传递的buffer,传null只分配内存
            // 4.7 设置FBO分配内存大小
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mWidth
                    , mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            // 4.8 把纹理绑定到FBO
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureid[0], 0);
            // 4.9 检查FBO绑定是否成功
            if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                LogUtils.d(TAG, "fbo wrong");
            } else {
                LogUtils.d(TAG, "fbo success");
            }
            // 解绑fbo
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            imageTexure = loadTexture(R.mipmap.ic_launcher);
            imageTexure2 = loadTexture(R.mipmap.image_01);
            imageTexure3 = loadTexture(R.mipmap.img_222);

            if (mOnRenderCreateListener != null) {
                mOnRenderCreateListener.onCreate(textureid[0]);
            }
        }*/
    }

    private void renderFrame() {
        if (program > 0) {
            GLES20.glUniformMatrix4fv(u_matrix, 1, false, mMatrix, 0);
            // 10.使用渲染器
            GLES20.glUseProgram(program);
            GLES30.glBindVertexArray(m_vaoId[0]);

//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            for (int i = 0; i < 1; i++) {

//            GLES20.glUniform1i(m_SamplerLoc, 0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexure);
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, m_vboId[0]);
//                GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
//                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
//                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

                // 11.使顶点坐标和纹理坐标属性数组有效
                GLES20.glEnableVertexAttribArray(av_position);
                // 使用VBO 缓存时最后一个参数传0，不使用VBO ,最后一个参数传vertexBuffer
                GLES20.glVertexAttribPointer(av_position, 2, GLES20.GL_FLOAT, false, 8, 0);

                GLES20.glEnableVertexAttribArray(af_position);
                // 设置VBO 缓存时最后一个参数传入顶点坐标buffer的内存大小，偏移量
                GLES20.glVertexAttribPointer(af_position, 2, GLES20.GL_FLOAT, false, 8, vertexData.length * 4);
                // 12.绘制
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            }
            GLES30.glBindVertexArray(0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);


            // -------------------------------------------
         /*   LogUtils.d(TAG, "renderFrame");
            // 10.使用渲染器
            GLES20.glUseProgram(program);
            GLES20.glUniformMatrix4fv(u_matrix, 1, false, mMatrix, 0);
            // 10.1绑定
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo[0]);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

            *//* 绑定多个纹理 *//*
            // 绑定第一张图片
            // 10.2绑定图片的纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexure2);

            // 11.使顶点坐标和纹理坐标属性数组有效
            GLES20.glEnableVertexAttribArray(av_position);
            // 使用VBO 缓存时最后一个参数传0，不使用VBO ,最后一个参数传vertexBuffer
            GLES20.glVertexAttribPointer(av_position, 2, GLES20.GL_FLOAT, false, 8, 0);

            GLES20.glEnableVertexAttribArray(af_position);
            // 设置VBO 缓存时最后一个参数传入顶点坐标buffer的内存大小，偏移量
            GLES20.glVertexAttribPointer(af_position, 2, GLES20.GL_FLOAT, false, 8, vertexData.length * 4);
            // 12.绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            // 绑定第二张图片
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexure);

            // 11.使顶点坐标和纹理坐标属性数组有效
            GLES20.glEnableVertexAttribArray(av_position);
            // 使用VBO 缓存时最后一个参数传0，不使用VBO ,最后一个参数传vertexBuffer ,一个 float 4 个字节,
            GLES20.glVertexAttribPointer(av_position, 2, GLES20.GL_FLOAT, false, 8, 32);

            GLES20.glEnableVertexAttribArray(af_position);
            // 设置VBO 缓存时最后一个参数传入顶点坐标buffer的内存大小，偏移量
            GLES20.glVertexAttribPointer(af_position, 2, GLES20.GL_FLOAT, false, 8, vertexData.length * 4);
            // 12.绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            *//* 绑定多个纹理 *//*

            // 13.解绑
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);*/
        }
    }

    /**
     * 加载Texture
     *
     * @param drawable
     * @return
     */
    private int loadTexture(int drawable) {
        int[] textureid = new int[1];
        GLES20.glGenTextures(1, textureid, 0);
        if (textureid[0] == 0) {
            LogUtils.d(TAG, " textureid[0] == 0");
            return 0;
        }
        // 6.绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid[0]);
        // 7.设置环绕和过滤方式 环绕（超出纹理坐标范围）：（s==x t==y GL_REPEAT 重复）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        // 8.过滤（纹理像素映射到坐标点）：（缩小、放大：GL_LINEAR线性）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        // 9.绑定图片
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawable);
        if (bitmap != null) {
            imageWidth = bitmap.getWidth();
            imageHeight = bitmap.getHeight();
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
            bitmap = null;
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        LogUtils.d(TAG, "bind Bitmap");
        return textureid[0];
    }

    public void setOnRenderCreateListener(OnRenderCreateListener onRenderCreateListener) {
        this.mOnRenderCreateListener = onRenderCreateListener;
    }

    // 加载不同的纹理
    public void setTextureId(int textureId, int index) {
        if (textureid == null) {
            textureid = new int[1];
        }
        this.textureid[0] = textureId;
        this.mIndex = index;
    }
}
