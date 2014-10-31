package droidkit.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Daniel Serdyukov
 */
public final class DynamicMethod {

    private DynamicMethod() {
    }

    @Nullable
    public static <T> T invoke(@NonNull Object target, @NonNull String method, Object... args)
            throws DynamicException {
        return invoke(target, findMethod(target.getClass(), method, types(args)), args);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T invoke(@Nullable Object target, @NonNull Method method, Object... args)
            throws DynamicException {
        final boolean isAccessible = method.isAccessible();
        try {
            method.setAccessible(true);
            return (T) method.invoke(target, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new DynamicException(e);
        } finally {
            method.setAccessible(isAccessible);
        }
    }

    @Nullable
    public static <T> T invokeStatic(@NonNull String className, @NonNull String method, Object... args)
            throws DynamicException {
        return invokeStatic(Dynamic.forName(className), method, args);
    }

    @Nullable
    public static <T> T invokeStatic(@NonNull Class<?> target, @NonNull String method, Object... args)
            throws DynamicException {
        return invokeStatic(findMethod(target, method, types(args)), args);
    }

    @Nullable
    public static <T> T invokeStatic(@NonNull Method method, Object... args)
            throws DynamicException {
        return invoke(null, method, args);
    }

    @NonNull
    public static Method findMethod(@NonNull String className, @NonNull String name, Class<?>... argTypes)
            throws DynamicException {
        return findMethod(Dynamic.forName(className), name, argTypes);
    }

    @NonNull
    public static Method findMethod(@NonNull Class<?> clazz, @NonNull String name, Class<?>... argTypes)
            throws DynamicException {
        do {
            final Method[] methods = clazz.getDeclaredMethods();
            for (final Method method : methods) {
                if (TextUtils.equals(name, method.getName())
                        && hasValidSignature(method.getParameterTypes(), argTypes)) {
                    return method;
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new DynamicException("No such method " + name);
    }

    @NonNull
    static Class<?>[] types(Object... args) {
        final Class<?>[] argumentTypes = new Class<?>[args.length];
        for (int index = 0; index < args.length; ++index) {
            argumentTypes[index] = args[index].getClass();
        }
        return argumentTypes;
    }

    static boolean hasValidSignature(@NonNull Class<?>[] expected, @NonNull Class<?>[] actual) {
        if (expected.length != actual.length) {
            return false;
        }
        for (int index = 0; index < expected.length; ++index) {
            if (!isAssignable(expected[index], actual[index])) {
                return false;
            }
        }
        return true;
    }

    static boolean isAssignable(@NonNull Class<?> expected, @NonNull Class<?> actual) {
        if (expected.isAssignableFrom(actual)) {
            return true;
        }
        if (expected.isPrimitive()) {
            return Dynamic.unbox(expected).isAssignableFrom(actual);
        }
        return actual.isPrimitive() && expected.isAssignableFrom(Dynamic.unbox(actual));
    }

}
