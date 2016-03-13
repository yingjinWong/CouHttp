package cn.dengx.couhttp.exception;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/10,10:37.
 */
public class SuccessException extends Exception {
    public SuccessException() {
    }

    public SuccessException(String detailMessage) {
        super(detailMessage);
    }

    public SuccessException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public SuccessException(Throwable throwable) {
        super(throwable);
    }
}
