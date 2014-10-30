package droidkit.inject;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * @author Daniel Serdyukov
 */
final class JavacTools {

    private final JavacProcessingEnvironment mJavac;

    private final TreeMaker mTreeMaker;

    private final Names mNames;

    private final JavacElements mElements;

    private final Types mTypes;

    public JavacTools(ProcessingEnvironment javac) {
        mJavac = (JavacProcessingEnvironment) javac;
        mTreeMaker = TreeMaker.instance(mJavac.getContext());
        mNames = Names.instance(mJavac.getContext());
        mElements = mJavac.getElementUtils();
        mTypes = javac.getTypeUtils();
    }

    @SuppressWarnings("unchecked")
    static <T> List<T> list(T... items) {
        return List.from(items);
    }

    static <T> List<T> list(Iterable<T> items) {
        return List.from(items);
    }

    static JCTree.JCBlock block(TreeMaker maker, JCTree.JCStatement... statements) {
        return maker.Block(0, list(statements));
    }

    static JCTree.JCBlock block(TreeMaker maker, Iterable<JCTree.JCStatement> statements) {
        return maker.Block(0, list(statements));
    }

    static JCTree.JCExpression selector(TreeMaker maker, Names names, String... selectors) {
        final Iterator<String> iterator = Arrays.asList(selectors).iterator();
        if (iterator.hasNext()) {
            JCTree.JCExpression selector = maker.Ident(names.fromString(iterator.next()));
            while (iterator.hasNext()) {
                selector = maker.Select(selector, names.fromString(iterator.next()));
            }
            return selector;
        }
        throw new IllegalArgumentException("Empty selector");
    }

    void error(Element element, Object format, Object... args) {
        if (format instanceof String) {
            mJavac.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format((String) format, args), element);
        } else {
            mJavac.getMessager().printMessage(Diagnostic.Kind.ERROR, String.valueOf(format), element);
        }
    }

    void error(Object format, Object... args) {
        if (format instanceof String) {
            mJavac.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format((String) format, args));
        } else {
            mJavac.getMessager().printMessage(Diagnostic.Kind.ERROR, String.valueOf(format));
        }
    }

    void note(Object format, Object... args) {
        if (format instanceof String) {
            mJavac.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format((String) format, args));
        } else {
            mJavac.getMessager().printMessage(Diagnostic.Kind.NOTE, String.valueOf(format));
        }
    }

    JavaFileObject createSourceFile(String fqcn) throws IOException {
        return mJavac.getFiler().createSourceFile(fqcn);
    }

    boolean isSameType(Element element, Class<?> type) {
        return isSameType(element, type.getName());
    }

    boolean isSameType(Element element, String type) {
        return mTypes.isSameType(element.asType(), mElements.getTypeElement(type).asType());
    }

    boolean isSubtype(TypeMirror actual, TypeMirror expected) {
        return mTypes.isSubtype(actual, expected);
    }

    boolean isSubtype(Element actual, String expected) {
        return mTypes.isSubtype(actual.asType(), mElements.getTypeElement(expected).asType());
    }

    @SuppressWarnings("unchecked")
    <T extends JCTree> T getTree(Element element) {
        return (T) mElements.getTree(element);
    }

    @SuppressWarnings("unchecked")
    <T extends Element> T asElement(TypeMirror type) {
        return (T) mTypes.asElement(type);
    }

    JCTree.JCMethodDecl makeInjectViewMethod(VariableElement field, int viewId) {
        return makeInjectViewMethod(field, mTreeMaker.Ident(mNames._this), viewId);
    }

    JCTree.JCMethodDecl makeInjectViewMethod(VariableElement field, String root, int viewId) {
        return makeInjectViewMethod(field, mTreeMaker.Ident(mElements.getName(root)), viewId,
                mTreeMaker.VarDef(mTreeMaker.Modifiers(Flags.PARAMETER), mElements.getName(root),
                        mTreeMaker.Ident(mElements.getName("View")), null));
    }

    void emitMethod(Element target, JCTree method) {
        final JCTree.JCClassDecl classTree = (JCTree.JCClassDecl) mElements.getTree(target);
        classTree.defs = classTree.defs.append(method);
    }

    void extend(Element target, String extend) {
        ((JCTree.JCClassDecl) mElements.getTree(target)).extending = mTreeMaker.Ident(mElements.getName(extend));
    }

    private JCTree.JCMethodDecl makeInjectViewMethod(VariableElement field, JCTree.JCIdent root, int viewId,
                                                     JCTree.JCVariableDecl... params) {
        return mTreeMaker.MethodDef(
                mTreeMaker.Modifiers(0),
                mElements.getName(String.format("injectView%d", viewId)),
                mTreeMaker.TypeIdent(TypeTag.VOID),
                JavacTools.<JCTree.JCTypeParameter>list(),
                JavacTools.list(params),
                JavacTools.<JCTree.JCExpression>list(),
                JavacTools.block(mTreeMaker, mTreeMaker.Exec(mTreeMaker.Assign(
                        mTreeMaker.Ident(mElements.getName(field.getSimpleName().toString())),
                        mTreeMaker.Exec(mTreeMaker.Apply(
                                JavacTools.<JCTree.JCExpression>list(),
                                JavacTools.selector(mTreeMaker, mNames, "droidkit.view", "Views", "findById"),
                                JavacTools.list(root, mTreeMaker.Literal(viewId))
                        )).getExpression()
                ))),
                null
        );
    }

}
