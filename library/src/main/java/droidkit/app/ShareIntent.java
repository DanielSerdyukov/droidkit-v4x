package droidkit.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Serdyukov
 */
public final class ShareIntent {

    private ShareIntent() {
    }

    @NonNull
    public static Intent shareText(@NonNull String text) {
        return share("text/*", text, null);
    }

    @NonNull
    public static Intent shareImage(@NonNull List<Uri> attachments) {
        return share("image/*", null, attachments);
    }

    @NonNull
    public static Intent shareVideo(@NonNull List<Uri> attachments) {
        return share("video/*", null, attachments);
    }

    @NonNull
    public static Intent share(@Nullable String text, @NonNull List<Uri> attachments) {
        return share("*/*", text, attachments);
    }

    @NonNull
    private static Intent share(@NonNull String mime, @Nullable String text, @Nullable List<Uri> attachments) {
        final Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType(mime);
        if (!TextUtils.isEmpty(text)) {
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }
        if (attachments != null && !attachments.isEmpty()) {
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, new ArrayList<Parcelable>(attachments));
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

}
