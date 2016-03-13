package cn.dengx.couhttp.exception;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/4,21:37.
 */
public class CouHttpRuntimeException extends RuntimeException {
    public CouHttpRuntimeException() {
    }

    public CouHttpRuntimeException(String detailMessage) {
        super(detailMessage);
    }

    public CouHttpRuntimeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CouHttpRuntimeException(Throwable throwable) {
        super(throwable);
    }
}
