package droidkit.unit.fragment;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.FragmentTestUtil;

import droidkit.testapp.fragment.MainFragment;
import droidkit.unit.AndroidTestRunner;
import droidkit.view.Views;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidTestRunner.class)
public class MainFragmentTest {

    private MainFragment mFragment;

    @Before
    public void setUp() throws Exception {
        mFragment = new MainFragment();
        FragmentTestUtil.startVisibleFragment(mFragment);
    }

    @Test
    public void testViewInjected() throws Exception {
        Assert.assertNotNull(mFragment.getEditText());
        Assert.assertNotNull(mFragment.getButton1());
    }

    @Test
    public void testOnClick() throws Exception {
        Assert.assertNotNull(mFragment.getView());
        final View view = Views.findById(mFragment.getView(), android.R.id.button2);
        Robolectric.clickOn(view);
        Assert.assertSame(view, mFragment.getClickedView2());
    }

    @Test
    public void testOnClickWithoutArgs() throws Exception {
        Assert.assertNotNull(mFragment.getView());
        final View view = Views.findById(mFragment.getView(), android.R.id.button3);
        Robolectric.clickOn(view);
        Assert.assertSame(view, mFragment.getClickedView3());
    }

}