package droidkit.database;

import android.content.ContentValues;
import android.test.LoaderTestCase;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteLoaderTest extends LoaderTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final ContentValues values = new ContentValues();
        for (int i = 0; i < 10; ++i) {
        }
    }

    public void testSQLiteResult() throws Exception {
        /*final SQLiteResult<User> users = SQLite.with(getContext()).where(User.class).all();
        Assert.assertTrue(users.isEmpty());*/
    }

}
