package cn.dengx.couhttp;

import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import cn.dengx.couhttp.utils.HttpUtil;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/6,12:22.
 */
public class StringRequest extends Request<String> {

    public StringRequest(String url, Response.Listener<String> listener) {
        this(url, METHOD_GET, null, listener);
    }

    public StringRequest(String url, int method, Map<String, String> params, Response.Listener<String> listener) {
        this(url, method, null, params, listener);
    }

    public StringRequest(String url, int method, Map<String, String> headers, Map<String, String> params,
                         Response.Listener<String> listener) {
        super(url, method, Priority.NORMAL);
        setResponseListener(listener);
        setResponseType(ResponseType.TYPE_STRING);
        String form = HttpUtil.formatForm(params);
        setHeaders(headers);
        if (!TextUtils.isEmpty(form)) {
            if (method == METHOD_GET) {
                setUrl(url + '?' + form);
            } else if (method == METHOD_POST) {
                setRequestBodyObject(form);
            }
        }
        if (method == METHOD_POST) {
            setHeader(Headers.CONTENT_TYPE, Headers.VALUE_APPLICATION_FORM_URLENCODED);
        }
    }

    @Override
    void writeRequestBody(OutputStream outputStream, ByteBuffer buffer) throws IOException {
        buffer.clear();

        if (getMethod() == METHOD_POST) {
            String[] bodies = getRequestBodyObject();
            if (bodies != null) {
                for (String body : bodies) {
                    byte[] bytes = body.getBytes();
                    int size = bytes.length;
                    if (CouLog.debug)
                        CouLog.d("writeRequestBody size=" + size);
                    if (size > buffer.limit()) {//more than once
                        int length = 0;
                        while (length < size) {
                            int d = Math.abs(size - length);
                            buffer.put(bytes, length, d > buffer.limit() ? buffer.limit() : d);
                            length += buffer.position();
                            buffer.flip();
                            buffer.write(outputStream);
                            if (CouLog.debug)
                                CouLog.i("writeRequestBody write length=" + length + "   " + buffer);
                            buffer.rewind();
                        }
                    } else { //once
                        buffer.put(bytes, 0, size);
                        buffer.flip();
                        buffer.write(outputStream);
                    }
                    if (CouLog.debug)
                        CouLog.i("writeRequestBody String=" + body);
                }
            }
        }
    }

    @Override
    public String toString() {
        if (requestBodyObjects == null || requestBodyObjects.length <= 0)
            return super.toString();
        else {
            StringBuilder builder = new StringBuilder(super.toString());
            builder.append("body:");
            for (String body : requestBodyObjects)
                builder.append(body).append('-');
            builder.append('\n');
            return builder.toString();
        }
    }
}
