package droidkit.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import droidkit.io.IOUtils;
import droidkit.log.Logger;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteQuery<T> {

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

    private static final String BETWEEN = " BETWEEN ? AND ?";

    private static final String LIKE = " LIKE ?";

    private static final String MAX = "MAX";

    private static final String MIN = "MIN";

    private static final String SUM = "SUM";

    private static final String COUNT = "COUNT";

    private static final String DISTINCT = "DISTINCT ";

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

    private final StringBuilder mWhere = new StringBuilder();

    private final List<String> mOrderBy = new ArrayList<>();

    private final List<String> mWhereArgs = new ArrayList<>();

    private String mGroupBy;

    private String mHaving;

    private long mLimit;

    private boolean mHasGroupBayOrHavingOrLimit;

    SQLiteQuery(@NonNull ContentResolver db, @NonNull Uri uri, @NonNull SQLiteTable<T> table) {
        mDb = db;
        mUri = uri;
        mTable = table;
    }

    @NonNull
    public SQLiteQuery<T> groupBy(@NonNull String column) {
        mGroupBy = column;
        mHasGroupBayOrHavingOrLimit = true;
        return this;
    }

    @NonNull
    public SQLiteQuery<T> having(@NonNull String value) {
        mHaving = value;
        mHasGroupBayOrHavingOrLimit = true;
        return this;
    }

    @NonNull
    public SQLiteQuery<T> orderBy(@NonNull String column) {
        return orderBy(column, true);
    }

    @NonNull
    public SQLiteQuery<T> orderBy(@NonNull String column, boolean ascending) {
        mOrderBy.add(column + (ascending ? ASC : DESC));
        return this;
    }

    @NonNull
    public SQLiteQuery<T> limit(long limit) {
        mLimit = limit;
        mHasGroupBayOrHavingOrLimit = true;
        return this;
    }

    @NonNull
    public SQLiteQuery<T> equalTo(@NonNull String column, @NonNull Object value) {
        return appendWhere(column, EQ, value);
    }


    @NonNull
    public SQLiteQuery<T> notEqualTo(@NonNull String column, @NonNull Object value) {
        return appendWhere(column, NOT_EQ, value);
    }

    @NonNull
    public SQLiteQuery<T> lessThan(@NonNull String column, @NonNull Object value) {
        return appendWhere(column, LT, value);
    }

    @NonNull
    public SQLiteQuery<T> lessThanOrEqualTo(@NonNull String column, @NonNull Object value) {
        return appendWhere(column, LT_OR_EQ, value);
    }

    @NonNull
    public SQLiteQuery<T> greaterThan(@NonNull String column, @NonNull Object value) {
        return appendWhere(column, GT, value);
    }

    @NonNull
    public SQLiteQuery<T> greaterThanOrEqualTo(@NonNull String column, @NonNull Object value) {
        return appendWhere(column, GT_OR_EQ, value);
    }

    @NonNull
    public SQLiteQuery<T> between(@NonNull String column, @NonNull Object value1, @NonNull Object value2) {
        return appendWhere(column, BETWEEN, value1, value2);
    }

    @NonNull
    public SQLiteQuery<T> like(@NonNull String column, @NonNull Object value) {
        return appendWhere(column, LIKE, value);
    }

    @NonNull
    public SQLiteQuery<T> inSelect(@NonNull String column, @NonNull String select) {
        return appendWhere(column, " IN(" + select + ")");
    }

    @NonNull
    public SQLiteQuery<T> and() {
        mWhere.append(AND);
        return this;
    }

    @NonNull
    public SQLiteQuery<T> or() {
        mWhere.append(OR);
        return this;
    }

    @NonNull
    public SQLiteResult<T> all() {
        @SuppressLint("Recycle")
        final Cursor cursor = mDb.query(makeQueryUri(), null, where(), bindArgs(), TextUtils.join(COMMA, mOrderBy));
        cursor.setNotificationUri(mDb, mUri);
        if (!cursor.moveToFirst()) {
            Logger.error("%s: %s", mTable.getName(), "empty result set");
        }
        return new SQLiteResult<>(mTable, cursor);
    }

    @Nullable
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

    public int remove() {
        return mDb.delete(makeQueryUri(), where(), bindArgs());
    }

    public int maxInt(@NonNull String column) {
        return applyFunc(INT_FUNC, MAX, column, 0);
    }

    public long maxLong(@NonNull String column) {
        return applyFunc(LONG_FUNC, MAX, column, 0L);
    }

    public double maxDouble(@NonNull String column) {
        return applyFunc(DOUBLE_FUNC, MAX, column, 0.0);
    }

    public int minInt(@NonNull String column) {
        return applyFunc(INT_FUNC, MIN, column, 0);
    }

    public long minLong(@NonNull String column) {
        return applyFunc(LONG_FUNC, MIN, column, 0L);
    }

    public double minDouble(@NonNull String column) {
        return applyFunc(DOUBLE_FUNC, MIN, column, 0.0);
    }

    public int sumInt(@NonNull String column) {
        return applyFunc(INT_FUNC, SUM, column, 0);
    }

    public long sumLong(@NonNull String column) {
        return applyFunc(LONG_FUNC, SUM, column, 0L);
    }

    public double sumDouble(@NonNull String column) {
        return applyFunc(DOUBLE_FUNC, SUM, column, 0.0);
    }

    public long count(@NonNull String column) {
        return applyFunc(LONG_FUNC, COUNT, column, 0L);
    }

    public long countDistinct(@NonNull String column) {
        return applyFunc(LONG_FUNC, COUNT, DISTINCT + column, 0L);
    }

    @NonNull
    public Uri getUri() {
        return mUri;
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

    private SQLiteQuery<T> appendWhere(@NonNull String column, @NonNull String operand, @NonNull Object... values) {
        mWhere.append(column).append(operand);
        for (final Object value : values) {
            if (value instanceof Boolean) {
                mWhereArgs.add(((boolean) value) ? TRUE : FALSE);
            } else {
                mWhereArgs.add(String.valueOf(value));
            }
        }
        return this;
    }

    @Nullable
    private String where() {
        if (mWhere.length() > 0) {
            return mWhere.toString();
        }
        return null;
    }

    @Nullable
    private String[] bindArgs() {
        if (!mWhereArgs.isEmpty()) {
            return mWhereArgs.toArray(new String[mWhereArgs.size()]);
        }
        return null;
    }

    @NonNull
    private <F> F applyFunc(@NonNull SQLiteFunc<F> func, @NonNull String function, @NonNull String column,
                            @NonNull F defaultValue) {
        final Cursor cursor = mDb.query(makeQueryUri(), new String[]{function + "(" + column + ")"},
                where(), bindArgs(), TextUtils.join(COMMA, mOrderBy));
        try {
            if (cursor.moveToFirst()) {
                return func.apply(cursor, column);
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }
        return defaultValue;
    }

}
