package cn.dengx.couhttp;

import android.util.Log;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/3,14:54.
 */
public class CouLog {
    public static final String COU_HTTP = "CouHttp";
    static final String I = "[CouHttp i] ";
    static final String D = "[CouHttp d] ";
    static final String E = "[CouHttp e] ";
    static final String W = "[CouHttp w] ";

    public static boolean debug;

    public static void i(String msg) {
        Log.i(COU_HTTP, I + msg);
    }

    public static void i(String msg, Throwable tr) {
        Log.i(COU_HTTP, I + msg, tr);
    }

    public static void d(String msg) {
        Log.d(COU_HTTP, D + msg);
    }

    public static void d(String msg, Throwable tr) {
        Log.d(COU_HTTP, D + msg, tr);
    }


    public static void e(String msg) {
        Log.e(COU_HTTP, E + msg);
    }

    public static void e(String msg, Throwable tr) {
        Log.e(COU_HTTP, E + msg, tr);
    }

    public static void w(String msg) {
        Log.w(COU_HTTP, W + msg);
    }

    public static void w(String msg, Throwable tr) {
        Log.w(COU_HTTP, W + msg, tr);
    }

    public static boolean isLoggable(String s, int i) {
        return Log.isLoggable(s, i);
    }

}
