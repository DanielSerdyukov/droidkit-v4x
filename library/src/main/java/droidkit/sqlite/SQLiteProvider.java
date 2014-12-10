package droidkit.sqlite;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Serdyukov
 */
public abstract class SQLiteProvider extends ContentProvider {

    protected static final int MATCH_ALL = 0;

    protected static final int MATCH_ID = 1;

    private SQLiteOpenHelper mHelper;

    protected static int matchUri(@NonNull Uri uri) {
        final List<String> pathSegments = uri.getPathSegments();
        final int pathSegmentsSize = pathSegments.size();
        if (pathSegmentsSize == 1) {
            return MATCH_ALL;
        } else if (pathSegmentsSize == 2 && TextUtils.isDigitsOnly(pathSegments.get(1))) {
            return MATCH_ID;
        }
        throw new SQLiteException("Unknown uri '" + uri + "'");
    }

    @Override
    public boolean onCreate() {
        onRegisterTypes();
        mHelper = onCreateHelper();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String where, String[] whereArgs, String orderBy) {
        final int match = matchUri(uri);
        final String table = uri.getPathSegments().get(0);
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        if (MATCH_ID == match) {
            final Cursor cursor = db.query(table, columns, BaseColumns._ID + "=?",
                    new String[]{uri.getLastPathSegment()}, null, null, orderBy);
            cursor.setNotificationUri(getContext().getContentResolver(), new Uri.Builder()
                    .scheme(uri.getScheme()).authority(uri.getAuthority()).path(table).build());
            return cursor;
        } else {
            final Cursor cursor = db.query(table, columns, where, whereArgs, null, null, orderBy);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = matchUri(uri);
        final String table = uri.getPathSegments().get(0);
        if (MATCH_ID == match) {
            update(uri, values, null, null);
            return uri;
        } else {
            final long rowid = mHelper.getWritableDatabase().insert(table, BaseColumns._ID, values);
            onInsert(uri, rowid);
            return ContentUris.withAppendedId(uri, rowid);
        }
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        final int match = matchUri(uri);
        final String table = uri.getPathSegments().get(0);
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        if (MATCH_ID == match) {
            final int affectedRows = db.delete(table, BaseColumns._ID + "=?",
                    new String[]{uri.getLastPathSegment()});
            if (affectedRows > 0) {
                onDelete(new Uri.Builder().scheme(uri.getScheme()).authority(uri.getAuthority())
                        .path(table).build(), affectedRows);
            }
            return affectedRows;
        } else {
            final int affectedRows = db.delete(table, where, whereArgs);
            if (affectedRows > 0) {
                onDelete(uri, affectedRows);
            }
            return affectedRows;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        final int match = matchUri(uri);
        final String table = uri.getPathSegments().get(0);
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        if (MATCH_ID == match) {
            final int affectedRows = db.update(table, values, BaseColumns._ID + "=?",
                    new String[]{uri.getLastPathSegment()});
            if (affectedRows > 0) {
                onUpdate(new Uri.Builder().scheme(uri.getScheme()).authority(uri.getAuthority())
                        .path(table).build(), affectedRows);
            }
            return affectedRows;
        } else {
            final int affectedRows = db.update(table, values, where, whereArgs);
            if (affectedRows > 0) {
                onUpdate(uri, affectedRows);
            }
            return affectedRows;
        }
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] bulkValues) {
        final int match = matchUri(uri);
        if (match == MATCH_ID && bulkValues.length > 0) {
            return update(uri, bulkValues[0], null, null);
        } else if (match == MATCH_ALL) {
            int affectedRows = 0;
            final String table = uri.getPathSegments().get(0);
            final SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                for (final ContentValues value : bulkValues) {
                    mHelper.getWritableDatabase().insert(table, BaseColumns._ID, value);
                    ++affectedRows;
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            if (affectedRows > 0) {
                onUpdate(uri, affectedRows);
            }
            return affectedRows;
        }
        return 0;
    }

    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    protected abstract void onRegisterTypes();

    @NonNull
    protected abstract SQLiteHelper onCreateHelper();

    protected void registerType(@NonNull String authority, @NonNull Class<?> type) {
        SQLite.registerType(authority, type);
    }

    protected void onChange(@NonNull Uri baseUri) {
        getContext().getContentResolver().notifyChange(baseUri, null);
    }

    protected void onInsert(@NonNull Uri baseUri, long rowid) {
        onChange(baseUri);
    }

    protected void onUpdate(@NonNull Uri baseUri, int affectedRows) {
        onChange(baseUri);
    }

    protected void onDelete(@NonNull Uri baseUri, int affectedRows) {
        onChange(baseUri);
    }

}
