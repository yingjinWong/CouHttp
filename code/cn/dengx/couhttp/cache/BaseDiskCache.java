package cn.dengx.couhttp.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.dengx.couhttp.CouLog;
import cn.dengx.couhttp.utils.BitmapUtil;
import cn.dengx.couhttp.utils.DiskUtil;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/10,11:23.
 */
public class BaseDiskCache implements DiskCache {

    private Context context;
    private File cacheDir;
    protected File tempDir;
    protected File imageDir;

    public BaseDiskCache(@NonNull Context context, @NonNull File cacheDir) {
        this.context = context;
        this.cacheDir = cacheDir;
        tempDir = new File(cacheDir, DiskUtil.TEMP);
        if (!tempDir.exists())
            tempDir.mkdirs();
        imageDir = new File(cacheDir, DiskUtil.IMAGE);
        if (!imageDir.exists())
            imageDir.exists();
    }

    @Override
    public File getCacheDir() {
        return cacheDir;
    }

    @Override
    public Bitmap get(String key) {
        if (!imageDir.exists())
            imageDir.mkdirs();
        File image = new File(imageDir, key);
        Bitmap bitmap = null;
        if (image.exists() && image.isFile()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(image);
                int length = (int) image.length();
                byte[] bytes = new byte[length];
                in.read(bytes);
                CouLog.i("file " + image + " length : " + length);
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (FileNotFoundException e) {
                //ignore
            } catch (IOException e) {
                CouLog.e("get image from disk cache read data error", e);
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return bitmap;
    }

    @Override
    public boolean put(String key, Bitmap bitmap) {
        boolean ok = false;
        File image = new File(tempDir, key);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(image);
            ok = bitmap.compress(BitmapUtil.getCompressFormat(key), 100, out);
            if (ok) {
                DiskUtil.move(image, imageDir);
                File f = new File(imageDir, key);
                if (f.exists() && f.isFile())
                    f.setLastModified(System.currentTimeMillis());
            }
        } catch (FileNotFoundException e) {
            //ignore
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return ok;
    }

    @Override
    public long size() {
        return DiskUtil.size(imageDir) + DiskUtil.size(tempDir);
    }

    @Override
    public void remove(String key) {
        File image = new File(imageDir, key);
        if (image.exists() && image.isFile())
            image.delete();
    }

    @Override
    public void clear() {
        DiskUtil.clear(tempDir);
        DiskUtil.clear(imageDir);
    }

}
