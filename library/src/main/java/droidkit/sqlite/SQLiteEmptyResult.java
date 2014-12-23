package droidkit.sqlite;

import android.database.ContentObserver;
import android.support.annotation.NonNull;

import java.util.AbstractList;

/**
 * @author Daniel Serdyukov
 */
class SQLiteEmptyResult<T> extends AbstractList<T> implements SQLiteResult<T> {

    @Override
    public T get(int location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void registerContentObserver(@NonNull ContentObserver observer) {
        
    }

    @Override
    public void unregisterContentObserver(@NonNull ContentObserver observer) {

    }

    @Override
    public void close() {

    }

}
