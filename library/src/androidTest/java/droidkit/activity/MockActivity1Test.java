package droidkit.activity;

import android.app.Fragment;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.android.ActivityRule;
import org.junit.runner.RunWith;

import droidkit.view.Views;
import test.mock.MockActivity1;
import test.mock.MockAlert1;
import test.mock.MockDialog1;
import test.mock.MockFragment1;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidJUnit4.class)
public class MockActivity1Test {

    final ActivityRule<MockActivity1> mRule = new ActivityRule<>(MockActivity1.class);

    private MockActivity1 mActivity;

    @Before
    public void setUp() throws Exception {
        mActivity = mRule.get();
    }

    @Test
    public void androidButton1() {
        final View expected = Views.findById(mActivity, android.R.id.button1);
        Assert.assertNotNull(expected);
        Assert.assertSame(expected, mActivity.getAndroidButton1());
    }

    @Test
    public void androidButton1Click() {
        Espresso.onView(ViewMatchers
                .withId(android.R.id.button1))
                .perform(ViewActions.click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        final Fragment fragment = mActivity.getFragmentManager().findFragmentByTag(MockDialog1.class.getName());
        Assert.assertNotNull(fragment);
        Assert.assertNotNull(((MockDialog1) fragment).getChoice());
    }

    @Test
    public void droidkitButton1() {
        final View expected = Views.findById(mActivity, droidkit.test.R.id.button1);
        Assert.assertNotNull(expected);
        Assert.assertSame(expected, mActivity.getDroidkitButton1());
    }

    @Test
    public void droidkitButton1Click() {
        Espresso.onView(ViewMatchers
                .withId(droidkit.test.R.id.button1))
                .perform(ViewActions.click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        final Fragment fragment = mActivity.getFragmentManager().findFragmentByTag(MockAlert1.class.getName());
        Assert.assertNotNull(fragment);
        Assert.assertNotNull(((MockAlert1) fragment).getChoice());
    }

    @Test
    public void fragmentAndroidText1() {
        final View expected = Views.findById(mActivity, android.R.id.text1);
        Assert.assertNotNull(expected);
        final Fragment fragment = mActivity.getFragmentManager().findFragmentById(droidkit.test.R.id.content);
        Assert.assertNotNull(fragment);
        Assert.assertSame(expected, ((MockFragment1) fragment).getAndroidText1());
    }

    @Test
    public void fragmentDroidKitText1() {
        final View expected = Views.findById(mActivity, droidkit.test.R.id.text1);
        Assert.assertNotNull(expected);
        final Fragment fragment = mActivity.getFragmentManager().findFragmentById(droidkit.test.R.id.content);
        Assert.assertNotNull(fragment);
        Assert.assertSame(expected, ((MockFragment1) fragment).getDroidKitText1());
    }

}
