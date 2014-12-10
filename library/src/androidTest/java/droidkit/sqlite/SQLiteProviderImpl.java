package droidkit.sqlite;

import android.net.Uri;
import android.support.annotation.NonNull;

import droidkit.test.BuildConfig;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteProviderImpl extends SQLiteProvider {

    int mChanges;

    int mInserts;

    int mUpdates;

    int mDeletes;

    @Override
    protected void onRegisterTypes() {
        registerType(BuildConfig.APPLICATION_ID, MockUser.class);
        mChanges = 0;
        mInserts = 0;
        mUpdates = 0;
        mDeletes = 0;
    }

    @NonNull
    @Override
    protected SQLiteHelper onCreateHelper() {
        return new SQLiteHelper(getContext(), null, 1);
    }

    @Override
    protected void onChange(@NonNull Uri baseUri) {
        super.onChange(baseUri);
        ++mChanges;
    }

    @Override
    protected void onInsert(@NonNull Uri baseUri, long rowid) {
        super.onInsert(baseUri, rowid);
        ++mInserts;
    }

    @Override
    protected void onUpdate(@NonNull Uri baseUri, int affectedRows) {
        super.onUpdate(baseUri, affectedRows);
        ++mUpdates;
    }

    @Override
    protected void onDelete(@NonNull Uri baseUri, int affectedRows) {
        super.onDelete(baseUri, affectedRows);
        ++mDeletes;
    }

}
