package cn.dengx.couhttp;

import org.json.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/13,15:13.
 */
public class JSONArrayRequest extends Request<JSONArray> {

    public JSONArrayRequest(String url, JSONArray array, Response.Listener<String> l) {
        this(url, array, null, l);
    }

    public JSONArrayRequest(String url, JSONArray array, Map<String, String> headers,
                            Response.Listener<String> l) {
        super(url, METHOD_POST, Priority.NORMAL);
        if (array == null)
            throw new IllegalArgumentException();
        setResponseType(ResponseType.TYPE_STRING);
        setHeaders(headers);
        setHeader(Headers.CONTENT_TYPE, Headers.VALUE_APPLICATION_JSON);
        setRequestBodyObject(array);
        setResponseListener(l);
    }

    @Override
    void writeRequestBody(OutputStream outputStream, ByteBuffer buffer) throws IOException {
        buffer.clear();
        if (requestBodyObjects != null) {
            for (JSONArray array : requestBodyObjects) {
                byte[] bytes = array.toString().getBytes();
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
                    CouLog.i("writeRequestBody String=" + array);
            }
        }
    }
}
