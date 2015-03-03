package droidkit.sqlite;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import junit.framework.Assert;

import droidkit.database.CursorUtils;
import droidkit.sqlite.mock.User;
import droidkit.test.BuildConfig;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteProviderTest extends ProviderTestCase2<SQLiteProvider> {

    private ContentResolver mDb;

    public SQLiteProviderTest() {
        super(SQLiteProvider.class, BuildConfig.APPLICATION_ID);
    }

    private static Uri insertJohn(ContentResolver db) {
        final ContentValues values = new ContentValues();
        values.put(User.Columns.NAME, "John");
        values.put(User.Columns.AGE, 26);
        values.put(User.Columns.BALANCE, 9.99);
        values.put(User.Columns.BLOCKED, false);
        return db.insert(User.URI, values);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mDb = getMockContentResolver();
    }

    public void testInsert() throws Exception {
        final Uri uri = insertJohn(mDb);
        final Cursor cursor = mDb.query(uri, null, null, null, null);
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals(1, cursor.getCount());
        Assert.assertEquals("John", CursorUtils.getString(cursor, User.Columns.NAME));
        Assert.assertEquals(26, CursorUtils.getInt(cursor, User.Columns.AGE));
        Assert.assertEquals(9.99, CursorUtils.getDouble(cursor, User.Columns.BALANCE));
        Assert.assertFalse(CursorUtils.getBoolean(cursor, User.Columns.BLOCKED));
    }

    public void testUpdate() throws Exception {
        final Uri uri = insertJohn(mDb);
        final ContentValues values = new ContentValues();
        values.put(User.Columns.NAME, "Jane");
        mDb.update(User.URI, values, User.Columns.AGE + "=?", new String[]{"26"});
        final Cursor cursor = mDb.query(uri, null, null, null, null);
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals(1, cursor.getCount());
        Assert.assertEquals("Jane", CursorUtils.getString(cursor, User.Columns.NAME));
        Assert.assertEquals(26, CursorUtils.getInt(cursor, User.Columns.AGE));
        Assert.assertEquals(9.99, CursorUtils.getDouble(cursor, User.Columns.BALANCE));
        Assert.assertFalse(CursorUtils.getBoolean(cursor, User.Columns.BLOCKED));
    }

    public void testDelete() throws Exception {
        final Uri uri = insertJohn(mDb);
        mDb.delete(User.URI, User.Columns.AGE + "=?", new String[]{"26"});
        final Cursor cursor = mDb.query(uri, null, null, null, null);
        Assert.assertFalse(cursor.moveToFirst());
    }

    @Override
    public void tearDown() throws Exception {
        getMockContentResolver().delete(User.URI, null, null);
        super.tearDown();
    }

}
