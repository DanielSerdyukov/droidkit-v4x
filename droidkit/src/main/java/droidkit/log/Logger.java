package droidkit.log;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import droidkit.util.Dynamic;
import droidkit.util.IOUtils;

/**
 * @author Daniel Serdyukov
 */
public class Logger {

    private final String mTag;

    private Level mLevel = Level.DEBUG;

    Logger(@NonNull String tag) {
        mTag = tag;
    }

    public static void debug(@NonNull Object format, Object... args) {
        log(Dynamic.getCaller(), Level.DEBUG, message(format, args));
    }

    public static void info(@NonNull Object format, Object... args) {
        log(Dynamic.getCaller(), Level.INFO, message(format, args));
    }

    public static void warn(@NonNull Object format, Object... args) {
        log(Dynamic.getCaller(), Level.INFO, message(format, args));
    }

    public static void error(@NonNull Object format, Object... args) {
        log(Dynamic.getCaller(), Level.ERROR, message(format, args));
    }

    public static void error(@NonNull Throwable e) {
        log(Dynamic.getCaller(), Level.ERROR, message(e));
    }

    public static void wtf(@NonNull Object format, Object... args) {
        log(Dynamic.getCaller(), Level.WTF, message(format, args));
    }

    private static void log(@NonNull StackTraceElement caller, @NonNull Level level, @NonNull String message) {
        final Logger logger = LogManager.from(null).getLogger(caller.getClassName(), level);
        if (logger != null) {
            if (Level.DEBUG == level) {
                logger.logD(caller, message);
            } else if (Level.INFO == level) {
                logger.logI(caller, message);
            } else if (Level.WARN == level) {
                logger.logW(caller, message);
            } else if (Level.ERROR == level) {
                logger.logE(caller, message);
            } else if (Level.WTF == level) {
                logger.logWtf(caller, message);
            }
        }
    }

    @NonNull
    private static String message(@Nullable Object format, Object... args) {
        if (format instanceof String && args.length > 0) {
            return String.format((String) format, args);
        }
        return String.valueOf(format);
    }

    @NonNull
    private static String message(@Nullable Throwable e) {
        if (e != null) {
            final StringWriter trace = new StringWriter();
            final PrintWriter traceWriter = new PrintWriter(new BufferedWriter(trace, 1024), true);
            try {
                e.printStackTrace(traceWriter);
            } finally {
                IOUtils.closeQuietly(traceWriter);
            }
            return trace.toString();
        }
        return "null";
    }

    void setLevel(@NonNull Level level) {
        mLevel = level;
    }

    private void logD(@NonNull StackTraceElement caller, @NonNull String message) {
        Log.d(makeTag(caller), message);
    }

    private void logI(@NonNull StackTraceElement caller, @NonNull String message) {
        Log.i(makeTag(caller), message);
    }

    private void logW(@NonNull StackTraceElement caller, @NonNull String message) {
        Log.w(makeTag(caller), message);
    }

    private void logE(@NonNull StackTraceElement caller, @NonNull String message) {
        Log.e(makeTag(caller), message);
    }

    private void logWtf(@NonNull StackTraceElement caller, @NonNull String message) {
        Log.wtf(makeTag(caller), message);
    }

    private boolean isLoggable(@NonNull Level level) {
        return level.ordinal() >= mLevel.ordinal();
    }

    @NonNull
    private String makeTag(@NonNull StackTraceElement caller) {
        return makeCallerName(caller) + "[" + Thread.currentThread().getName() + "]";
    }

    @NonNull
    private String makeCallerName(@NonNull StackTraceElement caller) {
        final StringBuilder buf = new StringBuilder(256).append(mTag);
        if (caller.isNativeMethod()) {
            buf.append("(Native Method)");
        } else {
            final String fileName = caller.getFileName();
            final int lineNumber = caller.getLineNumber();
            if (!TextUtils.isEmpty(fileName)) {
                buf.append("(").append(fileName);
                if (lineNumber >= 0) {
                    buf.append(':');
                    buf.append(lineNumber);
                }
                buf.append(")");
            } else {
                buf.append("(Unknown Source)");
            }
        }
        return buf.toString();
    }

    public static enum Level {
        DEBUG, INFO, WARN, ERROR, WTF, NONE
    }

}
