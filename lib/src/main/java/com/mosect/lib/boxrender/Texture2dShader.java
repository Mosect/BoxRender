package com.mosect.lib.boxrender;

import android.content.Context;
import android.opengl.GLES20;

import com.mosect.lib.easygl.GLEnv;

import java.nio.Buffer;

/**
 * 2d渲染shader，用于渲染位图
 */
public class Texture2dShader extends AssetsShader {

    private int uniCameraMatrix;
    private int uniTextureMatrix;
    private int attrPosition;
    private int attrTextureCoord;
    private int uniTextureNum;

    public Texture2dShader(GLEnv env, Context context) {
        super(env, context, "boxrender/shader_texture2d.vert", "boxrender/shader_texture2d.frag");
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
        uniTextureNum = getUniformLocation("textureNum");
    }

    public void draw(int textureID, float[] cameraMatrix, float[] textureMatrix, Buffer vertices, Buffer textureCoords) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

        GLES20.glUseProgram(getProgramId());
        getEnv().checkGlError("glUseProgram");

        GLES20.glVertexAttribPointer(attrPosition, 3, GLES20.GL_FLOAT, false, 12, vertices);
        getEnv().checkGlError("glVertexAttribPointer/attrPosition");
        GLES20.glEnableVertexAttribArray(attrPosition);
        getEnv().checkGlError("glEnableVertexAttribArray/attrPosition");

        GLES20.glVertexAttribPointer(attrTextureCoord, 2, GLES20.GL_FLOAT, false, 8, textureCoords);
        getEnv().checkGlError("glVertexAttribPointer/attrTextureCoord");
        GLES20.glEnableVertexAttribArray(attrTextureCoord);
        getEnv().checkGlError("glEnableVertexAttribArray/attrTextureCoord");

        GLES20.glUniformMatrix4fv(uniCameraMatrix, 1, false, cameraMatrix, 0);
        getEnv().checkGlError("glUniformMatrix4fv/uniCameraMatrix");
        GLES20.glUniformMatrix4fv(uniTextureMatrix, 1, false, textureMatrix, 0);
        getEnv().checkGlError("glUniformMatrix4fv/uniTextureMatrix");
        GLES20.glUniform1i(uniTextureNum, 0);
        getEnv().checkGlError("glUniformMatrix4fv/uniTextureNum");

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        getEnv().checkGlError("glDrawArrays");
    }
}
