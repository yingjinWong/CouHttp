package cn.dengx.couhttp;

import android.os.Process;

import java.util.concurrent.ThreadFactory;

import cn.dengx.couhttp.utils.TimeUtil;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/3,21:22.
 */
public class CouThreadFactory implements ThreadFactory {
    public static final String THREAD_NAME = "couHttp_handle_thread_";

    private static int num;

    public CouThreadFactory() {
        num = 0;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new MyThread(r);
        thread.setName(THREAD_NAME + nextNum());
        if (CouLog.debug)
            CouLog.d("new thread " + thread.getName() + "  " + TimeUtil.getTime1(System.currentTimeMillis()));
        return thread;
    }

    private int nextNum() {
        return ++num;
    }

    public static class MyThread extends Thread {

        public MyThread(Runnable runnable) {
            super(runnable);
        }

        public MyThread(Runnable runnable, String threadName) {
            super(runnable, threadName);
        }

        public MyThread(String threadName) {
            super(threadName);
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }
}
