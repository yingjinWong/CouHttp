package cn.dengx.couhttp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import cn.dengx.couhttp.network.NetworkG;
import cn.dengx.couhttp.network.NetworkState;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/3,21:34.
 */
public class NetworkUtil {
    private NetworkUtil() {
    }

    public static void getCurrentNetwork(@NonNull Context context, @NonNull NetworkState networkState) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            setNetworkState(info, networkState);
        }
    }

    public static void setNetworkState(NetworkInfo info, @NonNull NetworkState networkState) {
        if (info == null)
            setNoNetwork(networkState);
        else {
            networkState.setNetworkInfo(info);
            networkState.setCurrentNetwork(NetworkG.adjustNetwork(info));
        }
    }

    public static void setNoNetwork(@NonNull NetworkState noNetwork) {
        noNetwork.setCurrentNetwork(NetworkG.NO_NETWORK);
    }
}
