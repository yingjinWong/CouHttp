package cn.dengx.couhttp;

import android.support.annotation.NonNull;

import cn.dengx.couhttp.exception.InitialzationException;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/8,14:54.
 */
class HunterHelper {

    static Hunter getHunter(@NonNull Request request, @NonNull CouHttp couHttp) {
        if (couHttp.getBufferPool() == null)
            throw new InitialzationException();

        if (request.getResponseType() == Request.ResponseType.TYPE_STRING)
            return new StringHunter(request, couHttp.getConfig(), couHttp.getBufferPool(),
                    couHttp.getHandler(), couHttp.getAppContext());

        else if (request.getResponseType() == Request.ResponseType.TYPE_IMAGE) {
            return new ImageHunter(request, couHttp.getConfig(), couHttp.getBufferPool(),
                    couHttp.getHandler(), couHttp.getMemoryCache(), couHttp.getDiskCache(),
                    couHttp.getNameGenerator(), couHttp.getAppContext());

        } else if (request.getResponseType() == Request.ResponseType.TYPE_FILE) {
            return new FileHunter(request, couHttp.getConfig(), couHttp.getBufferPool(),
                    couHttp.getHandler(), couHttp.getAppContext());

        }
        return null;
    }

}
