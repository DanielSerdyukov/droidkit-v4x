package droidkit.sqlite;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import droidkit.io.IOUtils;

/**
 * @author Daniel Serdyukov
 */
class SQLiteResultImpl<T> extends SQLiteEmptyResult<T> {

    private final ContentResolver mDb;

    private final Uri mUri;

    private final SQLiteTable<T> mTable;

    private final Cursor mRowIds;

    private final int mRowIdColumnIndex;

    SQLiteResultImpl(@NonNull ContentResolver db, @NonNull Uri uri, @NonNull SQLiteTable<T> table,
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
        T instance = mTable.get(rowId);
        if (instance == null) {
            final Cursor cursor = mDb.query(mUri, null, SQLiteProvider.WHERE_ID_EQ,
                    new String[]{String.valueOf(rowId)}, null);
            try {
                if (!cursor.moveToFirst()) {
                    throw new ArrayIndexOutOfBoundsException(cursor.getPosition());
                }
                instance = mTable.instantiate(cursor);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
        return instance;
    }

    @Override
    public int size() {
        return mRowIds.getCount();
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(mRowIds);
    }

}
