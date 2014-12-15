package droidkit.sqlite;

import java.io.Closeable;
import java.util.List;

/**
 * @author Daniel Serdyukov
 */
public interface SQLiteResult<T> extends List<T>, Closeable {

    void close();

}
