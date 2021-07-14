package com.taxiao.opengl.yuv;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.taxiao.opengl.R;
import com.taxiao.opengl.imagevideo.TXFBOImageRender;
import com.taxiao.opengl.util.DisplayUtil;
import com.taxiao.opengl.util.LogUtils;
import com.taxiao.opengl.util.ShaderUtils;
import com.taxiao.opengl.util.egl.TXEglRender;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * 渲染图片
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
public class TXMutiYUVRender extends TXEglRender {
    private String TAG = TXMutiYUVRender.this.getClass().getSimpleName();

    // 顶点坐标
    private final float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,
    };

    // TODO 纹理坐标 FBO 离屏绘制和手机正常坐标不同
    // * FBO离屏渲染的纹理坐标系以左下角为（0，0），左上角（0，1），右上角（1，1），右下角（1，0）
    // * 手机正常的纹理坐标系以左下角为（0，1），左上角（0，0），右上角（1，0），右下角（1，1）
    private final float[] textureData = {
//            0f, 1f,
//            1f, 1f,
//            0f, 0f,
//            1f, 0f

            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f
    };

    private Context mContext;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int program;
    private int av_position;
    private int af_position;
    private int sampler_y;
    private int sampler_u;
    private int sampler_v;
    private int[] vbo;
    private int[] textureYUVid;
    private int[] textureIMGId;
    private int[] fbo;
    private TXFBOYUVRender fboRender;
    // view 大小
    private int mWidth;
    private int mHeight;
    private int mViewWidth;
    private int mViewHeight;
    int w;
    int h;

    Buffer y;
    Buffer u;
    Buffer v;
    private boolean isSave = true;

    public TXMutiYUVRender(Context context) {
        LogUtils.d(TAG, "TXMutiCameraRender create");
        this.mContext = context;
        this.mWidth = DisplayUtil.getScreenWidth(context);
        this.mHeight = DisplayUtil.getScreenHeight(context);
        fboRender = new TXFBOYUVRender(context);
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
        super.onSurfaceCreated();
        LogUtils.d(TAG, "onSurfaceCreated");
        if (fboRender != null) {
            fboRender.onSurfaceCreated();
        }
        initOpenGLES();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        LogUtils.d(TAG, String.format("onSurfaceChanged width:%d height:%d ", width, height));
        if (fboRender != null) {
            fboRender.onSurfaceChanged(width, height);
        }
        mViewWidth = width;
        mViewHeight = height;
    }

    @Override
    public void onDrawFrame() {
        LogUtils.d(TAG, "onDrawFrame");
        if (fboRender != null && textureIMGId != null && textureIMGId.length > 0) {
            // 传递的是纹理
            fboRender.onSurfaceChanged(mViewWidth, mViewHeight);
            fboRender.onDrawFrame(textureIMGId[0]);
        }
        renderFrame();
    }

    private void initOpenGLES() {
        LogUtils.d(TAG, "initOpenGLES");
        // 2.加载 shader 注意需要使用不同的顶点shader
        String vertex = ShaderUtils.readRawTxt(mContext, R.raw.vertex_yuv_shader);
        String texture = ShaderUtils.readRawTxt(mContext, R.raw.fragment_yuv_shader);

        // 3.创建渲染程序
        program = ShaderUtils.createProgram(vertex, texture);
        if (program > 0) {
            // 4.得到着色器中的属性
            av_position = GLES20.glGetAttribLocation(program, "av_Position");
            af_position = GLES20.glGetAttribLocation(program, "af_Position");
            sampler_y = GLES20.glGetUniformLocation(program, "sampler_y");
            sampler_u = GLES20.glGetUniformLocation(program, "sampler_u");
            sampler_v = GLES20.glGetUniformLocation(program, "sampler_v");

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

            // 3.创建绑定纹理
            if (textureYUVid == null) {
                textureYUVid = new int[3];
            }
            GLES20.glGenTextures(3, textureYUVid, 0);
            for (int i = 0; i < 3; i++) {
                // 4.绑定纹理
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureYUVid[i]);
                // 5.设置环绕和过滤方式 环绕（超出纹理坐标范围）：（s==x t==y GL_REPEAT 重复）
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
                // 6.过滤（纹理像素映射到坐标点）：（缩小、放大：GL_LINEAR线性）边角无锯齿
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                // 解绑纹理
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            }

            // 使用FBO
            // 4.6 创建FBO
            fbo = new int[1];
            GLES20.glGenBuffers(1, fbo, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo[0]);

            textureIMGId = new int[1];
            GLES20.glGenTextures(1, textureIMGId, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIMGId[0]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
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
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureIMGId[0], 0);
            // 4.9 检查FBO绑定是否成功
            if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                LogUtils.d(TAG, "fbo wrong");
            } else {
                LogUtils.d(TAG, "fbo success");
            }

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            // 解绑fbo
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            LogUtils.d(TAG, "initOpenGLES end");
        }
    }

    private void renderFrame() {
        if (program > 0 && w > 0 && h > 0 && y != null && u != null && v != null) {
            LogUtils.d(TAG, "renderFrame");
            GLES20.glViewport(0, 0, mWidth, mHeight);
            // 10.使用渲染器
            GLES20.glUseProgram(program);
            // 10.1绑定
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo[0]);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

            // 11.使顶点坐标和纹理坐标属性数组有效
            GLES20.glEnableVertexAttribArray(av_position);
            // 使用VBO 缓存时最后一个参数传0，不使用VBO ,最后一个参数传vertexBuffer
            GLES20.glVertexAttribPointer(av_position, 2, GLES20.GL_FLOAT, false, 8, 0);

            GLES20.glEnableVertexAttribArray(af_position);
            // 设置VBO 缓存时最后一个参数传入顶点坐标buffer的内存大小，偏移量
            GLES20.glVertexAttribPointer(af_position, 2, GLES20.GL_FLOAT, false, 8, vertexData.length * 4);

            if (isSave) {
                // 渲染的图片保存
                ByteBuffer imageByteBuffer = ByteBuffer.allocate(mWidth * mHeight * 4);
                GLES20.glReadPixels(0, 0, mWidth, mHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, imageByteBuffer);
                saveImage(imageByteBuffer);
                isSave = false;
            }

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureYUVid[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w, h, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, y);
            GLES20.glUniform1i(sampler_y, 0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureYUVid[1]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w / 2, h / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, u);
            GLES20.glUniform1i(sampler_u, 1);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureYUVid[2]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w / 2, h / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, v);
            GLES20.glUniform1i(sampler_v, 2);

            y.clear();
            u.clear();
            v.clear();

            y = null;
            u = null;
            v = null;
            // 12.绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            // 13.解绑
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }
    }

    // opengl 渲染的图片保存
    private void saveImage(ByteBuffer imageByteBuffer) {
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(imageByteBuffer);
        String folderPath = mContext.getCacheDir() + "/opengl_yuv.png";
        File filePath = new File(folderPath);
        BufferedOutputStream bos = null;
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
            LogUtils.d(TAG, "save imagePath: " + filePath.getAbsolutePath());
        }
    }

    // todo 共享纹理传值有问题
    public int getTextureID() {
        return textureYUVid[0];
    }

    public void setFrameData(int w, int h, byte[] by, byte[] bu, byte[] bv) {
        this.w = w;
        this.h = h;
        this.y = ByteBuffer.wrap(by);
        this.u = ByteBuffer.wrap(bu);
        this.v = ByteBuffer.wrap(bv);
    }
}
