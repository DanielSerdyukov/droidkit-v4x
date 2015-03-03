package droidkit.sqlite;

import android.test.ProviderTestCase2;

import junit.framework.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import droidkit.sqlite.mock.User;
import droidkit.test.BuildConfig;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteLoaderTest extends ProviderTestCase2<SQLiteProvider> {

    private SQLite mSQLite;

    public SQLiteLoaderTest() {
        super(SQLiteProvider.class, BuildConfig.APPLICATION_ID);
        SQLite.attach(BuildConfig.APPLICATION_ID);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mSQLite = SQLite.with(getMockContext());
        final User user = new User();
        user.setName("John");
        user.setAge(25);
        mSQLite.insert(user);
    }

    public void testLoad() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        final SQLiteLoader<User> loader = new SQLiteLoader<User>(getMockContext(), User.class) {
            @Override
            public SQLiteResult<User> loadInBackground() {
                latch.countDown();
                return super.loadInBackground();
            }
        };
        loader.startLoading();
        final User user = mSQLite.where(User.class).first();
        Assert.assertNotNull(user);
        Assert.assertEquals("John", user.getName());
        user.setName("Jane");
        mSQLite.update(user);
        latch.await(1, TimeUnit.SECONDS);
    }

}
