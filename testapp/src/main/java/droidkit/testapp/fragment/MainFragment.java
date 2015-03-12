package droidkit.testapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import droidkit.annotation.InjectView;
import droidkit.annotation.OnClick;
import droidkit.testapp.R;
import droidkit.view.Views;

/**
 * @author Daniel Serdyukov
 */
public class MainFragment extends Fragment {

    @InjectView(R.id.login)
    private EditText mEditText;

    @InjectView(android.R.id.button1)
    private Button mButton1;

    private View mClickedView2;

    private View mClickedView3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main, container, false);
    }

    public EditText getEditText() {
        return mEditText;
    }

    public Button getButton1() {
        return mButton1;
    }

    public View getClickedView2() {
        return mClickedView2;
    }

    public View getClickedView3() {
        return mClickedView3;
    }

    @OnClick(android.R.id.button2)
    void onButton2Click() {
        mClickedView2 = Views.findById(getView(), android.R.id.button2);
    }

    @OnClick(android.R.id.button3)
    void onButton3Click(@NonNull View view) {
        mClickedView3 = view;
    }

}
