package droidkit.sqlite;

import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

import droidkit.sqlite.mock.User;
import droidkit.test.BuildConfig;

/**
 * @author Daniel Serdyukov
 */
public class LoaderCallbacksTest extends ActivityInstrumentationTestCase2<LoaderCallbacksActivity> {

    private SQLite mSQLite;

    private LoaderCallbacksActivity mActivity;

    public LoaderCallbacksTest() {
        super(LoaderCallbacksActivity.class);
        SQLite.attach(BuildConfig.APPLICATION_ID);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mSQLite = SQLite.with(getInstrumentation().getContext());
        final User user = new User();
        user.setName("John");
        user.setAge(25);
        mSQLite.insert(user);
        mActivity = getActivity();
    }

    public void testLoaderCallbacks() throws Exception {
        SQLiteResult<User> users = mActivity.mLoadedData.take();
        Assert.assertEquals(1, users.size());
        final User user = new User();
        user.setName("John");
        user.setAge(25);
        mSQLite.insert(user);
        users = mActivity.mLoadedData.take();
        Assert.assertEquals(2, users.size());
        Assert.assertEquals(2, mActivity.mOnLoadFinishedCalls.get());
    }

    @Override
    public void tearDown() throws Exception {
        getInstrumentation().getContext().getContentResolver().delete(User.URI, null, null);
        super.tearDown();
    }

}
