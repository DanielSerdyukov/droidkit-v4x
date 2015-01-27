package droidkit.view;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * @author Daniel Serdyukov
 */
public final class Views {

    private Views() {
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends View> T findById(@NonNull View root, @IdRes int viewId) {
        return (T) root.findViewById(viewId);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends View> T findById(@NonNull Activity root, @IdRes int viewId) {
        return (T) root.findViewById(viewId);
    }

    @NonNull
    public static <T extends View> T findById(@NonNull Object root, @IdRes int viewId) {
        if (root instanceof View) {
            return findById((View) root, viewId);
        } else if (root instanceof Activity) {
            return findById((Activity) root, viewId);
        }
        throw new IllegalArgumentException("root must be instance of Activity or ViewGroup");
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends View> T findByIdOrThrow(@NonNull View root, @IdRes int viewId) {
        return checkNonNull(root, root.findViewById(viewId), root.getResources(), viewId);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends View> T findByIdOrThrow(@NonNull Activity root, @IdRes int viewId) {
        return checkNonNull(root, root.findViewById(viewId), root.getResources(), viewId);
    }

    @NonNull
    public static <T extends View> T findByIdOrThrow(@NonNull Object root, @IdRes int viewId) {
        if (root instanceof View) {
            return findByIdOrThrow((View) root, viewId);
        } else if (root instanceof Activity) {
            return findByIdOrThrow((Activity) root, viewId);
        }
        throw new IllegalArgumentException("root must be instance of Activity or ViewGroup");
    }

    @SuppressWarnings("unchecked")
    private static <T extends View> T checkNonNull(@NonNull Object root, @Nullable View view, @NonNull Resources res,
                                                   @IdRes int viewId) {
        if (view == null) {
            throw new IllegalArgumentException("View with @id=" + res.getResourceName(viewId)
                    + " not found in " + root);
        }
        return (T) view;
    }

}
