package cn.dengx.couhttp;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/13,14:47.
 */
public class RequestFactory {

    private RequestFactory() {
    }

    public static StringRequest string(String url, Response.Listener<String> listener) {
        return string(url, Request.METHOD_GET, null, listener);
    }

    public static StringRequest string(String url, int method, Map<String, String> params,
                                       Response.Listener<String> listener) {
        return string(url, method, null, params, listener);
    }

    public static StringRequest string(String url, int method, Map<String, String> headers,
                                       Map<String, String> params, Response.Listener<String> listener) {
        return new StringRequest(url, method, headers, params, listener);
    }

    public static JSONObjectRequest jsonObject(String url, JSONObject object,
                                               Response.Listener<String> l) {
        return jsonObject(url, object, null, l);
    }

    public static JSONObjectRequest jsonObject(String url, JSONObject object, Map<String, String> headers,
                                               Response.Listener<String> l) {
        return new JSONObjectRequest(url, object, headers, l);
    }

    public static JSONArrayRequest jsonArray(String url, JSONArray array, Response.Listener<String> l) {
        return jsonArray(url, array, null, l);
    }


    public static JSONArrayRequest jsonArray(String url, JSONArray array, Map<String, String> headers,
                                             Response.Listener<String> l) {
        return new JSONArrayRequest(url, array, headers, l);
    }

    /**
     * @param uri  like:   http://192.168.88.231/ab.txt
     *             content://ac.doc
     *             file://ad.txt
     *             assets://aa.jpg
     * @param dir  the directory that file save in
     * @param name file name save with
     * @return
     */
    public static FileRequest file(String uri, File dir, String name, Response.Listener<File> listener) {
        return new FileRequest(uri, dir, name, listener);
    }

    /**
     * @param uri   like:   http://192.168.88.231/ab.txt
     *              content://ac.doc
     *              file://ad.txt
     *              assets://aa.jpg
     * @param saved like:  new File(dir,name)
     * @return
     */
    public static FileRequest file(String uri, File saved, Response.Listener listener) {
        return new FileRequest(uri, saved, listener);
    }


    /**
     * @param uri like:   http://192.168.88.231/aa.jpg
     *            content://aa.jpg
     *            file:///bb.png
     *            assets://cc.webp
     * @return
     */
    public static ImageRequest image(String uri, Response.Listener<Bitmap> listener) {
        return image(uri, null, listener);
    }

    /**
     * @param uri             like:   http://192.168.88.231/aa.jpg
     *                        content://aa.jpg
     *                        file:///bb.png
     *                        assets://cc.webp
     * @param bitmapProcessor you can process bitmap which decode from inputStream,
     *                        processor work in thread that out of UI thread.
     * @return
     */
    public static ImageRequest image(String uri, ImageRequest.BitmapProcessor bitmapProcessor,
                                     Response.Listener<Bitmap> listener) {
        return image(uri, 0, 0, bitmapProcessor, listener);
    }

    /**
     * @param uri             like:   http://192.168.88.231/aa.jpg
     *                        content://aa.jpg
     *                        file:///bb.png
     *                        assets://cc.webp
     * @param width           the destination width
     * @param height          the destination height
     * @param bitmapProcessor you can process bitmap which decode from inputStream,
     *                        processor work in thread that out of UI thread
     * @return
     */
    public static ImageRequest image(String uri, int width, int height, ImageRequest.BitmapProcessor bitmapProcessor,
                                     Response.Listener<Bitmap> listener) {
        return new ImageRequest(uri, listener, width, height, bitmapProcessor);
    }

    public static BitmapUpload upload(String url, Response.Listener<String> l, BitmapUpload.BitmapContainer... containers) {
        return upload(url, null, l, containers);
    }

    public static BitmapUpload upload(String url, Map<String, String> params, Response.Listener<String> l,
                                      BitmapUpload.BitmapContainer... containers) {
        return upload(url, params, l, null, containers);
    }

    public static BitmapUpload upload(String url, Map<String, String> params, Response.Listener<String> l,
                                      Map<String, String> headers, BitmapUpload.BitmapContainer... containers) {
        return new BitmapUpload(url, params, l, headers, containers);
    }

    public static FileUpload upload(String url, Response.Listener<String> l, FileUpload.FileContainer... containers) {
        return upload(url, null, l, containers);
    }

    public static FileUpload upload(String url, Map<String, String> params, Response.Listener<String> l,
                                    FileUpload.FileContainer... containers) {
        return upload(url, null, params, l, containers);
    }

    public static FileUpload upload(String url, Map<String, String> headers, Map<String, String> params,
                                    Response.Listener<String> l, FileUpload.FileContainer... containers) {
        return new FileUpload(url, headers, params, l, containers);
    }
}
