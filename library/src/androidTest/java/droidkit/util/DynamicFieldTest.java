package droidkit.util;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidJUnit4.class)
public class DynamicFieldTest {

    private MockObject mObject;

    @Before
    public void setUp() throws Exception {
        MockObject.sStaticField = MockObject.EXPECTED_VALUE;
        MockObject.sPrivateStaticField = MockObject.EXPECTED_VALUE;
        mObject = new MockObject();
    }

    @Test
    public void getStaticPublic() throws Exception {
        Assert.assertEquals(MockObject.EXPECTED_VALUE, DynamicField.getStatic(MockObject.class, "sStaticField"));
    }

    @Test
    public void getStaticPrivate() throws Exception {
        Assert.assertEquals(MockObject.EXPECTED_VALUE, DynamicField.getStatic(MockObject.class, "sPrivateStaticField"));
    }

    @Test
    public void setStaticPublic() throws Exception {
        final String expected = "new_expected";
        DynamicField.setStatic(MockObject.class, "sStaticField", expected);
        Assert.assertEquals(expected, MockObject.sStaticField);
    }

    @Test
    public void setStaticPrivate() throws Exception {
        final String expected = "new_expected";
        DynamicField.setStatic(MockObject.class, "sPrivateStaticField", expected);
        Assert.assertEquals(expected, MockObject.sPrivateStaticField);
    }

    @Test
    public void getPublic() throws Exception {
        Assert.assertEquals(MockObject.EXPECTED_VALUE, DynamicField.get(mObject, "mInstanceField"));
    }

    @Test
    public void getPrivate() throws Exception {
        Assert.assertEquals(MockObject.EXPECTED_VALUE, DynamicField.get(mObject, "mPrivateInstanceField"));
    }

    @Test
    public void setPublic() throws Exception {
        final String expected = "new_expected";
        DynamicField.set(mObject, "mInstanceField", expected);
        Assert.assertEquals(expected, mObject.mInstanceField);
    }

    @Test
    public void setPrivate() throws Exception {
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
