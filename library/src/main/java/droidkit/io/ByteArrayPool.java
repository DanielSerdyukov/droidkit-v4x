package droidkit.io;

import android.support.annotation.NonNull;

import droidkit.util.ObjectPool;

/**
 * @author Daniel Serdyukov
 */
public class ByteArrayPool extends ObjectPool<byte[]> {

    public static ByteArrayPool get() {
        return Holder.INSTANCE;
    }

    @NonNull
    @Override
    protected byte[] newEntry() {
        return new byte[IOUtils.BUFFER_SIZE];
    }

    private static final class Holder {
        public static final ByteArrayPool INSTANCE = new ByteArrayPool();
    }

}
