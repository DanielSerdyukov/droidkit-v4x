package droidkit.inject;

import com.squareup.javawriter.JavaWriter;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

/**
 * @author Daniel Serdyukov
 */
class ProxyClassGenerator {

    private static final String PROXY_SUFFIX = "$Proxy";

    private final JavacTools mTools;

    private final TypeElement mClassElement;

    private final PackageElement mPackageElement;

    private final Map<VariableElement, int[]> mInjectView = new HashMap<>();

    private final Map<ExecutableElement, int[]> mOnClick = new HashMap<>();

    ProxyClassGenerator(JavacTools tools, TypeElement classElement) {
        mTools = tools;
        mClassElement = classElement;
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

    void injectView(VariableElement field, Annotation annotation) {
        final int[] ids = getAnnotationValue(annotation);
        mInjectView.put(field, ids);
    }

    void injectOnClick(ExecutableElement method, Annotation annotation) {
        if (method.getModifiers().contains(Modifier.PRIVATE)) {
            mTools.<JCTree.JCMethodDecl>getTree(method).mods.flags ^= Flags.PRIVATE;
        }
        final int[] ids = getAnnotationValue(annotation);
        mOnClick.put(method, ids);
    }

    void generate() throws IOException {
        final String className = mClassElement.getSimpleName().toString();
        final String fqcn = String.format("%s.%s%s", mPackageElement, className, PROXY_SUFFIX);
        final JavaFileObject sourceFile = mTools.createSourceFile(fqcn);
        final JavaWriter writer = new JavaWriter(new BufferedWriter(sourceFile.openWriter()));
        try {
            writer.setIndent("    ");
            writer.emitSingleLineComment("AUTO-GENERATED FILE. DO NOT MODIFY.");
            writer.emitPackage(mPackageElement.getQualifiedName().toString());
            onEmitImports(writer);
            writer.emitEmptyLine();
            writer.beginType(fqcn, "class", EnumSet.noneOf(Modifier.class),
                    mTools.asElement(mClassElement.getSuperclass()).getSimpleName().toString());
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
        mTools.extend(mClassElement, String.format("%s%s", mClassElement.getSimpleName(), PROXY_SUFFIX));
    }

    protected JavacTools getTools() {
        return mTools;
    }

    protected TypeElement getClassElement() {
        return mClassElement;
    }

    protected void onEmitImports(JavaWriter writer) throws IOException {
        writer.emitImports(
                "android.support.v4.util.ArrayMap",
                "android.view.View",
                "droidkit.view.Views",
                "java.util.Map"
        );
    }

    protected void onEmitFields(JavaWriter writer) throws IOException {
        writer.emitField("ArrayMap<View, View.OnClickListener>", "mOnClick",
                EnumSet.of(Modifier.PRIVATE, Modifier.FINAL),
                "new ArrayMap<View, View.OnClickListener>()");
        writer.emitEmptyLine();
    }

    protected void onEmitMethods(JavaWriter writer) throws IOException {

    }

    protected Map<VariableElement, int[]> getInjectView() {
        return mInjectView;
    }

    protected Map<ExecutableElement, int[]> getOnClick() {
        return mOnClick;
    }

    protected void emitInjectOnClickMethods(JavaWriter writer, boolean rootViewAttached) throws IOException {
        final Map<ExecutableElement, int[]> onClick = getOnClick();
        for (final Map.Entry<ExecutableElement, int[]> entry : onClick.entrySet()) {
            final ExecutableElement method = entry.getKey();
            final List<? extends VariableElement> parameters = method.getParameters();
            boolean hasViewParameter = false;
            if (!parameters.isEmpty()) {
                if (parameters.size() == 1 && getTools().isSubtype(parameters.get(0), "android.view.View")) {
                    hasViewParameter = true;
                } else {
                    throw new IOException(String.format("Invalid method signature, expected '%1$s.%2$s(View view)'" +
                            " or '%1$s.%2$s()'", getClassElement().getSimpleName(), method.getSimpleName()));
                }
            }
            final int[] viewIds = entry.getValue();
            for (final int viewId : viewIds) {
                if (rootViewAttached) {
                    writer.beginMethod("void", String.format("injectOnClick%d", viewId), EnumSet.of(Modifier.PRIVATE),
                            "View", "rootViewAttached");
                } else {
                    writer.beginMethod("void", String.format("injectOnClick%d", viewId), EnumSet.of(Modifier.PRIVATE));
                }
                writer.emitStatement("final View view = Views.findById(%s, %d)",
                        rootViewAttached ? "rootViewAttached" : "this", viewId);
                writer.beginControlFlow("if(view != null)");
                writer.beginControlFlow("mOnClick.put(view, new View.OnClickListener()");
                writer.emitAnnotation(Override.class);
                writer.beginMethod("void", "onClick", EnumSet.of(Modifier.PUBLIC), "View", "view");
                if (hasViewParameter) {
                    writer.emitStatement("mDelegate.%s(view)", method.getSimpleName());
                } else {
                    writer.emitStatement("mDelegate.%s()", method.getSimpleName());
                }
                writer.endMethod();
                writer.endControlFlow(")");
                writer.endControlFlow();
                writer.endMethod();
                writer.emitEmptyLine();
            }
        }
    }

    protected void emitResumeOnClickMethod(JavaWriter writer) throws IOException {
        writer.beginMethod("void", "resumeOnClick", EnumSet.of(Modifier.PRIVATE));
        writer.beginControlFlow("for(final Map.Entry<View, View.OnClickListener> entry : mOnClick.entrySet())");
        writer.emitStatement("entry.getKey().setOnClickListener(entry.getValue())");
        writer.endControlFlow();
        writer.endMethod();
        writer.emitEmptyLine();
    }

    protected void emitPauseOnClickMethod(JavaWriter writer) throws IOException {
        writer.beginMethod("void", "pauseOnClick", EnumSet.of(Modifier.PRIVATE));
        writer.beginControlFlow("for(final View view : mOnClick.keySet())");
        writer.emitStatement("view.setOnClickListener(null)");
        writer.endControlFlow();
        writer.endMethod();
        writer.emitEmptyLine();
    }

}
