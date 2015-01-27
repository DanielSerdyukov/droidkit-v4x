package droidkit.app;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.Arrays;

import droidkit.app.CommonIntent;
import droidkit.app.IntentUtils;

/**
 * @author Daniel Serdyukov
 */
public class CommonIntentTest extends ApplicationTestCase<Application> {

    public CommonIntentTest() {
        super(Application.class);
    }

    public void testOpenUrl() throws Exception {
        IntentAssert.assertResolution(Arrays.asList("com.android.browser", "com.android.chrome"),
                IntentUtils.getResolution(getContext(), CommonIntent.openUrl("https://google.com")));
    }

    public void testSearch() throws Exception {
        IntentAssert.assertResolution(Arrays.asList("com.google.android.googlequicksearchbox"),
                IntentUtils.getResolution(getContext(), CommonIntent.search("olololo")));
    }

    public void testSendEmail() throws Exception {
        IntentAssert.assertResolution(Arrays.asList("com.google.android.gm"),
                IntentUtils.getResolution(getContext(), CommonIntent.sendEmail(
                        new String[]{"test@google.com"}, "CommonIntentTest", "Test Passed", null)));
    }

    public void testSendSms() throws Exception {
        IntentAssert.assertResolution(Arrays.asList("com.google.android.talk"),
                IntentUtils.getResolution(getContext(), CommonIntent.sendSms("+1234567890", "Test Passed")));
    }

    public void testOpenDialer() throws Exception {
        IntentAssert.assertResolution(Arrays.asList("com.android.dialer", "com.google.android.dialer"),
                IntentUtils.getResolution(getContext(), CommonIntent.openDialer("+1234567890")));
    }

}
