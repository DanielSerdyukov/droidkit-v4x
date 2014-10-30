package droidkit.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * @author Daniel Serdyukov
 */
public final class PlayIntents {

    public static final String APPS = "apps";

    public static final String MOVIES = "movies";

    public static final String MUSIC = "music";

    public static final String BOOKS = "books";

    private PlayIntents() {
    }

    @NonNull
    public static Intent details(@NonNull Context context) {
        return details(context.getPackageName());
    }

    @NonNull
    public static Intent details(@NonNull String packageName) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
    }

    @NonNull
    public static Intent publisher(@NonNull String publisher) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:" + publisher));
    }

    @NonNull
    public static Intent search(@NonNull String query) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=" + query));
    }

    @NonNull
    public static Intent apps(@NonNull String query) {
        return search(query, APPS);
    }

    @NonNull
    public static Intent movies(@NonNull String query) {
        return search(query, MOVIES);
    }

    @NonNull
    public static Intent music(@NonNull String query) {
        return search(query, MUSIC);
    }

    @NonNull
    public static Intent books(@NonNull String query) {
        return search(query, BOOKS);
    }

    @NonNull
    private static Intent search(@NonNull String query, @NonNull String category) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=" + query + "&c=" + category));
    }

}
