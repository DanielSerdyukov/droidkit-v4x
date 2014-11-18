package droidkit.inject.mock;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import droidkit.app.Loaders;
import droidkit.annotation.InjectView;
import droidkit.annotation.OnClick;
import droidkit.annotation.OnCreateLoader;
import droidkit.annotation.OnLoadFinished;

/**
 * @author Daniel Serdyukov
 */
public class InjectFragment extends Fragment {

    public static final String LOADER_2_RESULT = "LOADER_2_RESULT";

    @InjectView(android.R.id.list)
    ListView mListView;

    @InjectView(android.R.id.button1)
    private Button mButton1;

    @InjectView(android.R.id.button2)
    private Button mButton2;

    @InjectView(android.R.id.button3)
    private Button mButton3;

    private View mClickedView1;

    private boolean mButton1Clicked;

    private boolean mButton2Clicked;

    private boolean mButton3Clicked;

    private int mOnCreateLoaderId;

    private String mOnLoadFinishedResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(droidkit.test.R.layout.fmt_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Loaders.init(getLoaderManager(), droidkit.test.R.id.mock_loader_2, null, this);
    }

    public ListView getListView() {
        return mListView;
    }

    public Button getButton1() {
        return mButton1;
    }

    public Button getButton2() {
        return mButton2;
    }

    public Button getButton3() {
        return mButton3;
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

    public int getOnCreateLoaderId() {
        return mOnCreateLoaderId;
    }

    public String getOnLoadFinishedResult() {
        return mOnLoadFinishedResult;
    }

    @OnClick(android.R.id.button1)
    private void onButton1Click(@NonNull View view) {
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

    @OnCreateLoader(droidkit.test.R.id.mock_loader_2)
    Loader<String> onCreateMockLoader() {
        mOnCreateLoaderId = droidkit.test.R.id.mock_loader_2;
        return new SupportMockLoader(getActivity().getApplicationContext(), LOADER_2_RESULT);
    }

    @OnLoadFinished(droidkit.test.R.id.mock_loader_2)
    void onOnMockLoaderFinished(String data) {
        mOnLoadFinishedResult = data;
        Loaders.destroy(getLoaderManager(), droidkit.test.R.id.mock_loader_2);
    }

}
