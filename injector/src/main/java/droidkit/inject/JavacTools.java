package droidkit.inject;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;

import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * @author Daniel Serdyukov
 */
class JavacTools {

    private final JavacProcessingEnvironment mJavac;

    private final Types mTypes;

    private final JavacElements mElements;

    private final TreeMaker mTreeMaker;

    JavacTools(ProcessingEnvironment env) {
        mJavac = (JavacProcessingEnvironment) env;
        mTypes = mJavac.getTypeUtils();
        mElements = mJavac.getElementUtils();
        mTreeMaker = TreeMaker.instance(mJavac.getContext());
    }

    void note(String format, Object... args) {
        mJavac.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(format, args));
    }

    @SuppressWarnings("unchecked")
    <T extends Element> T asElement(TypeMirror type) {
        return (T) mTypes.asElement(type);
    }

    JavaFileObject createSourceFile(String fqcn) throws IOException {
        return mJavac.getFiler().createSourceFile(fqcn);
    }

    boolean isSubtype(Element element, Class<?> type) {
        return isSubtype(element.asType(), type.getName());
    }

    boolean isSubtype(Element element, String type) {
        return isSubtype(element.asType(), type);
    }

    boolean isSubtype(TypeMirror typeMirror, String type) {
        return mTypes.isSubtype(typeMirror, mElements.getTypeElement(type).asType());
    }

    boolean isAssignable(TypeMirror typeMirror, String type) {
        return mTypes.isAssignable(mElements.getTypeElement(type).asType(), typeMirror);
    }

    void extend(Element target, String extend) {
        ((JCTree.JCClassDecl) mElements.getTree(target)).extending = mTreeMaker.Ident(mElements.getName(extend));
    }

    @SuppressWarnings("unchecked")
    <T extends JCTree> T getTree(Element element) {
        return (T) mElements.getTree(element);
    }

}
