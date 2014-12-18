package droidkit.sqlite;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import droidkit.content.StringValue;
import droidkit.io.IOUtils;

/**
 * @author Daniel Serdyukov
 */
class SQLiteQueryImpl<T> implements SQLiteQuery<T> {

    private static final String[] ROWID_COLUMNS = new String[]{BaseColumns._ID};

    private static final String ASC = " ASC";

    private static final String DESC = " DESC";

    private static final String AND = " AND ";

    private static final String OR = " OR ";

    private static final String COMMA = ", ";

    private static final String EQ = " = ?";

    private static final String NOT_EQ = " <> ?";

    private static final String LT = " < ?";

    private static final String LT_OR_EQ = " <= ?";

    private static final String GT = " > ?";

    private static final String GT_OR_EQ = " >= ?";

    private static final String BETWEEN = " BETWEEN";

    private static final String MAX = "MAX";

    private static final String MIN = "MIN";

    private static final String SUM = "SUM";

    private static final String TRUE = "1";

    private static final String FALSE = "0";

    private static final SQLiteFunc<Integer> INT_FUNC = new SQLiteFunc<Integer>() {
        @Override
        public Integer apply(@NonNull Cursor cursor, @NonNull String column) {
            return cursor.getInt(0);
        }
    };

    private static final SQLiteFunc<Long> LONG_FUNC = new SQLiteFunc<Long>() {
        @Override
        public Long apply(@NonNull Cursor cursor, @NonNull String column) {
            return cursor.getLong(0);
        }
    };

    private static final SQLiteFunc<Double> DOUBLE_FUNC = new SQLiteFunc<Double>() {
        @Override
        public Double apply(@NonNull Cursor cursor, @NonNull String column) {
            return cursor.getDouble(0);
        }
    };

    private final ContentResolver mDb;

    private final Uri mUri;

    private final SQLiteTable<T> mTable;

    private final List<String> mOrderBy = new ArrayList<>();

    private final List<String> mWhere = new ArrayList<>();

    private final List<String> mWhereArgs = new ArrayList<>();

    private String mGroupBy;

    private String mHaving;

    private long mLimit;

    private boolean mHasGroupBayOrHavingOrLimit;

    SQLiteQueryImpl(@NonNull ContentResolver db, @NonNull Uri uri, @NonNull SQLiteTable<T> table) {
        mDb = db;
        mUri = uri;
        mTable = table;
    }

    @NonNull
    @Override
    public SQLiteQuery<T> groupBy(@NonNull String column) {
        mGroupBy = column;
        mHasGroupBayOrHavingOrLimit = true;
        return this;
    }

    @NonNull
    @Override
    public SQLiteQuery<T> having(@NonNull String value) {
        mHaving = value;
        mHasGroupBayOrHavingOrLimit = true;
        return this;
    }

    @NonNull
    @Override
    public SQLiteQuery<T> orderBy(@NonNull String column) {
        return orderBy(column, true);
    }

    @NonNull
    @Override
    public SQLiteQuery<T> orderBy(@NonNull String column, boolean ascending) {
        mOrderBy.add(column + (ascending ? ASC : DESC));
        return this;
    }

    @NonNull
    @Override
    public SQLiteQuery<T> limit(long limit) {
        mLimit = limit;
        mHasGroupBayOrHavingOrLimit = true;
        return this;
    }

    @NonNull
    @Override
    public SQLiteQuery<T> equalTo(@NonNull String column, long value) {
        return equalTo(column, String.valueOf(value));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> equalTo(@NonNull String column, double value) {
        return equalTo(column, String.valueOf(value));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> equalTo(@NonNull String column, @NonNull String value) {
        mWhere.add(column + EQ);
        mWhereArgs.add(value);
        return this;
    }

    @NonNull
    @Override
    public SQLiteQuery<T> equalTo(@NonNull String column, boolean value) {
        return equalTo(column, value ? TRUE : FALSE);
    }

    @NonNull
    @Override
    public SQLiteQuery<T> notEqualTo(@NonNull String column, long value) {
        return notEqualTo(column, String.valueOf(value));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> notEqualTo(@NonNull String column, double value) {
        return notEqualTo(column, String.valueOf(value));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> notEqualTo(@NonNull String column, @NonNull String value) {
        mWhere.add(column + NOT_EQ);
        mWhereArgs.add(value);
        return this;
    }

    @NonNull
    @Override
    public SQLiteQuery<T> notEqualTo(@NonNull String column, boolean value) {
        return notEqualTo(column, value ? TRUE : FALSE);
    }

    @NonNull
    @Override
    public SQLiteQuery<T> lessThan(@NonNull String column, long value) {
        return lessThan(column, String.valueOf(value));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> lessThan(@NonNull String column, double value) {
        return lessThan(column, String.valueOf(value));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> lessThan(@NonNull String column, @NonNull String value) {
        mWhere.add(column + LT);
        mWhereArgs.add(value);
        return this;
    }

    @NonNull
    @Override
    public SQLiteQuery<T> lessThanOrEqualTo(@NonNull String column, long value) {
        return lessThanOrEqualTo(column, String.valueOf(value));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> lessThanOrEqualTo(@NonNull String column, double value) {
        return lessThanOrEqualTo(column, String.valueOf(value));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> lessThanOrEqualTo(@NonNull String column, @NonNull String value) {
        mWhere.add(column + LT_OR_EQ);
        mWhereArgs.add(value);
        return this;
    }

    @NonNull
    @Override
    public SQLiteQuery<T> greaterThan(@NonNull String column, long value) {
        return greaterThan(column, String.valueOf(value));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> greaterThan(@NonNull String column, double value) {
        return greaterThan(column, String.valueOf(value));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> greaterThan(@NonNull String column, @NonNull String value) {
        mWhere.add(column + GT);
        mWhereArgs.add(value);
        return this;
    }

    @NonNull
    @Override
    public SQLiteQuery<T> greaterThanOrEqualTo(@NonNull String column, long value) {
        return greaterThanOrEqualTo(column, String.valueOf(value));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> greaterThanOrEqualTo(@NonNull String column, double value) {
        return greaterThanOrEqualTo(column, String.valueOf(value));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> greaterThanOrEqualTo(@NonNull String column, @NonNull String value) {
        mWhere.add(column + GT_OR_EQ);
        mWhereArgs.add(value);
        return this;
    }

    @NonNull
    @Override
    public SQLiteQuery<T> between(@NonNull String column, long value1, long value2) {
        return between(column, String.valueOf(value1), String.valueOf(value2));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> between(@NonNull String column, double value1, double value2) {
        return between(column, String.valueOf(value1), String.valueOf(value2));
    }

    @NonNull
    @Override
    public SQLiteQuery<T> and() {
        mWhere.add(AND);
        return this;
    }

    @NonNull
    @Override
    public SQLiteQuery<T> or() {
        mWhere.add(OR);
        return this;
    }

    @NonNull
    @Override
    public SQLiteResult<T> all() {
        final Cursor cursor = mDb.query(makeQueryUri(), ROWID_COLUMNS, makeWhere(), makeWhereArgs(),
                TextUtils.join(COMMA, mOrderBy));
        if (cursor.moveToFirst()) {
            return new SQLiteResultImpl<>(mDb, mUri, mTable, cursor);
        }
        IOUtils.closeQuietly(cursor);
        return new SQLiteEmptyResult<>();
    }

    @Nullable
    @Override
    public T first() {
        final SQLiteResult<T> result = all();
        try {
            if (!result.isEmpty()) {
                return result.get(0);
            }
        } finally {
            IOUtils.closeQuietly(result);
        }
        return null;
    }

    @Nullable
    @Override
    public T last() {
        final SQLiteResult<T> result = all();
        try {
            if (!result.isEmpty()) {
                return result.get(result.size() - 1);
            }
        } finally {
            IOUtils.closeQuietly(result);
        }
        return null;
    }

    @Override
    public int maxInt(@NonNull String column) {
        return applyFunc(INT_FUNC, MAX, column, 0);
    }

    @Override
    public long maxLong(@NonNull String column) {
        return applyFunc(LONG_FUNC, MAX, column, 0L);
    }

    @Override
    public double maxDouble(@NonNull String column) {
        return applyFunc(DOUBLE_FUNC, MAX, column, 0.0);
    }

    @Override
    public int minInt(@NonNull String column) {
        return applyFunc(INT_FUNC, MIN, column, 0);
    }

    @Override
    public long minLong(@NonNull String column) {
        return applyFunc(LONG_FUNC, MIN, column, 0L);
    }

    @Override
    public double minDouble(@NonNull String column) {
        return applyFunc(DOUBLE_FUNC, MIN, column, 0.0);
    }

    @Override
    public int sumInt(@NonNull String column) {
        return applyFunc(INT_FUNC, SUM, column, 0);
    }

    @Override
    public long sumLong(@NonNull String column) {
        return applyFunc(LONG_FUNC, SUM, column, 0L);
    }

    @Override
    public double sumDouble(@NonNull String column) {
        return applyFunc(DOUBLE_FUNC, SUM, column, 0.0);
    }

    @NonNull
    private Uri makeQueryUri() {
        Uri uri = mUri;
        if (mHasGroupBayOrHavingOrLimit) {
            final Uri.Builder builder = mUri.buildUpon();
            if (mGroupBy != null) {
                builder.appendQueryParameter(SQLiteProvider.GROUP_BY, mGroupBy);
            }
            if (mHaving != null) {
                builder.appendQueryParameter(SQLiteProvider.HAVING, mHaving);
            }
            if (mLimit > 0) {
                builder.appendQueryParameter(SQLiteProvider.LIMIT, String.valueOf(mLimit));
            }
            uri = builder.build();
        }
        return uri;
    }

    @Nullable
    private String makeWhere() {
        if (!mWhere.isEmpty()) {
            return TextUtils.join(StringValue.EMPTY, mWhere);
        }
        return null;
    }

    @Nullable
    private String[] makeWhereArgs() {
        if (!mWhereArgs.isEmpty()) {
            return mWhereArgs.toArray(new String[mWhereArgs.size()]);
        }
        return null;
    }

    @NonNull
    private <F> F applyFunc(@NonNull SQLiteFunc<F> func, @NonNull String function, @NonNull String column,
                            @NonNull F defaultValue) {
        final Cursor cursor = mDb.query(makeQueryUri(), new String[]{function + "(" + column + ")"},
                makeWhere(), makeWhereArgs(), TextUtils.join(COMMA, mOrderBy));
        try {
            if (cursor.moveToFirst()) {
                return func.apply(cursor, column);
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
        return defaultValue;
    }

    @NonNull
    public SQLiteQuery<T> between(@NonNull String column, String value1, String value2) {
        mWhere.add(column + BETWEEN + " ? AND ?");
        mWhereArgs.add(value1);
        mWhereArgs.add(value2);
        return this;
    }

}
