package droidkit.crypto;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Daniel Serdyukov
 */
public class AesTest extends TestCase {

    private static final String ORIGINAL_STRING = "com.exzogeni.dk.test.crypto";

    private static final String KEY = "0259945b8ef3a4f79317fa04192fe964a988c742";

    private static final String AES_HEX_STRING = "f47c3b4ece50f6ebd22266ff55c52cc201dc2cd5c9a69e5e2a24c47b71783729";

    public void testEncrypt() throws Exception {
        Assert.assertEquals(AES_HEX_STRING, Aes.getInstance().encryptString(ORIGINAL_STRING, KEY));
    }

    public void testDecrypt() throws Exception {
        Assert.assertEquals(ORIGINAL_STRING, Aes.getInstance().decryptString(AES_HEX_STRING, KEY));
    }

}
