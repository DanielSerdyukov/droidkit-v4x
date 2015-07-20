package droidkit.sqlite;

import android.database.Cursor;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import droidkit.concurrent.AsyncQueue;
import droidkit.io.IOUtils;

/**
 * @author Daniel Serdyukov
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
class SQLiteGuard extends PhantomReference<SQLiteResult<?>> {

    private static final Set<Reference<?>> REFERENCES = new CopyOnWriteArraySet<>();

    private static final BlockingQueue<ReferenceQueue<SQLiteResult<?>>> QUEUE = new LinkedBlockingQueue<>();

    static {
        AsyncQueue.get().invoke(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        final ReferenceQueue<SQLiteResult<?>> referenceQueue = QUEUE.take();
                        final Reference<? extends SQLiteResult<?>> reference = referenceQueue.remove();
                        if (reference != null) {
                            ((SQLiteGuard) reference).finalizeReferent();
                            REFERENCES.remove(reference);
                        } else {
                            QUEUE.put(referenceQueue);
                        }
                    } catch (InterruptedException e) {
                        Log.e("SQLiteGuard", e.getMessage(), e);
                        Thread.interrupted();
                    }
                }
            }
        });
    }

    private final AtomicReference<Cursor> mCursorRef;

    private SQLiteGuard(@NonNull SQLiteResult<?> referent, @NonNull AtomicReference<Cursor> cursorRef) {
        super(referent, createReferenceQueue());
        mCursorRef = cursorRef;
        REFERENCES.add(this);
    }

    static SQLiteGuard guard(@NonNull SQLiteResult<?> referent) {
        return new SQLiteGuard(referent, referent.getCursorReference());
    }

    private static ReferenceQueue<SQLiteResult<?>> createReferenceQueue() {
        final ReferenceQueue<SQLiteResult<?>> referenceQueue = new ReferenceQueue<>();
        QUEUE.offer(referenceQueue);
        return referenceQueue;
    }

    public void finalizeReferent() {
        final Cursor cursor = mCursorRef.get();
        if (cursor != null) {
            IOUtils.closeQuietly(cursor);
            mCursorRef.set(null);
        }
    }

}
