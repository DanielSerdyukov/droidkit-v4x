package droidkit.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

import droidkit.annotation.InjectView;
import droidkit.annotation.OnActionClick;
import droidkit.annotation.OnClick;

/**
 * @author Daniel Serdyukov
 */
class ActivityMaker extends LifecycleMaker {

    public ActivityMaker(ProcessingEnvironment env, Element element, boolean hasEventBus) {
        super(env, element, hasEventBus);
    }

    @Override
    protected void brewMethods(TypeSpec.Builder builder) {
        brewSetContentView1Method(builder);
        brewSetContentView2Method(builder);
        brewOnPostCreateMethod(builder);
        brewOnOptionsItemSelectedMethod(builder);
        brewOnResumeMethod(builder, Modifier.PROTECTED);
        brewOnPauseMethod(builder, Modifier.PROTECTED);
        brewOnDestroyMethod(builder);
        brewFindAllViewsMethod(builder);
        brewOnActionClickEmitters(builder);
        brewOnClickEmitters(builder);
    }

    private void brewSetContentView1Method(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("setContentView")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(Integer.TYPE, "layoutResId")
                        .build())
                .addStatement("super.setContentView(layoutResId)")
                .addStatement("findAllViews()")
                .build());
    }

    private void brewSetContentView2Method(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("setContentView")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.view", "View"), "view")
                .addStatement("super.setContentView(view)")
                .addStatement("findAllViews()")
                .build());
    }

    private void brewOnPostCreateMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        for (final OnActionClick onClick : mOnActionClick.values()) {
            for (final int viewId : onClick.value()) {
                codeBlock.addStatement("emitOnActionClick$L()", viewId);
            }
        }
        for (final OnClick onClick : mOnClick.values()) {
            for (final int viewId : onClick.value()) {
                codeBlock.addStatement("emitOnClick$L()", viewId);
            }
        }
        builder.addMethod(MethodSpec.methodBuilder("onPostCreate")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(ClassName.get("android.os", "Bundle"), "savedInstanceState")
                .addStatement("super.onPostCreate(savedInstanceState)")
                .addCode(codeBlock.build())
                .build());
    }

    private void brewOnDestroyMethod(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("onDestroy")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .addStatement("$L.clear()", M_ON_CLICK)
                .addStatement("$L.clear()", M_ON_ACTION_CLICK)
                .addStatement("super.onDestroy()")
                .build());
    }

    private void brewFindAllViewsMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        for (final Map.Entry<Element, InjectView> entry : mInjectView.entrySet()) {
            codeBlock.addStatement("$L.$L = $T.findById(this, $L)",
                    M_DELEGATE, entry.getKey().getSimpleName(),
                    ClassName.get("droidkit.view", "Views"),
                    entry.getValue().value());
        }
        builder.addMethod(MethodSpec.methodBuilder("findAllViews")
                .addModifiers(Modifier.PRIVATE)
                .addCode(codeBlock.build())
                .build());
    }

    private void brewOnClickEmitters(TypeSpec.Builder builder) {
        final ClassName droidkitViews = ClassName.get("droidkit.view", "Views");
        for (final Map.Entry<ExecutableElement, OnClick> entry : mOnClick.entrySet()) {
            final ExecutableElement method = entry.getKey();
            final List<? extends VariableElement> parameters = method.getParameters();
            for (final int viewId : entry.getValue().value()) {
                final CodeBlock.Builder codeBlock = CodeBlock.builder();
                codeBlock.add("$L.put($T.findByIdOrThrow(this, $L), new View.OnClickListener() {\n",
                        M_ON_CLICK, droidkitViews, viewId);
                codeBlock.indent();
                codeBlock.add("@Override\n");
                codeBlock.add("public void onClick(View v) {\n");
                codeBlock.indent();
                if (parameters.isEmpty()) {
                    codeBlock.addStatement("$L.$L()", M_DELEGATE, method.getSimpleName());
                } else if (parameters.size() == 1 && mTypeUtils.isSubtype(parameters.get(0), "android.view.View")) {
                    codeBlock.addStatement("$L.$L(v)", M_DELEGATE, method.getSimpleName());
                } else {
                    mEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid method signature", method);
                    throw new RuntimeException("Invalid method signature");
                }
                codeBlock.unindent();
                codeBlock.add("}\n");
                codeBlock.unindent();
                codeBlock.add("});\n");
                builder.addMethod(MethodSpec.methodBuilder("emitOnClick" + viewId)
                        .addModifiers(Modifier.PRIVATE)
                        .addCode(codeBlock.build())
                        .build());
            }
        }
    }

}
