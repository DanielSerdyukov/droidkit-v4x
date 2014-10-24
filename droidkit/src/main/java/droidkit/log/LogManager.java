package droidkit.log;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import droidkit.util.Dynamic;
import droidkit.util.DynamicException;
import droidkit.util.Objects;

/**
 * @author Daniel Serdyukov
 */
public class LogManager {

    private static volatile LogManager sInstance;

    private final Map<String, Logger> mLoggers = new ConcurrentHashMap<>();

    private final Map<String, Logger.Level> mLevels = new ArrayMap<>();

    LogManager() {
    }

    @NonNull
    public static LogManager from(@Nullable Context context) {
        if (sInstance == null) {
            synchronized (LogManager.class) {
                if (sInstance == null) {
                    sInstance = new LogManager();
                }
            }
        }
        return sInstance;
    }

    @NonNull
    public LogManager setLevel(@NonNull Class<?> clazz, @NonNull Logger.Level level) {
        mLevels.put(clazz.getName(), level);
        return this;
    }

    @Nullable
    Logger getLogger(@NonNull String name, @NonNull Logger.Level level) {
        final Logger.Level logLevel = Objects.requireNonNull(mLevels.get(name), Logger.Level.DEBUG);
        if (level.ordinal() >= logLevel.ordinal()) {
            Logger logger = mLoggers.get(name);
            if (logger == null) {
                try {
                    final Class<?> clazz = Dynamic.forName(name);
                    logger = newLogger(clazz.getSimpleName());
                } catch (DynamicException e) {
                    logger = newLogger(name);
                }
                mLoggers.put(name, logger);
            }
            return logger;
        }
        return null;
    }

    @NonNull
    Logger newLogger(@NonNull String tag) {
        return new Logger(tag);
    }

}
