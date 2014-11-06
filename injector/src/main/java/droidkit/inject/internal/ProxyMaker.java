package droidkit.inject.internal;

import com.squareup.javawriter.JavaWriter;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

import droidkit.inject.InjectView;
import droidkit.inject.OnClick;

/**
 * @author Daniel Serdyukov
 */
abstract class ProxyMaker {

    static final String M_DELEGATE = "mDelegate";

    private static final String PROXY_SUFFIX = "$Proxy";

    final Map<VariableElement, int[]> mInjectView = new HashMap<>();

    final Map<ExecutableElement, int[]> mOnClick = new HashMap<>();

    final JavacTools mTools;

    private final Set<String> mImports = new TreeSet<>();

    private final Set<FieldDef> mFields = new HashSet<>();

    private final TypeElement mClassElement;

    ProxyMaker(JavacTools tools, Element classElement) {
        mTools = tools;
        mClassElement = (TypeElement) classElement;
    }

    final void emit(Element element, TypeElement annotation) {
        if (mTools.isSubtype(annotation, InjectView.class)) {
            mTools.<JCTree.JCVariableDecl>getTree(element).mods.flags &= ~Flags.PRIVATE;
            mInjectView.put((VariableElement) element, element.getAnnotation(InjectView.class).value());
        } else if (mTools.isSubtype(annotation, OnClick.class)) {
            mTools.<JCTree.JCMethodDecl>getTree(element).mods.flags &= ~Flags.PRIVATE;
            mOnClick.put((ExecutableElement) element, element.getAnnotation(OnClick.class).value());
        }
    }

    void brewJava() throws IOException {
        final String fqcn = getQualifiedName();
        final String className = mClassElement.getSimpleName().toString();
        final JavaFileObject sourceFile = mTools.createSourceFile(fqcn);
        final JavaWriter writer = new JavaWriter(new BufferedWriter(sourceFile.openWriter()));
        try {
            writer.setIndent("    ");
            writer.emitSingleLineComment("AUTO-GENERATED FILE. DO NOT MODIFY.");
            writer.emitPackage(getPackageName());
            writer.emitImports(getSuperClassName());
            writer.emitImports(mImports);
            writer.emitEmptyLine();
            writer.beginType(fqcn, "class", EnumSet.noneOf(Modifier.class), getSuperClassSimpleName());
            writer.emitEmptyLine();
            writer.emitField(className, M_DELEGATE, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL),
                    "(" + className + ") this");
            writer.emitEmptyLine();
            for (final FieldDef field : mFields) {
                writer.emitField(field.mType, field.mName, field.mModifiers, field.mInitialValue);
                writer.emitEmptyLine();
            }
            brewPublicMethods(writer);
            brewProtectedMethods(writer);
            brewPrivateMethods(writer);
            writer.endType();
        } finally {
            writer.close();
        }
    }

    final void patchClass() {
        mTools.extend(mClassElement, getSimpleName());
    }

    protected final void emitImports(String... imports) {
        Collections.addAll(mImports, imports);
    }

    protected final void emitField(Set<Modifier> modifiers, String type, String name, String initialValue) {
        mFields.add(new FieldDef(type, name, modifiers, initialValue));
    }

    protected void brewPublicMethods(JavaWriter writer) throws IOException {

    }

    protected void brewProtectedMethods(JavaWriter writer) throws IOException {

    }

    protected void brewPrivateMethods(JavaWriter writer) throws IOException {

    }

    private String getQualifiedName() {
        return String.format("%s.%s%s", getPackageName(), mClassElement.getSimpleName(), PROXY_SUFFIX);
    }

    private String getSimpleName() {
        return String.format("%s%s", mClassElement.getSimpleName(), PROXY_SUFFIX);
    }

    private String getPackageName() {
        return mClassElement.getEnclosingElement().toString();
    }

    private String getSuperClassName() {
        final TypeElement superClass = mTools.asElement(mClassElement.getSuperclass());
        return superClass.getQualifiedName().toString();
    }

    private String getSuperClassSimpleName() {
        final TypeElement superClass = mTools.asElement(mClassElement.getSuperclass());
        return superClass.getSimpleName().toString();
    }

    private static class FieldDef {

        final String mType;

        final String mName;

        final Set<Modifier> mModifiers;

        final String mInitialValue;

        private FieldDef(String type, String name, Set<Modifier> modifiers, String initialValue) {
            mType = type;
            mName = name;
            mModifiers = modifiers;
            mInitialValue = initialValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            FieldDef fieldDef = (FieldDef) o;
            return !(mName != null ? !mName.equals(fieldDef.mName) : fieldDef.mName != null);
        }

        @Override
        public int hashCode() {
            return mName != null ? mName.hashCode() : 0;
        }

    }

}
