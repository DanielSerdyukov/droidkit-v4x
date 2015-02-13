package droidkit.util;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidJUnit4.class)
public class DynamicTest {

    @Test
    public void getCaller() throws Exception {
        final StackTraceElement ste = getCallerWrap();
        Assert.assertEquals("getCaller", ste.getMethodName());
    }

    @Test
    public void forName() throws Exception {
        Assert.assertEquals(Sequence.class, Dynamic.<Sequence>forName("droidkit.util.Sequence"));
    }

    @Test
    public void initByClass() throws Exception {
        final MockObject instance = Dynamic.init(MockObject.class, "John", 25);
        Assert.assertNotNull(instance);
        Assert.assertEquals("John", instance.mName);
        Assert.assertEquals(25, instance.mAge);
    }

    @Test
    public void initByClassName() throws Exception {
        final MockObject instance = Dynamic.init(MockObject.class.getName(), "John", 25);
        Assert.assertNotNull(instance);
        Assert.assertEquals("John", instance.mName);
        Assert.assertEquals(25, instance.mAge);
    }

    private StackTraceElement getCallerWrap() {
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
