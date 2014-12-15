package droidkit.sqlite;

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
    public void close() {

    }

}
