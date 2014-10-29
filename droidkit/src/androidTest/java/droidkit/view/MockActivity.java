package droidkit.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Daniel Serdyukov
 */
public class MockActivity extends Activity {

    private TextView mText1;

    private Button mButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(droidkit.test.R.layout.ac_main);
        mText1 = Views.findById(this, android.R.id.text1);
        mButton1 = Views.findById(this, android.R.id.button1);
    }

    public TextView getText1() {
        return mText1;
    }

    public Button getButton1() {
        return mButton1;
    }

}
