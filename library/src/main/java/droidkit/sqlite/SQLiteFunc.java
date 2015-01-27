package droidkit.sqlite;

import android.database.Cursor;
import android.support.annotation.NonNull;

/**
 * @author Daniel Serdyukov
 */
interface SQLiteFunc<F> {

    F apply(@NonNull Cursor cursor, @NonNull String column);

}
