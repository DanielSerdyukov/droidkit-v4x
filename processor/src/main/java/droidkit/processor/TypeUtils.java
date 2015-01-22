package droidkit.processor;

import com.squareup.javapoet.Types;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;

import java.lang.reflect.Type;

import javax.lang.model.element.Element;

/**
 * @author Daniel Serdyukov
 */
final class TypeUtils {

    private final JavacTypes mJavacTypes;

    private final JavacElements mJavacElements;

    private final TreeMaker mTreeMaker;

    TypeUtils(JavacProcessingEnvironment env) {
        mJavacTypes = env.getTypeUtils();
        mJavacElements = env.getElementUtils();
        mTreeMaker = TreeMaker.instance(env.getContext());
    }

    Type asType(String className) {
        return Types.get(mJavacElements.getTypeElement(className).asType());
    }

    @SuppressWarnings("unchecked")
    <T extends JCTree> T asTree(Element element) {
        return (T) mJavacElements.getTree(element);
    }

    boolean isSubtype(Element element, String className) {
        return mJavacTypes.isSubtype(element.asType(), mJavacElements.getTypeElement(className).asType());
    }

    void extend(Element target, String superclass) {
        this.<JCTree.JCClassDecl>asTree(target).extending = mTreeMaker.Ident(
                mJavacElements.getName(superclass));
    }

    void extend(Element target, String packageName, String className) {
        this.<JCTree.JCClassDecl>asTree(target).extending = mTreeMaker.Ident(
                mJavacElements.getName(className));
    }

}
