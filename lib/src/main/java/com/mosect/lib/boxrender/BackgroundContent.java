package com.mosect.lib.boxrender;

import android.graphics.Color;
import android.opengl.GLES20;

import com.mosect.lib.easygl.GLContent;
import com.mosect.lib.easygl.GLObject;

/**
 * 背景色实体内容，注意：其GLObject必须最先添加，否则会出现背景色覆盖GLObject情况
 */
public class BackgroundContent implements GLContent {

    private final int argbColor;
    private float[] color;

    public BackgroundContent(int argbColor) {
        this.argbColor = argbColor;
    }

    @Override
    public void initContent(GLObject<?> object) {
        this.color = genColor(argbColor);
    }

    @Override
    public void drawContent() {
        GLES20.glClearColor(color[0], color[1], color[2], color[3]);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void destroyContent() {
    }

    private float[] genColor(int color) {
        float rv = Color.red(color);
        float gv = Color.green(color);
        float bv = Color.blue(color);
        float av = Color.alpha(color);
        return new float[]{
                rv / 0xFF,
                gv / 0xFF,
                bv / 0xFF,
                av / 0xFF
        };
    }
}
