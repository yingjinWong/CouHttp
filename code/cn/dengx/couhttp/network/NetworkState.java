package cn.dengx.couhttp.network;

import android.net.NetworkInfo;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/2,21:58.
 */
public class NetworkState {

    private int currentNetwork;
    private NetworkInfo networkInfo;

    public synchronized int getCurrentNetwork() {
        return currentNetwork;
    }

    public synchronized void setCurrentNetwork(int currentNetwork) {
        this.currentNetwork = currentNetwork;
    }

    public synchronized NetworkInfo getNetworkInfo() {
        return networkInfo;
    }

    public synchronized void setNetworkInfo(NetworkInfo networkInfo) {
        this.networkInfo = networkInfo;
    }

    @Override
    public String toString() {
        return "NetworkState{" +
                "currentNetwork=" + currentNetwork +
                ", networkInfo=" + networkInfo +
                '}';
    }
}
