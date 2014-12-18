package droidkit.sqlite;

import android.content.ContentUris;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.test.ProviderTestCase2;

import junit.framework.Assert;

import droidkit.database.CursorUtils;
import droidkit.io.IOUtils;
import droidkit.test.BuildConfig;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteTest extends ProviderTestCase2<SQLiteProvider> {

    private SQLite mSQLite;

    public SQLiteTest() {
        super(SQLiteProvider.class, BuildConfig.APPLICATION_ID);
        SQLite.attach(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID);
    }

    static void assertSQLiteUser(@NonNull Cursor cursor, @NonNull String name, int age, double balance,
                                 boolean blocked) {
        try {
            Assert.assertTrue(cursor.moveToFirst());
            Assert.assertEquals(name, CursorUtils.getString(cursor, "name"));
            Assert.assertEquals(age, CursorUtils.getInt(cursor, "age"));
            Assert.assertEquals(balance, CursorUtils.getDouble(cursor, "balance"));
            Assert.assertEquals(blocked, CursorUtils.getBoolean(cursor, "blocked"));
        } finally {
            cursor.close();
        }
    }

    static void insert10Users(@NonNull SQLite sqlite) {
        sqlite.beginTransaction();
        final double balance = 100.0;
        for (int i = 0; i < 10; ++i) {
            SQLiteUser user = new SQLiteUser();
            user.setName("User #" + i);
            user.setAge(i);
            user.setBalance(balance - i - 0.50);
            user.setBlocked(i % 2 == 0);
            sqlite.insert(user);
        }
        sqlite.commitTransaction();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getMockContentResolver();
        mSQLite = SQLite.with(getMockContext());
    }

    public void testInsert() throws Exception {
        final SQLiteUser user = new SQLiteUser();
        user.setName("John");
        user.setAge(25);
        user.setBalance(55.30);
        user.setBlocked(true);
        mSQLite.insert(user);
        assertSQLiteUser(getMockContentResolver().query(SQLiteUser.URI, null, null, null, null),
                "John", 25, 55.30, true);
    }

    public void testUpdate() throws Exception {
        insert10Users(mSQLite);
        final SQLiteUser john = mSQLite.where(SQLiteUser.class).first();
        Assert.assertNotNull(john);
        john.setName("Jane");
        john.setAge(22);
        john.setBalance(9.99);
        john.setBlocked(true);
        mSQLite.update(john);
        assertSQLiteUser(getMockContentResolver().query(ContentUris.withAppendedId(SQLiteUser.URI, 1),
                null, null, null, null), "Jane", 22, 9.99, true);
        assertSQLiteUser(getMockContentResolver().query(ContentUris.withAppendedId(SQLiteUser.URI, 2),
                null, null, null, null), "User #1", 1, 98.5, false);
    }

    public void testDelete() throws Exception {
        insert10Users(mSQLite);
        final SQLiteUser john = mSQLite.where(SQLiteUser.class).first();
        Assert.assertNotNull(john);
        final long removedId = john.getId();
        mSQLite.delete(john);
        final Cursor cursor = getMockContentResolver().query(SQLiteUser.URI, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Assert.assertFalse(removedId == CursorUtils.getLong(cursor, BaseColumns._ID));
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

}
