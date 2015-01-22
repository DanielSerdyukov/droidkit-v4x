package droidkit.activity;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import junit.framework.Assert;

import droidkit.view.Views;

/**
 * @author Daniel Serdyukov
 */
@LargeTest
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
    }

    public void testInjectView() throws Exception {
        final View expected = Views.findById(mActivity, droidkit.test.R.id.text1);
        Assert.assertNotNull(expected);
        Assert.assertEquals(expected, mActivity.mText1);
    }

    public void testOnClick() throws Exception {
        Espresso.onView(ViewMatchers
                .withId(droidkit.test.R.id.button1))
                .perform(ViewActions.click());
        Assert.assertTrue(mActivity.mButton1Clicked);
        Espresso.onView(ViewMatchers
                .withId(droidkit.test.R.id.button2))
                .perform(ViewActions.click());
        Assert.assertNotNull(mActivity.mButton2);
    }

    public void testOnActionClick() throws Exception {
        Espresso.onView(ViewMatchers
                .withId(droidkit.test.R.id.action_settings))
                .perform(ViewActions.click());
        Assert.assertTrue(mActivity.mSettingsActionClicked);
        Espresso.onView(ViewMatchers
                .withId(droidkit.test.R.id.action_add))
                .perform(ViewActions.click());
        Assert.assertNotNull(mActivity.mAddMenuItem);
    }

}
