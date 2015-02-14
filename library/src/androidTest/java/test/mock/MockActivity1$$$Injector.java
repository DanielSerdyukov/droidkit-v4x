package test.mock;

import android.view.View;

import java.util.Map;
import java.util.WeakHashMap;

import droidkit.view.Views;

/**
 * @author Daniel Serdyukov
 */
class MockActivity1$$$Injector {

    private static final Map<View, View.OnClickListener> ON_CLICK = new WeakHashMap<>();

    static void injectViews(Object root, MockActivity1 target) {
        target.mAndroidText1 = Views.findById(root, android.R.id.text1);
        target.mDroidkitText1 = Views.findById(root, droidkit.test.R.id.text1);
    }

    static void onCreate(MockActivity1 target) {
        ON_CLICK.clear();
    }

    static void onViewCreated(MockActivity1 target) {

    }

    static void onStart(MockActivity1 target) {

    }

    static void onResume(MockActivity1 target) {
        for (final Map.Entry<View, View.OnClickListener> entry : ON_CLICK.entrySet()) {
            entry.getKey().setOnClickListener(entry.getValue());
        }
    }

    static void onPause(MockActivity1 target) {
        for (final Map.Entry<View, View.OnClickListener> entry : ON_CLICK.entrySet()) {
            entry.getKey().setOnClickListener(null);
        }
    }

    static void onStop(MockActivity1 target) {

    }

    static void onDestroy(MockActivity1 target) {
        ON_CLICK.clear();
    }

}
