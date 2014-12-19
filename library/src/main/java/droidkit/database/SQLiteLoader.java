package droidkit.database;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;

import droidkit.io.IOUtils;
import droidkit.sqlite.SQLite;
import droidkit.sqlite.SQLiteQuery;
import droidkit.sqlite.SQLiteResult;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteLoader<T> extends AsyncTaskLoader<SQLiteResult<T>> {

    private final SQLiteQuery<T> mQuery;

    private SQLiteResult<T> mResult;

    public SQLiteLoader(@NonNull Context context, @NonNull Class<T> type) {
        super(context);
        mQuery = SQLite.with(context).where(type);
    }

    @NonNull
    public SQLiteQuery<T> query() {
        return mQuery;
    }

    @NonNull
    @Override
    public SQLiteResult<T> loadInBackground() {
        return query().all();
    }

    @Override
    public void deliverResult(@NonNull SQLiteResult<T> result) {
        if (isReset()) {
            if (mResult != null) {
                IOUtils.closeQuietly(mResult);
            }
            return;
        }
        SQLiteResult<T> oldResult = mResult;
        mResult = result;
        if (isStarted()) {
            super.deliverResult(result);
        }
        if (oldResult != null && oldResult != result) {
            IOUtils.closeQuietly(mResult);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mResult != null) {
            deliverResult(mResult);
        }
        if (takeContentChanged() || mResult == null) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        if (mResult != null) {
            IOUtils.closeQuietly(mResult);
        }
    }

}
