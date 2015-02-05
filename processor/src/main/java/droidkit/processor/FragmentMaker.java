package droidkit.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

import droidkit.annotation.OnActionClick;
import droidkit.annotation.OnClick;

/**
 * @author Daniel Serdyukov
 */
class FragmentMaker extends LifecycleMaker {

    private final boolean mIsDialogFragment;

    public FragmentMaker(ProcessingEnvironment env, Element element) {
        super(env, element);
        mIsDialogFragment = mTypeUtils.isSubtype(mOriginType, "android.app.DialogFragment") ||
                mTypeUtils.isSubtype(mOriginType, "android.support.v4.app.DialogFragment");
    }

    @Override
    protected void brewMethods(TypeSpec.Builder builder) {
        brewOnViewCreatedMethod(builder);
        brewOnActivityCreatedMethod(builder);
        brewOnOptionsItemSelectedMethod(builder);
        brewOnResumeMethod(builder, Modifier.PUBLIC);
        brewOnPauseMethod(builder, Modifier.PUBLIC);
        brewOnDestroyViewMethod(builder);
        brewOnActionClickEmitters(builder);
        brewOnClickEmitters(builder);
    }

    private void brewOnViewCreatedMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        if (!StringUtils.isEmpty(mViewInjectorName)) {
            codeBlock.addStatement("$L.inject(view, this)", mViewInjectorName);
        }
        if (!mIsDialogFragment) {
            for (final OnClick onClick : mOnClick.values()) {
                for (final int viewId : onClick.value()) {
                    codeBlock.addStatement("emitOnClick$L(view)", viewId);
                }
            }
        }
        builder.addMethod(MethodSpec.methodBuilder("onViewCreated")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.view", "View"), "view")
                .addParameter(ClassName.get("android.os", "Bundle"), "savedInstanceState")
                .addStatement("super.onViewCreated(view, savedInstanceState)")
                .addCode(codeBlock.build())
                .build());
    }

    private void brewOnDestroyViewMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        builder.addMethod(MethodSpec.methodBuilder("onDestroyView")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$L.clear()", M_ON_CLICK)
                .addStatement("$L.clear()", M_ON_ACTION_CLICK)
                .addStatement("super.onDestroyView()")
                .addCode(codeBlock.build())
                .build());
    }

    private void brewOnActivityCreatedMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        if (mIsDialogFragment) {
            codeBlock.addStatement("final $T dialog = getDialog()", ClassName.get("android.app", "Dialog"));
            for (final OnClick onClick : mOnClick.values()) {
                for (final int viewId : onClick.value()) {
                    codeBlock.addStatement("emitOnClick$L(dialog)", viewId);
                }
            }
        }
        for (final OnActionClick onClick : mOnActionClick.values()) {
            for (final int viewId : onClick.value()) {
                codeBlock.addStatement("emitOnActionClick$L()", viewId);
            }
        }
        builder.addMethod(MethodSpec.methodBuilder("onActivityCreated")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get("android.os", "Bundle"), "savedInstanceState")
                .addStatement("super.onActivityCreated(savedInstanceState)")
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
                codeBlock.add("$L.put($T.findByIdOrThrow(root, $L), new View.OnClickListener() {\n",
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
                        .addParameter(mIsDialogFragment ? ClassName.get("android.app", "Dialog") :
                                ClassName.get("android.view", "View"), "root")
                        .addCode(codeBlock.build())
                        .build());
            }
        }
    }

}
