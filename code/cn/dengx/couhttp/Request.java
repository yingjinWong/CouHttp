package cn.dengx.couhttp;


import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.dengx.couhttp.utils.TimeUtil;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/3,17:34.
 */
public abstract class Request<T> {
    public static final int METHOD_GET = 1;
    public static final int METHOD_POST = 2;

    Handler mainHandler;

    protected URI uri;

    private String url;
    private Map<String, String> headers;

    /**
     * Request method of this request.  Currently supports GET and POST.
     */
    private int method;
    private int connectTimeOut;
    private int readTimeOut;

    private boolean canceled;
    private boolean finished;
    private long birthTime;

    private int priority;

    private Object tag;

    private Object responseListener;

    private ResponseType responseType;

    protected T[] requestBodyObjects;

    private Response.ProgressListener progressListener;

    public Request(String url, int requestMethod, int priority) {
        if (url == null)
            throw new IllegalArgumentException();
        this.url = url;
        this.method = requestMethod;
        this.priority = priority;
        birthTime = System.currentTimeMillis();
        setHeader(Headers.CACHE_CONTROL, Headers.VALUE_NO_CACHE);
        setHeader(Headers.ACCEPT, "*/*");
        setHeader(Headers.CHARSET, Headers.VALUE_CHARSET_UTF_8);
    }

    static void constructURI(@NonNull Request request) {
        String url = request.getUrl();
        if (!TextUtils.isEmpty(url)) {
            URI uri = URI.create(url);
            request.setUri(uri);
        }
    }

    public String getUrl() {
        return url;
    }

    void setUrl(@NonNull String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        if (this.headers == null)
            this.headers = new HashMap<>();
        if (headers != null)
            this.headers.putAll(headers);
    }

    public String getHeader(String key) {
        if (headers == null)
            return null;
        return headers.get(key);
    }

    public void setHeader(String header, String value) {
        if (headers == null)
            headers = new HashMap<>();
        if (header == null || value == null)
            throw new IllegalArgumentException();
        headers.put(header, value);
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public boolean isFinished() {
        return finished;
    }

    void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Object getResponseListener() {
        return responseListener;
    }

    public void setResponseListener(Object responseListener) {
        this.responseListener = responseListener;
    }

    URI getUri() {
        return uri;
    }

    void setUri(URI uri) {
        this.uri = uri;
    }

    T[] getRequestBodyObject() {
        return requestBodyObjects;
    }

    void setRequestBodyObject(T... requestBodyObjects) {
        this.requestBodyObjects = requestBodyObjects;
    }

    ResponseType getResponseType() {
        return responseType;
    }

    void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    abstract void writeRequestBody(OutputStream outputStream, ByteBuffer buffer) throws IOException;

    long getBirthTime() {
        return birthTime;
    }

    int guessRequestMethod() {
        if (requestBodyObjects == null) {
            return METHOD_GET;
        } else {
            return METHOD_POST;
        }
    }

    public Response.ProgressListener getProgressListener() {
        return progressListener;
    }

    public void registProgressListener(Response.ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void unregistProgressListener() {
        this.progressListener = null;
    }

    Handler getMainHandler() {
        return mainHandler;
    }

    void setMainHandler(Handler mainHandler) {
        this.mainHandler = mainHandler;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Request:");
        builder.append("url='").append(url).append('\'').append('\n');
        builder.append("method=").append(method == 2 ? "POST" : "GET");
        builder.append(", connectTimeOut=").append(connectTimeOut);
        builder.append(", readTimeOut=").append(readTimeOut);
        builder.append('\n');
        if (headers != null) {
            builder.append("headers{\n");
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                builder.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
            }
            builder.append("}\n");
        }
        builder.append(", birthTime=").append(TimeUtil.getTime(birthTime));
        builder.append('\n');

        if (requestBodyObjects != null && requestBodyObjects.length > 0) {
            builder.append("body : \n");
            for (T t : requestBodyObjects) {
                builder.append(t.toString()).append('\n');
            }
        }
        return builder.toString();
    }

    public static class Priority {
        public static final int HIGH = 9;
        public static final int NORMAL = 6;
        public static final int LOW = 1;
        public static final int LAST = 0;
    }

    enum ResponseType {
        TYPE_STRING,
        TYPE_IMAGE,
        TYPE_FILE
    }
}
