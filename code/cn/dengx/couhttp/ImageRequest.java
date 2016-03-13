package cn.dengx.couhttp;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/9,16:14.
 */
public class ImageRequest extends Request<Bitmap> {

    private int width, height;
    private BitmapProcessor bitmapProcessor;

    public ImageRequest(String url, Response.Listener<Bitmap> listener) {
        this(url, listener, 0, 0);
    }

    public ImageRequest(String url, Response.Listener<Bitmap> listener, int width, int height) {
        this(url, listener, width, height, null);
    }

    public ImageRequest(String url, Response.Listener<Bitmap> listener, int width, int height,
                        BitmapProcessor bitmapProcessor) {
        super(url, METHOD_GET, Priority.LOW);
        setResponseListener(listener);
        setResponseType(ResponseType.TYPE_IMAGE);
        this.width = width;
        this.height = height;
        this.bitmapProcessor = bitmapProcessor;
    }

    @Override
    void writeRequestBody(OutputStream outputStream, ByteBuffer buffer) throws IOException {
        //do nothing
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BitmapProcessor getBitmapProcessor() {
        return bitmapProcessor;
    }

    public void setBitmapProcessor(BitmapProcessor bitmapProcessor) {
        this.bitmapProcessor = bitmapProcessor;
    }

    public interface BitmapProcessor {


        /**
         * processing bitmap that get from url,than it will cache in memory and disk
         *
         * @param bitmap the bitmap get from url
         * @return
         */
        Bitmap process(Bitmap bitmap);
    }

}
