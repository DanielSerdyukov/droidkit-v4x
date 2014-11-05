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
class FragmentProxyClassGenerator extends ActivityProxyClassGenerator {

    FragmentProxyClassGenerator(JavacTools tools, TypeElement classElement) {
        super(tools, classElement, true);
    }

    @Override
    protected void onEmitInjectView(JavaWriter writer) throws IOException {
        emitOnViewCreated(writer);
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

}
