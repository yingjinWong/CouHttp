package cn.dengx.couhttp;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/3,16:08.
 */
public interface Config {

    String getEncoding();

    int getConnectTimeOut();

    int getReadTimeOut();

    int getThreadNumber();

    void changedByNetwork(int network);

    Controller getController();
}
