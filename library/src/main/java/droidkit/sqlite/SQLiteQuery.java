package droidkit.sqlite;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author Daniel Serdyukov
 */
public interface SQLiteQuery<T> {

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

    @NonNull
    SQLiteQuery<T> equalTo(@NonNull String column, long value);

    @NonNull
    SQLiteQuery<T> equalTo(@NonNull String column, double value);

    @NonNull
    SQLiteQuery<T> equalTo(@NonNull String column, @NonNull String value);

    @NonNull
    SQLiteQuery<T> equalTo(@NonNull String column, boolean value);

    @NonNull
    SQLiteQuery<T> lessThan(@NonNull String column, long value);

    @NonNull
    SQLiteQuery<T> lessThan(@NonNull String column, double value);

    @NonNull
    SQLiteQuery<T> lessThan(@NonNull String column, @NonNull String value);

    @NonNull
    SQLiteQuery<T> lessThanOrEqualTo(@NonNull String column, long value);

    @NonNull
    SQLiteQuery<T> lessThanOrEqualTo(@NonNull String column, double value);

    @NonNull
    SQLiteQuery<T> lessThanOrEqualTo(@NonNull String column, @NonNull String value);

    @NonNull
    SQLiteQuery<T> greaterThan(@NonNull String column, long value);

    @NonNull
    SQLiteQuery<T> greaterThan(@NonNull String column, double value);

    @NonNull
    SQLiteQuery<T> greaterThan(@NonNull String column, @NonNull String value);

    @NonNull
    SQLiteQuery<T> greaterThanOrEqualTo(@NonNull String column, long value);

    @NonNull
    SQLiteQuery<T> greaterThanOrEqualTo(@NonNull String column, double value);

    @NonNull
    SQLiteQuery<T> greaterThanOrEqualTo(@NonNull String column, @NonNull String value);

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

    int maxInt(@NonNull String column);

    long maxLong(@NonNull String column);

    double maxDouble(@NonNull String column);

    int minInt(@NonNull String column);

    long minLong(@NonNull String column);

    double minDouble(@NonNull String column);

    int sumInt(@NonNull String column);

    long sumLong(@NonNull String column);

    double sumDouble(@NonNull String column);

}
