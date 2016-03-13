package cn.dengx.couhttp;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import cn.dengx.couhttp.exception.CouHttpRuntimeException;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/12,15:16.
 */
public class FileUpload extends Upload {

    public FileUpload(String url,Response.Listener<String> l, FileContainer... containers){
        this(url,null,l,containers);
    }

    public FileUpload(String url,Map<String,String> params,Response.Listener<String> l, FileContainer... containers){
        this(url,null,params,l,containers);
    }

    public FileUpload(String url, Map<String, String> headers, Map<String, String> params,
                      Response.Listener<String> l, FileContainer... containers) {
        super(url, headers, params, containers);
        setResponseListener(l);
    }

    @Override
    protected int calculateSize(Container container) {
        if(container.entry instanceof File){
            File file = (File) container.entry;
            return (int) file.length();
        }
        return 0;
    }

    @Override
    public int outputEntry(Container container, OutputStream out, ByteBuffer buffer, int curr, int length) throws IOException {
        if (container.entry instanceof File) {
            int offset = curr;
            buffer.clear();
            File file = (File) container.entry;
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                while (buffer.read(in) > 0) {
                    offset += buffer.position();
                    buffer.flip();
                    buffer.write(out);
                    buffer.clear();
                    showProgress(offset,length);
                }
            } finally {
                if (in != null)
                    in.close();
            }
            return offset;
        } else
            throw new CouHttpRuntimeException("upload entry " + container.entry + " class "
                    + container.entry.getClass().toString() + " use " + getClass().toString());
    }

    public static class FileContainer extends Upload.Container<File> {

        public FileContainer(String field, File file) {
            if (TextUtils.isEmpty(field) || file == null)
                throw new IllegalArgumentException();
            this.field = field;
            this.entry = file;
            this.name = file.getName();
        }
    }
}
