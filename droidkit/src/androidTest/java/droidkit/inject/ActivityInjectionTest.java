package droidkit.inject;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.Suppress;

import junit.framework.Assert;

import droidkit.inject.mock.InjectActivity;

/**
 * @author Daniel Serdyukov
 */
public class ActivityInjectionTest extends ActivityInstrumentationTestCase2<InjectActivity> {

    private InjectActivity mActivity;

    public ActivityInjectionTest() {
        super(InjectActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
        getInstrumentation().waitForIdleSync();
    }

    @Suppress
    public void testOnClickButton1() throws Exception {
        TouchUtils.clickView(this, mActivity.getButton1());
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue(mActivity.isButton1Clicked());
        Assert.assertEquals(mActivity.getButton1(), mActivity.getClickedView1());
    }

    @Suppress
    public void testOnClickButton2() throws Exception {
        TouchUtils.clickView(this, mActivity.findViewById(android.R.id.button2));
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue(mActivity.isButton2Clicked());
    }

    @Suppress
    public void testOnClickButton3() throws Exception {
        TouchUtils.clickView(this, mActivity.findViewById(android.R.id.button3));
        getInstrumentation().waitForIdleSync();
        Assert.assertTrue(mActivity.isButton3Clicked());
    }

    @Suppress
    public void testOnActionClickTest1() throws Exception {
        getInstrumentation().invokeMenuActionSync(mActivity, droidkit.test.R.id.action_test1, 0);
        Assert.assertTrue(mActivity.isActionTest1Clicked());
    }

    @Suppress
    public void testOnActionClickTest2() throws Exception {
        getInstrumentation().invokeMenuActionSync(mActivity, droidkit.test.R.id.action_test2, 0);
        Assert.assertTrue(mActivity.isActionTest2Clicked());
    }

    @Suppress
    public void testOnActionClickTest3() throws Exception {
        getInstrumentation().invokeMenuActionSync(mActivity, droidkit.test.R.id.action_test3, 0);
        Assert.assertEquals("test3", mActivity.getActionTest3Title());
    }

    public void testOnCreateLoader() throws Exception {
        Assert.assertEquals(droidkit.test.R.id.mock_loader_1, mActivity.getOnCreateLoaderId());
    }

    public void testOnLoadFinished() throws Exception {
        Assert.assertEquals(InjectActivity.LOADER_1_RESULT, mActivity.getOnLoadFinishedResult());
    }

}
