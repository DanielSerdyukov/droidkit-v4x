package droidkit.util;

import android.support.annotation.NonNull;

/**
 * @author Daniel Serdyukov
 */
public final class Dynamic {

    private Dynamic() {
    }

    @NonNull
    public static StackTraceElement getCaller() {
        return new Throwable().fillInStackTrace().getStackTrace()[2];
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T> Class<T> forName(@NonNull String name) throws DynamicException {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new DynamicException(e);
        }
    }

}
