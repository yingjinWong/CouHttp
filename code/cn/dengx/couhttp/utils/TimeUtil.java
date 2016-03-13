package cn.dengx.couhttp.utils;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/6,12:04.
 */
public class TimeUtil {
    private TimeUtil() {
    }

    /**
     * get a time string yyyy-MM-dd HH:mm:ss
     *
     * @param t
     * @return
     */
    public static String getTime(long t) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date(t));
    }

    /**
     * get a time string yyyy-MM-dd HH:mm:ss
     *
     * @param t
     * @return
     */
    public static String getTime(@NonNull Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * get a time string HH:mm:ss
     *
     * @param t
     * @return
     */
    public static String getTime1(long t) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date(t));
    }

    /**
     * get a time string HH:mm:ss
     *
     * @param t
     * @return
     */
    public static String getTime1(@NonNull Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
    }


}
