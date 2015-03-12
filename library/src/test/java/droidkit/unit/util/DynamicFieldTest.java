package droidkit.unit.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import droidkit.util.DynamicException;
import droidkit.util.DynamicField;

/**
 * @author Daniel Serdyukov
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class DynamicFieldTest {

    @Test
    public void testFindField() throws Exception {
        Assert.assertNotNull(DynamicField.find(MockObject2.class, "sPublicStaticValue"));
    }

    @Test(expected = DynamicException.class)
    public void testNoSuchField() throws Exception {
        DynamicField.find(MockObject2.class, "sStaticValue");
    }

    @Test
    public void testGetPublicStatic() throws Exception {
        MockObject2.sPublicStaticValue = 100;
        Assert.assertEquals(100, DynamicField.getStatic(MockObject2.class, "sPublicStaticValue"));
    }

    @Test
    public void testGetPrivateStatic() throws Exception {
        MockObject2.sPrivateStaticValue = 200;
        Assert.assertEquals(200, DynamicField.getStatic(MockObject2.class, "sPrivateStaticValue"));
    }

    @Test
    public void testGetInstance() throws Exception {
        final MockObject2 mock = new MockObject2(300);
        Assert.assertEquals(mock.mInstanceValue, DynamicField.get(mock, "mInstanceValue"));
    }

    @Test
    public void testSetPublicStatic() throws Exception {
        DynamicField.setStatic(MockObject2.class, "sPublicStaticValue", 400);
        Assert.assertEquals(400, MockObject2.sPublicStaticValue);
    }

    @Test
    public void testSetPrivateStatic() throws Exception {
        DynamicField.setStatic(MockObject2.class, "sPrivateStaticValue", 500);
        Assert.assertEquals(500, MockObject2.sPrivateStaticValue);
    }

    @Test
    public void testSetInstance() throws Exception {
        final MockObject2 mock = new MockObject2(300);
        DynamicField.set(mock, "mInstanceValue", 600);
        Assert.assertEquals(600, mock.mInstanceValue);
    }

    private static class MockObject2 {

        public static int sPublicStaticValue;

        private static int sPrivateStaticValue;

        private int mInstanceValue;

        public MockObject2(int instanceValue) {
            mInstanceValue = instanceValue;
        }

    }

}
