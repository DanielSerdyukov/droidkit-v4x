package droidkit.util;

import junit.framework.Assert;
import junit.framework.TestCase;

import droidkit.util.DynamicMethod;

/**
 * @author Daniel Serdyukov
 */
public class DynamicMethodTest extends TestCase {

    private MockObject mObject;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockObject.sPublicStaticMethodCalled = false;
        MockObject.sPrivateStaticMethodCalled = false;
        mObject = new MockObject();
    }

    public void testInvokeStaticPublic() throws Exception {
        DynamicMethod.invokeStatic(MockObject.class, "publicStaticMethod", 1, 2.5, "Test");
        Assert.assertTrue(MockObject.sPublicStaticMethodCalled);
    }

    public void testInvokeStaticPrivate() throws Exception {
        DynamicMethod.invokeStatic(MockObject.class, "privateStaticMethod", 1, 2.5, "Test");
        Assert.assertTrue(MockObject.sPrivateStaticMethodCalled);
    }

    public void testInvokePublic() throws Exception {
        final String actual = DynamicMethod.invoke(mObject, "publicInstanceMethod", "Test");
        Assert.assertTrue(mObject.mPublicInstanceMethodCalled);
        Assert.assertEquals("Test", actual);
    }

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
            Assert.assertEquals(2.5, d);
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
