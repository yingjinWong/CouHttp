package cn.dengx.couhttp.network;

import android.net.NetworkInfo;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/2,22:14.
 */
public interface NetworkObserver {

    /**
     * the method will invoke in UI thread.
     *
     * @param network NetworkG.NO_NETWORK,NetworkG.NETWORK_2G
     *                NetworkG.NETWORK_3G,NetworkG.NETWORK_4G
     *                or NetworkG.NETWORK_WIFI.
     * @param info    may null
     */
    void onChange(int network, NetworkInfo info);
}
