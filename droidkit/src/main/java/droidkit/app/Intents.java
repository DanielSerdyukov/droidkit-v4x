package droidkit.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * @author Daniel Serdyukov
 */
public final class Intents {

    private Intents() {
    }

    @NonNull
    public static List<ResolveInfo> getResolution(@NonNull Context context, @NonNull Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, 0);
    }

    public static boolean hasResolution(@NonNull Context context, @NonNull Intent intent) {
        return !getResolution(context, intent).isEmpty();
    }

    public static void startActivity(@NonNull Context context, @NonNull Intent intent, @Nullable CharSequence title) {
        if (hasResolution(context, intent)) {
            context.startActivity(intent);
        } else {
            context.startActivity(Intent.createChooser(intent, title));
        }
    }

}
