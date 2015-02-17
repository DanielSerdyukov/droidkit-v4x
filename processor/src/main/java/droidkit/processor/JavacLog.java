package droidkit.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * @author Daniel Serdyukov
 */
class JavacLog {

    private static volatile ProcessingEnvironment sEnv;

    public static void init(ProcessingEnvironment env) {
        if (sEnv == null) {
            synchronized (JavacLog.class) {
                if (sEnv == null) {
                    sEnv = env;
                }
            }
        }
    }

    public static void error(String format, Object... args) {
        sEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(format, args));
    }

    public static void error(Element element, String format, Object... args) {
        sEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(format, args), element);
    }

    public static void info(String format, Object... args) {
        sEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(format, args));
    }

}
