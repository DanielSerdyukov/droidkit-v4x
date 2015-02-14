package droidkit.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidJUnit4.class)
public class CommonIntentTest {

    @Test
    public void openUrl() throws Exception {
        Assert.assertFalse(IntentUtils.getResolution(InstrumentationRegistry.getContext(),
                CommonIntent.openUrl("https://google.com")).isEmpty());
    }

    @Test
    public void search() throws Exception {
        Assert.assertFalse(IntentUtils.getResolution(InstrumentationRegistry.getContext(),
                CommonIntent.search("olololo")).isEmpty());
    }

    @Test
    public void sendEmail() throws Exception {
        Assert.assertFalse(IntentUtils.getResolution(InstrumentationRegistry.getContext(),
                CommonIntent.sendEmail(new String[]{
                                "test@google.com"},
                        "CommonIntentTest",
                        "Test Passed",
                        null)
        ).isEmpty());
    }

    @Test
    public void sendSms() throws Exception {
        Assert.assertFalse(IntentUtils.getResolution(InstrumentationRegistry.getContext(),
                CommonIntent.sendSms("+1234567890", "Test Passed")).isEmpty());
    }

    @Test
    public void openDialer() throws Exception {
        Assert.assertFalse(IntentUtils.getResolution(InstrumentationRegistry.getContext(),
                CommonIntent.openDialer("+1234567890")).isEmpty());
    }

}
