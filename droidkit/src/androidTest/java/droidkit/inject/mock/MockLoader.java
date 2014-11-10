package droidkit.inject.mock;

import android.content.Context;
import android.content.Loader;

/**
 * @author Daniel Serdyukov
 */
public class MockLoader extends Loader<String> {

    private final String mExpected;

    private String mResult;

    public MockLoader(Context context, String expected) {
        super(context);
        mExpected = expected;
    }

    @Override
    public void deliverResult(String data) {
        mResult = data;
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (mResult == null) {
            forceLoad();
        } else {
            deliverResult(mResult);
        }
    }

    @Override
    protected void onForceLoad() {
        deliverResult(mExpected);
    }

}
