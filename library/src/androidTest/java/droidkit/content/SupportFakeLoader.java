package droidkit.content;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v4.content.Loader;

/**
 * @author Daniel Serdyukov
 */
public class SupportFakeLoader extends Loader<Cursor> {

    public static final String NAME = "name";

    private final String mName;

    public SupportFakeLoader(Context context, String name) {
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
