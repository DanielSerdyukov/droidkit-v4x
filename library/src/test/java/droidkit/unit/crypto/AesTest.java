package droidkit.unit.crypto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import droidkit.crypto.Aes;

/**
 * @author Daniel Serdyukov
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class AesTest {

    private static final String STRING_KEY = "droidkit.aes";

    private static final String STRING_DATA = "droidkit.crypto";

    private static final String AES_HEX_STRING = "c4aade1343a92079dd719f06b3cd533a";

    private Aes mAes;

    @Before
    public void setUp() throws Exception {
        mAes = new Aes("AES/CBC/PKCS5Padding", "PBKDF2WithHmacSHA1", 128);

    }

    @Test
    public void testEncrypt() throws Exception {
        Assert.assertEquals(AES_HEX_STRING, mAes.encryptString(STRING_DATA, STRING_KEY));
    }

    @Test
    public void testDecrypt() throws Exception {
        Assert.assertEquals(STRING_DATA, mAes.decryptString(AES_HEX_STRING, STRING_KEY));
    }

}
