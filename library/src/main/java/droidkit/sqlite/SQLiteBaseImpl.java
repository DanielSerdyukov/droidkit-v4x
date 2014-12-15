package droidkit.sqlite;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * @author Daniel Serdyukov
 */
class SQLiteBaseImpl extends SQLite {

    private static final int TRANSACTION_CAPACITY = 1024;

    private final ContentResolver mDb;

    private final String mAuthority;

    private ArrayList<ContentProviderOperation> mOperations;

    SQLiteBaseImpl(@NonNull ContentResolver db, @NonNull String authority) {
        mDb = db;
        mAuthority = authority;
    }

    @Override
    public void beginTransaction() {
        if (mOperations == null) {
            mOperations = new ArrayList<>(TRANSACTION_CAPACITY);
        }
    }

    @Override
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
    @Override
    public SQLite insert(@NonNull Object object) {
        final Class<?> type = object.getClass();
        if (mOperations != null) {
            getTable(type).insert(mOperations, getUri(type), object);
        } else {
            getTable(type).insert(mDb, getUri(type), object);
        }
        return this;
    }

    @NonNull
    @Override
    public SQLite update(@NonNull Object object) {
        final Class<?> type = object.getClass();
        if (mOperations != null) {
            getTable(type).update(mOperations, getUri(type), object);
        } else {
            getTable(type).update(mDb, getUri(type), object);
        }
        return this;
    }

    @NonNull
    @Override
    public SQLite delete(@NonNull Object object) {
        final Class<?> type = object.getClass();
        if (mOperations != null) {
            getTable(type).delete(mOperations, getUri(type), object);
        } else {
            getTable(type).delete(mDb, getUri(type), object);
        }
        return this;
    }

    @NonNull
    @Override
    public <T> SQLiteQuery<T> where(@NonNull Class<T> type) {
        return new SQLiteBaseQuery<>(mDb, getUri(type), SQLite.<T>getTable(type));
    }

}
