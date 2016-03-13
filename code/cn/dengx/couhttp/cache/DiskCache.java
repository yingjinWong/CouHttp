package cn.dengx.couhttp.cache;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/9,21:38.
 */
public interface DiskCache {
    File getCacheDir();

    Bitmap get(String key);

    boolean put(String key, Bitmap bitmap);

    /**
     * Returns the current size of the cache in bytes.
     */
    long size();

    void remove(String key);

    void clear();

}
