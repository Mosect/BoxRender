package com.mosect.lib.boxrender;

import android.view.Surface;

import com.mosect.lib.easygl.GLObject;
import com.mosect.lib.easygl.GLSurface;

/**
 * Surface窗口，用于将图像输出到{@link Surface Surface}，
 * 适用于{@link com.mosect.lib.easygl.GLOutput GLOutput}内容
 */
public class SurfaceWindow implements GLSurface {

    private final Surface surface;
    private final int width;
    private final int height;
    private final float ratio;

    public SurfaceWindow(Surface surface, int width, int height, float ratio) {
        this.surface = surface;
        this.width = width;
        this.height = height;
        this.ratio = ratio;
    }

    @Override
    public void initContent(GLObject<?> object) {
    }

    @Override
    public void drawContent() {
    }

    @Override
    public Object getWindowObject() {
        return surface;
    }

    @Override
    public int getWindowWidth() {
        return width;
    }

    @Override
    public int getWindowHeight() {
        return height;
    }

    @Override
    public void destroyContent() {
    }

    public Surface getSurface() {
        return surface;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getRatio() {
        return ratio;
    }
}
