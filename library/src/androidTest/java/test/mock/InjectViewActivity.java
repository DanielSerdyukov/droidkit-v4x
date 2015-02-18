package test.mock;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import droidkit.annotation.InjectView;

/**
 * @author Daniel Serdyukov
 */
public class InjectViewActivity extends Activity implements View.OnClickListener {

    @InjectView(android.R.id.button1)
    private Button mAndroidButton1;

    @InjectView(droidkit.test.R.id.button1)
    private Button mDroidkitButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(droidkit.test.R.layout.ac_inject_view);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(droidkit.test.R.id.content, new InjectViewFragment())
                    .commit();
        }
    }

    @Nullable
    public Button getAndroidButton1() {
        return mAndroidButton1;
    }

    @Nullable
    public Button getDroidkitButton1() {
        return mDroidkitButton1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAndroidButton1.setOnClickListener(this);
        mDroidkitButton1.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        mAndroidButton1.setOnClickListener(null);
        mDroidkitButton1.setOnClickListener(null);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (mAndroidButton1 == v) {
            new InjectViewDialog().show(getFragmentManager(), InjectViewDialog.class.getName());
        } else if (mDroidkitButton1 == v) {
            new InjectViewAlert().show(getFragmentManager(), InjectViewAlert.class.getName());
        }
    }

}
