package com.mosect.lib.boxrender;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.mosect.lib.easygl.GLEnv;

import java.nio.Buffer;

/**
 * 2d oes渲染，用于渲染SurfaceTexture
 */
public class TextureOESShader extends AssetsShader {

    private int uniCameraMatrix;
    private int uniTextureMatrix;
    private int attrPosition;
    private int attrTextureCoord;
    private final float[] textureMatrix = new float[16];

    public TextureOESShader(GLEnv env, Context context) {
        super(env, context, "boxrender/shader_texture2d.vert", "boxrender/shader_texture_oes.frag");
    }

    @Override
    protected void onLinkProgramBefore() {
        super.onLinkProgramBefore();
        attrTextureCoord = 1;
        GLES20.glBindAttribLocation(getProgramId(), attrTextureCoord, "textureCoord");
    }

    @Override
    protected void onInitProgram() {
        uniCameraMatrix = getUniformLocation("cameraMatrix");
        uniTextureMatrix = getUniformLocation("textureMatrix");
        attrPosition = getAttribLocation("position");
    }

    public void draw(SurfaceTexture texture, int textureID, float[] cameraMatrix, Buffer vertices, Buffer textureCoords) {
        // 不能缓存纹理矩阵，每次都需要从SurfaceTexture中获取，保证不会出现画面异常
        texture.getTransformMatrix(textureMatrix);
        // SurfaceTexture纹理与普通2d纹理不一样，需要翻转Y轴
        Matrix.scaleM(textureMatrix, 0, 1, -1, 1);
        Matrix.translateM(textureMatrix, 0, 0, -1, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID);

        GLES20.glUseProgram(getProgramId());
        getEnv().checkGlError("glUseProgram");

        GLES20.glVertexAttribPointer(attrPosition, 3, GLES20.GL_FLOAT, false, 12, vertices);
        getEnv().checkGlError("glVertexAttribPointer attrPosition");
        GLES20.glEnableVertexAttribArray(attrPosition);
        getEnv().checkGlError("glEnableVertexAttribArray attrPosition");

        GLES20.glVertexAttribPointer(attrTextureCoord, 2, GLES20.GL_FLOAT, false, 8, textureCoords);
        getEnv().checkGlError("glVertexAttribPointer attrTextureCoord");
        GLES20.glEnableVertexAttribArray(attrTextureCoord);
        getEnv().checkGlError("glEnableVertexAttribArray attrTextureCoord");

        GLES20.glUniformMatrix4fv(uniCameraMatrix, 1, false, cameraMatrix, 0);
        getEnv().checkGlError("glUniformMatrix4fv/uniCameraMatrix");
        GLES20.glUniformMatrix4fv(uniTextureMatrix, 1, false, textureMatrix, 0);
        getEnv().checkGlError("glUniformMatrix4fv/uniTextureMatrix");

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        getEnv().checkGlError("glDrawArrays");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }
}
