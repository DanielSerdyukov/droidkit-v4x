package droidkit.processor;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * @author Daniel Serdyukov
 */
class JavacUtils {

    private static volatile JavacUtils sInstance;

    private final JavacTypes mJavacTypes;

    private final JavacElements mJavacElements;

    private final TreeMaker mTreeMaker;

    private JavacUtils(JavacProcessingEnvironment env) {
        mJavacTypes = env.getTypeUtils();
        mJavacElements = env.getElementUtils();
        mTreeMaker = TreeMaker.instance(env.getContext());
    }

    static void init(ProcessingEnvironment env) {
        if (sInstance == null) {
            synchronized (JavacLog.class) {
                if (sInstance == null) {
                    sInstance = new JavacUtils((JavacProcessingEnvironment) env);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends JCTree> T asTree(Element element) {
        return (T) sInstance.mJavacElements.getTree(element);
    }

    static void extend(Element element, String superclass) {
        JavacUtils.<JCTree.JCClassDecl>asTree(element).extending = sInstance.mTreeMaker.Ident(
                sInstance.mJavacElements.getName(superclass));
    }

    static boolean isSubtype(Element type, String baseType) {
        return isSubtype(type.asType(), baseType);
    }

    static boolean isSubtype(TypeMirror type, String baseType) {
        return sInstance.mJavacTypes.isSubtype(type, sInstance.mJavacElements.getTypeElement(baseType).asType());
    }

}
