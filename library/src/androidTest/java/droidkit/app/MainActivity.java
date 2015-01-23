package droidkit.app;

import android.app.Activity;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import droidkit.annotation.InjectView;
import droidkit.annotation.OnActionClick;
import droidkit.annotation.OnClick;
import droidkit.annotation.OnCreateLoader;
import droidkit.annotation.OnLoadFinished;
import droidkit.content.FakeLoader;
import droidkit.content.Loaders;

/**
 * @author Daniel Serdyukov
 */
public class MainActivity extends Activity {

    @InjectView(droidkit.test.R.id.text1)
    TextView mText1;

    boolean mButton1Clicked;

    View mButton2;

    boolean mSettingsActionClicked;

    Cursor mFakeCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(droidkit.test.R.layout.ac_main);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(droidkit.test.R.id.content, new MainFragment())
                    .commit();
        }
        Loaders.init(getLoaderManager(), droidkit.test.R.id.fake_loader, Bundle.EMPTY, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(droidkit.test.R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @OnClick(droidkit.test.R.id.button1)
    void onButton1Click() {
        mButton1Clicked = true;
    }

    @OnClick(droidkit.test.R.id.button2)
    void onButton2Click(View v) {
        mButton2 = v;
    }

    @OnActionClick(droidkit.test.R.id.action_settings)
    void onSettingsAction() {
        mSettingsActionClicked = true;
    }

    @OnLoadFinished(droidkit.test.R.id.fake_loader)
    void onFakeLoad(Loader<Cursor> loader, Cursor result) {
        mFakeCursor = result;
    }

    @OnCreateLoader(droidkit.test.R.id.fake_loader)
    Loader<Cursor> onCreateFakeLoader() {
        return new FakeLoader(getApplicationContext(), MainActivity.class.getSimpleName());
    }

}
