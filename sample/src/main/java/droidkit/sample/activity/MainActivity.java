package droidkit.sample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;

import droidkit.annotation.InjectView;
import droidkit.annotation.OnActionClick;
import droidkit.annotation.OnClick;
import droidkit.sample.R;

/**
 * @author Daniel Serdyukov
 */
public class MainActivity extends Activity {

    @InjectView(R.id.drawer_layout)
    private DrawerLayout mDrawer;

    private boolean mButton1Clicked;

    private boolean mAddActionClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public DrawerLayout getDrawer() {
        return mDrawer;
    }

    public boolean isButton1Clicked() {
        return mButton1Clicked;
    }

    public boolean isAddActionClicked() {
        return mAddActionClicked;
    }

    @OnClick(android.R.id.button1)
    void onButton1Click() {
        mButton1Clicked = true;
    }

    @OnActionClick(R.id.action_add)
    void onAddActionClick() {
        mAddActionClicked = true;
    }

}
