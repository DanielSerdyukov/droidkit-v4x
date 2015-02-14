package droidkit.activity;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.android.ActivityRule;
import org.junit.runner.RunWith;

import test.mock.MockActivity1;

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
    public void androidText1NotNull() {
        Assert.assertNotNull(mActivity.getAndroidText1());
    }

    @Test
    public void droidkitText1NotNull() {
        Assert.assertNotNull(mActivity.getDroidkitText1());
    }

}
