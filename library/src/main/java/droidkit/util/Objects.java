package droidkit.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Comparator;

import droidkit.content.StringValue;

/**
 * @author Daniel Serdyukov
 */
public final class Objects {

    private Objects() {
    }

    @NonNull
    public static <T> T requireNonNull(@Nullable T object, @NonNull T nullDefault) {
        if (object == null) {
            return nullDefault;
        }
        return object;
    }

    @NonNull
    public static String stringNonNull(@Nullable String object) {
        return stringNonNull(object, StringValue.EMPTY);
    }

    @NonNull
    public static String stringNonNull(@Nullable String object, @NonNull String nullString) {
        if (object == null) {
            return nullString;
        }
        return object;
    }

    @NonNull
    public static String toString(@Nullable Object object) {
        return String.valueOf(object);
    }

    @NonNull
    public static String toString(@Nullable Object object, @NonNull String nullString) {
        if (object == null) {
            return nullString;
        }
        return String.valueOf(object);
    }

    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    public static <T> int compare(@Nullable T a, @Nullable T b, @NonNull Comparator<? super T> c) {
        if (a == null && b != null) {
            return -1;
        }
        if (a != null && b == null) {
            return 1;
        }
        if (equals(a, b)) {
            return 0;
        }
        return c.compare(a, b);
    }

}
