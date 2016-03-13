package cn.dengx.couhttp;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import cn.dengx.couhttp.exception.CanceledException;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/5,16:33.
 */
public class HttpConnector implements Connector {
    public static final String SCHEME = "http";
    public static final String GET = "GET";
    public static final String POST = "POST";

    private HttpURLConnection connection;
    private OutputStream out;
    private InputStream in;

    private Request request;

    @Override
    public void connect(@NonNull Request request, @NonNull Context context) throws CanceledException, IOException {
        this.request = request;
        URI uri = request.getUri();
        URL url;
        try {
            url = uri.toURL();
            connection = (HttpURLConnection) url.openConnection();
            setRequestProperty(connection, request);
            setRequestMethod(connection, request);
            setTimeOUt(connection, request);
            setOther(connection, request);
//            connection.connect();
        } catch (MalformedURLException e) {
            CouLog.e("URI=" + uri + " toURL() error", e);
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (request.getMethod() == Request.METHOD_POST)
            out = connection.getOutputStream();
        return out;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            in = connection.getInputStream();
        else
            in = connection.getErrorStream();
        return in;
    }

    @Override
    public void getResponseCode(Hunter.Entry entry) throws IOException {
        if (connection != null) {
            entry.responseHeaders = connection.getHeaderFields();
            entry.code = connection.getResponseCode();
            if (CouLog.debug)
                CouLog.d("http response code " + entry.code);
        }
    }

    @Override
    public boolean supportOutput() {
        return true;
    }

    @Override
    public void close() throws IOException {
        if (out != null) {
            out.close();
            out = null;
        }
        if (in != null) {
            in.close();
            in = null;
        }
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }

    private void setRequestProperty(@NonNull HttpURLConnection connection, @NonNull Request request) {
        Map<String, String> headers = request.getHeaders();
        if (headers != null) {
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    private void setRequestMethod(@NonNull HttpURLConnection connection, @NonNull Request request) {
        int method = request.getMethod();
        try {
            if (method == Request.METHOD_GET) {
                connection.setRequestMethod(GET);
            } else if (method == Request.METHOD_POST) {
                connection.setRequestMethod(POST);
            } else {
                //ignore
            }
        } catch (ProtocolException e) {
            CouLog.e("setRequestMethod exception", e);
        }
    }

    private void setTimeOUt(@NonNull HttpURLConnection connection, @NonNull Request request) {
        connection.setReadTimeout(request.getReadTimeOut());
        connection.setConnectTimeout(request.getConnectTimeOut());
    }

    private void setOther(@NonNull HttpURLConnection connection, Request request) {
        connection.setUseCaches(false);
        connection.setDoInput(true);
        if (request.getMethod() == Request.METHOD_POST)
            connection.setDoOutput(true);
    }

}
