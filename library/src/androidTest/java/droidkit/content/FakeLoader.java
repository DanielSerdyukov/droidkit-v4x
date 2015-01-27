package droidkit.content;

import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

/**
 * @author Daniel Serdyukov
 */
public class FakeLoader extends Loader<Cursor> {

    public static final String NAME = "name";

    private final String mName;

    public FakeLoader(Context context, String name) {
        super(context);
        mName = name;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onForceLoad() {
        final MatrixCursor cursor = new MatrixCursor(new String[]{BaseColumns._ID, NAME});
        cursor.addRow(new Object[]{1, mName});
        deliverResult(cursor);
    }

}
