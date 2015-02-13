package droidkit.crypto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Daniel Serdyukov
 */
public class Digest {

    public static final String MD5 = "MD5";

    public static final String SHA1 = "SHA-1";

    public static final String SHA256 = "SHA-256";

    @NonNull
    public static byte[] md5(@NonNull byte[] data) throws DigestException {
        return hash(data, MD5);
    }

    @NonNull
    public static String md5sum(@NonNull String data) throws DigestException {
        return hashSum(data, MD5);
    }

    @NonNull
    public static byte[] sha1(@NonNull byte[] data) throws DigestException {
        return hash(data, SHA1);
    }

    @NonNull
    public static String sha1sum(@NonNull String data) throws DigestException {
        return hashSum(data, SHA1);
    }

    @NonNull
    public static byte[] sha256(@NonNull byte[] data) throws DigestException {
        return hash(data, SHA256);
    }

    @NonNull
    public static String sha256sum(@NonNull String data) throws DigestException {
        return hashSum(data, SHA256);
    }

    @NonNull
    public static byte[] hash(@NonNull byte[] data, @Nullable String algorithm) throws DigestException {
        String localAlgorithm = algorithm;
        if (TextUtils.isEmpty(localAlgorithm)) {
            localAlgorithm = SHA256;
        }
        try {
            final MessageDigest hash = MessageDigest.getInstance(localAlgorithm);
            hash.update(data);
            return hash.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new DigestException(e);
        }
    }

    @NonNull
    public static String hashSum(@NonNull String data, @Nullable String algorithm) throws DigestException {
        return Hex.toHexString(hash(data.getBytes(Hex.UTF_8), algorithm));
    }

}
