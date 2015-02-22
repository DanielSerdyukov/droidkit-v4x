package test.mock;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author Daniel Serdyukov
 */
public class OnActionClickActivity2 extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(droidkit.test.R.layout.ac_on_action_click);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(droidkit.test.R.id.content, new OnActionClickFragment())
                    .commit();
        }
    }

}
