package droidkit.sample.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import droidkit.annotation.InjectView;
import droidkit.sample.R;

/**
 * @author Daniel Serdyukov
 */
public class Drawer extends Fragment {

    @InjectView(android.R.id.list)
    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fmt_drawer, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuItem.OnMenuItemClickListener listener = null;
        return listener != null && listener.onMenuItemClick(item) || super.onOptionsItemSelected(item);
    }

    public ListView getListView() {
        return mListView;
    }

}
