package droidkit.activity;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.android.ActivityRule;
import org.junit.runner.RunWith;

import test.mock.OnActionClickActivity2;
import test.mock.OnActionClickFragment;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidJUnit4.class)
public class OnActionClickTest2 {

    @Rule
    public final ActivityRule<OnActionClickActivity2> mRule = new ActivityRule<>(OnActionClickActivity2.class);

    private OnActionClickFragment mFragment;

    @Before
    public void setUp() throws Exception {
        final OnActionClickActivity2 activity = mRule.get();
        mFragment = (OnActionClickFragment) activity.getFragmentManager()
                .findFragmentById(droidkit.test.R.id.content);
        Assert.assertNotNull(mFragment);
    }

    @Test
    public void addActionClick() {
        Espresso.onView(ViewMatchers.withId(droidkit.test.R.id.action_add)).perform(ViewActions.click());
        Assert.assertEquals(droidkit.test.R.id.action_add, mFragment.getAddActionId());
    }

    @Test
    public void editActionClick() {
        Espresso.onView(ViewMatchers.withId(droidkit.test.R.id.action_edit)).perform(ViewActions.click());
        Assert.assertEquals(droidkit.test.R.id.action_edit, mFragment.getEditActionId());
    }

    @Test
    public void settingsActionClick() {
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        Espresso.onView(ViewMatchers.withText("Settings")).perform(ViewActions.click());
        Assert.assertEquals(droidkit.test.R.id.action_settings, mFragment.getSettingsActionId());
    }

    @Test
    public void helpActionClick() {
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        Espresso.onView(ViewMatchers.withText("Help")).perform(ViewActions.click());
        Assert.assertEquals(droidkit.test.R.id.action_help, mFragment.getHelpActionId());
    }

}
