package droidkit.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Daniel Serdyukov
 */
public interface Observer<T> {

    void onChange(@NonNull Observable<T> observable, @Nullable T data);

}
