package droidkit.concurrent;

import android.support.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Daniel Serdyukov
 */
public interface ExecQueue {

    @NonNull
    <V> Future<V> invoke(@NonNull Callable<V> task);

    @NonNull
    Future<?> invoke(@NonNull Runnable task);

    @NonNull
    <V> Future<V> invoke(@NonNull Callable<V> task, long delay);

    @NonNull
    Future<?> invoke(@NonNull Runnable task, long delay);

    @NonNull
    ExecutorService getExecutor();

}
