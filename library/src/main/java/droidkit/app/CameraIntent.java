package droidkit.app;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Daniel Serdyukov
 */
public final class CameraIntent {

    private CameraIntent() {
    }

    @NonNull
    public static Intent capturePhoto(@Nullable Uri output) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (output != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        }
        return intent;
    }

    @NonNull
    public static Intent captureVideo(@Nullable Uri output) {
        final Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (output != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        }
        return intent;
    }

}
