package cn.dengx.couhttp;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import cn.dengx.couhttp.exception.CouHttpRuntimeException;
import cn.dengx.couhttp.utils.BitmapUtil;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/11,14:59.
 */
public class BitmapUpload extends Upload {

    public BitmapUpload(String url, Response.Listener<String> l, BitmapContainer... containers) {
        this(url, null, l, containers);
    }

    public BitmapUpload(String url, Map<String, String> params, Response.Listener<String> l,
                        BitmapContainer... containers) {
        this(url, params, l, null, containers);
    }

    public BitmapUpload(String url, Map<String, String> params, Response.Listener<String> l,
                        Map<String, String> headers, BitmapContainer... containers) {
        super(url, headers, params, containers);
        setResponseListener(l);
    }

    @Override
    protected int calculateSize(Container container) {
        if(container.entry instanceof Bitmap){
            Bitmap bitmap = (Bitmap) container.entry;
            return BitmapUtil.getBitmapSize(bitmap);
        }
        return 0;
    }

    @Override
    public int outputEntry(Container container, OutputStream out, ByteBuffer buffer, int curr,
                            int length) throws IOException {
        if (container.entry instanceof Bitmap) {
            Bitmap bitmap = (Bitmap) container.entry;
            bitmap.compress(BitmapUtil.getCompressFormat(container.name), 100, out);
            return curr+BitmapUtil.getBitmapSize(bitmap);
        } else
            throw new CouHttpRuntimeException("upload entry " + container.entry + " class "
                    + container.entry.getClass().toString() + " use " + getClass().toString());
    }


    public static class BitmapContainer extends Upload.Container<Bitmap> {

        public BitmapContainer(String field, String ImageName, Bitmap bitmap) {
            if (TextUtils.isEmpty(field) || TextUtils.isEmpty(ImageName) || bitmap == null)
                throw new IllegalArgumentException();
            this.name = ImageName;
            this.field = field;
            this.entry = bitmap;
        }
    }


}
