package droidkit.app;

import android.support.annotation.NonNull;

/**
 * @author Daniel Serdyukov
 * @since 5.0.1
 */
public final class Lifecycle {

    public static interface Callbacks<T> {

        void onCreate(@NonNull T target);

        void injectViews(@NonNull Object root, @NonNull T target);

        void onStart(@NonNull T target);

        void onResume(@NonNull T target);

        void onPause(@NonNull T target);

        void onStop(@NonNull T target);

        void onDestroy(@NonNull T target);

    }

}
