package droidkit.crypto;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Arrays;

import droidkit.crypto.Hex;

/**
 * @author Daniel Serdyukov
 */
public class HexTest extends TestCase {

    private static final String ORIGINAL_STRING = "com.exzogeni.dk.test.crypto";

    private static final byte[] ORIGINAL_STRING_BYTES = ORIGINAL_STRING.getBytes();

    private static final String HEX_STRING = "636f6d2e65787a6f67656e692e646b2e746573742e63727970746f";

    public void testToHexString() throws Exception {
        Assert.assertEquals(HEX_STRING, Hex.toHexString(ORIGINAL_STRING));
        Assert.assertEquals(HEX_STRING.toUpperCase(), Hex.toHexString(ORIGINAL_STRING, true));
    }

    public void testFromHexString() throws Exception {
        Assert.assertEquals(ORIGINAL_STRING, new String(Hex.fromHexString(HEX_STRING)));
        Assert.assertTrue(Arrays.equals(ORIGINAL_STRING_BYTES, Hex.fromHexString(HEX_STRING)));
    }

}
