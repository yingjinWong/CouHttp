package cn.dengx.couhttp;

import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import java.util.concurrent.ThreadPoolExecutor;

import cn.dengx.couhttp.network.NetworkObserver;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/3,17:15.
 */
public class CouNetworkObsever implements NetworkObserver {

    private final Config config;
    private final ThreadPoolExecutor executorService;

    public CouNetworkObsever(@NonNull Config config, @NonNull ThreadPoolExecutor executorService) {
        this.config = config;
        this.executorService = executorService;
    }

    @Override
    public void onChange(int network, NetworkInfo info) {
        config.changedByNetwork(network);
        int num = config.getThreadNumber();
        executorService.setCorePoolSize(num);
        executorService.setMaximumPoolSize(num);
        if (CouLog.debug)
            CouLog.d("network state is " + network + " executorService set thread size " + num);
    }


}
