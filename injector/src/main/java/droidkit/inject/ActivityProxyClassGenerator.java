package droidkit.inject;

import com.squareup.javawriter.JavaWriter;
import com.sun.tools.javac.tree.JCTree;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author Daniel Serdyukov
 */
class ActivityProxyClassGenerator extends ProxyClassGenerator {

    ActivityProxyClassGenerator(ProcessingEnvironment env, TypeElement classElement) {
        super(env, classElement);
    }

    @Override
    protected void onEmitImports(JavaWriter writer) throws IOException {
        super.onEmitImports(writer);
        writer.emitImports("android.view.View", "droidkit.view.Views");
    }

    @Override
    protected void onEmitMethods(JavaWriter writer) throws IOException {
        super.onEmitMethods(writer);
        emitSetContentView(writer);
        emitInjectViews(writer);
    }

    private void emitSetContentView(JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "setContentView", EnumSet.of(Modifier.PUBLIC), "int", "layoutResId");
        writer.emitStatement("super.setContentView(layoutResId)");
        writer.emitStatement("injectViews()");
        writer.endMethod().emitEmptyLine();

        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "setContentView", EnumSet.of(Modifier.PUBLIC), "View", "view");
        writer.emitStatement("super.setContentView(view)");
        writer.emitStatement("injectViews()");
        writer.endMethod().emitEmptyLine();
    }

    private void emitInjectViews(JavaWriter writer) throws IOException {
        writer.beginMethod("void", "injectViews", EnumSet.of(Modifier.PRIVATE));
        final Map<VariableElement, int[]> views = getInjectViews();
        for (final Map.Entry<VariableElement, int[]> entry : views.entrySet()) {
            final int[] ids = entry.getValue();
            if (ids.length > 0) {
                final VariableElement field = entry.getKey();
                if (field.getModifiers().contains(Modifier.PRIVATE)) {
                    final JCTree.JCMethodDecl methodDecl = makeInjectViewMethod(field, ids[0]);
                    emitMethodToDelegate(methodDecl);
                    writer.emitStatement("mDelegate.%s()", methodDecl.name);
                } else {
                    writer.emitStatement("mDelegate.%s = Views.findById(this, %d)", field.getSimpleName(), ids[0]);
                }
            }
        }
        writer.endMethod().emitEmptyLine();
    }

}
