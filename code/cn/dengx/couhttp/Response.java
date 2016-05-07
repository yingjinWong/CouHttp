package cn.dengx.couhttp;

import android.support.annotation.NonNull;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/7,13:55.
 */
public abstract class Response<T> {
    private final Request request;
    private final T result;

    Response(@NonNull Request request, @NonNull T result) {
        this.request = request;
        this.result = result;
    }

    public interface Listener<T> {
        void onResponse(T result,Request request);

        void onFail(Request request, Error error);
    }

    public interface ProgressListener {
        /**
         * the method will not invoke if response body get from cache
         *
         * @param curr
         * @param total -1 if do not know the length
         */
        void onProgress(int curr, int total);
    }

    public static class Error {
        public static final int DO_NOT_KNOW_ERROR = -1;
        private Exception e;
        private int code = DO_NOT_KNOW_ERROR;

        public Error(Exception e) {
            this.e = e;
        }

        public Error(int code) {
            this.code = code;
        }

        public Error(Exception e, int code) {
            this.e = e;
            this.code = code;
        }

        public Exception getE() {
            return e;
        }

        public void setE(Exception e) {
            this.e = e;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "e=" + e +
                    ", code=" + code +
                    '}';
        }
    }
}
