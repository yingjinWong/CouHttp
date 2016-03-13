package cn.dengx.couhttp;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * the class is http header helper
 * Current project:CouHttp.
 * Created by dengx on 16/3/5,10:29.
 */
public class Headers {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_CHARSET = "Accept-Charset";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String HOST = "Host";
    public static final String USER_AGENT = "User-Agent";
    public static final String CHARSET = "Charset";

    public static final String VALUE_CHARSET_UTF_8 = "utf-8";
    public static final String VALUE_NO_CACHE = "no-cache";
    public static final String VALUE_APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String VALUE_APPLICATION_JSON = "application/json";
    public static final String VALUE_APPLICATION_XML = "application/xml";
    public static final String VALUE_MULTIPART_FORM_DATE = "multipart/form-data";

    private final Map<String, String> headers;

    public Headers() {
        headers = new HashMap<>();
    }

    public void setHost(@NonNull String host) {
        headers.put(HOST, host);
    }

    public void setContentType(@NonNull String type) {
        headers.put(CONTENT_TYPE, type);
    }

    public void setAccept(@NonNull String accept) {
        headers.put(ACCEPT, accept);
    }

    public void setAcceptCharset(@NonNull String charset) {
        headers.put(ACCEPT_CHARSET, charset);
    }

    public void setCacheControl(@NonNull String cacheControl) {
        headers.put(CACHE_CONTROL, cacheControl);
    }

    public void setUserAgent(@NonNull String userAgent) {
        headers.put(USER_AGENT, userAgent);
    }

    public void setHeader(@NonNull String header, @NonNull String value) {
        headers.put(header, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    Set<Map.Entry<String, String>> getHeadersEntry() {
        return headers.entrySet();
    }

}
