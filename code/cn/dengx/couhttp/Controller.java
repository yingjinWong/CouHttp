package cn.dengx.couhttp;

import java.net.ProtocolException;
import java.net.URI;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/5,14:47.
 */
public interface Controller {
    Class<? extends Connector> control(URI uri) throws ProtocolException;
}
