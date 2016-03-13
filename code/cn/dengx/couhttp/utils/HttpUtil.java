package cn.dengx.couhttp.utils;

import android.support.annotation.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.dengx.couhttp.CouLog;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/7,15:05.
 */
public class HttpUtil {

    public static final String CONTENT_DISPOSITION = "Content-Disposition: form-data; ";
    public static final String CONTENT_TYPE = "Content-Type: ";
    public static final String NAME = "name=";
    public static final String FILE_NAME = "filename=";
    public static final char COLON = '\"';
    public static final String LINE_BREAK = "\r\n";
    public static final String TYPE_STREAM = "application/octet-stream";
    public static final String PREFIX = "--";



    private HttpUtil() {
    }


    /**
     * 转换成表单形式
     *
     * @param params
     * @return
     */
    public static String formatForm(Map<String, String> params) {
        if (params != null) {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, String>> entries = params.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                builder.append(entry.getKey()).append('=').append(entry.getValue());
                if (iterator.hasNext())
                    builder.append('&');
            }
            return builder.toString();
        }
        return null;
    }

    public static byte[] getBiggerArray(@Nullable byte[] bytes, int add) {
        if (add < 0)
            throw new IllegalArgumentException();
        if(bytes!=null) {
            int length = bytes.length;
            byte[] array = new byte[length + add];
            System.arraycopy(bytes, 0, array, 0, length);
            return array;
        }else {
            return new byte[add];
        }
    }


    public static Charset tryGetCharset(Map<String, List<String>> headers) {
        Charset charset = null;
        List<String> contentTypes = headers.get("Content-Type");
        if (contentTypes != null && contentTypes.size() > 0) {
            String contentType = contentTypes.get(0);
            String[] ss = contentType.split(";");
            if (ss.length == 2) {
                String set = ss[1];
                set = set.substring(8);
                try {
                    charset = Charset.forName(set);
                } catch (UnsupportedCharsetException e) {
                    CouLog.e("Content-Type : " + contentType + "try to get charset error", e);
                }
            }
        }
        return charset;
    }

    public static int tryGetContentLength(Map<String, List<String>> headers) {
        int length = -1;
        List<String> content = headers.get("Content-Length");
        if (content != null && content.size() > 0) {
            String contentLength = content.get(0);
            try {
                length = Integer.valueOf(contentLength);
            } catch (Exception e) {
                CouLog.e("Content-Length : " + contentLength + "try to get length error", e);
            }
        }
        return length;
    }

}
