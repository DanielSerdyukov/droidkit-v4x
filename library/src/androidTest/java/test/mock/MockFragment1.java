package test.mock;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import droidkit.annotation.InjectView;

/**
 * @author Daniel Serdyukov
 */
public class MockFragment1 extends Fragment {

    @InjectView(android.R.id.button1)
    private Button mAndroidButton1;

    @InjectView(droidkit.test.R.id.button1)
    private Button mDroidKitButton1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(droidkit.test.R.layout.fmt_mock1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public Button getAndroidButton1() {
        return mAndroidButton1;
    }

    public Button getDroidKitButton1() {
        return mDroidKitButton1;
    }

}
