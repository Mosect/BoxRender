package com.mosect.lib.boxrender;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.mosect.lib.easygl.GLContent;
import com.mosect.lib.easygl.GLObject;

/**
 * SurfaceTexture内容，用于一些能将图像输出到SurfaceText对象地方，其输出图像又会被绘制到
 * {@link com.mosect.lib.easygl.GLOutput GLOutput}，
 * 只适用于{@link Rect2dObject Rect2dObject}，
 * 比如{@link CameraContent CameraContent}、{@link ScreenContent ScreenContent}，或者MediaCodec等
 */
public class SurfaceTextureContent implements GLContent {

    private Rect2dObject object;
    private SurfaceTexture surfaceTexture;
    private int textureID;
    private TextureOESShader textureOESShader;
    private final byte[] needUpdateLock = new byte[0];
    private boolean needUpdate;
    private float[] textureMatrix = null;

    @Override
    public void initContent(GLObject<?> object) {
        this.object = (Rect2dObject) object;
        textureOESShader = object.getEnv().requestShader(TextureOESShader.class);

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        textureID = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID);
        object.getEnv().checkGlError("glBindTexture");
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        object.getEnv().checkGlError("glTexParameter");
        surfaceTexture = new SurfaceTexture(textureID);
        surfaceTexture.setOnFrameAvailableListener(surfaceTexture -> {
            synchronized (needUpdateLock) {
                needUpdate = true;
            }
        });
    }

    @Override
    public void drawContent() {
        if (null != surfaceTexture) {
            synchronized (needUpdateLock) {
                if (needUpdate) {
                    needUpdate = false;
                    surfaceTexture.updateTexImage();
                }
            }
            object.getVerticesBuffer().position(0);
            object.getTextureCoordsBuffer().position(0);
            textureOESShader.draw(surfaceTexture, textureID, object.getProjection().getCameraMatrix(),
                    object.getVerticesBuffer(), object.getTextureCoordsBuffer());
        }
    }

    @Override
    public void destroyContent() {
        textureOESShader = null;
        if (null != surfaceTexture) {
            surfaceTexture.release();
            surfaceTexture = null;
        }
        if (textureID != 0) {
            GLES20.glDeleteTextures(1, new int[]{textureID}, 0);
            textureID = 0;
        }
    }

    public int getTextureID() {
        return textureID;
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }
}
