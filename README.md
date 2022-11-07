# BoxRender
Android箱式渲染，基于EasyGL（OpenGL），只要用于绘制2d图形

# 指引

## 1. 引用项目
[![](https://jitpack.io/v/Mosect/BoxRender.svg)](https://jitpack.io/#Mosect/BoxRender)

## 2. 示例

### 2.1 创建并启动
```
// 1. 创建BoxRender并启动
BoxRender boxRender = new BoxRender(context);
boxRender.start();
// 2. 可以设置背景色
boxRender.getBackgroundObject().setContent(new BackgroundContent(Color.GREEN));
// 3. 需要监听fps变化，可以使用以下代码：
FpsObject fpsObject = new FpsObject(boxRender);
fpsObject.setOnFpsChangedListener(fpsValue -> {
    // 注意：此处是绘制线程，非主线程
});
fpsObject.create();
```

### 2.2 添加输出

**BoxRender必须包含一个主输出，其相关功能才能正常工作**

```
// 添加输出，可以多个，必须将其中一个设置成主输出：
// 有两种Window: SurfaceWindow和TextureWindow

// SurfaceWindow: 用于将图形输出到Surface
GLOutput<SurfaceWindow> surfaceOutput = boxRender.addOutput();
// 如果为主输出，需要调用
surfaceOutput.setMain();
// 如果Surface已准备好
SurfaceWindow surfaceWindow = new SurfaceWindow(surface, width, height);
surfaceOutput.setContent(surfaceWindow);
// Surface被销毁时，
surfaceOutput.setContent(null);
// 或者将GLOutput销毁
surfaceOutput.destroy();

// TextureWindow：用于将图形输出到SurfaceTexture
GLOutput<TextureWindow> textureOutput = boxRender.addOutput();
// 如果SurfaceTexture已准备好
TextureWindow textureWindow = new TextureWindow(surfaceTexture, width, height);
// 如果为主输出，需要调用
textureOutput.setMain();
// SurfaceTexture被销毁时，
textureOutput.setContent(null);
// 或者将GLOutput销毁
textureOutput.destroy();
```

### 2.3 添加绘制实体

目前仅实现了Rect2dObject，用于绘制2d矩形

```
// 1. 创建Rect2dObject
Rect2dObject obj1 = boxRender.addRect2d();
Rect2dObject obj2 = boxRender.addRect2d();
Rect2dObject obj3 = boxRender.addRect2d();
// 2. 设置矩形显示位置
obj1.setTransfer(
        // 需要4个三维坐标点，每个轴有效数值范围：[0.0, 1.0]，按比例计算
        // 左下角为原点，右上角为(1.0, 1.0)，靠近镜头z为0.0，最远z为1.0
        // 不设置位置信息，则默认铺满，且z为1.0
        new float[]{
                0.05f, 0.5f, 0.2f,
                0.5f, 0.5f, 0.2f,
                0.05f, 0.99f, 0.2f,
                0.5f, 0.99f, 0.2f,
        },
        // 需要4个二维坐标，表示要渲染的纹理范围，可以利用此坐标实现裁剪显示操作
        // 左下角为原点，右上角为(1.0, 1.0)
        // 不设置位置信息，则不裁剪
        new float[]{
                0, 0,
                1, 0,
                0, 1,
                1, 1
        }
);
// 创建完后仅仅为空内容对象，不会绘制任何东西，需要给Rect2dObject对象设置内容才能显示内容

// 绘制摄像头内容：
obj1.setContent(new CameraContent(){

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
});

// 绘制屏幕内容：
obj2.setContent(new ScreenContent(context, mediaProjection, "屏幕捕获"));

// 绘制图片：
// 创建图片内容，不同类型图片，请查看BitmapObject进行创建
PictureContent pictureContent = new PictureContent(BitmapObject.assetsBitmap(context, "dir/mypic.jpg"));
obj3.setContent(pictureContent);
```

### 2.4 销毁BoxRender

不再使用BoxRender，需要调用BoxRender.destroy进行销毁
```
boxRender.destroy();
```

