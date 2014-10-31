package droidkit.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;

/**
 * @author Daniel Serdyukov
 */
public final class DynamicField {

    private DynamicField() {
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T get(@NonNull Object target, @NonNull String fieldName) throws DynamicException {
        return get(target, findField(target.getClass(), fieldName));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T get(@Nullable Object target, @NonNull Field field) throws DynamicException {
        final boolean isAccessible = field.isAccessible();
        try {
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            throw new DynamicException(e);
        } finally {
            field.setAccessible(isAccessible);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getStatic(@NonNull Class<?> clazz, @NonNull String fieldName) throws DynamicException {
        return get(null, findField(clazz, fieldName));
    }

    @SuppressWarnings("unchecked")
    public static void set(@NonNull Object target, @NonNull String fieldName, @Nullable Object value)
            throws DynamicException {
        set(target, findField(target.getClass(), fieldName), value);
    }

    @SuppressWarnings("unchecked")
    public static void set(@Nullable Object target, @NonNull Field field, @Nullable Object value)
            throws DynamicException {
        final boolean isAccessible = field.isAccessible();
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new DynamicException(e);
        } finally {
            field.setAccessible(isAccessible);
        }
    }

    @SuppressWarnings("unchecked")
    public static void setStatic(@NonNull Class<?> clazz, @NonNull String fieldName, @Nullable Object value)
            throws DynamicException {
        set(null, findField(clazz, fieldName), value);
    }

    @NonNull
    public static Field findField(@NonNull Class<?> clazz, @NonNull String name) throws DynamicException {
        do {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new DynamicException(new NoSuchFieldException(name));
    }

    @NonNull
    public static Field findField(@NonNull String className, @NonNull String name) throws DynamicException {
        return findField(Dynamic.forName(className), name);
    }

}
