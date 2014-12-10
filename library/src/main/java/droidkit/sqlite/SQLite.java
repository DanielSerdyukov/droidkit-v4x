package droidkit.sqlite;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import droidkit.util.Dynamic;
import droidkit.util.DynamicException;
import droidkit.util.DynamicMethod;

/**
 * @author Daniel Serdyukov
 */
public class SQLite {

    private static final String PROXY_SUFFIX = "$SQLiteObject";

    private static final Map<Class<?>, Class<?>> PROXY_TYPES = new HashMap<>();

    private static final Map<Class<?>, Method> CREATE_TABLE_METHODS = new HashMap<>();

    private static final Map<Class<?>, Method> UPGRADE_TABLE_METHODS = new HashMap<>();

    private static final Map<Class<?>, Uri> TYPE_URIS = new HashMap<>();

    private static final String SCHEME = "content";

    private final ContentResolver mDb;

    private final AtomicBoolean mInTransaction = new AtomicBoolean();

    private final Map<String, Map<Object, ContentProviderOperation.Builder>> mOperations = new HashMap<>();

    private SQLite(ContentResolver db) {
        mDb = db;
    }

    public static SQLite with(@NonNull Context context) {
        return new SQLite(context.getContentResolver());
    }

    static void registerType(@NonNull String authority, @NonNull Class<?> type) {
        Class<?> proxyType = PROXY_TYPES.get(type);
        if (proxyType == null) {
            try {
                proxyType = Dynamic.forName(type.getName() + PROXY_SUFFIX);
                CREATE_TABLE_METHODS.put(type, DynamicMethod.find(proxyType, "createTable", SQLiteDatabase.class));
                UPGRADE_TABLE_METHODS.put(type, DynamicMethod.find(proxyType, "upgradeTable", SQLiteDatabase.class,
                        int.class, int.class));
                PROXY_TYPES.put(type, proxyType);
                TYPE_URIS.put(type, new Uri.Builder().scheme(SCHEME).authority(authority)
                        .path(DynamicMethod.<String>invokeStatic(proxyType, "table")).build());
            } catch (DynamicException e) {
                throw new SQLiteException(e);
            }
        }
    }

    static void createTables(@NonNull SQLiteDatabase db) {
        for (final Method method : CREATE_TABLE_METHODS.values()) {
            try {
                DynamicMethod.invokeStatic(method, db);
            } catch (DynamicException e) {
                throw new SQLiteException("Unable to create table", e);
            }
        }
    }

    static void upgradeTables(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        for (final Method method : UPGRADE_TABLE_METHODS.values()) {
            try {
                DynamicMethod.invokeStatic(method, db, oldVersion, newVersion);
            } catch (DynamicException e) {
                throw new SQLiteException("Unable to upgrade table", e);
            }
        }
    }

    public void beginTransaction() {
        if (mInTransaction.compareAndSet(false, true)) {
            mOperations.clear();
        }
    }

    public void commitTransaction() {
        if (mInTransaction.compareAndSet(true, false)) {
            final Set<Map.Entry<String, Map<Object, ContentProviderOperation.Builder>>> entries =
                    mOperations.entrySet();
            for (final Map.Entry<String, Map<Object, ContentProviderOperation.Builder>> entry : entries) {
                final Collection<ContentProviderOperation.Builder> builders = entry.getValue().values();
                final ArrayList<ContentProviderOperation> operations = new ArrayList<>(builders.size());
                for (final ContentProviderOperation.Builder builder : builders) {
                    operations.add(builder.build());
                }
                try {
                    mDb.applyBatch(entry.getKey(), operations);
                } catch (RemoteException | OperationApplicationException e) {
                    throw new SQLiteException(e);
                }
            }
            mOperations.clear();
        }
    }

    public boolean inTransaction() {
        return mInTransaction.get();
    }

    @SuppressWarnings("unchecked")
    public <T> T createObject(@NonNull Class<T> type) {
        final Class<T> proxyType = (Class<T>) PROXY_TYPES.get(type);
        if (proxyType == null) {
            throw new SQLiteException("Unknown type " + type);
        }
        final Uri uri = TYPE_URIS.get(type);
        final String lastId = mDb.insert(uri, new ContentValues()).getLastPathSegment();
        try {
            return Dynamic.init(proxyType, this, uri, Long.parseLong(lastId));
        } catch (DynamicException e) {
            throw new SQLiteException(e);
        }
    }

    <T> ContentProviderOperation.Builder update(@NonNull Uri uri, @NonNull T object) {
        final Map<Object, ContentProviderOperation.Builder> operations = getOperations(uri.getAuthority());
        ContentProviderOperation.Builder operation = operations.get(object);
        if (operation == null) {
            operation = ContentProviderOperation.newUpdate(uri);
            operations.put(object, operation);
        }
        return operation;
    }

    void updateImmediately(@NonNull Uri uri, @NonNull ContentValues values, long rowid) {
        mDb.update(uri, values, BaseColumns._ID + "=?", new String[]{String.valueOf(rowid)});
    }

    private Map<Object, ContentProviderOperation.Builder> getOperations(@NonNull String authority) {
        Map<Object, ContentProviderOperation.Builder> operations = mOperations.get(authority);
        if (operations == null) {
            operations = new HashMap<>();
            mOperations.put(authority, operations);
        }
        return operations;
    }

}
