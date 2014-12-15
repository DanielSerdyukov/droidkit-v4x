package droidkit.sqlite;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import droidkit.database.CursorUtils;

/**
 * @author Daniel Serdyukov
 */
public final class SQLiteUser$SQLiteTable implements SQLiteTable<SQLiteUser> {

    private static final String TABLE = "users";

    private final Map<Long, SQLiteUser> mObjectsCache = new HashMap<>(1024);

    public static void create(@NonNull SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users(_id INTEGER PRIMARY KEY, name TEXT, age INTEGER);");
    }

    public static void drop(@NonNull SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS users;");
    }

    @NonNull
    @Override
    public String getTableName() {
        return TABLE;
    }

    @Nullable
    @Override
    public SQLiteUser get(long rowId) {
        return mObjectsCache.get(rowId);
    }

    @NonNull
    @Override
    public SQLiteUser instantiate(@NonNull Cursor cursor) {
        final SQLiteUser object = new SQLiteUser();
        object.mId = CursorUtils.getLong(cursor, BaseColumns._ID);
        object.mName = CursorUtils.getString(cursor, "name");
        object.mAge = CursorUtils.getInt(cursor, "age");
        mObjectsCache.put(object.mId, object);
        return object;
    }

    @Override
    public void insert(@NonNull ContentResolver db, @NonNull Uri uri, @NonNull SQLiteUser object) {
        final ContentValues values = new ContentValues();
        values.put("name", object.mName);
        values.put("age", object.mAge);
        db.insert(uri, values);
    }

    @Override
    public void insert(@NonNull ArrayList<ContentProviderOperation> operations, @NonNull Uri uri,
                       @NonNull SQLiteUser object) {
        operations.add(ContentProviderOperation.newInsert(uri)
                .withValue("name", object.mName)
                .withValue("age", object.mAge)
                .build());
    }

    @Override
    public void update(@NonNull ContentResolver db, @NonNull Uri uri, @NonNull SQLiteUser object) {
        final ContentValues values = new ContentValues();
        values.put("name", object.mName);
        values.put("age", object.mAge);
        db.update(uri, values, SQLiteProvider.WHERE_ID_EQ, new String[]{String.valueOf(object.mId)});
    }

    @Override
    public void update(@NonNull ArrayList<ContentProviderOperation> operations, @NonNull Uri uri,
                       @NonNull SQLiteUser object) {
        operations.add(ContentProviderOperation.newUpdate(uri)
                .withValue("name", object.mName)
                .withValue("age", object.mAge)
                .withSelection(SQLiteProvider.WHERE_ID_EQ, new String[]{String.valueOf(object.mId)})
                .build());
    }

    @Override
    public void delete(@NonNull ContentResolver db, @NonNull Uri uri, @NonNull SQLiteUser object) {
        db.delete(uri, SQLiteProvider.WHERE_ID_EQ, new String[]{String.valueOf(object.mId)});
    }

    @Override
    public void delete(@NonNull ArrayList<ContentProviderOperation> operations, @NonNull Uri uri,
                       @NonNull SQLiteUser object) {
        operations.add(ContentProviderOperation.newDelete(uri)
                .withSelection(SQLiteProvider.WHERE_ID_EQ, new String[]{String.valueOf(object.mId)})
                .build());
    }

    @Override
    public void onTrimMemory() {
        mObjectsCache.clear();
    }

}
