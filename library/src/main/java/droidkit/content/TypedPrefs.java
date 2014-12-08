package droidkit.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import droidkit.util.Dynamic;

/**
 * @author Daniel Serdyukov
 */
public final class TypedPrefs extends KeyValueProxy {

    private TypedPrefs(@NonNull SharedPreferences prefs) {
        super(new PreferenceDelegate(prefs));
    }

    public static <T> T from(@NonNull Context context, @NonNull Class<? extends T> type) {
        return from(PreferenceManager.getDefaultSharedPreferences(context), type);
    }

    public static <T> T from(@NonNull SharedPreferences prefs, @NonNull Class<? extends T> type) {
        return Dynamic.newProxy(new TypedPrefs(prefs), type);
    }

}
