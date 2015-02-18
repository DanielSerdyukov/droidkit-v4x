package test.mock;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import droidkit.annotation.OnClick;

/**
 * @author Daniel Serdyukov
 */
public class OnClickFragment extends Fragment {

    private int mClickedId1;

    private int mClickedId2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(droidkit.test.R.layout.fmt_on_click, container, false);
    }

    public int getClickedId1() {
        return mClickedId1;
    }

    public int getClickedId2() {
        return mClickedId2;
    }

    @OnClick(droidkit.test.R.id.button1)
    private void onButton1Click() {
        mClickedId1 = droidkit.test.R.id.button1;
    }

    @OnClick(droidkit.test.R.id.button2)
    private void onButton2Click(@NonNull View view) {
        mClickedId2 = view.getId();
    }

}
