package droidkit.processor;

import com.squareup.javapoet.*;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Daniel Serdyukov
 */
class ActivityMaker implements ClassMaker {

    private final JavacProcessingEnvironment mEnv;

    private TypeElement mOriginElement;

    private ClassName mOriginType;

    private TypeName mSuperType;

    private ClassName mLifecycle;

    ActivityMaker(ProcessingEnvironment env) {
        mEnv = (JavacProcessingEnvironment) env;
    }

    ActivityMaker withOriginType(TypeElement originType) {
        mOriginElement = originType;
        mOriginType = ClassName.get(originType);
        mSuperType = TypeName.get(originType.getSuperclass());
        return this;
    }

    ActivityMaker withLifecycle(ClassName lifecycle) {
        mLifecycle = lifecycle;
        return this;
    }

    @Override
    public JavaFile make() throws IOException {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(mOriginType.simpleName() + PROXY)
                .addOriginatingElement(mOriginElement)
                .superclass(mSuperType);
        makeFields(builder);
        makeMethods(builder);
        final TypeSpec spec = builder.build();
        final JavaFile javaFile = JavaFile.builder(mOriginType.packageName(), spec)
                .addFileComment(AUTO_GENERATED)
                .build();
        final JavaFileObject sourceFile = mEnv.getFiler().createSourceFile(javaFile.packageName + "."
                + spec.name, mOriginElement);
        try (final Writer writer = new BufferedWriter(sourceFile.openWriter())) {
            javaFile.writeTo(writer);
        }
        JavacUtils.extend(mOriginElement, spec.name);
        return javaFile;
    }

    private void makeFields(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(mLifecycle, M_LIFECYCLE, Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T()", mLifecycle)
                .build());
    }

    private void makeMethods(TypeSpec.Builder builder) {
        makeSetContentView1(builder);
        makeSetContentView2(builder);
        makeOnCreate(builder);
        makeOnOptionsItemSelected(builder);
        makeOnStart(builder);
        makeOnResume(builder);
        makeOnPause(builder);
        makeOnStop(builder);
        makeOnDestroy(builder);
    }

    private void makeSetContentView1(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("setContentView")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.view", "View"), "view")
                .addStatement("super.setContentView(view)")
                .addStatement("$L.injectViews(view, ($T) this)", M_LIFECYCLE, mOriginType)
                .build());
    }

    private void makeSetContentView2(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("setContentView")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.INT, "layoutResID")
                .addStatement("super.setContentView(layoutResID)")
                .addStatement("$L.injectViews(getWindow(), ($T) this)", M_LIFECYCLE, mOriginType)
                .build());
    }

    private void makeOnCreate(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("onCreate")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ClassName.get("android.os", "Bundle"), "savedInstanceState")
                .addStatement("super.onCreate(savedInstanceState)")
                .addStatement("$L.onCreate(($T) this)", M_LIFECYCLE, mOriginType)
                .build());
    }

    private void makeOnOptionsItemSelected(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("onOptionsItemSelected")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addParameter(LifecycleMaker.MENU_ITEM, "menuItem")
                .addStatement("return $L.performActionClick(menuItem)" +
                        " || super.onOptionsItemSelected(menuItem)", M_LIFECYCLE)
                .build());
    }

    private void makeOnStart(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("onStart")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addStatement("super.onStart()")
                .build());
    }

    private void makeOnResume(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("onResume")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addStatement("super.onResume()")
                .addStatement("$L.onResume(($T) this)", M_LIFECYCLE, mOriginType)
                .build());
    }

    private void makeOnPause(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("onPause")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addStatement("$L.onPause(($T) this)", M_LIFECYCLE, mOriginType)
                .addStatement("super.onPause()")
                .build());
    }

    private void makeOnStop(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("onStop")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addStatement("super.onStop()")
                .build());
    }

    private void makeOnDestroy(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("onDestroy")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addStatement("$L.onDestroy(($T) this)", M_LIFECYCLE, mOriginType)
                .addStatement("super.onDestroy()")
                .build());
    }

}
