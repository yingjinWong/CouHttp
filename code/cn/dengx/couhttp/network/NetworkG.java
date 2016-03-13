package cn.dengx.couhttp.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/2,20:48.
 */
public class NetworkG {
    public static final int NO_NETWORK = -1;
    public static final int NETWORK_2G = 2;
    public static final int NETWORK_3G = 3;
    public static final int NETWORK_4G = 4;
    public static final int NETWORK_WIFI = 9;

    /**
     * @param info
     * @return NO_NETWORK, NETWORK_2G, NETWORK_3G, NETWORK_4G or NETWORK_WIFI
     */
    public static int adjustNetwork(@NonNull NetworkInfo info) {
        if (!info.isConnectedOrConnecting())
            return NO_NETWORK;

        final int type = info.getType();
        switch (type) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                return NETWORK_WIFI;

            case ConnectivityManager.TYPE_MOBILE:
                switch (info.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        return NETWORK_4G;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        return NETWORK_3G;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return NETWORK_2G;
                }
            default:
                return NO_NETWORK;
        }
    }


    /**
     * @param network NO_NETWORK,NETWORK_2G,NETWORK_3G,NETWORK_4G or NETWORK_WIFI
     * @return
     */
    public static int adjustThreadNum(int network) {
        int num;
        switch (network) {
            case NO_NETWORK:
                num = 0;
                break;
            case NETWORK_2G:
                num = 2;
                break;
            case NETWORK_3G:
                num = 3;
                break;
            case NETWORK_4G:
                num = 4;
                break;
            case NETWORK_WIFI:
                num = 5;
                break;
            default:
                num = 0;
        }
        return num;
    }

}
