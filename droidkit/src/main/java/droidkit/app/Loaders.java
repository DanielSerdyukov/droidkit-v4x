package droidkit.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import droidkit.log.Logger;
import droidkit.util.Dynamic;
import droidkit.util.DynamicException;

/**
 * @author Daniel Serdyukov
 */
public final class Loaders {

    private Loaders() {
    }

    private static String makeLcClassName(@NonNull Object target, int loaderId) {
        return target.getClass().getName() + "$LC$$" + loaderId;
    }

    private static String makeSlcClassName(@NonNull Object target, int loaderId) {
        return target.getClass().getName() + "$SLC$$" + loaderId;
    }

    public static void init(@NonNull android.app.LoaderManager lm, int loaderId, @Nullable Bundle args,
                            @NonNull Object target) {
        if (target instanceof android.app.LoaderManager.LoaderCallbacks) {
            lm.initLoader(loaderId, args == null ? Bundle.EMPTY : args,
                    (android.app.LoaderManager.LoaderCallbacks<?>) target);
        } else {
            try {
                final android.app.LoaderManager.LoaderCallbacks<?> lc =
                        Dynamic.init(makeLcClassName(target, loaderId), target);
                lm.initLoader(loaderId, args == null ? Bundle.EMPTY : args, lc);
            } catch (DynamicException e) {
                Logger.error("Can't start loader @%d: invalid callbacks", loaderId);
            }
        }
    }

    public static void restart(@NonNull android.app.LoaderManager lm, int loaderId, @Nullable Bundle args,
                               @NonNull Object target) {
        if (target instanceof android.app.LoaderManager.LoaderCallbacks) {
            lm.restartLoader(loaderId, args == null ? Bundle.EMPTY : args,
                    (android.app.LoaderManager.LoaderCallbacks<?>) target);
        } else {
            try {
                final android.app.LoaderManager.LoaderCallbacks<?> lc =
                        Dynamic.init(makeLcClassName(target, loaderId), target);
                lm.restartLoader(loaderId, args == null ? Bundle.EMPTY : args, lc);
            } catch (DynamicException e) {
                Logger.error("Can't start loader @%d: invalid callbacks", loaderId);
            }
        }
    }

    public static void destroy(@NonNull android.app.LoaderManager lm, int loaderId) {
        lm.destroyLoader(loaderId);
    }

    public static void init(@NonNull android.support.v4.app.LoaderManager lm, int loaderId, @Nullable Bundle args,
                            @NonNull Object target) {
        if (target instanceof android.support.v4.app.LoaderManager.LoaderCallbacks) {
            lm.initLoader(loaderId, args == null ? Bundle.EMPTY : args,
                    (android.support.v4.app.LoaderManager.LoaderCallbacks<?>) target);
        } else {
            try {
                final android.support.v4.app.LoaderManager.LoaderCallbacks<?> lc =
                        Dynamic.init(makeSlcClassName(target, loaderId), target);
                lm.initLoader(loaderId, args == null ? Bundle.EMPTY : args, lc);
            } catch (DynamicException e) {
                Logger.error("Can't start loader @%d: invalid callbacks", loaderId);
            }
        }
    }

    public static void restart(@NonNull android.support.v4.app.LoaderManager lm, int loaderId, @Nullable Bundle args,
                               @NonNull Object target) {
        if (target instanceof android.support.v4.app.LoaderManager.LoaderCallbacks) {
            lm.restartLoader(loaderId, args == null ? Bundle.EMPTY : args,
                    (android.support.v4.app.LoaderManager.LoaderCallbacks<?>) target);
        } else {
            try {
                final android.support.v4.app.LoaderManager.LoaderCallbacks<?> lc =
                        Dynamic.init(makeSlcClassName(target, loaderId), target);
                lm.restartLoader(loaderId, args == null ? Bundle.EMPTY : args, lc);
            } catch (DynamicException e) {
                Logger.error("Can't start loader @%d: invalid callbacks", loaderId);
            }
        }
    }

    public static void destroy(@NonNull android.support.v4.app.LoaderManager lm, int loaderId) {
        lm.destroyLoader(loaderId);
    }

}
