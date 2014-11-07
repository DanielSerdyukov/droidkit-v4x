package droidkit.inject.mock;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import droidkit.inject.InjectView;
import droidkit.inject.OnActionClick;
import droidkit.inject.OnClick;

/**
 * @author Daniel Serdyukov
 */
public class InjectActivity extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(droidkit.test.R.layout.ac_mock);
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

}
