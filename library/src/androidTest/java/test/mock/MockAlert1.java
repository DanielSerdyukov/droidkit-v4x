package test.mock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.RadioGroup;

import droidkit.annotation.InjectView;

/**
 * @author Daniel Serdyukov
 */
public class MockAlert1 extends DialogFragment {

    @InjectView(droidkit.test.R.id.group)
    private RadioGroup mChoice;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setView(LayoutInflater.from(getActivity())
                        .inflate(droidkit.test.R.layout.fmt_mock_dialog1, null))
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Nullable
    public RadioGroup getChoice() {
        return mChoice;
    }

}
