package droidkit.inject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

/**
 * @author Daniel Serdyukov
 */
public class MockFragment extends Fragment {

    @InjectView(android.R.id.list)
    ListView mListView;

    @InjectView(android.R.id.inputArea)
    private EditText mInputArea;

    private boolean mOnActivityCreatedCalled;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(droidkit.test.R.layout.fmt_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOnActivityCreatedCalled = true;
    }

    public ListView getListView() {
        return mListView;
    }

    public EditText getInputArea() {
        return mInputArea;
    }

    public boolean isOnActivityCreatedCalled() {
        return mOnActivityCreatedCalled;
    }

}
