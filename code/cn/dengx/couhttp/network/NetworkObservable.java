package cn.dengx.couhttp.network;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import cn.dengx.couhttp.CouLog;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/2,22:13.
 */
public class NetworkObservable {
    private Handler handler;

    public NetworkObservable(Handler handler) {
        this.handler = handler;
    }

    protected final ArrayList<Task> tasks = new ArrayList<>();

    public void registerObserver(@NonNull NetworkObserver observer) {
        synchronized (tasks) {
            Task task = new Task(observer);
            if (!tasks.contains(task)) {
                tasks.add(task);
            }
        }
    }

    public void unregisterObserver(@NonNull NetworkObserver observer) {
        synchronized (tasks) {
            Task task = new Task(observer);
            int index = tasks.indexOf(task);
            if (index > -1)
                tasks.remove(index);
        }
    }

    public void unregisterAll() {
        synchronized (tasks) {
            tasks.clear();
        }
    }

    void dispatchChange(@NonNull NetworkState networkState) {
        synchronized (tasks) {
            for (Task task : tasks) {
                task.setNetworkState(networkState);
                if (handler != null) {
                    handler.post(task);
                } else {
                    try {
                        task.run();
                    } catch (Exception e) {
                        CouLog.e("dispatchChange run error", e);
                    }
                }
            }
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    static class Task implements Runnable {
        private NetworkObserver observer;
        private NetworkState networkState;

        public Task(NetworkObserver observer) {
            this.observer = observer;
        }

        @Override
        public void run() {
            if (observer != null && networkState != null) {
                observer.onChange(networkState.getCurrentNetwork(), networkState.getNetworkInfo());
            }
        }

        public NetworkObserver getObserver() {
            return observer;
        }

        public void setObserver(NetworkObserver observer) {
            this.observer = observer;
        }

        public NetworkState getNetworkState() {
            return networkState;
        }

        public void setNetworkState(NetworkState networkState) {
            this.networkState = networkState;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o instanceof Task) {
                Task task = (Task) o;
                return this.getObserver() == task.getObserver();
            }
            return false;
        }
    }
}
