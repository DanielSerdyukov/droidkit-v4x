package droidkit.inject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Daniel Serdyukov
 */
public class MockActivity extends Activity {

    @InjectView(android.R.id.text1)
    TextView mText1;

    @InjectView(android.R.id.button1)
    private Button mButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(droidkit.test.R.layout.ac_main);
    }

    public TextView getText1() {
        return mText1;
    }

    public Button getButton1() {
        return mButton1;
    }

}
