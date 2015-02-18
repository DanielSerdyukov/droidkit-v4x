package droidkit.view;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

/**
 * @author Daniel Serdyukov
 */
public final class Views {

    private Views() {
    }

    @NonNull
    public static <T extends View> T findById(@NonNull Object root, @IdRes int viewId) {
        if (root instanceof View) {
            return findById((View) root, viewId);
        } else if (root instanceof Window) {
            return findById((Window) root, viewId);
        } else if (root instanceof Activity) {
            return findById(((Activity) root).getWindow(), viewId);
        } else if (root instanceof Dialog) {
            return findById(((Dialog) root).getWindow(), viewId);
        }
        throw new IllegalArgumentException("Can't find view in " + root);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends View> T findById(@NonNull View root, @IdRes int viewId) {
        return (T) root.findViewById(viewId);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends View> T findById(@NonNull Window root, @IdRes int viewId) {
        return (T) root.findViewById(viewId);
    }

}
