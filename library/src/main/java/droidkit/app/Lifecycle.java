package droidkit.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Daniel Serdyukov
 * @since 5.0.1
 */
public final class Lifecycle {

    private Lifecycle() {
    }

    @Nullable
    public static <T> Callbacks<T> get(@NonNull Class<?> type) {
        return null;
    }

    public static interface Callbacks<T> {

        void onCreate(@NonNull T target, @NonNull Bundle savedInstanceState);

        void onStart(@NonNull T target);

        void onResume(@NonNull T target);

        void onPause(@NonNull T target);

        void onStop(@NonNull T target);

        void onDestroy(@NonNull T target);

    }

}
