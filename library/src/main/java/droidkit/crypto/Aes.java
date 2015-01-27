package droidkit.crypto;

import android.support.annotation.NonNull;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Daniel Serdyukov
 */
public class Aes {

    private static final String TRANSFORMATION = "AES/CBC/PKCS7Padding";

    private static final String KEY_FACTORY_ALG = "PBKDF2WithHmacSHA1";

    private static final int KEY_SIZE = 256;

    private static final String AES = "AES";

    private static final byte[] IV = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private final String mTransformation;

    private final String mKeyFactory;

    private final int mKeySize;

    public Aes(@NonNull String transformation, @NonNull String keyFactory, int keySize) {
        mTransformation = transformation;
        mKeyFactory = keyFactory;
        mKeySize = keySize;
    }

    public static Aes getInstance() {
        return Holder.INSTANCE;
    }

    @NonNull
    public byte[] encrypt(@NonNull byte[] data, @NonNull String key) throws DigestException {
        return doFinal(Cipher.ENCRYPT_MODE, data, key);
    }

    @NonNull
    public byte[] decrypt(@NonNull byte[] data, @NonNull String key) throws DigestException {
        return doFinal(Cipher.DECRYPT_MODE, data, key);
    }

    @NonNull
    public String encryptString(@NonNull String data, @NonNull String key) throws DigestException {
        return Hex.toHexString(encrypt(data.getBytes(Hex.UTF_8), key));
    }

    @NonNull
    public String decryptString(@NonNull String data, @NonNull String key) throws DigestException {
        return new String(decrypt(Hex.fromHexString(data), key), Hex.UTF_8);
    }

    @NonNull
    private SecretKey getSecretKey(@NonNull String key) throws DigestException {
        try {
            final SecretKeyFactory factory = SecretKeyFactory.getInstance(mKeyFactory);
            final KeySpec keySpec = new PBEKeySpec(key.toCharArray(), key.getBytes(Hex.UTF_8), 1024, mKeySize);
            return new SecretKeySpec(factory.generateSecret(keySpec).getEncoded(), AES);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new DigestException(e);
        }
    }

    @NonNull
    private byte[] doFinal(int mode, @NonNull byte[] data, @NonNull String key) throws DigestException {
        try {
            final Cipher cipher = Cipher.getInstance(mTransformation);
            cipher.init(mode, getSecretKey(key), new IvParameterSpec(IV));
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | InvalidAlgorithmParameterException e) {
            throw new DigestException(e);
        }
    }

    private static final class Holder {
        public static final Aes INSTANCE = new Aes(TRANSFORMATION, KEY_FACTORY_ALG, KEY_SIZE);
    }

}
