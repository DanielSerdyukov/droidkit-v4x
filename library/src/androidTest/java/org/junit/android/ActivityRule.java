package org.junit.android;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Daniel Serdyukov
 */
public class ActivityRule<T extends Activity> implements TestRule {

    private final Class<T> mActivityClass;

    private T mActivity;

    public ActivityRule(@NonNull Class<T> activityClass) {
        mActivityClass = activityClass;
    }

    @NonNull
    protected Intent getLaunchIntent(@NonNull String targetPackage, @NonNull Class<T> activityClass) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(targetPackage, activityClass.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public final T get() {
        return launchActivity();
    }

    @Override
    public final Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                launchActivity();
                base.evaluate();
                if (!mActivity.isFinishing()) {
                    mActivity.finish();
                }
                mActivity = null;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private T launchActivity() {
        if (mActivity != null) {
            return mActivity;
        }
        final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        final String targetPackage = instrumentation.getTargetContext().getPackageName();
        final Intent intent = getLaunchIntent(targetPackage, mActivityClass);
        mActivity = (T) instrumentation.startActivitySync(intent);
        instrumentation.waitForIdleSync();
        return mActivity;
    }

}
