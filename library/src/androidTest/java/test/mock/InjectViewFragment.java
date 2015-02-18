package test.mock;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import droidkit.annotation.InjectView;

/**
 * @author Daniel Serdyukov
 */
public class InjectViewFragment extends Fragment {

    @InjectView(android.R.id.text1)
    private TextView mAndroidText1;

    @InjectView(droidkit.test.R.id.text1)
    private TextView mDroidKitText1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(droidkit.test.R.layout.fmt_inject_view, container, false);
    }

    @Nullable
    public TextView getAndroidText1() {
        return mAndroidText1;
    }

    @Nullable
    public TextView getDroidKitText1() {
        return mDroidKitText1;
    }

}
