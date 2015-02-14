package test.mock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * @author Daniel Serdyukov
 */
class MockActivity1$$$Proxy extends Activity {

    private final MockActivity1 mDelegate = (MockActivity1) this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MockActivity1$$$Injector.onCreate(mDelegate);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        MockActivity1$$$Injector.injectViews(getWindow(), mDelegate);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        MockActivity1$$$Injector.injectViews(view, mDelegate);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MockActivity1$$$Injector.onStart(mDelegate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MockActivity1$$$Injector.onResume(mDelegate);
    }

    @Override
    protected void onPause() {
        MockActivity1$$$Injector.onPause(mDelegate);
        super.onPause();
    }

    @Override
    protected void onStop() {
        MockActivity1$$$Injector.onStop(mDelegate);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        MockActivity1$$$Injector.onDestroy(mDelegate);
        super.onDestroy();
    }

}
