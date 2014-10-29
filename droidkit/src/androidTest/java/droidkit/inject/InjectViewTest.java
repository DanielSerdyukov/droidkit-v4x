package droidkit.inject;

import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

/**
 * @author Daniel Serdyukov
 */
public class InjectViewTest extends ActivityInstrumentationTestCase2<MockActivity> {

    public InjectViewTest() {
        super(MockActivity.class);
    }

    public void testInject() throws Exception {
        Assert.assertNotNull(getActivity().getText1());
    }

    public void testInjectPrivate() throws Exception {
        Assert.assertNotNull(getActivity().getButton1());
    }

}
