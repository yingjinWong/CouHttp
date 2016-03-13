package cn.dengx.couhttp.exception;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/4,20:13.
 */
public class CouHttpException extends Exception {

    public CouHttpException() {
    }

    public CouHttpException(String detailMessage) {
        super(detailMessage);
    }

    public CouHttpException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public CouHttpException(Throwable throwable) {
        super(throwable);
    }
}
