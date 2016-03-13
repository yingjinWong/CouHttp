package cn.dengx.couhttp;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/13,15:02.
 */
public class JSONObjectRequest extends Request<JSONObject> {

    public JSONObjectRequest(String url, JSONObject object, Response.Listener<String> l) {
        this(url, object, null, l);
    }

    public JSONObjectRequest(String url, JSONObject object, Map<String, String> headers,
                             Response.Listener<String> l) {
        super(url, METHOD_POST, Priority.NORMAL);
        if (object == null)
            throw new IllegalArgumentException();
        setResponseType(ResponseType.TYPE_STRING);
        setHeaders(headers);
        setHeader(Headers.CONTENT_TYPE, Headers.VALUE_APPLICATION_JSON);
        setRequestBodyObject(object);
        setResponseListener(l);
    }

    @Override
    void writeRequestBody(OutputStream outputStream, ByteBuffer buffer) throws IOException {
        buffer.clear();
        if (requestBodyObjects != null) {
            for (JSONObject object : requestBodyObjects) {
                byte[] bytes = object.toString().getBytes();
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
                    CouLog.i("writeRequestBody String=" + object);
            }
        }
    }
}
