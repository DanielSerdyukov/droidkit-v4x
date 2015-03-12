package droidkit.unit.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import droidkit.crypto.Hex;

/**
 * @author Daniel Serdyukov
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class HexTest {

    private static final String DATA_STRING = "droidkit.unit.crypto";

    private static final byte[] DATA_BYTES = DATA_STRING.getBytes();

    private static final String HEX_STRING = "64726f69646b69742e756e69742e63727970746f";

    @Test
    public void testByteArrayToHex() throws Exception {
        Assert.assertEquals(HEX_STRING, Hex.toHexString(DATA_BYTES));
    }

    @Test
    public void testStringToHexString() throws Exception {
        Assert.assertEquals(HEX_STRING, Hex.toHexString(DATA_STRING));
    }

    @Test
    public void testByteArrayFromHex() throws Exception {
        Assert.assertArrayEquals(DATA_BYTES, Hex.fromHexString(HEX_STRING));
    }

}
