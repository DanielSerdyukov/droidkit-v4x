package droidkit.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Daniel Serdyukov
 */
class SQLiteHelper extends SQLiteOpenHelper {

    private final SQLiteSchema mSchema;

    public SQLiteHelper(@NonNull Context context, @Nullable String name, int version, @NonNull SQLiteSchema schema) {
        super(context, name, null, version);
        mSchema = schema;
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        mSchema.onCreate(db);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        mSchema.onUpgrade(db, oldVersion, newVersion);
    }

}
