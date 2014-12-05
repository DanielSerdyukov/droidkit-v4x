package droidkit.crypto;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Daniel Serdyukov
 */
public class DigestTest extends TestCase {

    private static final String ORIGINAL_STRING = "com.exzogeni.dk.test.crypto";

    private static final String MD5_STRING = "7dd0d8cf2ac40e5292ae8fc52cc4c0c8";

    private static final String SHA1_STRING = "a51ea9134f52805c384b19600ea2e9e50a975f11";

    private static final String SHA256_STRING = "577a4218999599eec25a0a3e077f0f4578df401d42a159ee8e2b7847abd18d0d";

    public void testDefault() throws Exception {
        Assert.assertEquals(SHA1_STRING, Digest.getInstance().hashString(ORIGINAL_STRING, null));
    }

    public void testMd5() throws Exception {
        Assert.assertEquals(MD5_STRING, Digest.getInstance().hashString(ORIGINAL_STRING, Digest.MD5));
    }

    public void testSha1() throws Exception {
        Assert.assertEquals(SHA1_STRING, Digest.getInstance().hashString(ORIGINAL_STRING, Digest.SHA1));
    }

    public void testSha256() throws Exception {
        Assert.assertEquals(SHA256_STRING, Digest.getInstance().hashString(ORIGINAL_STRING, Digest.SHA256));
    }

}
