package droidkit.sample.activity;

import android.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;

import com.google.android.apps.common.testing.ui.espresso.Espresso;
import com.google.android.apps.common.testing.ui.espresso.action.ViewActions;
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;

import junit.framework.Assert;

import droidkit.sample.R;
import droidkit.sample.fragment.Drawer;

/**
 * @author Daniel Serdyukov
 */
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
        Assert.assertNotNull(mActivity.getDrawer());
    }

    public void testOnClick() throws Exception {
        Espresso.onView(ViewMatchers
                .withId(android.R.id.button1))
                .perform(ViewActions.click());
        Assert.assertTrue(mActivity.isButton1Clicked());
    }

    public void testActionClick() throws Exception {
        Espresso.onView(ViewMatchers
                .withId(R.id.action_add))
                .perform(ViewActions.click());
        Assert.assertTrue(mActivity.isAddActionClicked());
    }

    public void testInjectViewInFragment() throws Exception {
        final Fragment fragment = mActivity.getFragmentManager().findFragmentById(R.id.drawer);
        Assert.assertNotNull(fragment);
        Assert.assertNotNull(((Drawer) fragment).getListView());
    }

    public void testLoaderInFragment() throws Exception {
        final Fragment fragment = mActivity.getFragmentManager().findFragmentById(R.id.drawer);
        Assert.assertNotNull(fragment);
        final Drawer drawer = (Drawer) fragment;
        Assert.assertTrue(drawer.isOnCreateLoader());
        Assert.assertTrue(drawer.isOnLoadFinished());
    }

}
