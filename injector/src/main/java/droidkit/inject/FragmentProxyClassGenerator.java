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
class FragmentProxyClassGenerator extends ProxyClassGenerator {

    FragmentProxyClassGenerator(JavacTools tools, TypeElement classElement) {
        super(tools, classElement);
    }

    @Override
    protected void onEmitImports(JavaWriter writer) throws IOException {
        super.onEmitImports(writer);
        writer.emitImports(
                "android.app.Fragment",
                "android.os.Bundle"
        );
    }

    @Override
    protected void onEmitMethods(JavaWriter writer) throws IOException {
        super.onEmitMethods(writer);
        emitOnViewCreated(writer);
        emitOnResume(writer);
        emitOnPause(writer);
        emitOnDestroyView(writer);
        emitInjectOnClickMethods(writer, true);
        emitResumeOnClickMethod(writer);
        emitPauseOnClickMethod(writer);
    }

    private void emitOnViewCreated(JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "onViewCreated", EnumSet.of(Modifier.PUBLIC),
                "View", "view", "Bundle", "savedInstanceState");
        writer.emitStatement("super.onViewCreated(view, savedInstanceState)");
        final Map<VariableElement, int[]> views = getInjectView();
        for (final Map.Entry<VariableElement, int[]> entry : views.entrySet()) {
            final int[] ids = entry.getValue();
            if (ids.length > 0) {
                final VariableElement field = entry.getKey();
                if (field.getModifiers().contains(Modifier.PRIVATE)) {
                    final JCTree.JCMethodDecl methodDecl = getTools().makeInjectViewMethod(field, "view", ids[0]);
                    getTools().emitMethod(getClassElement(), methodDecl);
                    writer.emitStatement("mDelegate.%s(view)", methodDecl.name);
                } else {
                    writer.emitStatement("mDelegate.%s = Views.findById(view, %d)", field.getSimpleName(), ids[0]);
                }
            }
        }
        final Map<ExecutableElement, int[]> onClick = getOnClick();
        for (final int[] viewIds : onClick.values()) {
            for (final int viewId : viewIds) {
                writer.emitStatement("injectOnClick%d(view)", viewId);
            }
        }
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitOnResume(JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "onResume", EnumSet.of(Modifier.PUBLIC));
        writer.emitStatement("super.onResume()");
        writer.emitStatement("resumeOnClick()");
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitOnPause(JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "onPause", EnumSet.of(Modifier.PUBLIC));
        writer.emitStatement("pauseOnClick()");
        writer.emitStatement("super.onPause()");
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void emitOnDestroyView(JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("void", "onDestroyView", EnumSet.of(Modifier.PUBLIC));
        writer.emitStatement("mOnClick.clear()");
        writer.emitStatement("super.onDestroyView()");
        writer.endMethod();
        writer.emitEmptyLine();
    }


}
