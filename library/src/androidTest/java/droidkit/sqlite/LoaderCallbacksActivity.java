package droidkit.sqlite;

import android.app.Activity;
import android.content.Loader;
import android.os.Bundle;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import droidkit.annotation.OnCreateLoader;
import droidkit.annotation.OnLoadFinished;
import droidkit.content.Loaders;
import droidkit.sqlite.mock.User;

/**
 * @author Daniel Serdyukov
 */
public class LoaderCallbacksActivity extends Activity {

    final BlockingQueue<SQLiteResult<User>> mLoadedData = new LinkedBlockingQueue<>();

    final AtomicInteger mOnLoadFinishedCalls = new AtomicInteger();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Loaders.init(getLoaderManager(), droidkit.test.R.id.fake_loader, Bundle.EMPTY, this);
    }

    @OnCreateLoader(droidkit.test.R.id.fake_loader)
    Loader<SQLiteResult<User>> onCreateLoader() {
        return new SQLiteLoader<>(getApplicationContext(), User.class);
    }

    @OnLoadFinished(droidkit.test.R.id.fake_loader)
    void onLoadFinished(SQLiteResult<User> data) {
        mLoadedData.add(data);
        mOnLoadFinishedCalls.incrementAndGet();
    }

}
