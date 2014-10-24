package droidkit.util;

import junit.framework.Assert;
import junit.framework.TestCase;

import droidkit.Sequence;

/**
 * @author Daniel Serdyukov
 */
public class DynamicTest extends TestCase {

    public void testGetCaller() throws Exception {
        final StackTraceElement ste = getCallerInternal();
        Assert.assertEquals("testGetCaller", ste.getMethodName());
    }

    public void testForName() throws Exception {
        Assert.assertEquals(Sequence.class, Dynamic.<Sequence>forName("droidkit.Sequence"));
    }

    private StackTraceElement getCallerInternal() {
        return Dynamic.getCaller();
    }

}
