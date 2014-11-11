package droidkit.concurrent;

import android.support.annotation.NonNull;

import java.util.concurrent.ExecutorService;

/**
 * @author Daniel Serdyukov
 */
public class SerialQueue extends AsyncQueue {

    public SerialQueue() {
        super(1, 1);
    }

    public static SerialQueue get() {
        return Holder.INSTANCE;
    }

    @NonNull
    @Override
    public ExecutorService getExecutor() {
        return getScheduledExecutor();
    }

    private static final class Holder {
        public static final SerialQueue INSTANCE = new SerialQueue();
    }

}
