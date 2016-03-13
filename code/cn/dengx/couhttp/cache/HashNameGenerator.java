package cn.dengx.couhttp.cache;

import android.text.TextUtils;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/9,22:31.
 */
public class HashNameGenerator extends NameGenerator {

    @Override
    public String generate(String text) {
        split(text);
        String hash = String.valueOf(getEntry().hashCode());
        if (!TextUtils.isEmpty(getPostfix()))
            hash += getPostfix();
        return hash;
    }
}
