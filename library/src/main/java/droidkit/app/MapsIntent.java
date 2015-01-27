package droidkit.app;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Locale;

/**
 * @author Daniel Serdyukov
 */
public final class MapsIntent {

    private static final String MAPS_URL = "https://maps.google.com/maps";

    private MapsIntent() {
    }

    @NonNull
    public static Intent openMaps() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(MAPS_URL));
    }

    @NonNull
    public static Intent openMaps(double lat, double lng) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.US, MAPS_URL + "?q=%f,%f", lat, lng)));
    }

    @NonNull
    public static Intent route(double lat, double lng) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.US, MAPS_URL + "?daddr=%f,%f", lat, lng)));
    }

    @NonNull
    public static Intent route(double fromLat, double fromLng, double toLat, double toLng) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.US, MAPS_URL +
                "?saddr=%f,%f&daddr=%f,%f", fromLat, fromLng, toLat, toLng)));
    }

    @NonNull
    public static Intent search(@NonNull String query) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(MAPS_URL + "?q=" + query));
    }

}
