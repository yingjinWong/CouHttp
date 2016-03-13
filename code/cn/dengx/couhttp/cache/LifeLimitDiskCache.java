package cn.dengx.couhttp.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * life calculate since saving from download
 *
 * Current project:CouHttp.
 * Created by dengx on 16/3/10,16:12.
 */
public class LifeLimitDiskCache extends BaseDiskCache {
    private long life;

    public LifeLimitDiskCache(@NonNull Context context, @NonNull File cacheDir, long life) {
        super(context, cacheDir);
        if (life <= 0)
            throw new IllegalArgumentException();
        this.life = life;
    }

    @Override
    public Bitmap get(String key) {
        Bitmap bitmap = null;
        File image = new File(imageDir, key);
        if (image.exists() && image.isFile()) {
            long modified = image.lastModified();
            long curr = System.currentTimeMillis();
            if (life > curr - modified) {
                bitmap = super.get(key);
            } else {
                remove(key);
            }
        }
        return bitmap;
    }
}
