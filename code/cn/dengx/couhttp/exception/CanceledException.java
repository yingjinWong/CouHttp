package cn.dengx.couhttp.exception;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/6,18:09.
 */
public class CanceledException extends Exception {
    public CanceledException() {
    }

    public CanceledException(String detailMessage) {
        super(detailMessage);
    }

    public CanceledException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CanceledException(Throwable throwable) {
        super(throwable);
    }
}
