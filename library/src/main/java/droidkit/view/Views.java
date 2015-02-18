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
    @SuppressWarnings("unchecked")
    public static <T extends View> T findById(@NonNull Dialog root, @IdRes int viewId) {
        return (T) root.findViewById(viewId);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends View> T findById(@NonNull Window root, @IdRes int viewId) {
        return (T) root.findViewById(viewId);
    }

}
