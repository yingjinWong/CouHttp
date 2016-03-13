package cn.dengx.couhttp;

import android.os.Handler;
import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Set;

import cn.dengx.couhttp.utils.HttpUtil;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/12,15:29.
 */
public abstract class Upload extends Request<Upload.Container> {
    private Map<String, String> params;
    private String boundary;

    private String bodyBoundary;

    public Upload(String url, Map<String, String> headers, Map<String, String> params, Container... containers) {
        super(url, METHOD_POST, Priority.LOW);
        setHeader(Headers.CONTENT_TYPE, getContentType());
        bodyBoundary = HttpUtil.PREFIX + boundary;
        setParams(params);
        setHeaders(headers);
        setResponseType(ResponseType.TYPE_STRING);
        setRequestBodyObject(containers);
        setReadTimeOut(120000);
    }

    @Override
    void writeRequestBody(OutputStream out, ByteBuffer buffer) throws IOException {

        byte[] boundary = (bodyBoundary + HttpUtil.LINE_BREAK).getBytes();

        Map<String, String> params = getParams();
        if (params != null) {
            Set<Map.Entry<String, String>> entries = params.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                out.write(boundary);
                StringBuilder builder = new StringBuilder(HttpUtil.CONTENT_DISPOSITION);
                builder.append(HttpUtil.NAME).append(HttpUtil.COLON).append(entry.getKey())
                        .append(HttpUtil.COLON).append(HttpUtil.LINE_BREAK).append(HttpUtil.LINE_BREAK)
                        .append(entry.getValue()).append(HttpUtil.LINE_BREAK);
                out.write(builder.toString().getBytes());
            }
        }

        Container[] containers = getRequestBodyObject();
        if (containers != null) {
            int length = 0;
            for (Container container : containers) {
                length += calculateSize(container);
            }

            int curr = 0;
            FileNameMap map = HttpURLConnection.getFileNameMap();
            for (Container container : containers) {
                out.write(boundary);

                StringBuilder builder = new StringBuilder(HttpUtil.CONTENT_DISPOSITION);
                builder.append(HttpUtil.NAME).append(HttpUtil.COLON).append(container.field)
                        .append(HttpUtil.COLON).append("; ").append(HttpUtil.FILE_NAME)
                        .append(HttpUtil.COLON).append(container.name).append(HttpUtil.COLON)
                        .append(HttpUtil.LINE_BREAK).append(HttpUtil.CONTENT_TYPE);
                String mine = map.getContentTypeFor(container.name);
                if (TextUtils.isEmpty(mine))
                    mine = HttpUtil.TYPE_STREAM;
                builder.append(mine).append(HttpUtil.LINE_BREAK).append(HttpUtil.LINE_BREAK);
                out.write(builder.toString().getBytes());

                curr = outputEntry(container, out, buffer, curr, length);

                out.write(HttpUtil.LINE_BREAK.getBytes());
            }
        }
        out.write((bodyBoundary + HttpUtil.PREFIX + HttpUtil.LINE_BREAK).getBytes());
    }

    protected abstract int calculateSize(Container container);

    /**
     * write Container.entry only
     */
    public abstract int outputEntry(Container container, OutputStream out, ByteBuffer buffer,
                                    int curr, int length) throws IOException;


    protected void showProgress(final int curr, final int total) {
        Handler handler = getMainHandler();
        if (getMethod() == METHOD_POST && getProgressListener() != null)
            handler.obtainMessage(CouHandler.SHOW_PROGRESS_MESSAGE, curr, total,
                    getProgressListener()).sendToTarget();
    }

    private String getBoundary() {
        if (boundary == null) {
            boundary = "----CouHttpForBoundary" + hashCode();
            if (CouLog.debug)
                CouLog.i("boundary " + boundary);
        }
        return boundary;
    }

    protected String getContentType() {
        return Headers.VALUE_MULTIPART_FORM_DATE + "; boundary=" + getBoundary();
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        if (params != null) {
            builder.append("params:\n");
            Set<Map.Entry<String, String>> entries = params.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                builder.append(entry.getKey()).append(" : ").append(entry.getValue()).append('\n');
            }
        }
        return builder.toString();
    }

    public static class Container<T> {
        public T entry;
        public String field;
        public String name;

        @Override
        public String toString() {
            return "Container{" +
                    "entry=" + entry +
                    ", field='" + field + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
