package cn.dengx.couhttp;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.net.ProtocolException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/5,15:10.
 */
public class DefaultController implements Controller {

    private final Map<String, Class<? extends Connector>> connections;

    public DefaultController() {
        connections = new HashMap<>();
        addController(HttpConnector.SCHEME, HttpConnector.class);
        addController(AssetConnector.SCHEME, AssetConnector.class);
        addController(FileConnector.SCHEME, FileConnector.class);
        addController(ContentConnector.SCHEME, ContentConnector.class);
    }

    public DefaultController(@NonNull Map<String, Class<? extends Connector>> connections) {
        this.connections = connections;
    }

    public Class<? extends Connector> getController(String scheme) {
        scheme = scheme.toLowerCase();
        return connections.get(scheme);
    }

    public void addController(String scheme, Class<? extends Connector> connection) {
        connections.put(scheme.toLowerCase(), connection);
    }

    @Override
    public Class<? extends Connector> control(URI uri) throws ProtocolException {
        String scheme = uri.getScheme();
        if (TextUtils.isEmpty(scheme)) {
            throw new ProtocolException("uri:" + uri.toString() + " has no scheme");
        }
        scheme = scheme.toLowerCase();
        Class<? extends Connector> clazz = connections.get(scheme);
        if (clazz == null) {
            throw new ProtocolException("no connection to handle scheme " + scheme);
        }
        return clazz;
    }
}
