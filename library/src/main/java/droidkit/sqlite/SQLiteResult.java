package droidkit.sqlite;

import android.database.ContentObserver;
import android.support.annotation.NonNull;

import java.io.Closeable;
import java.util.List;

/**
 * @author Daniel Serdyukov
 */
public interface SQLiteResult<T> extends List<T>, Closeable {

    void registerContentObserver(@NonNull ContentObserver observer);

    void unregisterContentObserver(@NonNull ContentObserver observer);

    void close();

}
