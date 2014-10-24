package droidkit.util;

import android.database.Cursor;
import android.support.annotation.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import droidkit.log.Logger;

/**
 * @author Daniel Serdyukov
 */
public final class IOUtils {

    public static final int EOF = -1;

    public static final int BUFFER_SIZE = 64 * 1024;

    private IOUtils() {
    }

    public static void closeQuietly(@NonNull Closeable... closeable) {
        for (final Closeable c : closeable) {
            try {
                c.close();
            } catch (IOException e) {
                Logger.error(e.getMessage());
            }
        }
    }

    public static void closeQuietly(@NonNull Cursor... cursors) {
        for (final Cursor cursor : cursors) {
            cursor.close();
        }
    }

    public static void copy(@NonNull InputStream in, @NonNull OutputStream out) throws IOException {
        int bytes;
        final byte[] buffer = new byte[BUFFER_SIZE];
        while ((bytes = in.read(buffer)) != EOF) {
            out.write(buffer, 0, bytes);
        }
    }

    public static void copyQuietly(@NonNull InputStream in, @NonNull OutputStream out) {
        try {
            copy(in, out);
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

}
