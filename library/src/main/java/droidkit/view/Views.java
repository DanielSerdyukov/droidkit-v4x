package droidkit.view;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * @author Daniel Serdyukov
 */
public final class Views {

    private Views() {
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends View> T findById(@NonNull View root, int viewId) {
        return (T) root.findViewById(viewId);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T extends View> T findById(@NonNull Activity root, int viewId) {
        return (T) root.findViewById(viewId);
    }

    @NonNull
    public static <T extends View> T findById(@NonNull Object root, int viewId) {
        if (root instanceof View) {
            return findById((View) root, viewId);
        } else if (root instanceof Activity) {
            return findById((Activity) root, viewId);
        }
        throw new IllegalArgumentException("root must be instance of Activity or ViewGroup");
    }

}
