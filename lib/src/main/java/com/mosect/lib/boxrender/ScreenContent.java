package com.mosect.lib.boxrender;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.WindowManager;
import android.view.WindowMetrics;

import com.mosect.lib.easygl.GLObject;

/**
 * 屏幕捕获内容，用于绘制屏幕内容到{@link com.mosect.lib.easygl.GLOutput GLOutput}，
 * 只适用于{@link Rect2dObject Rect2dObject}
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenContent extends SurfaceTextureContent {

    private final Context context;
    private final MediaProjection mediaProjection;
    private final String name;
    private Surface surface;
    private VirtualDisplay virtualDisplay;

    public ScreenContent(Context context, MediaProjection mediaProjection, String name) {
        this.context = context;
        this.mediaProjection = mediaProjection;
        this.name = name;
    }

    @Override
    public void initContent(GLObject<?> object) {
        super.initContent(object);
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        getSurfaceTexture().setDefaultBufferSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
        surface = new Surface(getSurfaceTexture());
        virtualDisplay = mediaProjection.createVirtualDisplay(
                name,
                displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                surface, null, null
        );
    }

    @Override
    public void destroyContent() {
        if (null != virtualDisplay) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        if (null != surface) {
            surface.release();
            surface = null;
        }
        super.destroyContent();
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = wm.getMaximumWindowMetrics();
            Rect bounds = windowMetrics.getBounds();
            displayMetrics.densityDpi = context.getResources().getConfiguration().densityDpi;
            displayMetrics.widthPixels = bounds.width();
            displayMetrics.heightPixels = bounds.height();
        } else {
            wm.getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        return displayMetrics;
    }
}
