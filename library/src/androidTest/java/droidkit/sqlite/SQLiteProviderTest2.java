package droidkit.sqlite;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.test.ProviderTestCase2;

import junit.framework.Assert;

import droidkit.database.CursorUtils;
import droidkit.io.IOUtils;
import droidkit.test.BuildConfig;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteProviderTest2 extends ProviderTestCase2<SQLiteProviderImpl> {

    private static final Uri USER_URI_1 = new Uri.Builder()
            .scheme("content")
            .authority(BuildConfig.APPLICATION_ID)
            .appendPath("users")
            .appendPath("1")
            .build();

    private SQLite mSQLite;

    private SQLiteProviderImpl mProvider;

    public SQLiteProviderTest2() {
        super(SQLiteProviderImpl.class, BuildConfig.APPLICATION_ID);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mSQLite = SQLite.with(getMockContext());
        mProvider = getProvider();
    }

    public void testCreateUser() throws Exception {
        final MockUser user = mSQLite.createObject(MockUser.class);
        Assert.assertNotNull(user);
        final Cursor cursor = getMockContentResolver().query(USER_URI_1, null, null, null, null);
        try {
            Assert.assertTrue(cursor.moveToFirst());
            Assert.assertEquals(CursorUtils.getLong(cursor, BaseColumns._ID), user.getRowId());
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    public void testUpdateUserName() throws Exception {
        final MockUser user = mSQLite.createObject(MockUser.class);
        Assert.assertNotNull(user);
        user.setName("John");
        final Cursor cursor = getMockContentResolver().query(USER_URI_1, null, null, null, null);
        try {
            Assert.assertTrue(cursor.moveToFirst());
            Assert.assertEquals(CursorUtils.getString(cursor, "name"), user.getName());
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    public void testUpdateUserAge() throws Exception {
        final MockUser user = mSQLite.createObject(MockUser.class);
        Assert.assertNotNull(user);
        user.setAge(25);
        final Cursor cursor = getMockContentResolver().query(USER_URI_1, null, null, null, null);
        try {
            Assert.assertTrue(cursor.moveToFirst());
            Assert.assertEquals(CursorUtils.getInt(cursor, "age"), user.getAge());
        } finally {
            IOUtils.closeQuietly(cursor);
        }
    }

    public void testTransaction() throws Exception {
        mSQLite.beginTransaction();
        final MockUser user = mSQLite.createObject(MockUser.class);
        Assert.assertNotNull(user);
        user.setName("John");
        user.setAge(25);
        Assert.assertEquals(1, mProvider.mInserts);
        Assert.assertEquals(0, mProvider.mUpdates);
        mSQLite.commitTransaction();
        Assert.assertEquals(1, mProvider.mUpdates);
    }

}
