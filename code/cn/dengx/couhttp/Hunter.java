package cn.dengx.couhttp;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.util.List;
import java.util.Map;

import cn.dengx.couhttp.exception.CanceledException;
import cn.dengx.couhttp.exception.CouHttpRuntimeException;
import cn.dengx.couhttp.exception.SuccessException;
import cn.dengx.couhttp.utils.HttpUtil;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/6,16:42.
 */
public abstract class Hunter implements Runnable, Comparable<Hunter> {

    private final Request request;
    private final Config config;
    private final ByteBufferPool pool;
    private final Handler handler;
    private final Context context;

    public Hunter(@NonNull Request request, @NonNull Config config, @NonNull ByteBufferPool pool,
                  @NonNull Handler handler, @NonNull Context context) {
        this.request = request;
        this.config = config;
        this.pool = pool;
        this.handler = handler;
        this.context = context;
    }

    @Override
    public void run() {

        if (CouLog.debug)
            CouLog.d(getClass().getSimpleName() + " init");
        init();

        Class<? extends Connector> clazz;
        Controller controller = config.getController();
        if (controller == null)
            throw new NullPointerException();
        try {
            clazz = controller.control(getRequest().getUri());
        } catch (ProtocolException e) {
            CouLog.e("control error of request = " + getRequest(), e);
            return;  //give up this request if has protocolException
        }
        if (clazz == null) {
            throw new CouHttpRuntimeException("do not find a Connection for uri = " + getRequest().getUri());
        }

        Connector connector = getConnectionInstance(clazz);
        if (CouLog.debug)
            CouLog.d("hunter getConnectionInstance");

        ByteBuffer buffer = null;
        Entry entry = new Entry();

        boolean requestSucc = false;
        try {
            buffer = pool.borrow();
            buffer.clear();

            checkCanceled(getRequest());

            //you can get data from cache
            beforeConnect(buffer, entry);
            if (entry.body != null) {
                throw new SuccessException();
            }

            connector.connect(request, context);
            if (CouLog.debug)
                CouLog.d("hunter connector.connect");

            OutputStream outputStream = null;
            InputStream inputStream;
            if (connector.supportOutput()) {
                outputStream = connector.getOutputStream();
            }

            if (outputStream != null) {
                writeDateFirstOnConnected(outputStream, buffer);
                checkCanceled(getRequest());
                outputStream.flush();
            }

            connector.getResponseCode(entry);

            //outputStream will close after httpUrlConnection get inputStream
            //it will throw outputStream closed exception after getInputStream
            inputStream = connector.getInputStream();
            if (inputStream == null)
                throw new NullPointerException();

            readDateOnConnected(inputStream, buffer, entry);
            checkCanceled(getRequest());
            if (CouLog.debug)
                CouLog.d("hunter readDateOnConnected");

            beforeConnectionClose(inputStream, buffer, entry);

            requestSucc = true;
        } catch (CanceledException e) {
            entry.e = e;
        } catch (SuccessException e) {
            requestSucc = true;
            entry.e = e;
        } catch (IOException e) {
            CouLog.e("io exception", e);
            entry.e = e;
        } catch (Exception e) {
            CouLog.e("hunter occur error", e);
            entry.e = e;
        } finally {
            try {
                connector.close();
            } catch (IOException e) {
                //ignore
            }

            afterConnectionClose(buffer, requestSucc, entry);
            if (CouLog.debug)
                CouLog.d("hunter afterConnectionClose");

            if (buffer != null) {
                pool.restore(buffer);
            }
        }

    }

    /**
     * those methods will not invoke if Entry.body has value in this method
     *
     * @param buffer
     * @param entry
     */
    public void beforeConnect(ByteBuffer buffer, Entry entry) {
    }

    public void init() {
        confirmMethod();
        Request.constructURI(getRequest());
        initTimeOut();
    }

    /**
     * @param out    can't be null if the method invoke
     * @param buffer
     * @throws IOException
     */
    public void writeDateFirstOnConnected(OutputStream out, ByteBuffer buffer) throws IOException {
        Request request = getRequest();
        request.writeRequestBody(out, buffer);
    }

    /**
     * @param in     can't be null if the method invoke
     * @param buffer
     * @param entry
     * @throws IOException
     */
    public abstract void readDateOnConnected(InputStream in, ByteBuffer buffer, Entry entry) throws IOException;

    public void beforeConnectionClose(InputStream in, ByteBuffer buffer,
                                      final Entry entry) throws IOException {
        if (CouLog.debug)
            CouLog.d("result : " + entry);
    }

    public void afterConnectionClose(ByteBuffer buffer, final boolean requestSucc, final Entry entry) {

        final Request request = getRequest();
        request.setFinished(true);
        Object l = request.getResponseListener();
        Response.Listener listener = null;
        if (l != null && l instanceof Response.Listener)
            listener = (Response.Listener) l;
        if (listener != null && !request.isCanceled()) {
            final Response.Listener finalListener = listener;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestSucc && entry.body != null) {
                        finalListener.onResponse(entry.body);
                    } else {
                        Response.Error error = new Response.Error(entry.e, entry.code);
                        finalListener.onFail(request, error);
                    }
                }
            });
        }
    }

    private void confirmMethod() {
        int method = request.getMethod();
        if (method == 0) {
            method = request.guessRequestMethod();//ignore zero this time
            request.setMethod(method);
        }
    }

    private void initTimeOut() {
        Config config = getConfig();
        Request request = getRequest();
        if (request.getConnectTimeOut() < 1)
            request.setConnectTimeOut(config.getConnectTimeOut());
        if (request.getReadTimeOut() < 1)
            request.setReadTimeOut(config.getReadTimeOut());
    }

    static void checkCanceled(@NonNull Request request) throws CanceledException {
        if (request.isCanceled())
            throw new CanceledException();
    }

    static Connector getConnectionInstance(Class<? extends Connector> clazz) {
        Connector connector = null;
        try {
            connector = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (connector == null)
            throw new CouHttpRuntimeException("class " + clazz.toString() + " can't newInstance");
        return connector;
    }

    protected byte[] readDateAndShowProgress(@NonNull InputStream in, @NonNull ByteBuffer buffer,
                                             Request request, int length,
                                             @NonNull Handler handler) throws IOException {
        int method = getRequest().getMethod();

        buffer.clear();
        byte[] bytes = null;
        if (length <= 0) {
            int offset = 0;
            while (buffer.read(in) > 0) {
                int position = buffer.position();
                bytes = HttpUtil.getBiggerArray(bytes, position);
                buffer.flip();
                buffer.get(bytes, offset, position);
                offset += position;
                buffer.clear();
                Response.ProgressListener l = request.getProgressListener();
                if (l != null && method == Request.METHOD_GET) {
                    showProgress(l, length, handler, offset);
                }
            }
        } else {
            bytes = new byte[length];
            int offset = 0;
            while (buffer.read(in) > 0) {
                int position = buffer.position();
                buffer.flip();
                buffer.get(bytes, offset, position);
                offset += position;
                if (CouLog.debug)
                    CouLog.i("readDateOnConnected length=" + offset + "  " + buffer);
                buffer.clear();
                Response.ProgressListener l = request.getProgressListener();
                if (l != null && method == Request.METHOD_GET) {
                    showProgress(l, length, handler, offset);
                }
            }
        }
        return bytes;
    }

    static void showProgress(@NonNull final Response.ProgressListener l, final int total,
                             @NonNull Handler handler, final int curr) {
        handler.obtainMessage(CouHandler.SHOW_PROGRESS_MESSAGE, curr, total, l).sendToTarget();
    }


    static void checkContentLength(byte[] bytes, int length) {
        if (bytes == null) {
            if (CouLog.debug)
                CouLog.d("StringHunter can't get response body from inputStream so that byte[] " +
                        "is null and instance byte[0]");
        } else if (length > 0) {
            if (bytes.length < length) {
                CouLog.e("StringHunter get response body less than contentLength");
            }
        }
    }

    protected int tryGetLength(Entry entry) {
        Map<String, List<String>> headers = entry.responseHeaders;
        int length = -1;
        if (headers != null) {
            length = HttpUtil.tryGetContentLength(headers);
            logLength(length);
        }
        return length;
    }

    static void logLength(int length) {
        if (CouLog.debug)
            CouLog.i("tryGetContentLength content length " + length);
    }

    public Request getRequest() {
        return request;
    }

    public Config getConfig() {
        return config;
    }

    public Handler getHandler() {
        return handler;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int compareTo(Hunter other) {
        Request l = getRequest();
        Request r = other.getRequest();
        int d = r.getPriority() - l.getPriority();
        if (d == 0) {
            d = (int) (r.getBirthTime() - l.getBirthTime());
        }
        return d;
    }

    public static class Entry {
        int code = -1;
        Map<String, List<String>> responseHeaders;
        Object body;
        Exception e;

        @Override
        public String toString() {
            return "Entry{" +
                    "code=" + code +
                    ", e=" + e +
                    "\nbody=" + body +
                    '}';
        }
    }
}
