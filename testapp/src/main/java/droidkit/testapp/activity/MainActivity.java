package droidkit.testapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import droidkit.annotation.InjectView;
import droidkit.annotation.OnClick;
import droidkit.testapp.R;

/**
 * @author Daniel Serdyukov
 */
public class MainActivity extends Activity {

    @InjectView(R.id.login)
    private EditText mEditText;

    @InjectView(android.R.id.button1)
    private Button mButton1;

    private View mClickedView2;

    private View mClickedView3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
        mClickedView2 = findViewById(android.R.id.button2);
    }

    @OnClick(android.R.id.button3)
    void onButton3Click(@NonNull View view) {
        mClickedView3 = view;
    }

}
