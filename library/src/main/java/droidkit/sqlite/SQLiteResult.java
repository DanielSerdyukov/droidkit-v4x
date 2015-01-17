package droidkit.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
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

    private final ContentResolver mDb;

    private final Uri mUri;

    private final SQLiteTable<T> mTable;

    private final Cursor mRowIds;

    private final int mRowIdColumnIndex;

    SQLiteResult(@NonNull ContentResolver db, @NonNull Uri uri, @NonNull SQLiteTable<T> table,
                 @NonNull Cursor rowIds) {
        mDb = db;
        mUri = uri;
        mTable = table;
        mRowIds = rowIds;
        mRowIdColumnIndex = rowIds.getColumnIndexOrThrow(BaseColumns._ID);
    }

    @Override
    public T get(int location) {
        if (!mRowIds.moveToPosition(location)) {
            throw new ArrayIndexOutOfBoundsException(location);
        }
        final long rowId = mRowIds.getLong(mRowIdColumnIndex);
        T instance = mTable.getRow(rowId);
        if (instance == null) {
            instance = query(rowId);
        }
        return instance;
    }

    @Override
    public int size() {
        return mRowIds.getCount();
    }

    public void registerContentObserver(@NonNull ContentObserver observer) {
        mRowIds.registerContentObserver(observer);
    }

    public void unregisterContentObserver(@NonNull ContentObserver observer) {
        mRowIds.unregisterContentObserver(observer);
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(mRowIds);
    }

    private T query(long rowId) {
        final Cursor cursor = mDb.query(mUri, null, SQLiteProvider.WHERE_ID_EQ,
                new String[]{String.valueOf(rowId)}, null);
        try {
            if (!cursor.moveToFirst()) {
                throw new ArrayIndexOutOfBoundsException(cursor.getPosition());
            }
            return mTable.instantiate(cursor);
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

}
