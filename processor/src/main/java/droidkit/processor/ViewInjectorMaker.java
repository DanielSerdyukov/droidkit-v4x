package droidkit.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;

import java.io.BufferedWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import droidkit.annotation.InjectView;

/**
 * @author Daniel Serdyukov
 */
class ViewInjectorMaker implements ClassMaker {

    private static final String VIEW_INJECTOR = "$ViewInjector";

    private final Map<VariableElement, Integer> mFields = new HashMap<>();

    private final JavacProcessingEnvironment mEnv;

    private final TypeElement mOriginType;

    private final TypeUtils mTypeUtils;

    public ViewInjectorMaker(ProcessingEnvironment env, Element element) {
        mEnv = (JavacProcessingEnvironment) env;
        mOriginType = (TypeElement) element;
        mTypeUtils = new TypeUtils(mEnv);
    }

    public void emit(VariableElement field) {
        if (!mTypeUtils.isSubtype(field, "android.view.View")) {
            final String message = "Unexpected field type. Expected subtype of android.view.View";
            mEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, field);
            throw new RuntimeException(message);
        }
        mTypeUtils.<JCTree.JCVariableDecl>asTree(field).mods.flags &= ~Flags.PRIVATE;
        mFields.put(field, field.getAnnotation(InjectView.class).value());
    }

    @Override
    public JavaFile make() throws Exception {
        mEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generate " + mOriginType + " view injector");
        final TypeSpec.Builder builder = TypeSpec.classBuilder(mOriginType.getSimpleName() + VIEW_INJECTOR)
                .addOriginatingElement(mOriginType);
        brewInjectMethod1(builder);
        brewInjectMethod2(builder);
        final TypeSpec spec = builder.build();
        final JavaFile javaFile = JavaFile.builder(mOriginType.getEnclosingElement().toString(), spec)
                .addFileComment(AUTO_GENERATED)
                .build();
        final JavaFileObject sourceFile = mEnv.getFiler()
                .createSourceFile(javaFile.packageName + "." + spec.name, mOriginType);
        try (final Writer writer = new BufferedWriter(sourceFile.openWriter())) {
            javaFile.writeTo(writer);
        }
        return javaFile;
    }

    private void brewInjectMethod1(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        final ClassName originType = ClassName.get(mOriginType);
        final ClassName views = ClassName.get("droidkit.view", "Views");
        codeBlock.addStatement("$T delegate = ($T) target", originType, originType);
        for (final Map.Entry<VariableElement, Integer> entry : mFields.entrySet()) {
            codeBlock.addStatement("delegate.$L = $T.findById(root, $L)",
                    entry.getKey().getSimpleName(), views, entry.getValue());
        }
        builder.addMethod(MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("android.view", "View"), "root")
                .addParameter(ClassName.get(Object.class), "target")
                .addCode(codeBlock.build())
                .build());
    }

    private void brewInjectMethod2(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        final ClassName originType = ClassName.get(mOriginType);
        final ClassName views = ClassName.get("droidkit.view", "Views");
        codeBlock.addStatement("$T delegate = ($T) target", originType, originType);
        for (final Map.Entry<VariableElement, Integer> entry : mFields.entrySet()) {
            codeBlock.addStatement("delegate.$L = $T.findById(root, $L)",
                    entry.getKey().getSimpleName(), views, entry.getValue());
        }
        builder.addMethod(MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("android.app", "Activity"), "root")
                .addParameter(ClassName.get(Object.class), "target")
                .addCode(codeBlock.build())
                .build());
    }

}
