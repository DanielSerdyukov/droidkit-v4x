package droidkit.processor;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Daniel Serdyukov
 */
final class StringUtils {

    private StringUtils() {
    }

    static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    static String nonEmpty(String string, Object value) {
        return isEmpty(string) ? String.valueOf(value) : string;
    }

    static String join(String glue, Iterable<?> tokens) {
        final StringBuilder sb = new StringBuilder();
        final Iterator<?> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(glue);
            }
        }
        return sb.toString();
    }

    static String join(String glue, Map<String, String> tokens, String tokensGlue) {
        final StringBuilder sb = new StringBuilder();
        final Iterator<Map.Entry<String, String>> iterator = tokens.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, String> entry = iterator.next();
            sb.append(entry.getKey()).append(tokensGlue).append(entry.getValue());
            if (iterator.hasNext()) {
                sb.append(glue);
            }
        }
        return sb.toString();
    }

}
