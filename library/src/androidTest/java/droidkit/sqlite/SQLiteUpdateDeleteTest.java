package droidkit.sqlite;

import android.test.ProviderTestCase2;

import junit.framework.Assert;

import droidkit.sqlite.mock.User;
import droidkit.test.BuildConfig;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteUpdateDeleteTest extends ProviderTestCase2<SQLiteProvider> {

    private SQLite mSQLite;

    public SQLiteUpdateDeleteTest() {
        super(SQLiteProvider.class, BuildConfig.APPLICATION_ID);
        SQLite.attach(BuildConfig.APPLICATION_ID);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mSQLite = SQLite.with(getMockContext());
        final User user = new User();
        user.setName("John");
        user.setAge(26);
        mSQLite.insert(user);
    }

    public void testUpdate() throws Exception {
        final User john = mSQLite.where(User.class)
                .equalTo(User.Columns.NAME, "John")
                .first();
        Assert.assertNotNull(john);
        john.setName("Jane");
        mSQLite.update(john);

        final User jane = mSQLite.where(User.class)
                .equalTo(User.Columns.NAME, "Jane")
                .first();
        Assert.assertNotNull(jane);
        Assert.assertEquals(john.getId(), jane.getId());
        Assert.assertEquals("Jane", jane.getName());
    }

    public void testUpdateInTransaction() throws Exception {
        final User john = mSQLite.where(User.class)
                .equalTo(User.Columns.NAME, "John")
                .first();
        Assert.assertNotNull(john);
        john.setName("Jane");
        mSQLite.beginTransaction();
        mSQLite.update(john);
        mSQLite.commitTransaction();

        final User jane = mSQLite.where(User.class)
                .equalTo(User.Columns.NAME, "Jane")
                .first();
        Assert.assertNotNull(jane);
        Assert.assertEquals(john.getId(), jane.getId());
        Assert.assertEquals("Jane", jane.getName());
    }

    public void testDelete() throws Exception {
        final User john = mSQLite.where(User.class)
                .equalTo(User.Columns.NAME, "John")
                .first();
        Assert.assertNotNull(john);
        mSQLite.delete(john);

        Assert.assertNull(mSQLite.where(User.class)
                .equalTo(User.Columns.NAME, "John")
                .first());
    }

    public void testDeleteInTransaction() throws Exception {
        final User john = mSQLite.where(User.class)
                .equalTo(User.Columns.NAME, "John")
                .first();
        Assert.assertNotNull(john);
        mSQLite.beginTransaction();
        mSQLite.delete(john);
        mSQLite.commitTransaction();

        Assert.assertNull(mSQLite.where(User.class)
                .equalTo(User.Columns.NAME, "John")
                .first());
    }

}
