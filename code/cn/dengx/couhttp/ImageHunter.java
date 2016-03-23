package cn.dengx.couhttp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import cn.dengx.couhttp.cache.DiskCache;
import cn.dengx.couhttp.cache.LruCache;
import cn.dengx.couhttp.cache.NameGenerator;
import cn.dengx.couhttp.utils.BitmapUtil;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/9,14:44.
 */
public class ImageHunter extends Hunter {

    private LruCache<String, Bitmap> lruCache;

    private DiskCache diskCache;

    private NameGenerator generator;

    public ImageHunter(@NonNull Request request, @NonNull Config config, @NonNull ByteBufferPool pool,
                       @NonNull Handler handler, LruCache<String, Bitmap> lruCache,
                       DiskCache diskCache, @NonNull NameGenerator generator, @NonNull Context context) {
        super(request, config, pool, handler, context);
        this.lruCache = lruCache;
        this.diskCache = diskCache;
        this.generator = generator;
    }

    @Override
    public void readDateOnConnected(InputStream in, ByteBuffer buffer, Entry entry) throws IOException {

        int length = tryGetLength(entry);
        getProgressImage(in, buffer, entry, length, (ImageRequest) getRequest());
    }

    private void getProgressImage(InputStream in, ByteBuffer buffer, Entry entry,
                                  int length, ImageRequest imageRequest) throws IOException {
        byte[] bytes = readDateAndShowProgress(in, buffer, imageRequest, length, getHandler());
        checkContentLength(bytes, length);
        if (bytes != null) {
            int width = imageRequest.getWidth();
            int height = imageRequest.getHeight();
            if (width <= 0 || height <= 0) {
                entry.body = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                int factW = options.outWidth;
                int factH = options.outHeight;
                String mineType = options.outMimeType;
                if (CouLog.debug)
                    CouLog.i("url " + imageRequest.getUrl() + " fact width=" + factW + " fact height="
                            + factH + " mineType=" + mineType);
                BitmapUtil.calculateInSampleSize(width, height, factW, factH, options);
                entry.body = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            }
        }
    }


    @Override
    public void beforeConnect(ByteBuffer buffer, Entry entry) {
        Bitmap bitmap;
        String key = generator.generate(getTextWithBound(getRequest().getUrl()));
        if (CouLog.debug)
            CouLog.i("split url " + generator.toString());
        bitmap = getFromLruCache(key);
        if (bitmap == null)
            bitmap = getFromDisk(key);

        if (bitmap != null) {
            entry.body = bitmap;
            entry.code = HttpURLConnection.HTTP_OK;
            if (CouLog.debug)
                CouLog.i("get bitmap from cache");
        }
    }

    @Override
    public void beforeConnectionClose(InputStream in, ByteBuffer buffer, Entry entry) throws IOException {
        super.beforeConnectionClose(in, buffer, entry);
        Bitmap bitmap = null;
        if (entry.body != null) {
            if (entry.body instanceof Bitmap)
                bitmap = (Bitmap) entry.body;
        }
        if (bitmap != null) {

            Request request = getRequest();
            if (request instanceof ImageRequest) {
                ImageRequest imageRequest = (ImageRequest) request;
                ImageRequest.BitmapProcessor process = imageRequest.getBitmapProcessor();
                if (process != null) { // process bitmap
                    bitmap = process.process(bitmap);
                    entry.body = bitmap;
                }
            }

            String key = generator.generate(getTextWithBound(getRequest().getUrl()));
            putLruCache(key, bitmap);
            putDiskCache(key, bitmap);
        }
    }

    private String getTextWithBound(String text) {
        Request request = getRequest();
        ImageRequest imageRequest = null;
        if (request instanceof ImageRequest) {
            imageRequest = (ImageRequest) request;
        }
        if (imageRequest != null) {
            int w = imageRequest.getWidth();
            int h = imageRequest.getHeight();
            if (w > 0 && h > 0) {
                text = w + "*" + h + "-" + text;
            }
        }
        return text;
    }

    private Bitmap getFromLruCache(String key) {
        Bitmap bitmap = null;
        if (lruCache != null) {
            bitmap = lruCache.get(key);
        }
        return bitmap;
    }

    private Bitmap getFromDisk(String key) {
        Bitmap bitmap = null;
        if (diskCache != null) {
            bitmap = diskCache.get(key);
        }
        return bitmap;
    }

    private void putLruCache(String key, Bitmap bitmap) {
        if (lruCache != null) {
            lruCache.put(key, bitmap);
            if (CouLog.debug)
                CouLog.i("put lruCache key=" + key);
        }
    }

    private void putDiskCache(String key, Bitmap bitmap) {
        if (diskCache != null) {
            diskCache.put(key, bitmap);
            if (CouLog.debug)
                CouLog.i("put disk cache key=" + key);
        }
    }
}
