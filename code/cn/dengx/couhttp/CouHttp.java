package cn.dengx.couhttp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.dengx.couhttp.cache.BaseDiskCache;
import cn.dengx.couhttp.cache.DiskCache;
import cn.dengx.couhttp.cache.LruCache;
import cn.dengx.couhttp.cache.MD5NameGenerator;
import cn.dengx.couhttp.cache.NameGenerator;
import cn.dengx.couhttp.exception.CouHttpRuntimeException;
import cn.dengx.couhttp.exception.InitialzationException;
import cn.dengx.couhttp.network.NetworkChangeReceiver;
import cn.dengx.couhttp.network.NetworkObservable;
import cn.dengx.couhttp.network.NetworkState;
import cn.dengx.couhttp.utils.BitmapUtil;
import cn.dengx.couhttp.utils.DiskUtil;
import cn.dengx.couhttp.utils.NetworkUtil;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/2,22:59.
 */
public class CouHttp {

    private Handler handler;

    private NetworkState networkState;
    private NetworkObservable networkObservable;

    private Config config;

    private Context appContext;

    private ThreadPoolExecutor executor;

    private ByteBufferPool bufferPool;

    private NetworkChangeReceiver receiver;

    private NameGenerator nameGenerator;
    private LruCache<String, WeakReference<Bitmap>> memoryCache;
    private DiskCache diskCache;

    private static CouHttp instance;

    private CouHttp() {
    }


    public static CouHttp getInstance() {
        if (instance == null) {
            synchronized (CouHttp.class) {
                if (instance == null)
                    instance = new CouHttp();
            }
        }
        return instance;
    }

    /**
     * must invoke the method first
     *
     * @param appContext
     */
    public void init(Context appContext) {
        if (appContext == null)
            throw new IllegalArgumentException();
        this.appContext = appContext;
        if (networkObservable != null)
            throw new InitialzationException("CouHttp init yet");

        checkNetworkPermission();

        handler = new CouHandler();
        networkState = new NetworkState();
        NetworkUtil.getCurrentNetwork(appContext, networkState);

        Config c = getConfig();
        c.changedByNetwork(networkState.getCurrentNetwork());
        int threadNum = c.getThreadNumber();
        executor = new ThreadPoolExecutor(threadNum, threadNum, 0, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<Runnable>(), new CouThreadFactory());

        networkObservable = new NetworkObservable(handler);
        CouNetworkObsever obsever = new CouNetworkObsever(c, executor);
        networkObservable.registerObserver(obsever);

        bufferPool = new ByteBufferPool(6);

        receiver = new NetworkChangeReceiver(networkState, networkObservable, appContext);
        receiver.register();
        int memory = DiskUtil.calculateMemoryCacheSize(appContext);
        if (memory < DiskUtil.MINI_SIZE_INT)
            memory = DiskUtil.MINI_SIZE_INT;
        memoryCache = new LruCache<String, WeakReference<Bitmap>>(memory) {
            @Override
            protected int sizeOf(String key, WeakReference<Bitmap> value) {
                Bitmap bitmap = value.get();
                if (bitmap != null) {
                    return BitmapUtil.getBitmapSize(bitmap);
                }
                return 1;
            }
        };
        CouLog.i("new LruCache capacity " + memory / 1024 + "kB");
    }

    public void destory() {
        checkInit();
        networkObservable.unregisterAll();
        networkObservable = null;

        executor.shutdown();
        executor = null;

        receiver.unregister();
        receiver = null;

        bufferPool = null;

        instance = null;
    }

    public Request addRequest(@NonNull Request request) {
        request.setMainHandler(getHandler());
        Hunter hunter = HunterHelper.getHunter(request, this);
        if (hunter != null)
            executor.execute(hunter);
        else
            return null;
        return request;
    }

    public void setDebug(boolean debug) {
        CouLog.debug = debug;
    }

    public boolean getDebug() {
        return CouLog.debug;
    }

    public ByteBufferPool getBufferPool() {
        return bufferPool;
    }

    public void setBufferPool(ByteBufferPool bufferPool) {
        if (bufferPool == null)
            throw new NullPointerException();
        this.bufferPool = bufferPool;
    }

    public Config getConfig() {
        if (config == null)
            config = new DefaultConfig();
        return config;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setConfig(Config config) {
        if (networkObservable != null)
            throw new InitialzationException("the method must invoke before init()");
        this.config = config;
    }

    public Context getAppContext() {
        return appContext;
    }

    /**
     * 获得网络状态
     *
     * @return
     */
    public NetworkState getNetworkState() {
        checkInit();
        return networkState;
    }

    public NetworkObservable getNetworkObservable() {
        checkInit();
        return networkObservable;
    }

    public LruCache<String, WeakReference<Bitmap>> getMemoryCache() {
        return memoryCache;
    }

    public DiskCache getDiskCache() {
        return diskCache;
    }

    public void setDiskCache() {
        checkInit();
        if (diskCache == null) {
            checkExternalPermission();
            File dir = DiskUtil.getDiskCacheDirectory(getAppContext());
            if (dir != null)
                diskCache = new BaseDiskCache(getAppContext(), dir);
        }
    }

    public NameGenerator getNameGenerator() {
        if (nameGenerator == null)
            setNameGenerator(new MD5NameGenerator());
        return nameGenerator;
    }

    public void setNameGenerator(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    public void setDiskCache(DiskCache diskCache) {
        this.diskCache = diskCache;
    }

    private void checkExternalPermission() {
        if (ContextCompat.checkSelfPermission(getAppContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            throw new CouHttpRuntimeException("no permission permission.READ_EXTERNAL_STORAGE");
        if (ContextCompat.checkSelfPermission(getAppContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            throw new CouHttpRuntimeException("no permission permission.WRITE_EXTERNAL_STORAGE");
    }

    private void checkNetworkPermission() {
        if (ContextCompat.checkSelfPermission(getAppContext(),
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            throw new CouHttpRuntimeException("no permission permission.INTERNET");
        if (ContextCompat.checkSelfPermission(getAppContext(),
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
            throw new CouHttpRuntimeException("no permission permission.ACCESS_NETWORK_STATE");
    }

    private void checkInit() {
        if (networkObservable == null)
            throw new InitialzationException("you must invoke init() before");
    }
}
