package droidkit.sqlite;

import android.database.Cursor;
import android.test.ProviderTestCase2;

import junit.framework.Assert;

import droidkit.BuildConfig;
import droidkit.database.CursorUtils;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteInsertTest extends ProviderTestCase2<SQLiteProvider> {

    public SQLiteInsertTest() {
        super(SQLiteProvider.class, BuildConfig.APPLICATION_ID);
        SQLite.attach(BuildConfig.APPLICATION_ID);
    }

    public void testInsert() throws Exception {
        super.setUp();
        final SQLite sqlite = SQLite.with(getMockContext());
        final User user = new User();
        user.setName("John");
        user.setAge(26);
        sqlite.insert(user);

        final Cursor cursor = getMockContentResolver().query(SQLite.acquireUri(User.class), null, null, null, null);
        Assert.assertTrue(cursor.moveToFirst());
        Assert.assertEquals(1, cursor.getCount());
        Assert.assertEquals("John", CursorUtils.getString(cursor, User.Columns.NAME));
        Assert.assertEquals(26, CursorUtils.getInt(cursor, User.Columns.AGE));
        cursor.close();
    }

}
