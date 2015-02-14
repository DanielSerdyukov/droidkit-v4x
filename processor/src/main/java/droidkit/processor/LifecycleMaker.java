package droidkit.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Daniel Serdyukov
 */
class LifecycleMaker implements ClassMaker {

    private static final String PROXY = "$Proxy";

    private final JavacProcessingEnvironment mEnv;

    TypeElement mOriginElement;

    ClassName mOriginType;

    TypeName mSuperType;

    ClassName mInjector;

    public LifecycleMaker(ProcessingEnvironment env) {
        mEnv = (JavacProcessingEnvironment) env;
    }

    LifecycleMaker withOriginType(TypeElement originType) {
        mOriginElement = originType;
        mOriginType = ClassName.get(originType);
        mSuperType = TypeName.get(originType.getSuperclass());
        return this;
    }

    LifecycleMaker withInjector(ClassName injector) {
        return this;
    }

    @Override
    public JavaFile make() throws IOException {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(mOriginType.simpleName() + PROXY)
                .addOriginatingElement(mOriginElement)
                .superclass(mSuperType);
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
        //mTypeUtils.extend(mOriginType, spec.name);
        return javaFile;
    }

    void makeLifecycleMethods(TypeSpec.Builder builder) {
        makeOnCreate(builder);
    }

    void makeOnCreate(TypeSpec.Builder builder) {

    }

}
