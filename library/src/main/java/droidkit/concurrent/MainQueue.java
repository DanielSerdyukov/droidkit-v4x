package droidkit.concurrent;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Daniel Serdyukov
 */
public class MainQueue implements ExecQueue {

    private MainQueue() {
    }

    public static MainQueue get() {
        return Holder.INSTANCE;
    }

    public static Handler getHandler() {
        return HandlerHolder.INSTANCE;
    }

    @NonNull
    @Override
    public <V> Future<V> invoke(@NonNull Callable<V> task) {
        return invoke(task, 0);
    }

    @NonNull
    @Override
    public Future<?> invoke(@NonNull Runnable task) {
        return invoke(task, 0);
    }

    @NonNull
    @Override
    public <V> Future<V> invoke(@NonNull Callable<V> task, long delay) {
        final MainFuture<V> future = new MainFuture<>(getHandler(), task);
        getHandler().postDelayed(future, delay);
        return future;
    }

    @NonNull
    @Override
    public Future<?> invoke(@NonNull Runnable task, long delay) {
        final MainFuture<?> future = new MainFuture<>(getHandler(), task);
        getHandler().postDelayed(future, delay);
        return future;
    }

    @NonNull
    @Override
    public ExecutorService getExecutor() {
        throw new UnsupportedOperationException("MainQueue has no Executor");
    }

    private static final class Holder {
        public static final MainQueue INSTANCE = new MainQueue();
    }

    private static final class HandlerHolder {
        public static final Handler INSTANCE = new Handler(Looper.getMainLooper());
    }

}
