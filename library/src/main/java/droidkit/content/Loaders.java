package droidkit.content;

import android.os.Bundle;
import android.support.annotation.NonNull;

import droidkit.util.Dynamic;
import droidkit.util.DynamicException;

/**
 * @author Daniel Serdyukov
 */
public final class Loaders {

    private static final String LC = "$LC";

    private static final String LCV4 = "$LCv4";

    private Loaders() {
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <D> android.content.Loader<D> init(android.app.LoaderManager lm, int loaderId, @NonNull Bundle args,
                                                     @NonNull Object delegate) {
        if (delegate instanceof android.app.LoaderManager.LoaderCallbacks) {
            return lm.initLoader(loaderId, args, (android.app.LoaderManager.LoaderCallbacks<D>) delegate);
        }
        try {
            android.app.LoaderManager.LoaderCallbacks<D> callbacks =
                    Dynamic.init(getCallbacksClassName(loaderId, delegate), delegate);
            return lm.initLoader(loaderId, args, callbacks);
        } catch (DynamicException e) {
            throw new IllegalStateException("No such LoaderCallbacks", e);
        }
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <D> android.content.Loader<D> restart(android.app.LoaderManager lm, int loaderId,
                                                        @NonNull Bundle args, @NonNull Object delegate) {
        if (delegate instanceof android.app.LoaderManager.LoaderCallbacks) {
            return lm.restartLoader(loaderId, args, (android.app.LoaderManager.LoaderCallbacks<D>) delegate);
        }
        try {
            android.app.LoaderManager.LoaderCallbacks<D> callbacks =
                    Dynamic.init(getCallbacksClassName(loaderId, delegate), delegate);
            return lm.restartLoader(loaderId, args, callbacks);
        } catch (DynamicException e) {
            throw new IllegalStateException("No such LoaderCallbacks", e);
        }
    }

    public static void destroy(android.app.LoaderManager lm, int loaderId) {
        lm.destroyLoader(loaderId);
    }

    // SUPPORT_V4
    @NonNull
    @SuppressWarnings("unchecked")
    public static <D> android.support.v4.content.Loader<D> init(android.support.v4.app.LoaderManager lm,
                                                                int loaderId, @NonNull Bundle args,
                                                                @NonNull Object delegate) {
        if (delegate instanceof android.support.v4.app.LoaderManager.LoaderCallbacks) {
            return lm.initLoader(loaderId, args, (android.support.v4.app.LoaderManager.LoaderCallbacks<D>) delegate);
        }
        try {
            android.support.v4.app.LoaderManager.LoaderCallbacks<D> callbacks =
                    Dynamic.init(getCallbacksClassName(loaderId, delegate, true), delegate);
            return lm.initLoader(loaderId, args, callbacks);
        } catch (DynamicException e) {
            throw new IllegalStateException("No such LoaderCallbacks", e);
        }
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <D> android.support.v4.content.Loader<D> restart(android.support.v4.app.LoaderManager lm,
                                                                   int loaderId, @NonNull Bundle args,
                                                                   @NonNull Object delegate) {
        if (delegate instanceof android.support.v4.app.LoaderManager.LoaderCallbacks) {
            return lm.restartLoader(loaderId, args, (android.support.v4.app.LoaderManager.LoaderCallbacks<D>) delegate);
        }
        try {
            android.support.v4.app.LoaderManager.LoaderCallbacks<D> callbacks =
                    Dynamic.init(getCallbacksClassName(loaderId, delegate, true), delegate);
            return lm.restartLoader(loaderId, args, callbacks);
        } catch (DynamicException e) {
            throw new IllegalStateException("No such LoaderCallbacks", e);
        }
    }

    public static void destroy(android.support.v4.app.LoaderManager lm, int loaderId) {
        lm.destroyLoader(loaderId);
    }

    private static String getCallbacksClassName(int loaderId, @NonNull Object delegate) {
        return getCallbacksClassName(loaderId, delegate, false);
    }

    private static String getCallbacksClassName(int loaderId, @NonNull Object delegate, boolean support) {
        return String.format("%s%s%d", delegate.getClass().getName(), support ? LCV4 : LC, loaderId);
    }

}
