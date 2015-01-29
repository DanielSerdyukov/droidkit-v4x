package droidkit.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Daniel Serdyukov
 */
public class Observable<T> {

    private final List<Observer<T>> mObservers;

    private Observable(@NonNull List<Observer<T>> observers) {
        mObservers = observers;
    }

    @NonNull
    public static <T> Observable<T> create() {
        return new Observable<>(new ArrayList<Observer<T>>());
    }

    @NonNull
    public static <T> Observable<T> createThreadSafe() {
        return new Observable<>(new CopyOnWriteArrayList<Observer<T>>());
    }

    public boolean registerObserver(@NonNull Observer<T> observer) {
        return !mObservers.contains(observer) && mObservers.add(observer);
    }

    public boolean unregisterObserver(@NonNull Observer<T> observer) {
        return mObservers.remove(observer);
    }

    public void unregisterAllObservers() {
        mObservers.clear();
    }

    public void notifyChange(@Nullable T data) {
        for (final Observer<T> observer : mObservers) {
            observer.onChange(this, data);
        }
    }

}
