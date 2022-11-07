package com.mosect.app.boxrender;

import android.graphics.ImageFormat;
import android.hardware.Camera;

import com.mosect.lib.boxrender.CameraContent;

import java.util.List;

public class SimpleCameraContent extends CameraContent {

    @SuppressWarnings("deprecation")
    @Override
    protected Camera openCamera() {
        Camera camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = previewSizes.get(0);
        for (Camera.Size size : previewSizes) {
            int v1 = Math.abs(size.height - 1080);
            int v2 = Math.abs(previewSize.height - 1080);
            if (v1 < v2) previewSize = size;
        }
        List<int[]> previewFpsRanges = parameters.getSupportedPreviewFpsRange();
        int[] fpsRange = previewFpsRanges.get(0);
        for (int[] fr : previewFpsRanges) {
            if (fr[1] > fpsRange[1]) fpsRange = fr;
        }
        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setPreviewFpsRange(fpsRange[0], fpsRange[1]);
        camera.setParameters(parameters);
        return camera;
    }
}
