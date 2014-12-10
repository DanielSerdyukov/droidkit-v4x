package droidkit.sqlite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * @author Daniel Serdyukov
 */
public class MockUser$SQLiteObject extends MockUser {

    private static final String TABLE_NAME = "users";

    private final SQLite mSQLite;

    private final Uri mUri;

    private final long mRowId;

    public MockUser$SQLiteObject(@NonNull SQLite sqlite, @NonNull Uri uri) {
        this(sqlite, uri, 0);
    }

    public MockUser$SQLiteObject(@NonNull SQLite sqlite, @NonNull Uri uri, long rowid) {
        super();
        mSQLite = sqlite;
        mUri = uri;
        mRowId = rowid;
    }

    public static String table() {
        return TABLE_NAME;
    }

    public static void createTable(@NonNull SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users(_id INTEGER PRIMARY KEY, name TEXT, age INTEGER);");
    }

    public static void upgradeTable(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users;");
        createTable(db);
    }

    @Override
    public long getRowId() {
        return mRowId;
    }

    @Override
    public void setName(@NonNull String name) {
        super.setName(name);
        if (mSQLite.inTransaction()) {
            mSQLite.update(mUri, this).withValue("name", mName);
        } else {
            final ContentValues values = new ContentValues();
            values.put("name", mName);
            mSQLite.updateImmediately(mUri, values, mRowId);
        }
    }

    @Override
    public void setAge(int age) {
        super.setAge(age);
        if (mSQLite.inTransaction()) {
            mSQLite.update(mUri, this).withValue("age", mAge);
        } else {
            final ContentValues values = new ContentValues();
            values.put("age", mAge);
            mSQLite.updateImmediately(mUri, values, mRowId);
        }
    }

}
