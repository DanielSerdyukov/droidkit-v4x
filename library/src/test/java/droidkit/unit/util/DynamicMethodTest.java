package droidkit.unit.util;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import droidkit.util.DynamicException;
import droidkit.util.DynamicMethod;

/**
 * @author Daniel Serdyukov
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class DynamicMethodTest {

    @Test
    public void testFindMethod() throws Exception {
        Assert.assertNotNull(DynamicMethod.find(MockObject1.class,
                "method1", String.class, Integer.TYPE, byte[].class));
    }

    @Test(expected = DynamicException.class)
    public void testNoSuchMethodException() throws Exception {
        DynamicMethod.find(MockObject1.class, "method1", String.class, Integer.TYPE);
    }

    @Test
    public void testInvoke() throws Exception {
        final MockObject1 mock = Mockito.mock(MockObject1.class);
        Mockito.when(mock.invokeDynamic()).thenReturn("invokeDynamic");
        Assert.assertEquals("invokeDynamic", DynamicMethod.<String>invoke(mock, "invokeDynamic"));
    }

    @Test
    public void testInvokePublicStatic() throws Exception {
        Assert.assertEquals("public static", DynamicMethod.<String>invokeStatic(MockObject2.class,
                "getPublicStaticModifier"));
    }

    @Test
    public void testInvokePrivateStatic() throws Exception {
        Assert.assertEquals("private static", DynamicMethod.<String>invokeStatic(MockObject2.class,
                "getPrivateStaticModifier"));
    }

    @Test
    public void testInvokePublic() throws Exception {
        Assert.assertEquals("public", DynamicMethod.<String>invoke(new MockObject2(), "getPublicModifier"));
    }

    @Test
    public void testInvokePrivate() throws Exception {
        Assert.assertEquals("private", DynamicMethod.<String>invoke(new MockObject2(), "getPrivateModifier"));
    }

    private interface MockObject1 {

        void method1(String a1, int a2, byte[] a3);

        String invokeDynamic();

    }

    private static class MockObject2 {

        public static String getPublicStaticModifier() {
            return "public static";
        }

        public static String getPrivateStaticModifier() {
            return "private static";
        }

        public String getPublicModifier() {
            return "public";
        }

        private String getPrivateModifier() {
            return "private";
        }

    }

}
