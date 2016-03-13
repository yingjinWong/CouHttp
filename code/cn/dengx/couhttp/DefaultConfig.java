package cn.dengx.couhttp;

import cn.dengx.couhttp.network.NetworkG;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/3,16:21.
 */
public class DefaultConfig implements Config {

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final int SLOW_NETWORK_CONNECT_TIMEOUT = 16000;
    public static final int NORMAL_NETWORK_CONNECT_TIMEOUT = 12000;
    public static final int FAST_NETWORK_CONNECT_TIMEOUT = 6000;

    public static final int SLOW_NETWORK_READ_TIMEOUT = 20000;
    public static final int NORMAL_NETWORK_READ_TIMEOUT = 12000;
    public static final int FAST_NETWORK_READ_TIMEOUT = 8000;


    private int connectTimeOut;
    private int readTimeOut;
    private int threadNum;

    private Controller controller;

    public DefaultConfig() {
        this(null);
    }

    public DefaultConfig(Controller controller) {
        if (controller == null)
            this.controller = new DefaultController();
        else
            this.controller = controller;
    }

    @Override
    public void changedByNetwork(int network) {
        switch (network) {
            case NetworkG.NETWORK_2G:
                connectTimeOut = SLOW_NETWORK_CONNECT_TIMEOUT;
                readTimeOut = SLOW_NETWORK_READ_TIMEOUT;
                break;
            case NetworkG.NETWORK_3G:
            case NetworkG.NETWORK_4G:
                connectTimeOut = NORMAL_NETWORK_CONNECT_TIMEOUT;
                readTimeOut = NORMAL_NETWORK_READ_TIMEOUT;
                break;
            case NetworkG.NETWORK_WIFI:
                connectTimeOut = FAST_NETWORK_CONNECT_TIMEOUT;
                readTimeOut = FAST_NETWORK_READ_TIMEOUT;
                break;
            default:
                connectTimeOut = FAST_NETWORK_CONNECT_TIMEOUT;
                readTimeOut = FAST_NETWORK_READ_TIMEOUT;
        }
        threadNum = NetworkG.adjustThreadNum(network);
    }

    @Override
    public Controller getController() {
        return controller;
    }

    @Override
    public String getEncoding() {
        return DEFAULT_ENCODING;
    }

    @Override
    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    @Override
    public int getReadTimeOut() {
        return readTimeOut;
    }

    @Override
    public int getThreadNumber() {
        return threadNum;
    }
}
