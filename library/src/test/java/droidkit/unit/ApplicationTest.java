package droidkit.unit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * @author Daniel Serdyukov
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class ApplicationTest {

    @Test
    public void testApplication() throws Exception {
        Assert.assertNotNull(Robolectric.application);
    }

}
