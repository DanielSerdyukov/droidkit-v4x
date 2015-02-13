package droidkit.crypto;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidJUnit4.class)
public class DigestTest {

    private static final String ORIGINAL = "droidkit.crypto";

    private static final String MD5_SUM = "11c4304cd0a0cd88b107a543c60dfc3f";

    private static final String SHA1_SUM = "af8ce1193d709ef1dd371963bb87955f14b00e18";

    private static final String SHA256_SUM = "ad291cddcddd0417da7088f932f81e8b4db590a33ecfec90c470dd1f80828479";

    @Test
    public void md5() throws Exception {
        Assert.assertEquals(MD5_SUM, Digest.md5sum(ORIGINAL));
    }

    @Test
    public void sha1() throws Exception {
        Assert.assertEquals(SHA1_SUM, Digest.sha1sum(ORIGINAL));
    }

    @Test
    public void sha256() throws Exception {
        Assert.assertEquals(SHA256_SUM, Digest.sha256sum(ORIGINAL));
    }

}
