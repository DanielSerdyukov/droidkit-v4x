package droidkit.sqlite;

import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Daniel Serdyukov
 */
public interface SQLiteQuery<T> {

    String[] ROWID_COLUMNS = new String[]{BaseColumns._ID};

    String ASC = " ASC";

    String DESC = " DESC";

    String AND = " AND ";

    String OR = " OR ";

    String COMMA = ", ";

    String EQ = " = ?";

    String LT = " < ?";

    @NonNull
    SQLiteQuery<T> groupBy(@NonNull String column);

    @NonNull
    SQLiteQuery<T> having(@NonNull String value);

    @NonNull
    SQLiteQuery<T> orderBy(@NonNull String column);

    @NonNull
    SQLiteQuery<T> orderBy(@NonNull String column, boolean ascending);

    @NonNull
    SQLiteQuery<T> limit(long limit);

    /*@NonNull
    SQLiteQuery<T> beginWhereGroup();

    @NonNull
    SQLiteQuery<T> endWhereGroup();*/

    @NonNull
    SQLiteQuery<T> equalTo(@NonNull String column, long value);

    @NonNull
    SQLiteQuery<T> equalTo(@NonNull String column, double value);

    @NonNull
    SQLiteQuery<T> equalTo(@NonNull String column, @NonNull String value);

    @NonNull
    SQLiteQuery<T> lessThan(@NonNull String column, long value);

    @NonNull
    SQLiteQuery<T> lessThan(@NonNull String column, double value);

    @NonNull
    SQLiteQuery<T> lessThan(@NonNull String column, @NonNull String value);

    @NonNull
    SQLiteQuery<T> and();

    @NonNull
    SQLiteQuery<T> or();

    @NonNull
    SQLiteResult<T> all();

    @Nullable
    T first();

    @Nullable
    T last();

}
