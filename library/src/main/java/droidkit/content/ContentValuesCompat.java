package droidkit.content;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Daniel Serdyukov
 */
public final class ContentValuesCompat {

    private ContentValuesCompat() {

    }

    public static void put(@NonNull ContentValues values, @NonNull String key, @Nullable Object value) {
        if (value == null) {
            values.putNull(key);
        } else if (value instanceof String) {
            values.put(key, (String) value);
        } else if (value instanceof Byte) {
            values.put(key, (Byte) value);
        } else if (value instanceof Short) {
            values.put(key, (Short) value);
        } else if (value instanceof Integer) {
            values.put(key, (Integer) value);
        } else if (value instanceof Long) {
            values.put(key, (Long) value);
        } else if (value instanceof Float) {
            values.put(key, (Float) value);
        } else if (value instanceof Double) {
            values.put(key, (Double) value);
        } else if (value instanceof Boolean) {
            values.put(key, ((Boolean) value) ? 1 : 0);
        } else if (value instanceof byte[]) {
            values.put(key, (byte[]) value);
        } else {
            throw new IllegalArgumentException("bad value type: " + value.getClass().getName());
        }
    }

}
