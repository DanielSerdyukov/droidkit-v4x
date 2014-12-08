package droidkit.app;

import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

/**
 * @author Daniel Serdyukov
 */
public final class PickIntent {

    private PickIntent() {
    }

    @NonNull
    public static Intent pickContact() {
        return new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    }

    @NonNull
    public static Intent pickFile(@NonNull String mime) {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mime);
        return intent;
    }

    @NonNull
    public static Intent pickImage() {
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        return intent;
    }

    @NonNull
    public static Intent pickVideo() {
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Video.Media.CONTENT_TYPE);
        return intent;
    }

}
