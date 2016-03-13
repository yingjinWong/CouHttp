package cn.dengx.couhttp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/11,10:45.
 */
public class FileRequest extends Request<File> {
    private File saved;

    public FileRequest(String url, File dir, String name) {
        this(url, new File(dir, name));
    }

    public FileRequest(String url, File dir, String name, Response.Listener<File> listener) {
        this(url, new File(dir, name), listener);
    }

    public FileRequest(String url, File saved) {
        super(url, METHOD_GET, Priority.LAST);
        if (saved == null)
            throw new IllegalArgumentException();
        this.saved = saved;
        setResponseType(ResponseType.TYPE_FILE);
    }

    public FileRequest(String url, File saved, Response.Listener listener) {
        super(url, METHOD_GET, Priority.LAST);
        if (saved == null)
            throw new IllegalArgumentException();
        this.saved = saved;
        setResponseType(ResponseType.TYPE_FILE);
        setResponseListener(listener);
    }

    @Override
    void writeRequestBody(OutputStream outputStream, ByteBuffer buffer) throws IOException {
        //do nothing
    }

    public File getSaved() {
        return saved;
    }

    public void setSaved(File saved) {
        this.saved = saved;
    }
}
