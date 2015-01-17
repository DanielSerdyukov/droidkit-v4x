package droidkit.sqlite;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * @author Daniel Serdyukov
 */
public interface SQLiteTable<T> {

    @NonNull
    String getName();

    void create(@NonNull SQLiteDatabase db);

    void upgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion);

    @Nullable
    T getRow(long rowId);

    @NonNull
    T instantiate(@NonNull Cursor cursor);

    void insert(@NonNull ContentResolver db, @NonNull Uri uri, @NonNull T object);

    void insert(@NonNull ArrayList<ContentProviderOperation> operations, @NonNull Uri uri, @NonNull T object);

    void update(@NonNull ContentResolver db, @NonNull Uri uri, @NonNull T object);

    void update(@NonNull ArrayList<ContentProviderOperation> operations, @NonNull Uri uri, @NonNull T object);

    void delete(@NonNull ContentResolver db, @NonNull Uri uri, @NonNull T object);

    void delete(@NonNull ArrayList<ContentProviderOperation> operations, @NonNull Uri uri, @NonNull T object);

}
