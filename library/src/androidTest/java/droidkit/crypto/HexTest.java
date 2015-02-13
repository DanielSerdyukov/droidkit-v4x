package droidkit.crypto;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidJUnit4.class)
public class HexTest {

    private static final String ORIGINAL = "droidkit.crypto";

    private static final byte[] ORIGINAL_BYTES = ORIGINAL.getBytes();

    private static final String HEX_STRING = "64726f69646b69742e63727970746f";

    @Test
    public void toHexString() throws Exception {
        Assert.assertEquals(HEX_STRING, Hex.toHexString(ORIGINAL));
    }

    @Test
    public void fromHexString() throws Exception {
        Assert.assertArrayEquals(ORIGINAL_BYTES, Hex.fromHexString(HEX_STRING));
    }

}
