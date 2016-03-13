package cn.dengx.couhttp.cache;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * generate cache key
 * <p/>
 * <p/>
 * Current project:CouHttp.
 * Created by dengx on 16/3/9,21:45.
 */
public abstract class NameGenerator {
    public static final String IMAGE_WEBP = ".webp";
    public static final String IMAGE_PNG = ".png";
    public static final String IMAGE_JPG = ".jpg";

    public static final List<String> SUPPORT_POSTFIX = new ArrayList<>(3);

    static {
        SUPPORT_POSTFIX.add(IMAGE_JPG);
        SUPPORT_POSTFIX.add(IMAGE_PNG);
        SUPPORT_POSTFIX.add(IMAGE_WEBP);
    }

    private String postfix;
    private String entry;

    /**
     * @param text uri normal.
     *             but if you set with and height in ImageRequest,it is (width +'*'+ height +'-'+ uri)
     * @return
     */
    public abstract String generate(String text);

    protected void split(String text) {
        if (TextUtils.isEmpty(text)) {
            throw new IllegalArgumentException();
        }
        int index = text.lastIndexOf(".");
        if (index > 0) {
            String s = text.substring(index);
            if (SUPPORT_POSTFIX.contains(s)) {
                postfix = s;
                entry = text.substring(0, index);
            }
        }
        if (TextUtils.isEmpty(entry)) {
            entry = text;
        }
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "NameGenerator{" +
                "postfix='" + postfix + '\'' +
                ", entry='" + entry + '\'' +
                '}';
    }
}
