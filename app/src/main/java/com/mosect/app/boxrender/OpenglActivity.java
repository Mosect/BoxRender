package com.mosect.app.boxrender;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mosect.lib.boxrender.BackgroundContent;
import com.mosect.lib.boxrender.BitmapObject;
import com.mosect.lib.boxrender.BoxRender;
import com.mosect.lib.boxrender.FpsObject;
import com.mosect.lib.boxrender.PictureContent;
import com.mosect.lib.boxrender.Rect2dObject;
import com.mosect.lib.boxrender.ScreenContent;
import com.mosect.lib.boxrender.SurfaceWindow;
import com.mosect.lib.boxrender.TextureWindow;
import com.mosect.lib.easygl.GLOutput;

public class OpenglActivity extends AppCompatActivity {

    private static OpenglActivity current;

    public static OpenglActivity getCurrent() {
        return current;
    }

    private BoxRender boxRender;
    private Rect2dObject captureMaterial;
    private Rect2dObject cameraMaterial;
    private Intent serviceIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current = this;

        ActivityResultLauncher<String> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        cameraMaterial.setContent(new SimpleCameraContent());
                    }
                }
        );

        ActivityResultLauncher<Intent> mpLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        serviceIntent = new Intent(this, CaptureService.class);
                        serviceIntent.putExtra("data", result.getData());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(serviceIntent);
                        } else {
                            startService(serviceIntent);
                        }
                    }
                }
        );

        boxRender = new BoxRender(getApplicationContext());
        boxRender.setFps(60);
        // 设置背景色
        boxRender.getBackgroundObject().setContent(new BackgroundContent(Color.GREEN));
        boxRender.start();

        setContentView(R.layout.activity_opengl);
        ScaleLayout lyContent1 = findViewById(R.id.ly_content1);
        addSurfaceOutput(lyContent1, 4 / 3f, true);
        ScaleLayout lyContent2 = findViewById(R.id.ly_content2);
        addSurfaceOutput(lyContent2, 2 / 3f, false);
        ScaleLayout lyContent3 = findViewById(R.id.ly_content3);
        addTextureOutput(lyContent3, 4 / 3f, false);

        TextView tvFps = findViewById(R.id.tv_fps);
        FpsObject fpsObject = new FpsObject(boxRender);
        fpsObject.setOnFpsChangedListener(fpsValue -> {
            runOnUiThread(() -> {
                String text = "FPS: " + fpsValue;
                tvFps.setText(text);
            });
        });
        fpsObject.create();

        showPicture("test_pic1.jpg", 0, 0, 1f);
        showPicture("test_pic2.jpg", 0.15f, 0.15f, 0.9f);
        showPicture("test_pic1.jpg", 0.2f, 0.2f, 0.8f);

        captureMaterial = boxRender.addRect2d();
        captureMaterial.setTransfer(
                new float[]{
                        0.05f, 0.5f, 0.2f,
                        0.5f, 0.5f, 0.2f,
                        0.05f, 0.99f, 0.2f,
                        0.5f, 0.99f, 0.2f,
                },
                new float[]{
                        0, 0,
                        1, 0,
                        0, 1,
                        1, 1
                }
        );

        cameraMaterial = boxRender.addRect2d();
        cameraMaterial.setTransfer(
                new float[]{
                        0.5f, 0f, 0.1f,
                        1f, 0f, 0.1f,
                        0.5f, 0.5f, 0.1f,
                        1f, 0.5f, 0.1f,
                },
                new float[]{
                        0, 0,
                        1, 0,
                        0, 1,
                        1, 1
                }
        );

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            MediaProjectionManager mpm = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            Intent intent = mpm.createScreenCaptureIntent();
            mpLauncher.launch(intent);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraMaterial.setContent(new SimpleCameraContent());
        } else {
            cameraLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void addTextureOutput(ScaleLayout root, float ratio, boolean main) {
        root.setScale(ratio);
        ScaleLayout.LayoutParams lp = new ScaleLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        TextureView textureView = new TextureView(this);
        textureView.setKeepScreenOn(true);
        textureView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            private GLOutput<TextureWindow> output;

            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                output = boxRender.addOutput();
                if (main) output.setMain();
                TextureWindow textureWindow = new TextureWindow(surface, width, height);
                output.setContent(textureWindow);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                TextureWindow textureWindow = new TextureWindow(surface, width, height);
                output.setContent(textureWindow);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                if (null != output) {
                    output.destroy();
                    output = null;
                }
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            }
        });
        root.addView(textureView, lp);
    }

    private void addSurfaceOutput(ScaleLayout root, float ratio, boolean main) {
        root.setScale(ratio);
        ScaleLayout.LayoutParams lp = new ScaleLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        SurfaceView surfaceView = new SurfaceView(this);
        surfaceView.setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

            private GLOutput<SurfaceWindow> output;

            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                output = boxRender.addOutput();
                if (main) {
                    output.setMain();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                float ratio = width / (float) height;
                SurfaceWindow surfaceWindow = new SurfaceWindow(holder.getSurface(), width, height, ratio);
                output.setContent(surfaceWindow);
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                output.destroy();
                output = null;
            }
        });
        root.addView(surfaceView, lp);
    }

    private void showPicture(String name, float offsetX, float offsetY, float z) {
        Rect2dObject pictureObject = boxRender.addRect2d();
        pictureObject.setTransfer(
                new float[]{
                        0.1f + offsetX, 0.15f + offsetY, z,
                        0.7f + offsetX, 0.15f + offsetY, z,
                        0.1f + offsetX, 0.5f + offsetY, z,
                        0.7f + offsetX, 0.5f + offsetY, z,
                },
                new float[]{
                        0, 0,
                        1, 0,
                        0, 1,
                        1, 1
                }
        );
        PictureContent pictureContent = new PictureContent(BitmapObject.assetsBitmap(getApplicationContext(), name));
        pictureObject.setContent(pictureContent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (current == this) {
            current = null;
        }
        if (null != serviceIntent) {
            stopService(serviceIntent);
        }
        boxRender.destroy();
    }

    public void setMediaProjection(MediaProjection mediaProjection) {
        ScreenContent screenContent = new ScreenContent(this, mediaProjection, "屏幕捕获");
        captureMaterial.setContent(screenContent);
    }
}
