package droidkit.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import droidkit.annotation.OnClick;

/**
 * @author Daniel Serdyukov
 */
public class AlertFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage("Test")
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @OnClick(android.R.id.button1)
    void onTestClick() {

    }

}
