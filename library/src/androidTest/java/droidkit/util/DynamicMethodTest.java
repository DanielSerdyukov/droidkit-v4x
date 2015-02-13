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
public class DynamicMethodTest {

    private MockObject mObject;

    @Before
    public void setUp() throws Exception {
        MockObject.sPublicStaticMethodCalled = false;
        MockObject.sPrivateStaticMethodCalled = false;
        mObject = new MockObject();
    }

    @Test
    public void invokeStaticPublic() throws Exception {
        DynamicMethod.invokeStatic(MockObject.class, "publicStaticMethod", 1, 2.5, "Test");
        Assert.assertTrue(MockObject.sPublicStaticMethodCalled);
    }

    @Test
    public void invokeStaticPrivate() throws Exception {
        DynamicMethod.invokeStatic(MockObject.class, "privateStaticMethod", 1, 2.5, "Test");
        Assert.assertTrue(MockObject.sPrivateStaticMethodCalled);
    }

    @Test
    public void invokePublic() throws Exception {
        final String actual = DynamicMethod.invoke(mObject, "publicInstanceMethod", "Test");
        Assert.assertTrue(mObject.mPublicInstanceMethodCalled);
        Assert.assertEquals("Test", actual);
    }

    @Test
    public void testInvokePrivate() throws Exception {
        final int actual = DynamicMethod.invoke(mObject, "privateInstanceMethod", 1);
        Assert.assertTrue(mObject.mPrivateInstanceMethodCalled);
        Assert.assertEquals(1, actual);
    }

    private static class MockObject {

        static boolean sPublicStaticMethodCalled;

        static boolean sPrivateStaticMethodCalled;

        boolean mPublicInstanceMethodCalled;

        boolean mPrivateInstanceMethodCalled;

        public static void publicStaticMethod(int i, Double d, String s) {
            sPublicStaticMethodCalled = true;
            Assert.assertEquals(1, i);
            Assert.assertEquals(d, d);
            Assert.assertEquals("Test", s);
        }

        private static void privateStaticMethod(Integer i, double d, String s) {
            sPrivateStaticMethodCalled = true;
            Assert.assertEquals(Integer.valueOf(1), i);
            Assert.assertEquals(2.5, d, 0);
            Assert.assertEquals("Test", s);
        }

        public String publicInstanceMethod(String s) {
            mPublicInstanceMethodCalled = true;
            return s;
        }

        public int privateInstanceMethod(Integer i) {
            mPrivateInstanceMethodCalled = true;
            return i;
        }

    }
}
