package droidkit.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import droidkit.annotation.InjectView;
import droidkit.annotation.OnActionClick;
import droidkit.annotation.OnClick;

/**
 * @author Daniel Serdyukov
 */
public class MainActivity extends Activity {

    @InjectView(droidkit.test.R.id.text1)
    TextView mText1;

    boolean mButton1Clicked;

    View mButton2;

    boolean mSettingsActionClicked;

    MenuItem mAddMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(droidkit.test.R.layout.ac_main);
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

    @OnActionClick(droidkit.test.R.id.action_add)
    void onAddAction(MenuItem item) {
        mAddMenuItem = item;
    }

}
