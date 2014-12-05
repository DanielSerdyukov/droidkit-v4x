package droidkit.util;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Daniel Serdyukov
 */
public class DynamicTest extends TestCase {

    public void testGetCaller() throws Exception {
        final StackTraceElement ste = getCallerInternal();
        Assert.assertEquals("testGetCaller", ste.getMethodName());
    }

    public void testForName() throws Exception {
        Assert.assertEquals(Sequence.class, Dynamic.<Sequence>forName("droidkit.util.Sequence"));
    }

    public void testInitByClass() throws Exception {
        final MockObject instance = Dynamic.init(MockObject.class, "John", 25);
        Assert.assertNotNull(instance);
        Assert.assertEquals("John", instance.mName);
        Assert.assertEquals(25, instance.mAge);
    }

    public void testInitByClassName() throws Exception {
        final MockObject instance = Dynamic.init(MockObject.class.getName(), "John", 25);
        Assert.assertNotNull(instance);
        Assert.assertEquals("John", instance.mName);
        Assert.assertEquals(25, instance.mAge);
    }

    private StackTraceElement getCallerInternal() {
        return Dynamic.getCaller();
    }

    private static final class MockObject {

        private final String mName;

        private final int mAge;

        private MockObject(String name, int age) {
            mName = name;
            mAge = age;
        }

    }

}
