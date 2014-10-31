package droidkit.inject;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.Suppress;

import junit.framework.Assert;

import droidkit.inject.mock.InjectActivity;

/**
 * @author Daniel Serdyukov
 */
@Suppress
public class ActivityOnClickTest extends ActivityInstrumentationTestCase2<InjectActivity> {

    private InjectActivity mActivity;

    public ActivityOnClickTest() {
        super(InjectActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        getInstrumentation().waitForIdleSync();
    }

    public void testActivityOnButton1Click() throws Exception {
        TouchUtils.clickView(this, mActivity.getButton1());
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue(mActivity.isButton1Clicked());
        Assert.assertEquals(mActivity.getButton1(), mActivity.getClickedView1());
    }

    public void testActivityOnButton2Click() throws Exception {
        TouchUtils.clickView(this, mActivity.findViewById(android.R.id.button2));
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue(mActivity.isButton2Clicked());
    }

    public void testActivityOnButton3Click() throws Exception {
        TouchUtils.clickView(this, mActivity.findViewById(android.R.id.button3));
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue(mActivity.isButton3Clicked());
    }

}
