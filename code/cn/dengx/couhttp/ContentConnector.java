package cn.dengx.couhttp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import cn.dengx.couhttp.exception.CanceledException;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/12,23:03.
 */
public class ContentConnector implements Connector {
    public static final String SCHEME = "content";

    private InputStream in;

    @Override
    public void connect(@NonNull Request request, @NonNull Context context) throws CanceledException, IOException {
        URI uri = request.getUri();
        Uri u = Uri.parse(uri.toString());
        in = context.getContentResolver().openInputStream(u);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return in;
    }

    @Override
    public void getResponseCode(Hunter.Entry entry) throws IOException {
        entry.code = 200;
    }

    @Override
    public boolean supportOutput() {
        return false;
    }

    @Override
    public void close() throws IOException {
        if (in != null)
            in.close();
    }
}
