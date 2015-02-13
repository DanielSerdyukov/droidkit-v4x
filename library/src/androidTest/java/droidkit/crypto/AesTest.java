package droidkit.crypto;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidJUnit4.class)
public class AesTest {

    private static final String KEY = "droidkit.aes";

    private static final String ORIGINAL_STRING = "droidkit.crypto";

    private static final String AES_HEX_STRING = "1bdf2285141ec120a316bd0a0af5ddd6";

    @Test
    public void encrypt() throws Exception {
        Assert.assertEquals(AES_HEX_STRING, Aes.getInstance().encryptString(ORIGINAL_STRING, KEY));
    }

    @Test
    public void decrypt() throws Exception {
        Assert.assertEquals(ORIGINAL_STRING, Aes.getInstance().decryptString(AES_HEX_STRING, KEY));
    }

}
