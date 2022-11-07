package com.mosect.lib.boxrender;

import com.mosect.lib.easygl.GLContent;
import com.mosect.lib.easygl.GLEnv;
import com.mosect.lib.easygl.GLObject;

/**
 * 监听FPS变化实体
 */
public class FpsObject extends GLObject<GLContent> {

    private int fps = 0;
    private long startDrawTime = -1;
    private int currentFps;
    private OnFpsChangedListener onFpsChangedListener;

    public FpsObject(GLEnv env) {
        super(env);
    }

    @Override
    protected void onFrameEnd() {
        super.onFrameEnd();
        if (startDrawTime < 0) {
            startDrawTime = System.nanoTime();
        }
        ++fps;
        long now = System.nanoTime();
        if (now - startDrawTime > 1000000000L) {
            startDrawTime = now;
            setCurrentFps(fps);
            fps = 0;
        }
    }

    private void setCurrentFps(int currentFps) {
        if (this.currentFps != currentFps) {
            this.currentFps = currentFps;
            OnFpsChangedListener listener = this.onFpsChangedListener;
            if (null != listener) {
                listener.onFpsChanged(currentFps);
            }
        }
    }

    public int getCurrentFps() {
        return currentFps;
    }

    public void setOnFpsChangedListener(OnFpsChangedListener onFpsChangedListener) {
        this.onFpsChangedListener = onFpsChangedListener;
    }

    public interface OnFpsChangedListener {

        void onFpsChanged(int fpsValue);
    }
}
