package droidkit.processor;

import com.squareup.javapoet.*;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import droidkit.annotation.InjectView;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel Serdyukov
 */
class LifecycleMaker implements ClassMaker {

    private static final String LIFECYCLE = "$Lifecycle";

    private static final ClassName LIFECYCLE_CALLBACKS = ClassName.get("droidkit.app", "Lifecycle", "Callbacks");

    private final Map<Element, Integer> mInjectView = new HashMap<>();

    private final JavacProcessingEnvironment mEnv;

    private TypeElement mOriginElement;

    private ClassName mOriginType;

    LifecycleMaker(ProcessingEnvironment env) {
        mEnv = (JavacProcessingEnvironment) env;
    }

    LifecycleMaker withOriginType(TypeElement originType) {
        mOriginElement = originType;
        mOriginType = ClassName.get(originType);
        return this;
    }

    void emit(Element element, InjectView annotation) {
        JavacUtils.<JCTree.JCVariableDecl>asTree(element).mods.flags &= ~Flags.PRIVATE;
        mInjectView.put(element, annotation.value());
    }

    @Override
    public JavaFile make() throws IOException {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(mOriginType.simpleName() + LIFECYCLE)
                .addOriginatingElement(mOriginElement)
                .addSuperinterface(ParameterizedTypeName.get(LIFECYCLE_CALLBACKS, mOriginType));
        makeLifecycleMethods(builder);
        final TypeSpec spec = builder.build();
        final JavaFile javaFile = JavaFile.builder(mOriginType.packageName(), spec)
                .addFileComment(AUTO_GENERATED)
                .build();
        final JavaFileObject sourceFile = mEnv.getFiler().createSourceFile(javaFile.packageName + "."
                + spec.name, mOriginElement);
        try (final Writer writer = new BufferedWriter(sourceFile.openWriter())) {
            javaFile.writeTo(writer);
        }
        return javaFile;
    }

    private void makeLifecycleMethods(TypeSpec.Builder builder) {
        makeOnCreate(builder);
        makeInjectViews(builder);
        makeOnStart(builder);
        makeOnResume(builder);
        makeOnPause(builder);
        makeOnStop(builder);
        makeOnDestroy(builder);
    }

    private void makeOnCreate(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        builder.addMethod(MethodSpec.methodBuilder("onCreate")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mOriginType, "target")
                .addCode(codeBlock.build())
                .build());
    }

    private void makeInjectViews(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        final ClassName views = ClassName.get("droidkit.view", "Views");
        for (final Map.Entry<Element, Integer> entry : mInjectView.entrySet()) {
            codeBlock.addStatement("target.$L = $T.findById(root, $L)", entry.getKey().getSimpleName(),
                    views, entry.getValue());
        }
        builder.addMethod(MethodSpec.methodBuilder("injectViews")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(Object.class), "root")
                .addParameter(mOriginType, "target")
                .addCode(codeBlock.build())
                .build());
    }

    private void makeOnStart(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        builder.addMethod(MethodSpec.methodBuilder("onStart")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mOriginType, "target")
                .addCode(codeBlock.build())
                .build());
    }

    private void makeOnResume(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        builder.addMethod(MethodSpec.methodBuilder("onResume")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mOriginType, "target")
                .addCode(codeBlock.build())
                .build());
    }

    private void makeOnPause(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        builder.addMethod(MethodSpec.methodBuilder("onPause")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mOriginType, "target")
                .addCode(codeBlock.build())
                .build());
    }

    private void makeOnStop(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        builder.addMethod(MethodSpec.methodBuilder("onStop")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mOriginType, "target")
                .addCode(codeBlock.build())
                .build());
    }

    private void makeOnDestroy(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        builder.addMethod(MethodSpec.methodBuilder("onDestroy")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mOriginType, "target")
                .addCode(codeBlock.build())
                .build());
    }

}
