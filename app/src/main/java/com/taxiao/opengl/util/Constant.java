package com.taxiao.opengl.util;

/**
 * Created by hanqq on 2021/7/10
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Constant {
    /**
     * The renderer only renders
     * when the surface is created, or when {@link #requestRender} is called.
     * 手动渲染
     */
    public final static int RENDERMODE_WHEN_DIRTY = 0;

    /**
     * The renderer is called
     * continuously to re-render the scene.
     * 自动渲染
     */
    public final static int RENDERMODE_CONTINUOUSLY = 1;

}
