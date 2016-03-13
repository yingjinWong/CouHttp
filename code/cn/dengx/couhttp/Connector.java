package cn.dengx.couhttp;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.dengx.couhttp.exception.CanceledException;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/5,15:17.
 */
public interface Connector extends Closeable {

    void connect(@NonNull Request request, @NonNull Context context) throws CanceledException, IOException;

    /**
     * the method will not invoke if the connection do not support output
     */
    OutputStream getOutputStream() throws IOException;

    InputStream getInputStream() throws IOException;

    void getResponseCode(Hunter.Entry entry) throws IOException;

    boolean supportOutput();

}
