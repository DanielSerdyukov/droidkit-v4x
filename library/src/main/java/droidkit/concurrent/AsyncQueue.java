package droidkit.concurrent;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Daniel Serdyukov
 */
public class AsyncQueue implements ExecQueue {

    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

    private static final ThreadFactory THREAD_FACTORY = new NamedThreadFactory("async #");

    private final ScheduledExecutorService mAsyncExecutor;

    public AsyncQueue() {
        this(CORE_SIZE + 1);
    }

    protected AsyncQueue(int corePoolSize) {
        mAsyncExecutor = Executors.newScheduledThreadPool(corePoolSize, THREAD_FACTORY);
    }

    public static AsyncQueue get() {
        return Holder.INSTANCE;
    }

    @NonNull
    @Override
    public <V> Future<V> invoke(@NonNull Callable<V> task) {
        return mAsyncExecutor.submit(task);
    }

    @NonNull
    @Override
    public Future<?> invoke(@NonNull Runnable task) {
        return mAsyncExecutor.submit(task, null);
    }

    @NonNull
    @Override
    public <V> Future<V> invoke(@NonNull Callable<V> task, long delay) {
        return mAsyncExecutor.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    @NonNull
    @Override
    public Future<?> invoke(@NonNull Runnable task, long delay) {
        return mAsyncExecutor.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    @NonNull
    @Override
    public ExecutorService getExecutor() {
        return mAsyncExecutor;
    }

    private static final class Holder {
        public static final AsyncQueue INSTANCE = new AsyncQueue();
    }

}
