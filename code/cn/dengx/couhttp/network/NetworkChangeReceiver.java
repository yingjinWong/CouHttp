package cn.dengx.couhttp.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.net.ConnectivityManagerCompat;

import cn.dengx.couhttp.CouLog;
import cn.dengx.couhttp.utils.NetworkUtil;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/2,21:39.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private final NetworkState networkState;
    private final NetworkObservable observable;
    private final Context context;

    public NetworkChangeReceiver(@NonNull NetworkState networkState, @NonNull NetworkObservable observale,
                                 @NonNull Context context) {
        this.networkState = networkState;
        this.observable = observale;
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo info = ConnectivityManagerCompat.getNetworkInfoFromBroadcast(cm, intent);
                setNetworkStatus(info);
            }
            observable.dispatchChange(networkState);
        }
    }

    private void setNetworkStatus(NetworkInfo info) {
        NetworkUtil.setNetworkState(info, networkState);
        if (CouLog.debug)
            CouLog.d("get network info from broadcast" + networkState.toString());
    }

    public void register() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this,filter);
    }

    public void unregister(){
        context.unregisterReceiver(this);
    }

}
