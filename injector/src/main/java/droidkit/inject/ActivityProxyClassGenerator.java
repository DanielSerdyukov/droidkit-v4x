package droidkit.inject;

import com.squareup.javawriter.JavaWriter;
import com.sun.tools.javac.tree.JCTree;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author Daniel Serdyukov
 */
class ActivityProxyClassGenerator extends ProxyClassGenerator {

    ActivityProxyClassGenerator(JavacTools tools, TypeElement classElement) {
        super(tools, classElement);
    }

    @Override
    protected void onEmitImports(JavaWriter writer) throws IOException {
        super.onEmitImports(writer);
        writer.emitImports(
                "android.app.Activity",
                "android.os.Bundle"
        );
    }

    @Override
    protected void onEmitFields(JavaWriter writer) throws IOException {
        super.onEmitFields(writer);

    }

    @Override
    protected void onEmitMethods(JavaWriter writer) throws IOException {
        super.onEmitMethods(writer);
        emitSetContentView(writer);
        emitOnResume(writer);
        emitOnPause(writer);
        emitOnDestroy(writer);
        emitInjectViewsMethod(writer);
        emitInjectOnClickMethods(writer, false);
        emitResumeOnClickMethod(writer);
        emitPauseOnClickMethod(writer);
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

    private void emitOnResume(JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "onResume", EnumSet.of(Modifier.PROTECTED));
        writer.emitStatement("super.onResume()");
        writer.emitStatement("resumeOnClick()");
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitOnPause(JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "onPause", EnumSet.of(Modifier.PROTECTED));
        writer.emitStatement("pauseOnClick()");
        writer.emitStatement("super.onPause()");
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitOnDestroy(JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "onDestroy", EnumSet.of(Modifier.PROTECTED));
        writer.emitStatement("mOnClick.clear()");
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
