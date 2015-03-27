package droidkit.sqlite;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.io.Closeable;
import java.util.AbstractList;

import droidkit.io.IOUtils;

/**
 * @author Daniel Serdyukov
 */
@SuppressLint("NewApi")
public class SQLiteResult<T> extends AbstractList<T> implements Closeable, AutoCloseable {

    private final SQLiteTable<T> mTable;

    private final Cursor mCursor;

    private final int mRowIdColumnIndex;

    SQLiteResult(@NonNull SQLiteTable<T> table,
                 @NonNull Cursor cursor) {
        mTable = table;
        mCursor = cursor;
        mRowIdColumnIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
    }

    @Override
    public T get(int location) {
        if (!mCursor.moveToPosition(location)) {
            throw new ArrayIndexOutOfBoundsException(location);
        }
        final long rowId = mCursor.getLong(mRowIdColumnIndex);
        T instance = mTable.getRow(rowId);
        if (instance == null) {
            instance = mTable.instantiate(mCursor);
        }
        return instance;
    }

    @Override
    public int size() {
        return mCursor.getCount();
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(mCursor);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        IOUtils.closeQuietly(mCursor);
    }

}
