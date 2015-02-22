package test.mock;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import droidkit.annotation.OnActionClick;

/**
 * @author Daniel Serdyukov
 */
public class OnActionClickActivity extends Activity {

    private int mAddActionId;

    private int mEditActionId;

    private int mSettingsActionId;

    private int mHelpActionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(droidkit.test.R.layout.ac_on_action_click);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(droidkit.test.R.menu.action_click, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public int getAddActionId() {
        return mAddActionId;
    }

    public int getEditActionId() {
        return mEditActionId;
    }

    public int getSettingsActionId() {
        return mSettingsActionId;
    }

    public int getHelpActionId() {
        return mHelpActionId;
    }

    @OnActionClick(droidkit.test.R.id.action_add)
    private void onAddActionClick() {
        mAddActionId = droidkit.test.R.id.action_add;
    }

    @OnActionClick(droidkit.test.R.id.action_edit)
    private void onEditActionClick(@NonNull MenuItem menuItem) {
        mEditActionId = menuItem.getItemId();
    }

    @OnActionClick(droidkit.test.R.id.action_settings)
    private boolean onSettingsActionClick() {
        mSettingsActionId = droidkit.test.R.id.action_settings;
        return true;
    }

    @OnActionClick(droidkit.test.R.id.action_help)
    private boolean onHelpActionClick(@NonNull MenuItem menuItem) {
        mHelpActionId = menuItem.getItemId();
        return true;
    }

}
