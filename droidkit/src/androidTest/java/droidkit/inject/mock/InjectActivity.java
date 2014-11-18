package droidkit.inject.mock;

import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import droidkit.app.Loaders;
import droidkit.annotation.InjectView;
import droidkit.annotation.OnActionClick;
import droidkit.annotation.OnClick;
import droidkit.annotation.OnCreateLoader;
import droidkit.annotation.OnLoadFinished;

/**
 * @author Daniel Serdyukov
 */
public class InjectActivity extends FragmentActivity {

    public static final String LOADER_1_RESULT = "LOADER_1_RESULT";

    @InjectView(droidkit.test.R.id.fragment)
    FrameLayout mFrame;

    @InjectView(android.R.id.button1)
    private Button mButton1;

    private View mClickedView1;

    private boolean mButton1Clicked;

    private boolean mButton2Clicked;

    private boolean mButton3Clicked;

    private boolean mActionTest1Clicked;

    private boolean mActionTest2Clicked;

    private String mActionTest3Title;

    private int mOnCreateLoaderId;

    private String mOnLoadFinishedResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(droidkit.test.R.layout.ac_mock);
        Loaders.init(getLoaderManager(), droidkit.test.R.id.mock_loader_1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(droidkit.test.R.menu.mock, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public FrameLayout getFrame() {
        return mFrame;
    }

    public Button getButton1() {
        return mButton1;
    }

    public boolean isButton1Clicked() {
        return mButton1Clicked;
    }

    public View getClickedView1() {
        return mClickedView1;
    }

    public boolean isButton2Clicked() {
        return mButton2Clicked;
    }

    public boolean isButton3Clicked() {
        return mButton3Clicked;
    }

    public boolean isActionTest1Clicked() {
        return mActionTest1Clicked;
    }

    public boolean isActionTest2Clicked() {
        return mActionTest2Clicked;
    }

    public String getActionTest3Title() {
        return mActionTest3Title;
    }

    public int getOnCreateLoaderId() {
        return mOnCreateLoaderId;
    }

    public String getOnLoadFinishedResult() {
        return mOnLoadFinishedResult;
    }

    @OnClick(android.R.id.button1)
    private void onButton1Click(@NonNull View view) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        mClickedView1 = view;
        mButton1Clicked = true;
    }

    @OnClick(android.R.id.button2)
    private void onButton2Click() {
        mButton2Clicked = true;
    }

    @OnClick(android.R.id.button3)
    void onButton3Click() {
        mButton3Clicked = true;
    }

    @OnActionClick(droidkit.test.R.id.action_test1)
    void onActionTest1Click() {
        mActionTest1Clicked = true;
    }

    @OnActionClick(droidkit.test.R.id.action_test2)
    boolean onActionTest2Click() {
        mActionTest2Clicked = true;
        return true;
    }

    @OnActionClick(droidkit.test.R.id.action_test3)
    void onActionTest3Click(MenuItem item) {
        mActionTest3Title = item.getTitle().toString();
    }

    @OnCreateLoader(droidkit.test.R.id.mock_loader_1)
    Loader<String> onCreateMockLoader() {
        mOnCreateLoaderId = droidkit.test.R.id.mock_loader_1;
        return new MockLoader(getApplicationContext(), LOADER_1_RESULT);
    }

    @OnLoadFinished(droidkit.test.R.id.mock_loader_1)
    void onOnMockLoaderFinished(String data) {
        mOnLoadFinishedResult = data;
        Loaders.destroy(getLoaderManager(), droidkit.test.R.id.mock_loader_1);
    }

}
