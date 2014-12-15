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

    private static final String TAG = "SQLiteTest";

    private SQLite mSQLite;

    public SQLiteTest() {
        super(SQLiteProvider.class, BuildConfig.APPLICATION_ID);
        SQLite.attach(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID);
    }

    private static void assertSQLiteUser(@NonNull Cursor cursor, @NonNull String name, int age) {
        try {
            Assert.assertTrue(cursor.moveToFirst());
            Assert.assertEquals(name, CursorUtils.getString(cursor, "name"));
            Assert.assertEquals(age, CursorUtils.getInt(cursor, "age"));
        } finally {
            cursor.close();
        }
    }

    private static void insert10Users(@NonNull SQLite sqlite) {
        sqlite.beginTransaction();
        for (int i = 0; i < 10; ++i) {
            SQLiteUser user = new SQLiteUser();
            user.setName("User #" + i);
            user.setAge(i);
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
        mSQLite.insert(user);
        assertSQLiteUser(getMockContentResolver().query(SQLiteUser.URI, null, null, null, null), "John", 25);
    }

    public void testUpdate() throws Exception {
        insert10Users(mSQLite);
        final SQLiteUser john = mSQLite.where(SQLiteUser.class).first();
        Assert.assertNotNull(john);
        john.setName("Jane");
        john.setAge(22);
        mSQLite.update(john);
        assertSQLiteUser(getMockContentResolver().query(ContentUris.withAppendedId(SQLiteUser.URI, 1),
                null, null, null, null), "Jane", 22);
        assertSQLiteUser(getMockContentResolver().query(ContentUris.withAppendedId(SQLiteUser.URI, 2),
                null, null, null, null), "User #1", 1);
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

    public void testQueryAll() throws Exception {
        insert10Users(mSQLite);
        final SQLiteResult<SQLiteUser> users = mSQLite.where(SQLiteUser.class).all();
        try {
            Assert.assertEquals(10, users.size());
            Assert.assertEquals(4, users.get(4).getAge());
        } finally {
            IOUtils.closeQuietly(users);
        }
    }

    public void testQueryWithOrder() throws Exception {
        insert10Users(mSQLite);
        SQLiteUser user = mSQLite.where(SQLiteUser.class).orderBy("age", false).first();
        Assert.assertNotNull(user);
        Assert.assertEquals(9, user.getAge());
        user = mSQLite.where(SQLiteUser.class).orderBy("age", false).last();
        Assert.assertNotNull(user);
        Assert.assertEquals(0, user.getAge());
    }

    public void testQueryWithLimit() throws Exception {
        insert10Users(mSQLite);
        final SQLiteResult<SQLiteUser> users = mSQLite.where(SQLiteUser.class)
                .limit(3)
                .all();
        try {
            Assert.assertEquals(3, users.size());
        } finally {
            IOUtils.closeQuietly(users);
        }
    }

}
