package com.mosect.lib.boxrender;

import android.content.Context;

import com.mosect.lib.easygl.GLContent;
import com.mosect.lib.easygl.GLEnv;
import com.mosect.lib.easygl.GLShader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 加载assets目录下shader实体
 */
public abstract class AssetsShader extends GLShader<GLContent> {

    private final Context context;
    private final String vertSourceName;
    private final String fragSourceName;

    public AssetsShader(GLEnv env, Context context, String vertSourceName, String fragSourceName) {
        super(env);
        this.context = context;
        this.vertSourceName = vertSourceName;
        this.fragSourceName = fragSourceName;
    }

    @Override
    protected String onLoadVertSource() {
        return loadAssets(vertSourceName);
    }

    @Override
    protected String onLoadFragSource() {
        return loadAssets(fragSourceName);
    }

    private String loadAssets(String name) {
        try (InputStream ins = context.getAssets().open(name)) {
            ByteArrayOutputStream temp = new ByteArrayOutputStream();
            byte[] buffer = new byte[128];
            int len;
            while ((len = ins.read(buffer)) > 0) {
                temp.write(buffer, 0, len);
            }
            return temp.toString("utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
