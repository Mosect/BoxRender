package com.mosect.lib.boxrender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 位图实体，用于产生位图
 * {@link PictureContent PictureContent}
 */
public interface BitmapObject {

    /**
     * 加载位图
     *
     * @return 位图对象
     */
    Bitmap loadBitmap();

    /**
     * 加载assets目录下位图
     *
     * @param context 上下文
     * @param name    资源名称
     * @return 位图实体
     */
    static BitmapObject assetsBitmap(Context context, String name) {
        return () -> {
            try (InputStream ins = context.getAssets().open(name)) {
                return BitmapFactory.decodeStream(ins);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * 加载resource位图实体
     *
     * @param context 上下文
     * @param res     资源id
     * @return 位图实体
     */
    static BitmapObject resBitmap(Context context, int res) {
        return () -> BitmapFactory.decodeResource(context.getResources(), res);
    }

    /**
     * 加载文件位图实体
     *
     * @param file 文件
     * @return 位图实体
     */
    static BitmapObject fileBitmap(File file) {
        return () -> {
            try (InputStream ins = new FileInputStream(file)) {
                return BitmapFactory.decodeStream(ins);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * 加载uri位图实体
     *
     * @param context 上下文
     * @param uri     uri
     * @return 位图实体
     */
    static BitmapObject uriBitmap(Context context, Uri uri) {
        return () -> {
            if (TextUtils.isEmpty(uri.getScheme()) || "file".equals(uri.getScheme())) {
                File file = new File(uri.getPath());
                return fileBitmap(file).loadBitmap();
            } else if ("content".equals(uri.getScheme())) {
                try (InputStream ins = context.getContentResolver().openInputStream(uri)) {
                    return BitmapFactory.decodeStream(ins);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("Unsupported uri: " + uri);
            }
        };
    }
}
