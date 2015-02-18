package test.mock;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import droidkit.annotation.OnClick;

/**
 * @author Daniel Serdyukov
 */
public class OnClickActivity extends Activity {

    private int mClickedId1;

    private int mClickedId2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(droidkit.test.R.layout.ac_on_click);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(droidkit.test.R.id.content, new OnClickFragment())
                    .commit();
        }
    }

    public int getClickedId1() {
        return mClickedId1;
    }

    public int getClickedId2() {
        return mClickedId2;
    }

    @OnClick(android.R.id.button1)
    private void onButton1Click() {
        mClickedId1 = android.R.id.button1;
    }

    @OnClick(android.R.id.button2)
    private void onButton2Click(@NonNull View v) {
        mClickedId2 = v.getId();
    }

}
