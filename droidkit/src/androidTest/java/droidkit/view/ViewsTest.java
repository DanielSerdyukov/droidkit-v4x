package droidkit.view;

import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

/**
 * @author Daniel Serdyukov
 */
public class ViewsTest extends ActivityInstrumentationTestCase2<MockActivity> {

    public ViewsTest() {
        super(MockActivity.class);
    }

    public void testFindById() throws Exception {
        Assert.assertNotNull(getActivity().getText1());
        Assert.assertNotNull(getActivity().getButton1());
    }

}
