package droidkit.activity;

import android.app.Fragment;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.android.ActivityRule;
import org.junit.runner.RunWith;

import droidkit.view.Views;
import test.mock.MockActivity1;
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
    public void androidText1() {
        final View expected = Views.findById(mActivity, android.R.id.text1);
        Assert.assertNotNull(expected);
        Assert.assertSame(expected, mActivity.getAndroidText1());
    }

    @Test
    public void droidkitText1() {
        final View expected = Views.findById(mActivity, droidkit.test.R.id.text1);
        Assert.assertNotNull(expected);
        Assert.assertSame(expected, mActivity.getDroidkitText1());
    }

    @Test
    public void fragmentAndroidButton1() {
        final View expected = Views.findById(mActivity, android.R.id.button1);
        Assert.assertNotNull(expected);
        final Fragment fragment = mActivity.getFragmentManager().findFragmentById(droidkit.test.R.id.content);
        Assert.assertNotNull(fragment);
        Assert.assertSame(expected, ((MockFragment1) fragment).getAndroidButton1());
    }

    @Test
    public void fragmentDroidKitButton1() {
        final View expected = Views.findById(mActivity, droidkit.test.R.id.button1);
        Assert.assertNotNull(expected);
        final Fragment fragment = mActivity.getFragmentManager().findFragmentById(droidkit.test.R.id.content);
        Assert.assertNotNull(fragment);
        Assert.assertSame(expected, ((MockFragment1) fragment).getDroidKitButton1());
    }

}
