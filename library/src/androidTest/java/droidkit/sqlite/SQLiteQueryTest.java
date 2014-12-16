package droidkit.sqlite;

import android.test.ProviderTestCase2;

import junit.framework.Assert;

import droidkit.io.IOUtils;
import droidkit.test.BuildConfig;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteQueryTest extends ProviderTestCase2<SQLiteProvider> {

    private SQLite mSQLite;

    public SQLiteQueryTest() {
        super(SQLiteProvider.class, BuildConfig.APPLICATION_ID);
        SQLite.attach(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getMockContentResolver();
        mSQLite = SQLite.with(getMockContext());
    }

    public void testQueryAll() throws Exception {
        SQLiteTest.insert10Users(mSQLite);
        final SQLiteResult<SQLiteUser> users = mSQLite.where(SQLiteUser.class).all();
        try {
            Assert.assertEquals(10, users.size());
            Assert.assertEquals(4, users.get(4).getAge());
        } finally {
            IOUtils.closeQuietly(users);
        }
    }

    public void testQueryWithOrder() throws Exception {
        SQLiteTest.insert10Users(mSQLite);
        SQLiteUser user = mSQLite.where(SQLiteUser.class).orderBy("age", false).first();
        Assert.assertNotNull(user);
        Assert.assertEquals(9, user.getAge());
        user = mSQLite.where(SQLiteUser.class).orderBy("age", false).last();
        Assert.assertNotNull(user);
        Assert.assertEquals(0, user.getAge());
    }

    public void testQueryWithLimit() throws Exception {
        SQLiteTest.insert10Users(mSQLite);
        final SQLiteResult<SQLiteUser> users = mSQLite.where(SQLiteUser.class)
                .limit(3)
                .all();
        try {
            Assert.assertEquals(3, users.size());
        } finally {
            IOUtils.closeQuietly(users);
        }
    }

    public void testQueryWithEqual() throws Exception {
        SQLiteTest.insert10Users(mSQLite);
        final SQLiteResult<SQLiteUser> users = mSQLite.where(SQLiteUser.class)
                .equalTo("name", "User #3")
                .all();
        Assert.assertEquals(1, users.size());
        final SQLiteUser user = users.get(0);
        Assert.assertNotNull(user);
        Assert.assertEquals("User #3", user.getName());
    }

    public void testQueryWithEqualAnd() throws Exception {
        SQLiteTest.insert10Users(mSQLite);
        final SQLiteResult<SQLiteUser> users = mSQLite.where(SQLiteUser.class)
                .equalTo("name", "User #3")
                .and()
                .equalTo("age", 3)
                .all();
        Assert.assertEquals(1, users.size());
        final SQLiteUser user = users.get(0);
        Assert.assertNotNull(user);
        Assert.assertEquals("User #3", user.getName());
        Assert.assertEquals(3, user.getAge());
    }

    public void testQueryWithEqualOr() throws Exception {
        SQLiteTest.insert10Users(mSQLite);
        final SQLiteResult<SQLiteUser> users = mSQLite.where(SQLiteUser.class)
                .equalTo("name", "User #3")
                .or()
                .equalTo("age", 4)
                .all();
        Assert.assertEquals(2, users.size());
        SQLiteUser user = users.get(0);
        Assert.assertNotNull(user);
        Assert.assertEquals("User #3", user.getName());
        user = users.get(1);
        Assert.assertNotNull(user);
        Assert.assertEquals("User #4", user.getName());
    }

}
