package test.mock;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import droidkit.annotation.OnActionClick;

/**
 * @author Daniel Serdyukov
 */
public class OnActionClickFragment extends Fragment {

    private int mAddActionId;

    private int mEditActionId;

    private int mSettingsActionId;

    private int mHelpActionId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new FrameLayout(inflater.getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(droidkit.test.R.menu.action_click, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
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
