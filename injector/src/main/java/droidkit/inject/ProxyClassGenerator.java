package droidkit.inject;

import com.squareup.javawriter.JavaWriter;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

/**
 * @author Daniel Serdyukov
 */
class ProxyClassGenerator {

    private static final String PROXY_SUFFIX = "$Proxy";

    private final JavacProcessingEnvironment mJavac;

    private final TreeMaker mTreeMaker;

    private final JavacElements mElements;

    private final Names mNames;

    private final TypeElement mClassElement;

    private final PackageElement mPackageElement;

    private final Types mTypes;

    private final Map<VariableElement, int[]> mInjectViews = new HashMap<>();

    ProxyClassGenerator(ProcessingEnvironment env, TypeElement classElement) {
        mJavac = (JavacProcessingEnvironment) env;
        mTreeMaker = TreeMaker.instance(mJavac.getContext());
        mElements = mJavac.getElementUtils();
        mNames = Names.instance(mJavac.getContext());
        mClassElement = classElement;
        mTypes = env.getTypeUtils();
        mPackageElement = (PackageElement) mClassElement.getEnclosingElement();
    }

    @SuppressWarnings("unchecked")
    static <T> T getAnnotationValue(Annotation annotation) {
        try {
            final Method method = annotation.getClass().getDeclaredMethod("value");
            return (T) method.invoke(annotation);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    void injectView(Element element, Annotation annotation) {
        final VariableElement field = (VariableElement) element;
        final int[] ids = getAnnotationValue(annotation);
        mInjectViews.put(field, ids);
    }

    void generate() throws IOException {
        final String className = mClassElement.getSimpleName().toString();
        final String fqcn = String.format("%s.%s%s", mPackageElement, className, PROXY_SUFFIX);
        final JavaFileObject sourceFile = mJavac.getFiler().createSourceFile(fqcn);
        final JavaWriter writer = new JavaWriter(new BufferedWriter(sourceFile.openWriter()));
        try {
            writer.setIndent("    ");
            writer.emitSingleLineComment("AUTO-GENERATED FILE. DO NOT MODIFY.");
            writer.emitPackage(mPackageElement.getQualifiedName().toString());
            onEmitImports(writer);
            writer.emitEmptyLine();
            writer.beginType(fqcn, "class", EnumSet.noneOf(Modifier.class),
                    mTypes.asElement(mClassElement.getSuperclass()).getSimpleName().toString());
            writer.emitEmptyLine();
            writer.emitField(className, "mDelegate", EnumSet.of(Modifier.PRIVATE, Modifier.FINAL),
                    "(" + className + ") this");
            writer.emitEmptyLine();
            onEmitFields(writer);
            onEmitMethods(writer);
            writer.endType();
        } finally {
            writer.close();
        }
    }

    void proxy() {
        final JCTree.JCClassDecl classTree = (JCTree.JCClassDecl) mElements.getTree(mClassElement);
        classTree.extending = mTreeMaker.Ident(mElements.getName(String
                .format("%s%s", mClassElement.getSimpleName(), PROXY_SUFFIX)));
    }

    protected JCTree.JCMethodDecl makeInjectViewMethod(VariableElement field, int viewId) {
        return mTreeMaker.MethodDef(
                mTreeMaker.Modifiers(0),
                mElements.getName(String.format("injectView%d", viewId)),
                mTreeMaker.TypeIdent(TypeTag.VOID),
                JavacUtils.<JCTree.JCTypeParameter>list(),
                JavacUtils.<JCTree.JCVariableDecl>list(),
                JavacUtils.<JCTree.JCExpression>list(),
                JavacUtils.block(mTreeMaker, mTreeMaker.Exec(mTreeMaker.Assign(
                        mTreeMaker.Ident(mElements.getName(field.getSimpleName().toString())),
                        mTreeMaker.Exec(mTreeMaker.Apply(
                                JavacUtils.<JCTree.JCExpression>list(),
                                JavacUtils.selector(mTreeMaker, mNames, "droidkit.view", "Views", "findById"),
                                JavacUtils.list(mTreeMaker.Ident(mNames._this), mTreeMaker.Literal(viewId))
                        )).getExpression()
                ))),
                null
        );
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    protected void emitMethodToDelegate(JCTree... trees) {
        final JCTree.JCClassDecl classTree = (JCTree.JCClassDecl) mElements.getTree(mClassElement);
        final ListBuffer<JCTree> defs = new ListBuffer<>();
        defs.appendList(classTree.defs);
        defs.appendArray(trees);
        classTree.defs = defs.toList();
    }

    protected void onEmitImports(JavaWriter writer) throws IOException {
        writer.emitImports("android.app.Activity");
    }

    protected void onEmitFields(JavaWriter writer) throws IOException {

    }

    protected void onEmitMethods(JavaWriter writer) throws IOException {

    }

    protected JavacProcessingEnvironment getJavac() {
        return mJavac;
    }

    protected Map<VariableElement, int[]> getInjectViews() {
        return mInjectViews;
    }

}
