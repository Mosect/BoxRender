package com.mosect.lib.boxrender;

import android.opengl.Matrix;

/**
 * 箱式投影
 */
public class BoxProjection {

    private final float left;
    private final float right;
    private final float bottom;
    private final float top;
    private final float near;
    private final float far;
    private final float[] cameraMatrix = new float[16];

    public BoxProjection(float left, float right, float bottom, float top, float near, float far) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        this.near = near;
        this.far = far;
        Matrix.orthoM(cameraMatrix, 0, left, right, bottom, top, near, far);
    }

    public float getLeft() {
        return left;
    }

    public float getRight() {
        return right;
    }

    public float getTop() {
        return top;
    }

    public float getBottom() {
        return bottom;
    }

    public float getNear() {
        return near;
    }

    public float getFar() {
        return far;
    }

    public float[] getCameraMatrix() {
        return cameraMatrix;
    }
}
