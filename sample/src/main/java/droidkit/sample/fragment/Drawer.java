package droidkit.sample.fragment;

import android.app.Fragment;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import droidkit.annotation.InjectView;
import droidkit.annotation.OnCreateLoader;
import droidkit.annotation.OnLoadFinished;
import droidkit.content.Loaders;
import droidkit.sample.R;

/**
 * @author Daniel Serdyukov
 */
public class Drawer extends Fragment {

    @InjectView(android.R.id.list)
    private ListView mListView;

    private boolean mOnCreateLoader;

    private boolean mOnLoadFinished;

    public boolean isOnCreateLoader() {
        return mOnCreateLoader;
    }

    public boolean isOnLoadFinished() {
        return mOnLoadFinished;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fmt_drawer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Loaders.init(getLoaderManager(), 0, Bundle.EMPTY, this);
    }

    public ListView getListView() {
        return mListView;
    }

    @OnCreateLoader(0)
    public Loader<List<String>> onCreateLoader(@NonNull Bundle args) {
        mOnCreateLoader = true;
        return new Loader<List<String>>(getActivity().getApplicationContext()) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            protected void onForceLoad() {
                deliverResult(Arrays.asList("test"));
            }
        };
    }

    @OnLoadFinished(0)
    public void onUsersLoaded(List<String> users) {
        mOnLoadFinished = !users.isEmpty();
    }

}
