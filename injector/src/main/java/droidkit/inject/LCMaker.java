package droidkit.inject;

import com.squareup.javawriter.JavaWriter;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

import droidkit.annotation.OnCreateLoader;
import droidkit.annotation.OnLoadFinished;
import droidkit.annotation.OnResetLoader;

/**
 * @author Daniel Serdyukov
 */
public class LCMaker implements ClassMaker {

    private static final String LC_SUFFIX = "LC";

    private static final String SLC_SUFFIX = "SLC";

    private final JavacTools mTools;

    private final TypeElement mClassElement;

    private final Map<Integer, ExecutableElement> mOnCreateLoader = new HashMap<>();

    private final Map<Integer, ExecutableElement> mOnLoadFinished = new HashMap<>();

    private final Map<Integer, ExecutableElement> mOnResetLoader = new HashMap<>();

    LCMaker(JavacTools tools, Element classElement) {
        mTools = tools;
        mClassElement = (TypeElement) classElement;
    }

    @Override
    public void emit(Element element, TypeElement annotation) {
        if (mTools.isSubtype(annotation, "droidkit.annotation.OnCreateLoader")) {
            mTools.<JCTree.JCMethodDecl>getTree(element).mods.flags &= ~Flags.PRIVATE;
            for (final int loaderId : element.getAnnotation(OnCreateLoader.class).value()) {
                mOnCreateLoader.put(loaderId, (ExecutableElement) element);
            }
        } else if (mTools.isSubtype(annotation, "droidkit.annotation.OnLoadFinished")) {
            mTools.<JCTree.JCMethodDecl>getTree(element).mods.flags &= ~Flags.PRIVATE;
            for (final int loaderId : element.getAnnotation(OnLoadFinished.class).value()) {
                mOnLoadFinished.put(loaderId, (ExecutableElement) element);
            }
        } else if (mTools.isSubtype(annotation, "droidkit.annotation.OnResetLoader")) {
            mTools.<JCTree.JCMethodDecl>getTree(element).mods.flags &= ~Flags.PRIVATE;
            for (final int loaderId : element.getAnnotation(OnResetLoader.class).value()) {
                mOnResetLoader.put(loaderId, (ExecutableElement) element);
            }
        }
    }

    @Override
    public void brewJava() throws IOException {
        for (final Integer loaderId : mOnCreateLoader.keySet()) {
            final boolean isSupport = isSupportLoader(loaderId);
            final String loaderType = getLoaderType(loaderId);
            final String fqcn = getQualifiedName(loaderId, isSupport);
            final String enclosingClassName = mClassElement.getSimpleName().toString();
            final JavaFileObject sourceFile = mTools.createSourceFile(fqcn);
            final JavaWriter writer = new JavaWriter(new BufferedWriter(sourceFile.openWriter()));
            try {
                writer.setIndent("    ");
                writer.emitSingleLineComment("AUTO-GENERATED FILE. DO NOT MODIFY.");
                writer.emitPackage(getPackageName());
                if (isSupport) {
                    writer.emitImports("android.support.v4.app.LoaderManager");
                    writer.emitImports("android.support.v4.content.Loader");
                } else {
                    writer.emitImports("android.app.LoaderManager");
                    writer.emitImports("android.content.Loader");
                }
                writer.emitImports("android.os.Bundle");
                writer.emitEmptyLine();
                writer.emitAnnotation(SuppressWarnings.class, JavaWriter.stringLiteral("unchecked"));
                writer.beginType(fqcn, "class", EnumSet.noneOf(Modifier.class), null,
                        "LoaderManager.LoaderCallbacks<" + loaderType + ">");
                writer.emitEmptyLine();
                writer.emitField(enclosingClassName, M_DELEGATE, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL));
                writer.emitEmptyLine();
                writer.beginConstructor(EnumSet.of(Modifier.PUBLIC), "Object", "delegate");
                writer.emitStatement("%s = (%s) delegate", M_DELEGATE, enclosingClassName);
                writer.endConstructor();
                writer.emitEmptyLine();
                brewOnCreateLoader(writer, loaderId, loaderType);
                brewOnLoadFinished(writer, loaderId, loaderType);
                brewOnResetLoader(writer, loaderId, loaderType);
                writer.endType();
            } finally {
                writer.close();
            }
        }
    }

    @Override
    public void patchClass() {

    }

    private void brewOnCreateLoader(JavaWriter writer, int loaderId, String loaderType) throws IOException {
        writer.beginMethod("Loader<" + loaderType + ">", "onCreateLoader", EnumSet.of(Modifier.PUBLIC),
                "int", "loaderId", "Bundle", "args");
        final ExecutableElement method = mOnCreateLoader.get(loaderId);
        if (method != null) {
            final String methodName = method.getSimpleName().toString();
            final List<? extends VariableElement> parameters = method.getParameters();
            if (parameters.isEmpty()) {
                writer.emitStatement("return %s.%s()", M_DELEGATE, methodName);
            } else if (mTools.isSubtype(parameters.get(0), "android.os.Bundle")) {
                writer.emitStatement("return %s.%s(args)", M_DELEGATE, methodName);
            } else {
                throw new IOException("Invalid method signature '" + method + "'. Signature must be () or (Bundle)");
            }
        } else {
            writer.emitStatement("return null");
        }
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void brewOnLoadFinished(JavaWriter writer, int loaderId, String loaderType) throws IOException {
        writer.beginMethod("void", "onLoadFinished", EnumSet.of(Modifier.PUBLIC),
                "Loader<" + loaderType + ">", "loader", loaderType, "data");
        final ExecutableElement method = mOnLoadFinished.get(loaderId);
        if (method != null) {
            final String methodName = method.getSimpleName().toString();
            final List<? extends VariableElement> parameters = method.getParameters();
            if (parameters.isEmpty()) {
                writer.emitStatement("%s.%s()", M_DELEGATE, methodName);
            } else if (parameters.size() == 1) {
                writer.emitStatement("%s.%s(data)", M_DELEGATE, methodName);
            } else if (parameters.size() == 2) {
                writer.emitStatement("%s.%s(loader, data)", M_DELEGATE, methodName);
            } else {
                throw new IOException("Invalid method signature '" + method +
                        "'. Signature must be (Loader<ResultType>, ResultType) or (ResultType)");
            }
        }
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void brewOnResetLoader(JavaWriter writer, int loaderId, String loaderType) throws IOException {
        writer.beginMethod("void", "onLoaderReset", EnumSet.of(Modifier.PUBLIC),
                "Loader<" + loaderType + ">", "loader");
        final ExecutableElement method = mOnResetLoader.get(loaderId);
        if (method != null) {
            final String methodName = method.getSimpleName().toString();
            final List<? extends VariableElement> parameters = method.getParameters();
            if (parameters.isEmpty()) {
                writer.emitStatement("%s.%s()", M_DELEGATE, methodName);
            } else if (parameters.size() == 1) {
                writer.emitStatement("%s.%s(loader)", M_DELEGATE, methodName);
            } else {
                throw new IOException("Invalid method signature '" + method + "'. Signature must be () or (Loader)");
            }
        }
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private String getQualifiedName(int loaderId, boolean isSupport) {
        return String.format("%s.%s$%s$$%d", getPackageName(), mClassElement.getSimpleName(),
                isSupport ? SLC_SUFFIX : LC_SUFFIX, loaderId);
    }

    private String getPackageName() {
        return mClassElement.getEnclosingElement().toString();
    }

    private boolean isSupportLoader(int loaderId) {
        final ExecutableElement method = mOnCreateLoader.get(loaderId);
        return method != null && mTools.isSubtype(mTools.<TypeElement>asElement(method.getReturnType()),
                "android.support.v4.content.Loader");
    }

    private String getLoaderType(int loaderId) {
        final ExecutableElement method = mOnLoadFinished.get(loaderId);
        if (method != null) {
            final List<? extends VariableElement> parameters = method.getParameters();
            if (parameters.size() == 1) {
                return parameters.get(0).asType().toString();
            } else if (parameters.size() == 2) {
                return parameters.get(1).asType().toString();
            }
        }
        return "Object";
    }

}
