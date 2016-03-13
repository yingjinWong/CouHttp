package cn.dengx.couhttp;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import cn.dengx.couhttp.utils.HttpUtil;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/7,21:09.
 */
public class StringHunter extends Hunter {

    public StringHunter(@NonNull Request request, @NonNull Config config, @NonNull ByteBufferPool pool,
                        @NonNull Handler handler, @NonNull Context context) {
        super(request, config, pool, handler, context);
    }

    @Override
    public void readDateOnConnected(InputStream in, ByteBuffer buffer, Entry entry) throws IOException {
        buffer.clear();

        int length = -1;
        Charset charset = null;
        Map<String, List<String>> headers = entry.responseHeaders;
        if (headers != null) {
            charset = HttpUtil.tryGetCharset(headers);
            length = HttpUtil.tryGetContentLength(headers);
            logLength(length);
        }
        if (charset == null)
            try {
                charset = Charset.forName(getConfig().getEncoding());
            } catch (Exception e) {
                CouLog.e("getConfig().getEncoding():" + getConfig().getEncoding() + " UnsupportedCharset", e);
            }

        byte[] bytes = readDateAndShowProgress(in, buffer, getRequest(), length, getHandler());
//            bytes = new byte[length];
//            int offset = 0;
//            do {
//                buffer.rewind();
//                buffer.read(in);
//                buffer.flip();
//                buffer.get(bytes, offset, buffer.position());
//                offset += buffer.position();
//            } while (offset >= length);

        checkContentLength(bytes, length);
        if (bytes == null)
            bytes = new byte[0];
        String body;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && charset != null) {
            body = new String(bytes, charset);
        } else
            body = new String(bytes);
        entry.body = body;
    }

}
