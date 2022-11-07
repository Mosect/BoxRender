package com.mosect.lib.boxrender;

import android.graphics.SurfaceTexture;
import android.view.Surface;

import com.mosect.lib.easygl.GLObject;
import com.mosect.lib.easygl.GLSurface;

/**
 * SurfaceTexture窗口，用于将图像输出到{@link SurfaceTexture SurfaceTexture}，
 * 适用于{@link com.mosect.lib.easygl.GLOutput GLOutput}内容
 */
public class TextureWindow implements GLSurface {

    private final SurfaceTexture texture;
    private final int width;
    private final int height;
    private Surface surface;

    public TextureWindow(SurfaceTexture texture, int width, int height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    @Override
    public void initContent(GLObject<?> object) {
        texture.setDefaultBufferSize(width, height);
        surface = new Surface(texture);
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
        if (null != surface) {
            surface.release();
            surface = null;
        }
    }
}
