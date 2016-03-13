package cn.dengx.couhttp;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/11,11:19.
 */
public class FileHunter extends Hunter {

    public FileHunter(@NonNull Request request, @NonNull Config config, @NonNull ByteBufferPool pool,
                      @NonNull Handler handler,@NonNull Context context) {
        super(request, config, pool, handler,context);
    }

    @Override
    public void readDateOnConnected(InputStream in, ByteBuffer buffer, Entry entry) throws IOException {
        buffer.clear();
        int length = tryGetLength(entry);
        FileRequest fileRequest = (FileRequest) getRequest();
        File saved = fileRequest.getSaved();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(saved);
            int offset = 0;
            while (buffer.read(in) > 0) {
                int position = buffer.position();
                buffer.flip();
                buffer.write(out);
                offset += position;
                buffer.clear();
                Response.ProgressListener l = fileRequest.getProgressListener();
                if (l != null && getRequest().getMethod() == Request.METHOD_GET)
                    getHandler().obtainMessage(CouHandler.SHOW_PROGRESS_MESSAGE, offset, length, l)
                            .sendToTarget();
            }
            entry.body = saved;
            entry.code = HttpURLConnection.HTTP_OK;
        } finally {
            if (out != null)
                out.close();
        }
    }
}
