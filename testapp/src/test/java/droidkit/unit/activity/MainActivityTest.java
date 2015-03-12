package droidkit.unit.activity;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import droidkit.testapp.activity.MainActivity;
import droidkit.unit.AndroidTestRunner;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidTestRunner.class)
public class MainActivityTest {

    private MainActivity mActivity;

    @Before
    public void setUp() throws Exception {
        mActivity = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void testViewInjected() throws Exception {
        Assert.assertNotNull(mActivity.getEditText());
        Assert.assertNotNull(mActivity.getButton1());
    }

    @Test
    public void testOnClick() throws Exception {
        final View view = mActivity.findViewById(android.R.id.button2);
        Robolectric.clickOn(view);
        Assert.assertSame(view, mActivity.getClickedView2());
    }

    @Test
    public void testOnClickWithoutArgs() throws Exception {
        final View view = mActivity.findViewById(android.R.id.button3);
        Robolectric.clickOn(view);
        Assert.assertSame(view, mActivity.getClickedView3());
    }

}