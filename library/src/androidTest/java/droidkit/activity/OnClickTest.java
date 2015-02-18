package droidkit.activity;

import android.app.Fragment;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.android.ActivityRule;
import org.junit.runner.RunWith;

import test.mock.OnClickActivity;
import test.mock.OnClickFragment;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidJUnit4.class)
public class OnClickTest {

    private final ActivityRule<OnClickActivity> mRule = new ActivityRule<>(OnClickActivity.class);

    private OnClickActivity mActivity;

    @Before
    public void setUp() throws Exception {
        mActivity = mRule.get();
    }

    @Test
    public void button1Click() throws Exception {
        Espresso.onView(ViewMatchers
                .withId(android.R.id.button1))
                .perform(ViewActions.click());
        Assert.assertEquals(android.R.id.button1, mActivity.getClickedId1());
    }

    @Test
    public void button2Click() throws Exception {
        Espresso.onView(ViewMatchers
                .withId(android.R.id.button2))
                .perform(ViewActions.click());
        Assert.assertEquals(android.R.id.button2, mActivity.getClickedId2());
    }

    @Test
    public void fragmentButton1Click() throws Exception {
        final Fragment fragment = mActivity.getFragmentManager().findFragmentById(droidkit.test.R.id.content);
        Assert.assertNotNull(fragment);
        Espresso.onView(ViewMatchers
                .withId(droidkit.test.R.id.button1))
                .perform(ViewActions.click());
        Assert.assertEquals(droidkit.test.R.id.button1, ((OnClickFragment) fragment).getClickedId1());
    }

    @Test
    public void fragmentButton2Click() throws Exception {
        final Fragment fragment = mActivity.getFragmentManager().findFragmentById(droidkit.test.R.id.content);
        Assert.assertNotNull(fragment);
        Espresso.onView(ViewMatchers
                .withId(droidkit.test.R.id.button2))
                .perform(ViewActions.click());
        Assert.assertEquals(droidkit.test.R.id.button2, ((OnClickFragment) fragment).getClickedId2());
    }

}
