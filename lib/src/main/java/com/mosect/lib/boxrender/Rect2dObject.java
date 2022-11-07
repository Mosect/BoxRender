package com.mosect.lib.boxrender;

import android.graphics.Matrix;

import com.mosect.lib.easygl.GLContent;
import com.mosect.lib.easygl.GLEnv;
import com.mosect.lib.easygl.GLObject;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 2D矩形实体，用于绘制一个矩形2d图像
 */
public class Rect2dObject extends GLObject<GLContent> {

    private final BoxProjection projection;
    private final ByteBuffer verticesBuffer = ByteBuffer.allocateDirect(12 * 4);
    private final ByteBuffer textureCoordsBuffer = ByteBuffer.allocateDirect(8 * 4);
    private final Matrix texturePositionMatrix;
    private boolean enabled = true;

    public Rect2dObject(GLEnv env, BoxProjection projection) {
        super(env);
        this.projection = projection;
        this.texturePositionMatrix = new Matrix();
        texturePositionMatrix.postScale(1, -1);
        texturePositionMatrix.postTranslate(0, 1);
        verticesBuffer.order(ByteOrder.nativeOrder());
        textureCoordsBuffer.order(ByteOrder.nativeOrder());
        // 默认铺满
        setTransferNow(
                new float[]{
                        0, 0, 1,
                        1, 0, 1,
                        0, 1, 1,
                        1, 1, 1
                },
                new float[]{
                        0, 0,
                        1, 0,
                        0, 1,
                        1, 1
                }
        );
    }

    @Override
    protected void onGLInitContent() {
        super.onGLInitContent();
        GLContent source = getContent();
        if (null != source) {
            source.initContent(this);
        }
    }

    @Override
    protected void onGLClearContent() {
        super.onGLClearContent();
        GLContent source = getContent();
        if (null != source) {
            source.destroyContent();
        }
    }

    @Override
    protected void onGLDraw() {
        super.onGLDraw();
        GLContent source = getContent();
        if (null != source && isEnabled()) {
            source.drawContent();
        }
    }

    public void setTransfer(float[] vertices, float[] textureCoords) {
        getEnv().runGLAction(() -> setTransferNow(vertices, textureCoords));
    }

    private void setTransferNow(float[] vertices, float[] textureCoords) {
        transferPosition3D(vertices);
        texturePositionMatrix.mapPoints(textureCoords);
        verticesBuffer.position(0);
        for (float v : vertices) {
            verticesBuffer.putFloat(v);
        }
        textureCoordsBuffer.position(0);
        for (float v : textureCoords) {
            textureCoordsBuffer.putFloat(v);
        }
    }

    private void transferPosition3D(float[] data) {
        float width = projection.getRight() - projection.getLeft();
        float height = projection.getTop() - projection.getBottom();
        float zLen = projection.getFar() - projection.getNear();
        for (int i = 0; i < data.length; i += 3) {
            float x = data[i];
            float y = data[i + 1];
            float z = data[i + 2];
            float fx = projection.getLeft() + width * x;
            float fy = projection.getBottom() + height * y;
            float fz = -projection.getNear() - zLen * z;
            data[i] = fx;
            data[i + 1] = fy;
            data[i + 2] = fz;
        }
    }

    public Buffer getVerticesBuffer() {
        return verticesBuffer;
    }

    public Buffer getTextureCoordsBuffer() {
        return textureCoordsBuffer;
    }

    public BoxProjection getProjection() {
        return projection;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
