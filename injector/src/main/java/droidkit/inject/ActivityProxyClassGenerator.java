package droidkit.inject;

import com.squareup.javawriter.JavaWriter;
import com.sun.tools.javac.tree.JCTree;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author Daniel Serdyukov
 */
class ActivityProxyClassGenerator extends ProxyClassGenerator {

    private final boolean mUseRootView;

    ActivityProxyClassGenerator(JavacTools tools, TypeElement classElement) {
        this(tools, classElement, false);
    }

    ActivityProxyClassGenerator(JavacTools tools, TypeElement classElement, boolean useRootView) {
        super(tools, classElement);
        mUseRootView = useRootView;
    }

    @Override
    protected void onEmitImports(JavaWriter writer) throws IOException {
        super.onEmitImports(writer);
        writer.emitImports(
                getClassElement().getSuperclass().toString(),
                "android.os.Bundle"
        );
    }

    @Override
    protected void onEmitMethods(JavaWriter writer) throws IOException {
        super.onEmitMethods(writer);
        final boolean injectView = !getInjectView().isEmpty();
        if (injectView) {
            onEmitInjectView(writer);
        }
        final boolean injectListeners = !getOnClick().isEmpty();
        final Set<Modifier> modifiers = mUseRootView ? EnumSet.of(Modifier.PUBLIC) : EnumSet.of(Modifier.PROTECTED);
        if (injectListeners) {
            emitOnResume(writer, modifiers);
            emitOnPause(writer, modifiers);
            emitOnDestroy(writer, modifiers);
        }
        if (injectView && !mUseRootView) {
            emitInjectViewsMethod(writer);
        }
        if (injectListeners) {
            emitInjectOnClickMethods(writer, mUseRootView);
            emitResumeOnClickMethod(writer);
            emitPauseOnClickMethod(writer);
        }
    }

    protected void onEmitInjectView(JavaWriter writer) throws IOException {
        emitSetContentView(writer);
    }

    private void emitSetContentView(JavaWriter writer) throws IOException {
        final Map<ExecutableElement, int[]> onClick = getOnClick();

        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "setContentView", EnumSet.of(Modifier.PUBLIC), "int", "layoutResId");
        writer.emitStatement("super.setContentView(layoutResId)");
        writer.emitStatement("injectViews()");
        for (final int[] viewIds : onClick.values()) {
            for (final int viewId : viewIds) {
                writer.emitStatement("injectOnClick%d()", viewId);
            }
        }
        writer.endMethod();
        writer.emitEmptyLine();

        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "setContentView", EnumSet.of(Modifier.PUBLIC), "View", "view");
        writer.emitStatement("super.setContentView(view)");
        writer.emitStatement("injectViews()");
        for (final int[] viewIds : onClick.values()) {
            for (final int viewId : viewIds) {
                writer.emitStatement("injectOnClick%d()", viewId);
            }
        }
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitOnResume(JavaWriter writer, Set<Modifier> modifiers) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "onResume", modifiers);
        writer.emitStatement("super.onResume()");
        writer.emitStatement("resumeOnClick()");
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitOnPause(JavaWriter writer, Set<Modifier> modifiers) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "onPause", modifiers);
        writer.emitStatement("pauseOnClick()");
        writer.emitStatement("super.onPause()");
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitOnDestroy(JavaWriter writer, Set<Modifier> modifiers) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "onDestroy", modifiers);
        writer.emitStatement("mOnClick.clear()");
        writer.emitStatement("mOnLongClick.clear()");
        writer.emitStatement("super.onDestroy()");
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitInjectViewsMethod(JavaWriter writer) throws IOException {
        writer.beginMethod("void", "injectViews", EnumSet.of(Modifier.PRIVATE));
        final Map<VariableElement, int[]> views = getInjectView();
        for (final Map.Entry<VariableElement, int[]> entry : views.entrySet()) {
            final int[] ids = entry.getValue();
            if (ids.length > 0) {
                final VariableElement field = entry.getKey();
                if (field.getModifiers().contains(Modifier.PRIVATE)) {
                    final JCTree.JCMethodDecl methodDecl = getTools().makeInjectViewMethod(field, ids[0]);
                    getTools().emitMethod(getClassElement(), methodDecl);
                    writer.emitStatement("mDelegate.%s()", methodDecl.name);
                } else {
                    writer.emitStatement("mDelegate.%s = Views.findById(this, %d)", field.getSimpleName(), ids[0]);
                }
            }
        }
        writer.endMethod().emitEmptyLine();
    }

}
