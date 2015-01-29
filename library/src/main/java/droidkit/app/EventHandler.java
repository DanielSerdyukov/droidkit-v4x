package droidkit.app;

import android.support.annotation.NonNull;

/**
 * @author Daniel Serdyukov
 */
public interface EventHandler {

    void onEvent(@NonNull Object event);

}
