package cn.dengx.couhttp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/8,15:27.
 */
public class CouHandler extends Handler {

    public static final int SHOW_PROGRESS_MESSAGE = 90;


    public CouHandler() {
        this(Looper.getMainLooper());
    }

    public CouHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_MESSAGE:
                int curr = msg.arg1;
                int total = msg.arg2;
                Response.ProgressListener l = (Response.ProgressListener) msg.obj;
                l.onProgress(curr,total);
                break;
        }
    }
}
