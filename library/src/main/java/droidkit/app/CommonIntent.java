package droidkit.app;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Serdyukov
 */
public final class CommonIntent {

    private CommonIntent() {
    }

    @NonNull
    public static Intent openUrl(@NonNull String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    @NonNull
    public static Intent search(@NonNull String query) {
        final Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        return intent;
    }

    @NonNull
    public static Intent sendEmail(@NonNull String[] to, @NonNull String subject, @NonNull String body,
                                   @Nullable List<Uri> attachments) {
        final Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        final ArrayList<CharSequence> extraText = new ArrayList<>(1);
        extraText.add(body);
        intent.putCharSequenceArrayListExtra(Intent.EXTRA_TEXT, extraText);
        if (attachments != null && !attachments.isEmpty()) {
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, new ArrayList<Parcelable>(attachments));
        }
        return intent;
    }

    @NonNull
    public static Intent sendSms(@NonNull String to, @NonNull String message) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + to));
        intent.putExtra("sms_body", message);
        return intent;
    }

    @NonNull
    public static Intent openContent(@NonNull Uri uri) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        final String mime = URLConnection.guessContentTypeFromName(uri.toString());
        if (!TextUtils.isEmpty(mime)) {
            intent.setType(mime);
        }
        return intent;
    }

    @NonNull
    public static Intent openDialer(@NonNull String number) {
        return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
    }

}
