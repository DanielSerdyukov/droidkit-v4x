package droidkit.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(@NonNull Context context, @Nullable String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SQLite.createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SQLite.upgradeTables(db, oldVersion, newVersion);
    }

}
