package droidkit.app;

import android.support.test.InstrumentationRegistry;

import junit.framework.TestCase;

import org.junit.Assert;

/**
 * @author Daniel Serdyukov
 */
public class CommonIntentTest extends TestCase {

    public void testOpenUrl() throws Exception {
        Assert.assertFalse(IntentUtils.getResolution(InstrumentationRegistry.getContext(),
                CommonIntent.openUrl("https://google.com")).isEmpty());
    }

    public void testSearch() throws Exception {
        Assert.assertFalse(IntentUtils.getResolution(InstrumentationRegistry.getContext(),
                CommonIntent.search("olololo")).isEmpty());
    }

    public void testSendEmail() throws Exception {
        Assert.assertFalse(IntentUtils.getResolution(InstrumentationRegistry.getContext(),
                CommonIntent.sendEmail(new String[]{
                                "test@google.com"},
                        "CommonIntentTest",
                        "Test Passed",
                        null)
        ).isEmpty());
    }

    public void testSendSms() throws Exception {
        Assert.assertFalse(IntentUtils.getResolution(InstrumentationRegistry.getContext(),
                CommonIntent.sendSms("+1234567890", "Test Passed")).isEmpty());
    }

    public void testOpenDialer() throws Exception {
        Assert.assertFalse(IntentUtils.getResolution(InstrumentationRegistry.getContext(),
                CommonIntent.openDialer("+1234567890")).isEmpty());
    }

}
