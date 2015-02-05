package droidkit.processor;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

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

    @SuppressWarnings("unchecked")
    <T extends JCTree> T asTree(Element element) {
        return (T) mJavacElements.getTree(element);
    }

    boolean isSubtype(Element type, String baseType) {
        return isSubtype(type.asType(), baseType);
    }

    boolean isSubtype(TypeMirror type, String baseType) {
        return mJavacTypes.isSubtype(type, mJavacElements.getTypeElement(baseType).asType());
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
