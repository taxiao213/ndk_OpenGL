package com.taxiao.opengl.egl;

/**
 * Created by hanqq on 2021/12/10
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public abstract class BaseRender {

    public abstract void initEGL();

    public abstract void release();

    public abstract void draw();
}
