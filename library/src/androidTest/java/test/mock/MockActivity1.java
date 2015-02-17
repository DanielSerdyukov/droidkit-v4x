package test.mock;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import droidkit.annotation.InjectView;

/**
 * @author Daniel Serdyukov
 */
public class MockActivity1 extends Activity {

    @InjectView(android.R.id.text1)
    private TextView mAndroidText1;

    @InjectView(droidkit.test.R.id.text1)
    private TextView mDroidkitText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(droidkit.test.R.layout.ac_mock1);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(droidkit.test.R.id.content, new MockFragment1())
                    .commit();
        }
    }

    public TextView getAndroidText1() {
        return mAndroidText1;
    }

    public TextView getDroidkitText1() {
        return mDroidkitText1;
    }

}
