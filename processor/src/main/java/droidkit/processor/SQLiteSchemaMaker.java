package droidkit.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import java.io.BufferedWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * @author Daniel Serdyukov
 */
class SQLiteSchemaMaker implements ClassMaker {

    private static final String DROIDKIT_SQLITE = "droidkit.sqlite";

    private static final String SQLITE_SCHEMA = "SQLiteSchema";

    private static final String SCHEMA_IMPL = "SQLiteSchemaImpl";

    private final Map<Type, JavaFile> mTables = new HashMap<>();

    private final JavacProcessingEnvironment mEnv;

    public SQLiteSchemaMaker(ProcessingEnvironment env) {
        mEnv = (JavacProcessingEnvironment) env;
    }

    public void put(Type type, JavaFile javaFile) {
        mTables.put(type, javaFile);
    }

    @Override
    public JavaFile make() throws Exception {
        mEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generate SQLiteSchema implementation");
        final TypeSpec.Builder builder = TypeSpec.classBuilder(SCHEMA_IMPL)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get(DROIDKIT_SQLITE, SQLITE_SCHEMA));
        brewMethods(builder);
        final TypeSpec spec = builder.build();
        final JavaFile javaFile = JavaFile.builder(DROIDKIT_SQLITE, spec)
                .addFileComment(AUTO_GENERATED)
                .build();
        final JavaFileObject sourceFile = mEnv.getFiler()
                .createSourceFile(javaFile.packageName + "." + spec.name);
        try (final Writer writer = new BufferedWriter(sourceFile.openWriter())) {
            javaFile.emit(writer);
        }
        return javaFile;
    }

    private void brewMethods(TypeSpec.Builder builder) {
        brewOnCreateMethod(builder);
        brewOnUpgradeMethod(builder);
    }

    private void brewOnCreateMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        for (final Type type : mTables.keySet()) {
            codeBlock.addStatement("SQLite.acquireTable($T.class).create(db)", type);
        }
        builder.addMethod(MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(SQLiteTableMaker.ANDROID_DATABASE_SQLITE,
                        SQLiteTableMaker.SQLITE_DATABASE), "db")
                .addCode(codeBlock.build())
                .build());
    }

    private void brewOnUpgradeMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        for (final Type type : mTables.keySet()) {
            codeBlock.addStatement("SQLite.acquireTable($T.class).upgrade(db, oldVersion, newVersion)", type);
        }
        builder.addMethod(MethodSpec.methodBuilder("onUpgrade")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(SQLiteTableMaker.ANDROID_DATABASE_SQLITE,
                        SQLiteTableMaker.SQLITE_DATABASE), "db")
                .addParameter(Integer.TYPE, "oldVersion")
                .addParameter(Integer.TYPE, "newVersion")
                .addCode(codeBlock.build())
                .build());
    }

}
