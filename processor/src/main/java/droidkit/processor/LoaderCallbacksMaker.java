package droidkit.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.Types;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;

import java.io.BufferedWriter;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import droidkit.annotation.OnCreateLoader;
import droidkit.annotation.OnLoadFinished;
import droidkit.annotation.OnResetLoader;

/**
 * @author Daniel Serdyukov
 */
class LoaderCallbacksMaker implements ClassMaker {

    final JavacProcessingEnvironment mEnv;

    final TypeElement mOriginType;

    final TypeUtils mTypeUtils;

    final int mLoaderId;

    ExecutableElement mOnCreateMethod;

    ExecutableElement mOnLoadMethod;

    ExecutableElement mOnResetMethod;

    public LoaderCallbacksMaker(ProcessingEnvironment env, Element element, int loaderId) {
        mEnv = (JavacProcessingEnvironment) env;
        mOriginType = (TypeElement) element;
        mLoaderId = loaderId;
        mTypeUtils = new TypeUtils(mEnv);
        collectLoaderInfo();
    }

    @Override
    public JavaFile make() throws Exception {
        mEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generate LoaderCallbacks @"
                + mLoaderId + " implementation (" + mOriginType + ")");
        final String className = mOriginType.getSimpleName() + getSuffix() + mLoaderId;
        final TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addOriginatingElement(mOriginType)
                .addSuperinterface(getLoaderCallbacksVersion());
        brewFields(builder);
        brewConstructor(builder);
        brewMethods(builder);
        final TypeSpec spec = builder.build();
        final JavaFile javaFile = JavaFile.builder(mOriginType.getEnclosingElement().toString(), spec)
                .addFileComment(AUTO_GENERATED)
                .build();
        final JavaFileObject sourceFile = mEnv.getFiler()
                .createSourceFile(javaFile.packageName + "." + spec.name, mOriginType);
        try (final Writer writer = new BufferedWriter(sourceFile.openWriter())) {
            javaFile.emit(writer);
        }
        return javaFile;
    }

    protected String getSuffix() {
        return "$LC";
    }

    protected ClassName getLoaderVersion() {
        return ClassName.get("android.content", "Loader");
    }

    protected ClassName getLoaderCallbacksVersion() {
        return ClassName.get("android.app", "LoaderManager", "LoaderCallbacks");
    }

    private void collectLoaderInfo() {
        for (final Element element : mOriginType.getEnclosedElements()) {
            if (ElementKind.METHOD == element.getKind()) {
                if (mOnCreateMethod == null) {
                    mOnCreateMethod = processOnCreateMethod((ExecutableElement) element);
                }
                if (mOnLoadMethod == null) {
                    mOnLoadMethod = processOnLoadMethod((ExecutableElement) element);
                }
                if (mOnResetMethod == null) {
                    mOnResetMethod = processOnResetMethod((ExecutableElement) element);
                }
            }
        }
    }

    private ExecutableElement processOnCreateMethod(ExecutableElement element) {
        final OnCreateLoader onCreate = element.getAnnotation(OnCreateLoader.class);
        if (onCreate != null) {
            for (final int loaderId : onCreate.value()) {
                if (mLoaderId == loaderId) {
                    mTypeUtils.<JCTree.JCMethodDecl>asTree(element).mods.flags &= ~Flags.PRIVATE;
                    return element;
                }
            }
        }
        return null;
    }

    private ExecutableElement processOnLoadMethod(ExecutableElement element) {
        final OnLoadFinished onLoad = element.getAnnotation(OnLoadFinished.class);
        if (onLoad != null) {
            for (final int loaderId : onLoad.value()) {
                if (mLoaderId == loaderId) {
                    mTypeUtils.<JCTree.JCMethodDecl>asTree(element).mods.flags &= ~Flags.PRIVATE;
                    return element;
                }
            }
        }
        return null;
    }

    private ExecutableElement processOnResetMethod(ExecutableElement element) {
        final OnResetLoader onReset = element.getAnnotation(OnResetLoader.class);
        if (onReset != null) {
            for (final int loaderId : onReset.value()) {
                if (mLoaderId == loaderId) {
                    mTypeUtils.<JCTree.JCMethodDecl>asTree(element).mods.flags &= ~Flags.PRIVATE;
                    return element;
                }
            }
        }
        return null;
    }

    private void brewFields(TypeSpec.Builder builder) {
        builder.addField(Types.get(mOriginType.asType()), M_DELEGATE, Modifier.PRIVATE, Modifier.FINAL);
    }

    private void brewConstructor(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Types.get(mOriginType.asType()), "delegate")
                .addStatement("$L = delegate", M_DELEGATE)
                .build());
    }

    private void brewMethods(TypeSpec.Builder builder) {
        brewOnCreateLoaderMethod(builder);
        brewOnLoadFinishedMethod(builder);
        brewOnLoaderResetMethod(builder);
    }

    private void brewOnCreateLoaderMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        if (mOnCreateMethod != null) {
            final List<? extends VariableElement> parameters = mOnCreateMethod.getParameters();
            if (parameters.isEmpty()) {
                codeBlock.addStatement("return $L.$L()", M_DELEGATE, mOnCreateMethod.getSimpleName());
            } else if (parameters.size() == 1 && mTypeUtils.isSubtype(parameters.get(0), "android.os.Bundle")) {
                codeBlock.addStatement("return $L.$L(args)", M_DELEGATE, mOnCreateMethod.getSimpleName());
            } else if (parameters.size() == 2
                    && TypeKind.INT == parameters.get(0).asType().getKind()
                    && mTypeUtils.isSubtype(parameters.get(1), "android.os.Bundle")) {
                codeBlock.addStatement("return $L.$L(loaderId, args)", M_DELEGATE, mOnCreateMethod.getSimpleName());
            } else {
                final String message = "Invalid method signature, expected [] or [int], or [int, Bundle] parameters";
                mEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, mOnCreateMethod);
                throw new RuntimeException(message);
            }
        } else {
            codeBlock.addStatement("throw new IllegalArgumentException($S)",
                    "No such method annotated with @OnCreateLoader({" + mLoaderId + "})");
        }
        builder.addMethod(MethodSpec.methodBuilder("onCreateLoader")
                .addAnnotation(Override.class)
                .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                        .addMember("value", "$S", "unchecked")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .returns(getLoaderVersion())
                .addParameter(Integer.TYPE, "loaderId")
                .addParameter(ClassName.get("android.os", "Bundle"), "args")
                .addCode(codeBlock.build())
                .build());
    }

    private void brewOnLoadFinishedMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        if (mOnLoadMethod != null) {
            final List<? extends VariableElement> parameters = mOnLoadMethod.getParameters();
            if (parameters.isEmpty()) {
                codeBlock.addStatement("$L.$L()", M_DELEGATE, mOnLoadMethod.getSimpleName());
            } else if (parameters.size() == 1) {
                codeBlock.addStatement("$L.$L(($L) result)", M_DELEGATE, mOnLoadMethod.getSimpleName(),
                        parameters.get(0).asType());
            } else if (parameters.size() == 2) {
                codeBlock.addStatement("$L.$L(($L) loader, ($L) result)", M_DELEGATE, mOnLoadMethod.getSimpleName(),
                        parameters.get(0).asType(), parameters.get(1).asType());
            } else {
                final String message = "Invalid method signature, expected [] or [R], or [Loader<R>, R] parameters";
                mEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, mOnLoadMethod);
                throw new RuntimeException(message);
            }
        }
        builder.addMethod(MethodSpec.methodBuilder("onLoadFinished")
                .addAnnotation(Override.class)
                .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                        .addMember("value", "$S", "unchecked")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(getLoaderVersion(), "loader")
                .addParameter(ClassName.get(Object.class), "result")
                .addCode(codeBlock.build())
                .build());
    }

    private void brewOnLoaderResetMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        if (mOnResetMethod != null) {
            final List<? extends VariableElement> parameters = mOnResetMethod.getParameters();
            if (parameters.isEmpty()) {
                codeBlock.addStatement("$L.$L()", M_DELEGATE, mOnResetMethod.getSimpleName());
            } else if (parameters.size() == 1) {
                codeBlock.addStatement("$L.$L(($L) loader)", M_DELEGATE, mOnResetMethod.getSimpleName(),
                        parameters.get(0).asType());
            } else {
                final String message = "Invalid method signature, expected [] or [Loader<R>] parameters";
                mEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, mOnResetMethod);
                throw new RuntimeException(message);
            }
        }
        builder.addMethod(MethodSpec.methodBuilder("onLoaderReset")
                .addAnnotation(Override.class)
                .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                        .addMember("value", "$S", "unchecked")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(getLoaderVersion(), "loader")
                .build());
    }

}
