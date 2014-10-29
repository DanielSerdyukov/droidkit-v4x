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

    public void testActivityInject() throws Exception {
        Assert.assertNotNull(getActivity().getText1());
    }

    public void testActivityInjectPrivate() throws Exception {
        Assert.assertNotNull(getActivity().getButton1());
    }

    public void testFragmentOnActivityCreatedCalled() throws Exception {
        Assert.assertTrue(getFragment().isOnActivityCreatedCalled());
    }

    public void testFragmentInject() throws Exception {
        Assert.assertNotNull(getFragment().getListView());
    }

    public void testFragmentInjectPrivate() throws Exception {
        Assert.assertNotNull(getFragment().getInputArea());
    }

    private MockFragment getFragment() {
        return (MockFragment) getActivity().getFragmentManager().findFragmentById(droidkit.test.R.id.fragment);
    }

}
