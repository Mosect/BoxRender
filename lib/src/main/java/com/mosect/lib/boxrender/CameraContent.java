package com.mosect.lib.boxrender;

import android.hardware.Camera;

import com.mosect.lib.easygl.GLObject;

/**
 * 摄像头内容，用于绘制摄像头图像，只适用于{@link Rect2dObject Rect2dObject}
 */
public abstract class CameraContent extends SurfaceTextureContent {

    private Camera camera;

    @Override
    public void initContent(GLObject<?> object) {
        super.initContent(object);
        camera = openCamera();
        if (null != camera) {
            try {
                camera.setPreviewTexture(getSurfaceTexture());
                camera.startPreview();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void destroyContent() {
        super.destroyContent();
        if (null != camera) {
            onCloseCamera();
        }
    }

    /**
     * 关闭摄像头
     */
    protected void onCloseCamera() {
        camera.release();
        camera = null;
    }

    /**
     * 打开摄像头，需要在此方法打开并初始化摄像头
     *
     * @return 摄像头对象
     */
    protected abstract Camera openCamera();
}
