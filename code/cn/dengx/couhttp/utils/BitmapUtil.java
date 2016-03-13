package cn.dengx.couhttp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.graphics.BitmapCompat;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.dengx.couhttp.CouLog;
import cn.dengx.couhttp.cache.NameGenerator;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/6,13:58.
 */
public class BitmapUtil {

    //cope from picasso ---start------
    /* WebP file header
     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |      'R'      |      'I'      |      'F'      |      'F'      |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                           File Size                           |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |      'W'      |      'E'      |      'B'      |      'P'      |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    */
    private static final int WEBP_FILE_HEADER_SIZE = 12;
    private static final String WEBP_FILE_HEADER_RIFF = "RIFF";
    private static final String WEBP_FILE_HEADER_WEBP = "WEBP";
    //cope from picasso  -----end-------


    private BitmapUtil() {
    }

    //cope from picasso ---start------

    public static byte[] toByteArray(@NonNull InputStream input) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        while (-1 != (n = input.read(buffer))) {
            byteArrayOutputStream.write(buffer, 0, n);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static boolean isWebPFile(@NonNull InputStream stream) throws IOException {
        byte[] fileHeaderBytes = new byte[WEBP_FILE_HEADER_SIZE];
        boolean isWebPFile = false;
        if (stream.read(fileHeaderBytes, 0, WEBP_FILE_HEADER_SIZE) == WEBP_FILE_HEADER_SIZE) {
            // If a file's header starts with RIFF and end with WEBP, the file is a WebP file
            isWebPFile = WEBP_FILE_HEADER_RIFF.equals(new String(fileHeaderBytes, 0, 4, "US-ASCII"))
                    && WEBP_FILE_HEADER_WEBP.equals(new String(fileHeaderBytes, 8, 4, "US-ASCII"));
        }
        return isWebPFile;
    }

    public static void calculateInSampleSize(int reqWidth, int reqHeight, int width, int height,
                                             @NonNull BitmapFactory.Options options) {
        int sampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            sampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
    }

    //cope from picasso  -----end-------

    public static int getBitmapSize(@NonNull Bitmap bitmap) {
        int bytes = BitmapCompat.getAllocationByteCount(bitmap);
        if (bytes < 0)
            throw new IllegalStateException("Negative size: " + bitmap);
        return bytes;
    }

    public static Bitmap.CompressFormat getCompressFormat(String key) {
        Bitmap.CompressFormat format = null;
        int index = key.lastIndexOf(".");
        if (index > 0) {
            String s = key.substring(index);
            if (!TextUtils.isEmpty(s)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    if (s.equals(NameGenerator.IMAGE_JPG))
                        format = Bitmap.CompressFormat.JPEG;
                    else if (s.equals(NameGenerator.IMAGE_PNG))
                        format = Bitmap.CompressFormat.PNG;
                    else if (s.equals(NameGenerator.IMAGE_WEBP)) {
                        format = Bitmap.CompressFormat.WEBP;
                    }
                } else {
                    if (s.equals(NameGenerator.IMAGE_JPG))
                        format = Bitmap.CompressFormat.JPEG;
                    else
                        format = Bitmap.CompressFormat.PNG;
                }
            }
        }
        if (format == null)
            format = Bitmap.CompressFormat.PNG;
        if (CouLog.debug)
            CouLog.i("getCompressFormat " + format);
        return format;
    }
}
