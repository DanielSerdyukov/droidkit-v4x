package test.mock;

import android.os.Bundle;
import android.widget.TextView;

import droidkit.annotation.InjectView;

/**
 * @author Daniel Serdyukov
 */
public class MockActivity1 extends MockActivity1$$$Proxy {

    @InjectView(android.R.id.text1)
    TextView mAndroidText1;

    @InjectView(droidkit.test.R.id.text1)
    TextView mDroidkitText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(droidkit.test.R.layout.ac_mock1);
    }

    public TextView getAndroidText1() {
        return mAndroidText1;
    }

    public TextView getDroidkitText1() {
        return mDroidkitText1;
    }

}
