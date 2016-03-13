package cn.dengx.couhttp.exception;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/3,17:31.
 */
public class InitialzationException extends RuntimeException {
    public InitialzationException() {
    }

    public InitialzationException(String detailMessage) {
        super(detailMessage);
    }
}
