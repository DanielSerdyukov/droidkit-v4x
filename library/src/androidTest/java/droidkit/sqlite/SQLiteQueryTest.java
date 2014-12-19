package droidkit.sqlite;

import android.content.ContentValues;
import android.test.ProviderTestCase2;

import junit.framework.Assert;

import droidkit.BuildConfig;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteQueryTest extends ProviderTestCase2<SQLiteProvider> {

    private SQLite mSQLite;

    public SQLiteQueryTest() {
        super(SQLiteProvider.class, BuildConfig.APPLICATION_ID);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mSQLite = SQLite.with(getMockContext(), BuildConfig.APPLICATION_ID);
        final ContentValues values = new ContentValues();
        for (int i = 1; i <= 10; ++i) {
            values.put(User.Columns.NAME, i % 2 == 0 ? "Jane" : "John");
            values.put(User.Columns.AGE, 20 + i);
            values.put(User.Columns.BALANCE, 9.99 + i);
            values.put(User.Columns.BLOCKED, i % 2 == 0);
            getMockContentResolver().insert(User.URI, values);
            values.clear();
        }
    }

    public void testQueryAll() throws Exception {
        Assert.assertEquals(10, mSQLite.all(User.class).size());
    }

    public void testOrderBy() throws Exception {
        final SQLiteResult<User> users = mSQLite.where(User.class).orderBy(User.Columns.AGE, false).all();
        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(30 - i, users.get(i).getAge());
        }
    }

    public void testLimit() throws Exception {
        Assert.assertEquals(5, mSQLite.where(User.class).limit(5).all().size());
    }

    public void testEqualTo() throws Exception {
        final SQLiteResult<User> users = mSQLite.where(User.class)
                .equalTo(User.Columns.NAME, "John")
                .all();
        for (final User user : users) {
            Assert.assertEquals("John", user.getName());
        }
    }

    public void testNotEqualTo() throws Exception {
        final SQLiteResult<User> users = mSQLite.where(User.class)
                .notEqualTo(User.Columns.NAME, "John")
                .all();
        Assert.assertFalse(users.isEmpty());
        for (final User user : users) {
            Assert.assertEquals("Jane", user.getName());
        }
    }

    public void testWhereAnd() throws Exception {
        final SQLiteResult<User> users = mSQLite.where(User.class)
                .equalTo(User.Columns.NAME, "Jane")
                .and()
                .equalTo(User.Columns.AGE, 22)
                .all();
        Assert.assertFalse(users.isEmpty());
        for (final User user : users) {
            Assert.assertEquals("Jane", user.getName());
            Assert.assertEquals(22, user.getAge());
        }
    }

    public void testWhereOr() throws Exception {
        final SQLiteResult<User> users = mSQLite.where(User.class)
                .equalTo(User.Columns.NAME, "Jane")
                .or()
                .equalTo(User.Columns.AGE, 23)
                .all();
        Assert.assertEquals(6, users.size());
    }

    public void testLessThan() throws Exception {
        final SQLiteResult<User> users = mSQLite.where(User.class)
                .lessThan(User.Columns.AGE, 25)
                .all();
        Assert.assertEquals(4, users.size());
    }

    public void testLessThanOrEqualTo() throws Exception {
        final SQLiteResult<User> users = mSQLite.where(User.class)
                .lessThanOrEqualTo(User.Columns.AGE, 25)
                .all();
        Assert.assertEquals(5, users.size());
    }

    public void testGreaterThan() throws Exception {
        final SQLiteResult<User> users = mSQLite.where(User.class)
                .greaterThan(User.Columns.AGE, 25)
                .all();
        Assert.assertEquals(5, users.size());
    }

    public void testGreaterThanOrEqualTo() throws Exception {
        final SQLiteResult<User> users = mSQLite.where(User.class)
                .greaterThanOrEqualTo(User.Columns.AGE, 25)
                .all();
        Assert.assertEquals(6, users.size());
    }

    @Override
    public void tearDown() throws Exception {
        getMockContentResolver().delete(User.URI, null, null);
        super.tearDown();
    }

    public void testMaxInt() throws Exception {
        Assert.assertEquals(24, mSQLite.where(User.class).lessThan(User.Columns.AGE, 25)
                .maxInt(User.Columns.AGE));
    }

    public void testMaxLong() throws Exception {
        Assert.assertEquals(24L, mSQLite.where(User.class).lessThan(User.Columns.AGE, 25)
                .maxLong(User.Columns.AGE));
    }

    public void testMaxDouble() throws Exception {
        Assert.assertEquals(13.99, mSQLite.where(User.class).lessThan(User.Columns.AGE, 25)
                .maxDouble(User.Columns.BALANCE));
    }

    public void testMinInt() throws Exception {
        Assert.assertEquals(26, mSQLite.where(User.class).greaterThan(User.Columns.AGE, 25)
                .minInt(User.Columns.AGE));
    }

    public void testMinLong() throws Exception {
        Assert.assertEquals(26L, mSQLite.where(User.class).greaterThan(User.Columns.AGE, 25)
                .minLong(User.Columns.AGE));
    }

    public void testMinDouble() throws Exception {
        Assert.assertEquals(15.99, mSQLite.where(User.class).greaterThan(User.Columns.AGE, 25)
                .minDouble(User.Columns.BALANCE));
    }

    public void testSumInt() throws Exception {
        Assert.assertEquals(21 + 22 + 23 + 24, mSQLite.where(User.class).lessThan(User.Columns.AGE, 25)
                .sumInt(User.Columns.AGE));
    }

    public void testSumLong() throws Exception {
        Assert.assertEquals(21L + 22L + 23L + 24L, mSQLite.where(User.class).lessThan(User.Columns.AGE, 25)
                .sumLong(User.Columns.AGE));
    }

    public void testSumDouble() throws Exception {
        Assert.assertEquals(10.99 + 11.99 + 12.99 + 13.99, mSQLite.where(User.class).lessThan(User.Columns.AGE, 25)
                .sumDouble(User.Columns.BALANCE));
    }

    public void testBetween() throws Exception {
        final SQLiteResult<User> users = mSQLite.where(User.class).between(User.Columns.AGE, 24, 26).all();
        final int age[] = new int[]{24, 25, 26};
        Assert.assertEquals(age.length, users.size());
        for (int i = 0; i < age.length; ++i) {
            Assert.assertEquals(age[i], users.get(i).getAge());
        }
    }

}
