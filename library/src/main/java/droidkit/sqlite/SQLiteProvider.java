package droidkit.sqlite;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteProvider extends ContentProvider {

    public static final String SCHEME = "content";

    public static final String DATABASE = "application.db";

    public static final String WHERE_ID_EQ = BaseColumns._ID + " = ?";

    static final String GROUP_BY = "groupBy";

    static final String HAVING = "having";

    static final String LIMIT = "limit";

    private static final int URI_MATCH_ALL = 1;

    private static final int URI_MATCH_ID = 2;

    private static final int DATABASE_VERSION = 1;

    private static final String HELPER_DELEGATE = "droidkit.sqlite.SQLite$Delegate";

    private static final String MIME_DIR = "vnd.android.cursor.dir/";

    private static final String MIME_ITEM = "vnd.android.cursor.item/";

    private static final Map<Uri, String> TABLE_NAMES = new ConcurrentHashMap<>();

    private static final Map<Uri, Uri> BASE_URIS = new ConcurrentHashMap<>();

    private SQLiteDelegate mHelperDelegate;

    private SQLiteHelper mHelper;

    private static int matchUri(@NonNull Uri uri) {
        final List<String> pathSegments = uri.getPathSegments();
        final int pathSegmentsSize = pathSegments.size();
        if (pathSegmentsSize == 1) {
            return URI_MATCH_ALL;
        } else if (pathSegmentsSize == 2 &&
                TextUtils.isDigitsOnly(pathSegments.get(1))) {
            return URI_MATCH_ID;
        }
        throw new SQLiteException("Unknown uri '" + uri + "'");
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        SQLite.attach(info.name, info.authority);
    }

    @Override
    public boolean onCreate() {
        try {
            final Class<?> type = Class.forName(HELPER_DELEGATE);
            mHelperDelegate = (SQLiteDelegate) type.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        mHelper = new SQLiteHelper(getContext(), getDatabaseName(), getDatabaseVersion());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String where,
                        @Nullable String[] whereArgs, @Nullable String orderBy) {
        final int match = matchUri(uri);
        final String tableName = getTableName(uri);
        final SQLiteDatabase db = mHelper.getReadableDatabase();
        final Cursor cursor;
        if (match == URI_MATCH_ID) {
            cursor = db.query(tableName, columns, BaseColumns._ID + "=?",
                    new String[]{uri.getLastPathSegment()}, null, null, orderBy);
        } else {
            cursor = db.query(tableName, columns, where, whereArgs, uri.getQueryParameter(GROUP_BY),
                    uri.getQueryParameter(HAVING), orderBy, uri.getQueryParameter(LIMIT));
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        if (matchUri(uri) == URI_MATCH_ID) {
            return MIME_ITEM + getTableName(uri);
        }
        return MIME_DIR + getTableName(uri);
    }

    @Override
    public Uri insert(@NonNull Uri uri, @NonNull ContentValues values) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        if (db.inTransaction()) {
            db.insert(getTableName(uri), BaseColumns._ID, values);
            return uri;
        }
        final int match = matchUri(uri);
        final long rowId = db.insert(getTableName(uri), BaseColumns._ID, values);
        if (match == URI_MATCH_ID) {
            onInsert(getBaseUri(uri), rowId);
            return uri;
        } else {
            onInsert(uri, rowId);
            return ContentUris.withAppendedId(uri, rowId);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String where, @Nullable String[] whereArgs) {
        final String tableName = getTableName(uri);
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        if (db.inTransaction()) {
            return db.delete(tableName, where, whereArgs);
        }
        final int match = matchUri(uri);
        final int affectedRows;
        final Uri baseUri;
        if (match == URI_MATCH_ID) {
            baseUri = getBaseUri(uri);
            affectedRows = db.delete(tableName, WHERE_ID_EQ, new String[]{uri.getLastPathSegment()});
        } else {
            baseUri = uri;
            affectedRows = db.delete(tableName, where, whereArgs);
        }
        if (affectedRows > 0) {
            onDelete(baseUri, affectedRows);
        }
        return affectedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @NonNull ContentValues values, @Nullable String where,
                      @Nullable String[] whereArgs) {
        final String tableName = getTableName(uri);
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        if (db.inTransaction()) {
            return db.update(tableName, values, where, whereArgs);
        }
        final int match = matchUri(uri);
        final int affectedRows;
        final Uri baseUri;
        if (match == URI_MATCH_ID) {
            baseUri = getBaseUri(uri);
            affectedRows = db.update(tableName, values, WHERE_ID_EQ, new String[]{uri.getLastPathSegment()});
        } else {
            baseUri = uri;
            affectedRows = db.update(tableName, values, where, whereArgs);
        }
        if (affectedRows > 0) {
            onDelete(baseUri, affectedRows);
        }
        return affectedRows;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        if (matchUri(uri) == URI_MATCH_ALL) {
            final SQLiteDatabase db = mHelper.getWritableDatabase();
            db.beginTransactionNonExclusive();
            try {
                final int insertedRows = super.bulkInsert(uri, values);
                db.setTransactionSuccessful();
                if (insertedRows > 0) {
                    onChange(uri, insertedRows);
                }
                return insertedRows;
            } finally {
                db.endTransaction();
            }
        }
        throw new SQLiteException("Unable to bulkInsert into " + uri);
    }

    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            final int opSize = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[opSize];
            final Set<Uri> notifyUris = new HashSet<>(opSize);
            for (int i = 0; i < opSize; ++i) {
                final ContentProviderResult result = operations.get(i).apply(this, results, i);
                if (matchUri(result.uri) == URI_MATCH_ID) {
                    notifyUris.add(getBaseUri(result.uri));
                } else {
                    notifyUris.add(result.uri);
                }
            }
            db.setTransactionSuccessful();
            for (final Uri baseUri : notifyUris) {
                onChange(baseUri, opSize);
            }
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        SQLite.onTrimMemory();
    }

    @Nullable
    protected String getDatabaseName() {
        return DATABASE;
    }

    protected int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    protected void onCreateDatabase(@NonNull SQLiteDatabase db) {
        mHelperDelegate.onCreate(db);
    }

    protected void onUpgradeDatabase(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        mHelperDelegate.onUpgrade(db, oldVersion, newVersion);
    }

    @SuppressWarnings("unused")
    protected void onChange(@NonNull Uri baseUri, int affectedRows) {
        getContext().getContentResolver().notifyChange(baseUri, null, false);
    }

    @SuppressWarnings("unused")
    protected void onInsert(@NonNull Uri baseUri, long rowid) {
        onChange(baseUri, 1);
    }

    @SuppressWarnings("unused")
    protected void onUpdate(@NonNull Uri baseUri, int affectedRows) {
        onChange(baseUri, affectedRows);
    }

    @SuppressWarnings("unused")
    protected void onDelete(@NonNull Uri baseUri, int affectedRows) {
        onChange(baseUri, affectedRows);
    }

    @NonNull
    private String getTableName(@NonNull Uri uri) {
        String tableName = TABLE_NAMES.get(uri);
        if (tableName == null) {
            tableName = uri.getPathSegments().get(0);
            TABLE_NAMES.put(uri, tableName);
        }
        return tableName;
    }

    @NonNull
    private Uri getBaseUri(@NonNull Uri uri) {
        Uri baseUri = BASE_URIS.get(uri);
        if (baseUri == null) {
            baseUri = new Uri.Builder()
                    .scheme(uri.getScheme())
                    .authority(uri.getAuthority())
                    .appendPath(getTableName(uri))
                    .build();
            BASE_URIS.put(uri, baseUri);
        }
        return baseUri;
    }

    private final class SQLiteHelper extends SQLiteOpenHelper {

        public SQLiteHelper(Context context, String name, int version) {
            super(context, name, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            SQLiteProvider.this.onCreateDatabase(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            SQLiteProvider.this.onUpgradeDatabase(db, oldVersion, newVersion);
        }

    }

}
