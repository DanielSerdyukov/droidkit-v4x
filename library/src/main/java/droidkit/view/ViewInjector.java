package droidkit.view;

import android.support.annotation.NonNull;
import android.view.View;

import java.lang.reflect.Field;
import java.util.List;

import droidkit.annotation.InjectView;
import droidkit.util.DynamicException;
import droidkit.util.DynamicField;

/**
 * @author Daniel Serdyukov
 */
public final class ViewInjector {

    private ViewInjector() {
    }

    public static void inject(@NonNull View view, @NonNull Object target) {
        tryInjectAtRuntime(view, target);
    }

    private static void tryInjectAtRuntime(@NonNull View view, @NonNull Object target) {
        final List<Field> fields = DynamicField.annotatedWith(target.getClass(), InjectView.class);
        for (final Field field : fields) {
            final InjectView injectView = field.getAnnotation(InjectView.class);
            try {
                DynamicField.set(target, field, Views.findById(view, injectView.value()));
            } catch (DynamicException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

}
