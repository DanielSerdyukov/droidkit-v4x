package droidkit.inject;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;

import junit.framework.Assert;

import droidkit.inject.mock.InjectActivity;
import droidkit.inject.mock.InjectFragment;

/**
 * @author Daniel Serdyukov
 */
public class FragmentInjectionTest extends ActivityInstrumentationTestCase2<InjectActivity> {

    private InjectFragment mFragment;

    public FragmentInjectionTest() {
        super(InjectActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mFragment = new InjectFragment();
        getActivity().getFragmentManager()
                .beginTransaction()
                .add(droidkit.test.R.id.fragment, mFragment)
                .commit();
        getInstrumentation().waitForIdleSync();
    }

    public void testActivityOnButton1Click() throws Exception {
        TouchUtils.clickView(this, mFragment.getButton1());
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue(mFragment.isButton1Clicked());
        Assert.assertEquals(mFragment.getButton1(), mFragment.getClickedView1());
    }

    public void testActivityOnButton2Click() throws Exception {
        TouchUtils.clickView(this, mFragment.getButton2());
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue(mFragment.isButton2Clicked());
    }

    public void testActivityOnButton3Click() throws Exception {
        TouchUtils.clickView(this, mFragment.getButton3());
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue(mFragment.isButton3Clicked());
    }

}
