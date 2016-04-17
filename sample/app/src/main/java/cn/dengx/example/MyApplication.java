package cn.dengx.example;

import android.Manifest;
import android.app.Application;
import android.support.v4.content.PermissionChecker;

import cn.dengx.couhttp.CouHttp;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/8,17:24.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CouHttp couHttp = CouHttp.getInstance();
        couHttp.init(getApplicationContext());
        couHttp.setDebug(true);
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PermissionChecker.PERMISSION_GRANTED)
            couHttp.setDiskCache();
    }
}
