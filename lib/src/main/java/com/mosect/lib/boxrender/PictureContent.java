package com.mosect.lib.boxrender;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.mosect.lib.easygl.GLContent;
import com.mosect.lib.easygl.GLObject;

/**
 * 图片内容，只能用于{@link Rect2dObject Rect2dObject}
 */
public class PictureContent implements GLContent {

    private Rect2dObject object;
    private final BitmapObject bitmapObject;
    private Bitmap bitmap;
    private int textureID;
    private Texture2dShader texture2dShader;
    private float[] textureMatrix;

    public PictureContent(BitmapObject bitmapObject) {
        this.bitmapObject = bitmapObject;
    }

    @Override
    public void initContent(GLObject<?> object) {
//        Log.d(TAG, "initContent: ");
        this.object = (Rect2dObject) object;
        textureMatrix = new float[16];
        Matrix.setIdentityM(textureMatrix, 0);

        bitmap = bitmapObject.loadBitmap();
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        textureID = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
        object.getEnv().checkGlError("glBindTexture");
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        object.getEnv().checkGlError("glTexParameter");
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        texture2dShader = object.getEnv().requestShader(Texture2dShader.class);
    }

    @Override
    public void drawContent() {
        if (null != texture2dShader && textureID != 0 && texture2dShader.isValid()) {
//            Log.d(TAG, "drawContent: " + textureID);
            float[] cameraMatrix = object.getProjection().getCameraMatrix();
            object.getVerticesBuffer().position(0);
            object.getTextureCoordsBuffer().position(0);
            texture2dShader.draw(textureID, cameraMatrix, textureMatrix, object.getVerticesBuffer(), object.getTextureCoordsBuffer());
        }
    }

    @Override
    public void destroyContent() {
//        Log.d(TAG, "clearContent: ");
        textureMatrix = null;
        texture2dShader = null;
        if (textureID != 0) {
            GLES20.glDeleteTextures(1, new int[]{textureID}, 0);
            textureID = 0;
        }
        if (null != bitmap) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
