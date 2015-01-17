package droidkit.sqlite;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import droidkit.util.Dynamic;
import droidkit.util.DynamicException;

/**
 * @author Daniel Serdyukov
 */
public class SQLite {

    private static final String SUFFIX = "$SQLiteTable";

    private static final AtomicReference<String> AUTHORITY_REF = new AtomicReference<>();

    private static final Map<Class<?>, SQLiteTable<?>> TABLES = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Uri> URIS = new ConcurrentHashMap<>();

    private static final int TRANSACTION_CAPACITY = 1024;

    private final ContentResolver mDb;

    private final String mAuthority;

    private ArrayList<ContentProviderOperation> mOperations;

    SQLite(@NonNull ContentResolver db, @NonNull String authority) {
        mDb = db;
        mAuthority = authority;
    }

    public static SQLite with(@NonNull Context context) {
        return new SQLite(context.getContentResolver(), AUTHORITY_REF.get());
    }

    static void attach(@NonNull String authority) {
        AUTHORITY_REF.lazySet(authority);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    static <T> SQLiteTable<T> acquireTable(@NonNull Class<?> type) {
        try {
            SQLiteTable<?> proxy = TABLES.get(type);
            if (proxy == null) {
                proxy = Dynamic.init(type.getName() + SUFFIX);
                TABLES.put(type, proxy);
            }
            return (SQLiteTable<T>) proxy;
        } catch (DynamicException e) {
            throw new SQLiteException(e);
        }
    }

    @NonNull
    @SuppressWarnings("unchecked")
    static Uri acquireUri(@NonNull Class<?> type) {
        Uri uri = URIS.get(type);
        if (uri == null) {
            uri = new Uri.Builder()
                    .scheme(SQLiteProvider.SCHEME)
                    .authority(AUTHORITY_REF.get())
                    .appendPath(acquireTable(type).getName())
                    .build();
            URIS.put(type, uri);
        }
        return uri;
    }

    public void beginTransaction() {
        if (mOperations == null) {
            mOperations = new ArrayList<>(TRANSACTION_CAPACITY);
        }
    }

    public void commitTransaction() {
        if (mOperations != null) {
            try {
                mDb.applyBatch(mAuthority, mOperations);
            } catch (RemoteException | OperationApplicationException e) {
                throw new SQLiteException(e);
            }
            mOperations.clear();
            mOperations = null;
        }
    }

    @NonNull
    public SQLite insert(@NonNull Object object) {
        final Class<?> type = object.getClass();
        if (mOperations != null) {
            acquireTable(type).insert(mOperations, acquireUri(type), object);
        } else {
            acquireTable(type).insert(mDb, acquireUri(type), object);
        }
        return this;
    }

    @NonNull
    public SQLite update(@NonNull Object object) {
        final Class<?> type = object.getClass();
        if (mOperations != null) {
            acquireTable(type).update(mOperations, acquireUri(type), object);
        } else {
            acquireTable(type).update(mDb, acquireUri(type), object);
        }
        return this;
    }

    @NonNull
    public SQLite delete(@NonNull Object object) {
        final Class<?> type = object.getClass();
        if (mOperations != null) {
            acquireTable(type).delete(mOperations, acquireUri(type), object);
        } else {
            acquireTable(type).delete(mDb, acquireUri(type), object);
        }
        return this;
    }

    @NonNull
    public <T> SQLiteQuery<T> where(@NonNull Class<T> type) {
        return new SQLiteQuery<>(mDb, acquireUri(type), SQLite.<T>acquireTable(type));
    }

    @NonNull
    public <T> SQLiteResult<T> all(@NonNull Class<T> type) {
        return where(type).all();
    }

}
