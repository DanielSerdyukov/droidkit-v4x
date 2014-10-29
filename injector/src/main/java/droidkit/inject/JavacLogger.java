package droidkit.inject;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * @author Daniel Serdyukov
 */
final class JavacLogger {

    private JavacLogger() {
    }

    static void error(ProcessingEnvironment env, Object message, Element element) {
        env.getMessager().printMessage(Diagnostic.Kind.ERROR, String.valueOf(message), element);
    }

    static void error(ProcessingEnvironment env, Object message) {
        env.getMessager().printMessage(Diagnostic.Kind.ERROR, String.valueOf(message));
    }

    static void note(ProcessingEnvironment env, Object message) {
        env.getMessager().printMessage(Diagnostic.Kind.NOTE, String.valueOf(message));
    }

}
