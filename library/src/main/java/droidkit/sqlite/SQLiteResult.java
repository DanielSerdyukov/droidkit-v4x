package droidkit.sqlite;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.io.Closeable;
import java.util.AbstractList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Daniel Serdyukov
 */
@SuppressLint("NewApi")
public class SQLiteResult<T> extends AbstractList<T> implements Closeable, AutoCloseable {

    private final SQLiteTable<T> mTable;

    private final AtomicReference<Cursor> mCursorRef;

    private final int mRowIdColumnIndex;

    SQLiteResult(@NonNull SQLiteTable<T> table, @NonNull Cursor cursor) {
        mTable = table;
        mCursorRef = new AtomicReference<>(cursor);
        mRowIdColumnIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        SQLiteGuard.guard(this);
    }

    @Override
    public T get(int location) {
        final Cursor cursor = mCursorRef.get();
        if (!cursor.moveToPosition(location)) {
            throw new ArrayIndexOutOfBoundsException(location);
        }
        final long rowId = cursor.getLong(mRowIdColumnIndex);
        T instance = mTable.getRow(rowId);
        if (instance == null) {
            instance = mTable.instantiate(cursor);
        }
        return instance;
    }

    @Override
    public int size() {
        final Cursor cursor = mCursorRef.get();
        if (cursor != null && !cursor.isClosed()) {
            return cursor.getCount();
        }
        return 0;
    }

    @Override
    public void close() {
    }

    @NonNull
    AtomicReference<Cursor> getCursorReference() {
        return mCursorRef;
    }

}
