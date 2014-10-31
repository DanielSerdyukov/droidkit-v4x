package droidkit.inject;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;

import junit.framework.Assert;

import droidkit.inject.mock.InjectActivity;
import droidkit.inject.mock.InjectFragment;

/**
 * @author Daniel Serdyukov
 */
@Suppress
public class InjectViewTest extends ActivityInstrumentationTestCase2<InjectActivity> {

    private InjectActivity mActivity;

    private InjectFragment mFragment;

    public InjectViewTest() {
        super(InjectActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        mFragment = new InjectFragment();
        mActivity.getFragmentManager()
                .beginTransaction()
                .add(droidkit.test.R.id.fragment, mFragment)
                .commit();
        getInstrumentation().waitForIdleSync();
    }

    public void testPreconditions() throws Exception {
        Assert.assertNotNull("Fragment view is null", mFragment.getView());
    }

    public void testActivityInjectView1() throws Exception {
        Assert.assertNotNull(mActivity.getFrame());
    }

    public void testActivityInjectView2() throws Exception {
        Assert.assertNotNull(mActivity.getButton1());
    }

    public void testFragmentInjectView3() throws Exception {
        Assert.assertNotNull(mFragment.getListView());
    }

    public void testFragmentInjectView4() throws Exception {
        Assert.assertNotNull(mFragment.getButton1());
    }

}
