package droidkit.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.Types;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;

import java.io.BufferedWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import droidkit.annotation.InjectView;
import droidkit.annotation.OnActionClick;
import droidkit.annotation.OnClick;

/**
 * @author Daniel Serdyukov
 */
class LifecycleMaker implements ClassMaker {

    static final String M_ON_CLICK = "mOnClick";

    static final String M_ON_ACTION_CLICK = "mOnActionClick";

    private static final String PROXY = "$Proxy";

    final Map<Element, InjectView> mInjectView = new LinkedHashMap<>();

    final Map<ExecutableElement, OnClick> mOnClick = new LinkedHashMap<>();

    final Map<ExecutableElement, OnActionClick> mOnActionClick = new LinkedHashMap<>();

    final JavacProcessingEnvironment mEnv;

    final TypeElement mOriginType;

    final TypeUtils mTypeUtils;

    final boolean mHasEventBus;

    public LifecycleMaker(ProcessingEnvironment env, Element element, boolean hasEventBus) {
        mEnv = (JavacProcessingEnvironment) env;
        mOriginType = (TypeElement) element;
        mHasEventBus = hasEventBus;
        mTypeUtils = new TypeUtils(mEnv);
        collectInjectionInfo();
    }

    private void collectInjectionInfo() {
        final List<? extends Element> elements = mOriginType.getEnclosedElements();
        for (final Element element : elements) {
            if (ElementKind.FIELD == element.getKind()) {
                processInjectView(element);
            } else if (ElementKind.METHOD == element.getKind()) {
                processOnClick(element);
                processOnActionClick(element);
            }
        }
    }

    private void processInjectView(Element element) {
        final InjectView annotation = element.getAnnotation(InjectView.class);
        if (annotation != null) {
            mInjectView.put(element, annotation);
            mTypeUtils.<JCTree.JCVariableDecl>asTree(element).mods.flags &= ~Flags.PRIVATE;
        }
    }

    private void processOnClick(Element element) {
        final OnClick annotation = element.getAnnotation(OnClick.class);
        if (annotation != null) {
            mOnClick.put((ExecutableElement) element, annotation);
            mTypeUtils.<JCTree.JCMethodDecl>asTree(element).mods.flags &= ~Flags.PRIVATE;
        }
    }

    private void processOnActionClick(Element element) {
        final OnActionClick annotation = element.getAnnotation(OnActionClick.class);
        if (annotation != null) {
            mOnActionClick.put((ExecutableElement) element, annotation);
            mTypeUtils.<JCTree.JCMethodDecl>asTree(element).mods.flags &= ~Flags.PRIVATE;
        }
    }

    @Override
    public JavaFile make() throws Exception {
        mEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generate " + mOriginType + " proxy");
        final TypeSpec.Builder builder = TypeSpec.classBuilder(mOriginType.getSimpleName() + PROXY)
                .addModifiers(Modifier.PUBLIC)
                .addOriginatingElement(mOriginType)
                .superclass(Types.get(mOriginType.getSuperclass()));
        brewFields(builder);
        brewMethods(builder);
        final TypeSpec spec = builder.build();
        final JavaFile javaFile = JavaFile.builder(mOriginType.getEnclosingElement().toString(), spec)
                .addFileComment(AUTO_GENERATED)
                .build();
        final JavaFileObject sourceFile = mEnv.getFiler()
                .createSourceFile(javaFile.packageName + "." + spec.name, mOriginType);

        try (final Writer writer = new BufferedWriter(sourceFile.openWriter())) {
            javaFile.emit(writer, "    ");
        }
        mTypeUtils.extend(mOriginType, spec.name);
        return javaFile;
    }

    protected void brewFields(TypeSpec.Builder builder) {
        brewDelegateField(builder);
        brewOnClickField(builder);
        brewOnActionClickField(builder);
    }

    protected void brewDelegateField(TypeSpec.Builder builder) {
        final Type origin = Types.get(mOriginType.asType());
        builder.addField(FieldSpec.builder(origin, M_DELEGATE, Modifier.PRIVATE, Modifier.FINAL)
                .initializer("($T) this", origin)
                .build());
    }

    protected void brewOnClickField(TypeSpec.Builder builder) {
        final ClassName view = ClassName.get("android.view", "View");
        final ClassName viewOnClickListener = ClassName.get("android.view", "View", "OnClickListener");
        builder.addField(FieldSpec
                .builder(Types.parameterizedType(ClassName.get("java.util", "Map"),
                        view, viewOnClickListener), M_ON_CLICK, Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T<>()", ClassName.get("java.util", "HashMap"))
                .build());
    }

    protected void brewOnActionClickField(TypeSpec.Builder builder) {
        final ClassName sparseArray = ClassName.get("android.util", "SparseArray");
        final ClassName onMenuItemClickListener = ClassName.get("android.view", "MenuItem", "OnMenuItemClickListener");
        builder.addField(FieldSpec
                .builder(Types.parameterizedType(sparseArray, onMenuItemClickListener),
                        M_ON_ACTION_CLICK, Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T<>()", sparseArray)
                .build());
    }

    protected void brewMethods(TypeSpec.Builder builder) {

    }

    protected void brewOnOptionsItemSelectedMethod(TypeSpec.Builder builder) {
        final ClassName menuItem = ClassName.get("android.view", "MenuItem");
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.addStatement("final MenuItem.OnMenuItemClickListener listener = $L.get(item.getItemId())",
                M_ON_ACTION_CLICK);
        codeBlock.beginControlFlow("if(listener != null)");
        codeBlock.addStatement("return listener.onMenuItemClick(item)");
        codeBlock.endControlFlow();
        codeBlock.addStatement("return super.onOptionsItemSelected(item)");
        builder.addMethod(MethodSpec.methodBuilder("onOptionsItemSelected")
                .addAnnotation(Override.class)
                .returns(Boolean.TYPE)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(menuItem, "item")
                .addCode(codeBlock.build())
                .build());
    }

    protected void brewOnResumeMethod(TypeSpec.Builder builder, Modifier modifier) {
        final ClassName mapEntry = ClassName.get("java.util", "Map", "Entry");
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.addStatement("super.onResume()");
        if (mHasEventBus) {
            codeBlock.addStatement("$T$$Bus.register(this)", ClassName.get(mOriginType));
        }
        codeBlock.beginControlFlow("for(final $T<View, View.OnClickListener> entry : $L.entrySet())",
                mapEntry, M_ON_CLICK);
        codeBlock.addStatement("entry.getKey().setOnClickListener(entry.getValue())");
        codeBlock.endControlFlow();
        builder.addMethod(MethodSpec.methodBuilder("onResume")
                .addAnnotation(Override.class)
                .addModifiers(modifier)
                .addCode(codeBlock.build())
                .build());
    }

    protected void brewOnPauseMethod(TypeSpec.Builder builder, Modifier modifier) {
        final ClassName mapEntry = ClassName.get("java.util", "Map", "Entry");
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.beginControlFlow("for(final $T<View, View.OnClickListener> entry : $L.entrySet())",
                mapEntry, M_ON_CLICK);
        codeBlock.addStatement("entry.getKey().setOnClickListener(null)");
        codeBlock.endControlFlow();
        if (mHasEventBus) {
            codeBlock.addStatement("$T$$Bus.unregister(this)", ClassName.get(mOriginType));
        }
        codeBlock.addStatement("super.onPause()");
        builder.addMethod(MethodSpec.methodBuilder("onPause")
                .addAnnotation(Override.class)
                .addModifiers(modifier)
                .addCode(codeBlock.build())
                .build());
    }

    protected void brewOnActionClickEmitters(TypeSpec.Builder builder) {
        for (final Map.Entry<ExecutableElement, OnActionClick> entry : mOnActionClick.entrySet()) {
            final ExecutableElement method = entry.getKey();
            final TypeMirror returnType = method.getReturnType();
            final boolean isVoid = TypeKind.VOID == returnType.getKind();
            if (TypeKind.BOOLEAN != returnType.getKind() && !isVoid) {
                mEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid method signature", method);
                throw new RuntimeException("Invalid method signature");
            }
            final List<? extends VariableElement> parameters = method.getParameters();
            for (final int viewId : entry.getValue().value()) {
                final CodeBlock.Builder codeBlock = CodeBlock.builder();
                codeBlock.add("$L.put($L, new MenuItem.OnMenuItemClickListener() {\n", M_ON_ACTION_CLICK, viewId);
                codeBlock.indent();
                codeBlock.add("@Override\n");
                codeBlock.add("public boolean onMenuItemClick(MenuItem menuItem) {\n");
                codeBlock.indent();
                if (parameters.isEmpty()) {
                    if (isVoid) {
                        codeBlock.addStatement("$L.$L()", M_DELEGATE, method.getSimpleName());
                        codeBlock.addStatement("return true");
                    } else {
                        codeBlock.addStatement("return $L.$L()", M_DELEGATE, method.getSimpleName());
                    }
                } else if (parameters.size() == 1 && mTypeUtils.isSubtype(parameters.get(0),
                        "android.view.MenuItem")) {
                    if (isVoid) {
                        codeBlock.addStatement("$L.$L(menuItem)", M_DELEGATE, method.getSimpleName());
                        codeBlock.addStatement("return true");
                    } else {
                        codeBlock.addStatement("return $L.$L(menuItem)", M_DELEGATE, method.getSimpleName());
                    }
                } else {
                    mEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid method signature", method);
                    throw new RuntimeException("Invalid method signature");
                }
                codeBlock.unindent();
                codeBlock.add("}\n");
                codeBlock.unindent();
                codeBlock.add("});\n");
                builder.addMethod(MethodSpec.methodBuilder("emitOnActionClick" + viewId)
                        .addModifiers(Modifier.PRIVATE)
                        .addCode(codeBlock.build())
                        .build());
            }
        }
    }

}
