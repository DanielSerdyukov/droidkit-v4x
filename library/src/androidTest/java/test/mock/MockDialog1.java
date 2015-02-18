package test.mock;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import droidkit.annotation.InjectView;

/**
 * @author Daniel Serdyukov
 */
public class MockDialog1 extends DialogFragment {

    @InjectView(droidkit.test.R.id.group)
    private RadioGroup mChoice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(droidkit.test.R.layout.fmt_mock_dialog1, container, false);
    }

    @Nullable
    public RadioGroup getChoice() {
        return mChoice;
    }

}
