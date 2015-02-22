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

import test.mock.OnActionClickActivity;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidJUnit4.class)
public class OnActionClickTest {

    @Rule
    public final ActivityRule<OnActionClickActivity> mRule = new ActivityRule<>(OnActionClickActivity.class);

    private OnActionClickActivity mActivity;

    @Before
    public void setUp() throws Exception {
        mActivity = mRule.get();
    }

    @Test
    public void addActionClick() {
        Espresso.onView(ViewMatchers.withId(droidkit.test.R.id.action_add)).perform(ViewActions.click());
        Assert.assertEquals(droidkit.test.R.id.action_add, mActivity.getAddActionId());
    }

    @Test
    public void editActionClick() {
        Espresso.onView(ViewMatchers.withId(droidkit.test.R.id.action_edit)).perform(ViewActions.click());
        Assert.assertEquals(droidkit.test.R.id.action_edit, mActivity.getEditActionId());
    }

    @Test
    public void settingsActionClick() {
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        Espresso.onView(ViewMatchers.withText("Settings")).perform(ViewActions.click());
        Assert.assertEquals(droidkit.test.R.id.action_settings, mActivity.getSettingsActionId());
    }

    @Test
    public void helpActionClick() {
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        Espresso.onView(ViewMatchers.withText("Help")).perform(ViewActions.click());
        Assert.assertEquals(droidkit.test.R.id.action_help, mActivity.getHelpActionId());
    }

}
