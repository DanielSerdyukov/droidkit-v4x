package droidkit.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * @author Daniel Serdyukov
 */
public class SQLite$Delegate implements SQLiteDelegate {

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        SQLiteUser$SQLiteTable.create(db);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        SQLiteUser$SQLiteTable.drop(db);
        onCreate(db);
    }

}
