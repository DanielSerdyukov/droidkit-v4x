package droidkit.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import junit.framework.Assert;

/**
 * @author Daniel Serdyukov
 */
public class ViewsTest extends ActivityUnitTestCase<ViewsTest.ActivityImpl> {

    private ActivityImpl mActivity;

    public ViewsTest() {
        super(ActivityImpl.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        startActivity(new Intent(getInstrumentation().getTargetContext(), ActivityImpl.class), null, null);
        getInstrumentation().waitForIdleSync();
        mActivity = getActivity();
    }

    public void testFindById() throws Exception {
        final View fragment = Views.findById(mActivity, droidkit.test.R.id.fragment);
        Assert.assertNotNull("findById(droidkit.test.R.id.fragment)", fragment);
        Assert.assertEquals(FrameLayout.class, fragment.getClass());
        final View button1 = Views.findById(mActivity, android.R.id.button1);
        Assert.assertNotNull("findById(android.R.id.button1)", fragment);
        Assert.assertEquals(Button.class, button1.getClass());
    }

    public static final class ActivityImpl extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(droidkit.test.R.layout.ac_mock);
        }

    }

}
