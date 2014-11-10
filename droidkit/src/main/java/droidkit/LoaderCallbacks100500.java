package droidkit;

import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

/**
 * @author Daniel Serdyukov
 */
@SuppressWarnings("unchecked")
public class LoaderCallbacks100500 implements android.app.LoaderManager.LoaderCallbacks<Object> {

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new MockLoader(null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }


    @Override
    public void onLoaderReset(Loader loader) {

    }

    private static class MockLoader extends Loader<Cursor> {
        public MockLoader(Context context) {
            super(context);
        }
    }

}
