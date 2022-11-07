package com.mosect.lib.boxrender;

import android.content.Context;
import android.graphics.Color;

import com.mosect.lib.easygl.GLEnv;
import com.mosect.lib.easygl.GLObject;
import com.mosect.lib.easygl.GLOutput;
import com.mosect.lib.easygl.GLSurface;

/**
 * 箱式渲染，所有素材必须放在{ left=0, right=1, bottom=0, top=1, near=0, far=1 }空间中才能渲染
 */
public class BoxRender extends GLEnv {

    private final GLObject<BackgroundContent> backgroundObject;
    // 创建投影，注意：near不能为0，必须大于0，所以设置成near=0.5，far=1.5
    private final BoxProjection projection = new BoxProjection(0, 1, 0, 1, 0.5f, 1.5f);

    public BoxRender(Context context) {
        // 需要创建以下shader
        new TextureOESShader(this, context).create();
        new Texture2dShader(this, context).create();

        // 添加背景色实体
        backgroundObject = new GLObject<>(this);
        backgroundObject.setName("background");
        backgroundObject.create();
        backgroundObject.setContent(new BackgroundContent(Color.BLACK));
    }

    /**
     * 快速添加输出
     *
     * @param <T> 输出内容类型
     * @return 输出对象
     */
    public <T extends GLSurface> GLOutput<T> addOutput() {
        GLOutput<T> output = new GLOutput<>(this);
        output.create();
        return output;
    }

    /**
     * 添加2d矩形
     *
     * @return 2d矩形实体对象
     */
    public Rect2dObject addRect2d() {
        Rect2dObject object = new Rect2dObject(this, projection);
        object.create();
        return object;
    }

    public BoxProjection getProjection() {
        return projection;
    }

    public GLObject<BackgroundContent> getBackgroundObject() {
        return backgroundObject;
    }
}
