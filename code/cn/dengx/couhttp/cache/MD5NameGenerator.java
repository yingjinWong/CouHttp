package cn.dengx.couhttp.cache;

import android.text.TextUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Current project:CouHttp.
 * Created by dengx on 16/3/9,22:11.
 */
public class MD5NameGenerator extends NameGenerator {

    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 10 + 26; // 10 digits + 26 letters

    @Override
    public String generate(String text) {
        split(text);
        byte[] md5 = getMD5(getEntry().getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return TextUtils.isEmpty(getPostfix()) ? bi.toString(RADIX) : bi.toString(RADIX) + getPostfix();
    }

    private byte[] getMD5(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(data);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
        }
        return hash;
    }
}
