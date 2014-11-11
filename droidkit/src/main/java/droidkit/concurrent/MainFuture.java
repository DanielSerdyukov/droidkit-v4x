package droidkit.concurrent;

import android.os.Handler;
import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Daniel Serdyukov
 */
class MainFuture<V> implements RunnableFuture<V> {

    private static final int NEW = 0;

    private static final int COMPLETING = 1;

    private static final int EXCEPTIONAL = 2;

    private static final int CANCELLED = 3;

    private final Handler mHandler;

    private final Runnable mRunnableTask;

    private final Callable<V> mCallableTask;

    private volatile int mState;

    public MainFuture(@NonNull Handler handler, @NonNull Runnable task) {
        mHandler = handler;
        mRunnableTask = task;
        mCallableTask = null;
        mState = NEW;
    }

    public MainFuture(@NonNull Handler handler, @NonNull Callable<V> task) {
        mHandler = handler;
        mRunnableTask = null;
        mCallableTask = task;
        mState = NEW;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        mHandler.removeCallbacks(this);
        mState = CANCELLED;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return mState >= CANCELLED;
    }

    @Override
    public boolean isDone() {
        return mState != NEW;
    }

    @Override
    public V get() throws ConcurrentException {
        throw new UnsupportedOperationException("MainQueue does not support getting the result");
    }

    @Override
    public V get(long timeout, @NonNull TimeUnit unit) throws ConcurrentException {
        throw new UnsupportedOperationException("MainQueue does not support getting the result");
    }

    @Override
    public void run() {
        if (mRunnableTask != null) {
            mRunnableTask.run();
            mState = COMPLETING;
        } else {
            try {
                mCallableTask.call();
                mState = COMPLETING;
            } catch (Exception e) {
                mState = EXCEPTIONAL;
            }
        }
    }

}
