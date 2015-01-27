package droidkit.app;

import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import junit.framework.Assert;

import java.util.List;

/**
 * @author Daniel Serdyukov
 */
final class IntentAssert {

    private IntentAssert() {
    }

    static void assertResolution(@NonNull List<String> expected, @NonNull List<ResolveInfo> resolution) {
        boolean resolved = false;
        for (final ResolveInfo resolveInfo : resolution) {
            if (expected.contains(resolveInfo.activityInfo.packageName)) {
                resolved = true;
                break;
            }
        }
        Assert.assertTrue(resolved);
    }

    static void dumpResolution(@NonNull List<ResolveInfo> resolution) {
        for (final ResolveInfo resolveInfo : resolution) {
            Log.e("IntentTestUtils", resolveInfo.activityInfo.packageName);
        }
    }

}
