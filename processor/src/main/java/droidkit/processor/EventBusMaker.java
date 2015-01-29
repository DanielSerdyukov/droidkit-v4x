package droidkit.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.Types;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import java.io.BufferedWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * @author Daniel Serdyukov
 */
public class EventBusMaker implements ClassMaker {

    private static final String BUS = "$Bus";

    private final List<ExecutableElement> mMethods = new ArrayList<>();

    private final JavacProcessingEnvironment mEnv;

    private final TypeElement mOriginType;

    public EventBusMaker(ProcessingEnvironment env, Element element) {
        mEnv = (JavacProcessingEnvironment) env;
        mOriginType = (TypeElement) element;
    }

    public void emit(ExecutableElement method) {
        if (method.getParameters().size() != 1) {
            final String message = "Invalid method signature. Expected parameters count = 1";
            mEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, method);
            throw new RuntimeException(message);
        }
        mMethods.add(method);
    }

    @Override
    public JavaFile make() throws Exception {
        mEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generate " + mOriginType + " event bus");
        final TypeSpec.Builder builder = TypeSpec.classBuilder(mOriginType.getSimpleName() + BUS)
                .addOriginatingElement(mOriginType)
                .superclass(ClassName.get("droidkit.app", "EventBus"));
        brewRegisterMethod(builder);
        brewUnregisterMethod(builder);
        final TypeSpec spec = builder.build();
        final JavaFile javaFile = JavaFile.builder(mOriginType.getEnclosingElement().toString(), spec)
                .addFileComment(AUTO_GENERATED)
                .build();
        final JavaFileObject sourceFile = mEnv.getFiler()
                .createSourceFile(javaFile.packageName + "." + spec.name, mOriginType);
        try (final Writer writer = new BufferedWriter(sourceFile.openWriter())) {
            javaFile.emit(writer, "    ");
        }
        return javaFile;
    }

    private void brewRegisterMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        for (final ExecutableElement method : mMethods) {
            final Type eventType = Types.get(method.getParameters().get(0).asType());
            codeBlock.add("register(subscriber, $T.class, new $T() {\n", eventType,
                    ClassName.get("droidkit.app", "EventHandler"));
            codeBlock.indent();
            codeBlock.add("@Override\n");
            codeBlock.add("public void onEvent(Object event) {\n");
            codeBlock.indent();
            codeBlock.addStatement("(($T) subscriber).$L(($T) event)", ClassName.get(mOriginType),
                    method.getSimpleName(), eventType);
            codeBlock.unindent();
            codeBlock.add("}\n");
            codeBlock.unindent();
            codeBlock.add("});\n");
        }
        builder.addMethod(MethodSpec.methodBuilder("register")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get(Object.class), "subscriber", Modifier.FINAL)
                .addCode(codeBlock.build())
                .build());
    }

    private void brewUnregisterMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        builder.addMethod(MethodSpec.methodBuilder("unregister")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get(Object.class), "subscriber")
                .addCode(codeBlock.build())
                .build());
    }

}
