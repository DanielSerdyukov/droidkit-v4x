package droidkit.unit.util;

import junit.framework.Assert;

import org.junit.Test;

import droidkit.util.Dynamic;

/**
 * @author Daniel Serdyukov
 */
public class DynamicTest {

    @Test
    public void testGetCaller() throws Exception {
        final MockObject1 mock = new MockObject1();
        Assert.assertEquals("testGetCaller", mock.getCaller());
    }

    @Test
    public void testInit() throws Exception {
        final MockObject2 mock = Dynamic.init(MockObject2.class, "John", 1);
        Assert.assertEquals("John", mock.mName);
        Assert.assertEquals(1, mock.mId);
    }

    private static class MockObject1 {

        String getCaller() {
            return Dynamic.getCaller().getMethodName();
        }

    }

    private static class MockObject2 {

        private String mName;

        private int mId;

        public MockObject2(String name, int id) {
            mName = name;
            mId = id;
        }

    }

}
