package droidkit.util;

import junit.framework.Assert;
import junit.framework.TestCase;

import droidkit.util.DynamicField;

/**
 * @author Daniel Serdyukov
 */
public class DynamicFieldTest extends TestCase {

    private MockObject mObject;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockObject.sStaticField = MockObject.EXPECTED_VALUE;
        MockObject.sPrivateStaticField = MockObject.EXPECTED_VALUE;
        mObject = new MockObject();
    }

    public void testGetStaticPublic() throws Exception {
        Assert.assertEquals(MockObject.EXPECTED_VALUE, DynamicField.getStatic(MockObject.class, "sStaticField"));
    }

    public void testGetStaticPrivate() throws Exception {
        Assert.assertEquals(MockObject.EXPECTED_VALUE, DynamicField.getStatic(MockObject.class, "sPrivateStaticField"));
    }

    public void testSetStaticPublic() throws Exception {
        final String expected = "new_expected";
        DynamicField.setStatic(MockObject.class, "sStaticField", expected);
        Assert.assertEquals(expected, MockObject.sStaticField);
    }

    public void testSetStaticPrivate() throws Exception {
        final String expected = "new_expected";
        DynamicField.setStatic(MockObject.class, "sPrivateStaticField", expected);
        Assert.assertEquals(expected, MockObject.sPrivateStaticField);
    }

    public void testGetPublic() throws Exception {
        Assert.assertEquals(MockObject.EXPECTED_VALUE, DynamicField.get(mObject, "mInstanceField"));
    }

    public void testGetPrivate() throws Exception {
        Assert.assertEquals(MockObject.EXPECTED_VALUE, DynamicField.get(mObject, "mPrivateInstanceField"));
    }

    public void testSetPublic() throws Exception {
        final String expected = "new_expected";
        DynamicField.set(mObject, "mInstanceField", expected);
        Assert.assertEquals(expected, mObject.mInstanceField);
    }

    public void testSetPrivate() throws Exception {
        final String expected = "new_expected";
        DynamicField.set(mObject, "mPrivateInstanceField", expected);
        Assert.assertEquals(expected, mObject.mPrivateInstanceField);
    }

    private static class MockObject {

        static final String EXPECTED_VALUE = "expected";

        static String sStaticField = EXPECTED_VALUE;

        private static String sPrivateStaticField = EXPECTED_VALUE;

        String mInstanceField;

        private String mPrivateInstanceField;

        private MockObject() {
            mInstanceField = EXPECTED_VALUE;
            mPrivateInstanceField = EXPECTED_VALUE;
        }

    }
}
