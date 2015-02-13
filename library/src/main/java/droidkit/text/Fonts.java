package droidkit.text;

import android.content.res.AssetManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import droidkit.log.Logger;

/**
 * @author Daniel Serdyukov
 */
public final class Fonts {

    private static final String FONTS_DIR = "fonts";

    private static final String FONTS_PATH = FONTS_DIR + "/";

    private static final Map<String, Typeface> CACHE = new HashMap<>();

    private Fonts() {
    }

    @NonNull
    public static List<String> list(@NonNull AssetManager am) {
        try {
            return Arrays.asList(am.list(FONTS_DIR));
        } catch (IOException e) {
            Logger.error(e);
        }
        return Collections.emptyList();
    }

    @NonNull
    public static Typeface get(@NonNull AssetManager am, @NonNull String name) {
        Typeface tf = CACHE.get(name);
        if (tf == null) {
            tf = Typeface.createFromAsset(am, FONTS_PATH + name);
            CACHE.put(name, tf);
        }
        return tf;
    }

    public static void apply(@NonNull TextView textView, @NonNull Typeface typeface) {
        textView.setTypeface(typeface);
        textView.getPaint().setAntiAlias(true);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    public static void apply(@NonNull TextView textView, @NonNull String name) {
        final AssetManager am = textView.getContext().getAssets();
        final List<String> fonts = list(am);
        if (fonts.contains(name)) {
            apply(textView, get(am, name));
        }
    }

}
