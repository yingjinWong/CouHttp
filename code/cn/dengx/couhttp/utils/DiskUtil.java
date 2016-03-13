package cn.dengx.couhttp.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.os.EnvironmentCompat;

import java.io.File;

import cn.dengx.couhttp.CouLog;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/10,11:24.
 */
public class DiskUtil {

    public static final long MINI_SIZE = 1024 * 1024 * 5;//5M
    public static final int MINI_SIZE_INT = 1024 * 1024 * 5;//5M

    public static final String DIRECTORY_NAME = "CouHttp";
    public static final String TEMP = "temp";
    public static final String IMAGE = "image";

    public static File getDiskCacheDirectory(Context context) {
        if (context == null)
            throw new IllegalArgumentException();
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null)
            cacheDir = context.getCacheDir();
        String state = EnvironmentCompat.getStorageState(cacheDir);
        if (state.equals(Environment.MEDIA_UNMOUNTED))
            cacheDir = null;
        if (cacheDir == null)
            return null;
        long freeSize = calculateFreeSize(cacheDir.getAbsolutePath());
        CouLog.i("the device free size "+freeSize);
        if (freeSize < MINI_SIZE)
            return null;
        File directory = new File(cacheDir, DIRECTORY_NAME);
        if (!directory.exists())
            directory.mkdirs();
        return directory;
    }

    public static long calculateFreeSize(String path) {
        if (path == null)
            throw new IllegalArgumentException();
        StatFs statFs = new StatFs(path);
        long size;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            size = statFs.getFreeBytes();
        else
            size = statFs.getFreeBlocks() * statFs.getBlockSize();
        return size;
    }

    public static int calculateMemoryCacheSize(Context context) {
        if (context == null)
            throw new IllegalArgumentException();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int clazz;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            clazz = am.getLargeMemoryClass();
        else
            clazz = am.getMemoryClass();
        return 1024 * 1024 * clazz / 8;
    }

    public static boolean move(File srcFile, File dstDir) {
        if (!srcFile.exists() || !srcFile.isFile())
            return false;
        if (!dstDir.exists())
            dstDir.mkdirs();
        return srcFile.renameTo(new File(dstDir, srcFile.getName()));
    }

    public static boolean clear(File dir) {
        if (dir == null)
            return true;
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile())
                        f.delete();
                    if (f.isDirectory())
                        clear(f);
                }
            }
        }
        return dir.delete();
    }

    public static long size(File dir) {
        if (dir == null)
            throw new IllegalArgumentException();
        int size = 0;
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile())
                        size += f.length();
                    if (f.isDirectory())
                        size += size(f);
                }
            }
        }
        return size;
    }

}
