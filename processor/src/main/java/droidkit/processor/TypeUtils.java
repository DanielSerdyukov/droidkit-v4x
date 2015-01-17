package droidkit.processor;

import com.squareup.javapoet.Types;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;

import java.lang.reflect.Type;

import javax.lang.model.element.Element;

/**
 * @author Daniel Serdyukov
 */
final class TypeUtils {

    private final JavacTypes mTypes;

    private final JavacElements mElements;

    TypeUtils(JavacProcessingEnvironment env) {
        mTypes = env.getTypeUtils();
        mElements = env.getElementUtils();
    }

    Type asType(String className) {
        return Types.get(mElements.getTypeElement(className).asType());
    }

    @SuppressWarnings("unchecked")
    <T extends JCTree> T asTree(Element element) {
        return (T) mElements.getTree(element);
    }

}
