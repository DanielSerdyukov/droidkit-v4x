package droidkit.unit.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import droidkit.crypto.Digest;

/**
 * @author Daniel Serdyukov
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class DigestTest {

    private static final String DATA_STRING = "droidkit.unit.crypto";

    private static final byte[] DATA_BYTES = DATA_STRING.getBytes();

    private static final byte[] MD5_BYTES = {-35, 122, 97, 45, 8, 23, -52, -59, 103, 110, 54, 33, -55, -76, -118, -70};

    private static final byte[] SHA1_BYTES = {-45, 82, -9, 117, 44, -53, 96, 54, 93, 43, 61, -62, -128, -96, -98, 36,
            70, 49, 94, 120};

    private static final byte[] SHA256_BYTES = {-13, 114, -8, 73, -10, 4, 103, -11, 67, 58, 106, -69, -35, 110, 32,
            -54, 72, -101, -32, 86, -12, 24, -117, -58, 66, 31, 114, 10, 33, 33, -115, 2};

    @Test
    public void testMd5() throws Exception {
        Assert.assertArrayEquals(MD5_BYTES, Digest.md5(DATA_BYTES));
    }

    @Test
    public void testMd5Sum() throws Exception {
        Assert.assertEquals("dd7a612d0817ccc5676e3621c9b48aba", Digest.md5sum(DATA_STRING));
    }

    @Test
    public void testSha1() throws Exception {
        Assert.assertArrayEquals(SHA1_BYTES, Digest.sha1(DATA_BYTES));
    }

    @Test
    public void testSha1Sum() throws Exception {
        Assert.assertEquals("d352f7752ccb60365d2b3dc280a09e2446315e78", Digest.sha1sum(DATA_STRING));
    }

    @Test
    public void testSha256() throws Exception {
        Assert.assertArrayEquals(SHA256_BYTES, Digest.sha256(DATA_BYTES));
    }

    @Test
    public void testSha256Sum() throws Exception {
        Assert.assertEquals("f372f849f60467f5433a6abbdd6e20ca489be056f4188bc6421f720a21218d02",
                Digest.sha256sum(DATA_STRING));
    }

}
