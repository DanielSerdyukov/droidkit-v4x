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

    public void testOrderBy() throws Exception {
        SQLiteTest.insert10Users(mSQLite);
        SQLiteUser user = mSQLite.where(SQLiteUser.class).orderBy("age", false).first();
        Assert.assertNotNull(user);
        Assert.assertEquals(9, user.getAge());
        user = mSQLite.where(SQLiteUser.class).orderBy("age", false).last();
        Assert.assertNotNull(user);
        Assert.assertEquals(0, user.getAge());
    }

    public void testLimit() throws Exception {
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

    public void testEqual() throws Exception {
        SQLiteTest.insert10Users(mSQLite);
        final SQLiteResult<SQLiteUser> users = mSQLite.where(SQLiteUser.class)
                .equalTo("name", "User #3")
                .all();
        Assert.assertEquals(1, users.size());
        final SQLiteUser user = users.get(0);
        Assert.assertNotNull(user);
        Assert.assertEquals("User #3", user.getName());
    }

    public void testEqualAndEqual() throws Exception {
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

    public void testEqualOrEqual() throws Exception {
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

    public void testLessThan() throws Exception {
        SQLiteTest.insert10Users(mSQLite);
        final SQLiteResult<SQLiteUser> users = mSQLite.where(SQLiteUser.class)
                .lessThan("age", 5)
                .all();
        final long[] ids = new long[]{1, 2, 3, 4, 5};
        Assert.assertEquals(ids.length, users.size());
        for (int i = 0; i < ids.length; ++i) {
            final SQLiteUser user = users.get(i);
            Assert.assertNotNull(user);
            Assert.assertEquals(ids[i], user.getId());
        }
    }

    public void testLessThanOrEqualTo() throws Exception {
        SQLiteTest.insert10Users(mSQLite);
        final SQLiteResult<SQLiteUser> users = mSQLite.where(SQLiteUser.class)
                .lessThanOrEqualTo("age", 5)
                .all();
        final long[] ids = new long[]{1, 2, 3, 4, 5, 6};
        Assert.assertEquals(ids.length, users.size());
        for (int i = 0; i < ids.length; ++i) {
            final SQLiteUser user = users.get(i);
            Assert.assertNotNull(user);
            Assert.assertEquals(ids[i], user.getId());
        }
    }

    public void testGreaterThan() throws Exception {
        SQLiteTest.insert10Users(mSQLite);
        final SQLiteResult<SQLiteUser> users = mSQLite.where(SQLiteUser.class)
                .greaterThan("age", 5)
                .all();
        final long[] ids = new long[]{7, 8, 9, 10};
        Assert.assertEquals(ids.length, users.size());
        for (int i = 0; i < ids.length; ++i) {
            final SQLiteUser user = users.get(i);
            Assert.assertNotNull(user);
            Assert.assertEquals(ids[i], user.getId());
        }
    }

    public void testGreaterThanOrEqualTo() throws Exception {
        SQLiteTest.insert10Users(mSQLite);
        final SQLiteResult<SQLiteUser> users = mSQLite.where(SQLiteUser.class)
                .greaterThanOrEqualTo("age", 5)
                .all();
        final long[] ids = new long[]{6, 7, 8, 9, 10};
        Assert.assertEquals(ids.length, users.size());
        for (int i = 0; i < ids.length; ++i) {
            final SQLiteUser user = users.get(i);
            Assert.assertNotNull(user);
            Assert.assertEquals(ids[i], user.getId());
        }
    }

}
