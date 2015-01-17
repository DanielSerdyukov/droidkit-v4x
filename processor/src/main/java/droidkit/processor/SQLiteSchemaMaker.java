package droidkit.processor;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;

/**
 * @author Daniel Serdyukov
 */
public class SQLiteSchemaMaker implements ClassMaker {

    private static final String DROIDKIT_SQLITE = "droidkit.sqlite";

    private static final String DROIDKIT_SQLITE_SQLITE_SCHEMA = DROIDKIT_SQLITE + ".SQLiteSchema";

    private static final String SCHEMA_IMPL = "SQLiteSchemaImpl";

    private static final String TABLES = "TABLES";

    private final Map<Type, JavaFile> mTables = new HashMap<>();

    private final JavacProcessingEnvironment mEnv;

    private final TypeUtils mTypeUtils;

    public SQLiteSchemaMaker(ProcessingEnvironment env) {
        mEnv = (JavacProcessingEnvironment) env;
        mTypeUtils = new TypeUtils(mEnv);
    }

    public void put(Type type, JavaFile javaFile) {
        mTables.put(type, javaFile);
    }

    @Override
    public JavaFile make() throws Exception {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(SCHEMA_IMPL)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(mTypeUtils.asType(DROIDKIT_SQLITE_SQLITE_SCHEMA));
        implementMethods(builder);
        final TypeSpec spec = builder.build();
        final JavaFile javaFile = new JavaFile.Builder()
                .packageName(DROIDKIT_SQLITE)
                .fileComment(AUTO_GENERATED)
                .typeSpec(spec)
                .build();
        final JavaFileObject sourceFile = mEnv.getFiler()
                .createSourceFile(javaFile.packageName + "." + spec.name);
        try (final Writer writer = sourceFile.openWriter()) {
            javaFile.emit(writer);
        }
        return javaFile;
    }

    private void implementMethods(TypeSpec.Builder builder) {
        brewOnCreateMethod(builder);
        brewOnUpgradeMethod(builder);
    }

    private void brewOnCreateMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = new CodeBlock.Builder();
        for (final Type type : mTables.keySet()) {
            codeBlock.statement("SQLite.acquireTable($T.class).create(db)", type);
        }
        builder.addMethod(MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(mTypeUtils.asType(SQLiteTableMaker.ANDROID_DATABASE_SQLITE_SQLITE_DATABASE), "db")
                .addCode(codeBlock.build())
                .build());
    }

    private void brewOnUpgradeMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = new CodeBlock.Builder();
        for (final Type type : mTables.keySet()) {
            codeBlock.statement("SQLite.acquireTable($T.class).upgrade(db, oldVersion, newVersion)", type);
        }
        builder.addMethod(MethodSpec.methodBuilder("onUpgrade")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(mTypeUtils.asType(SQLiteTableMaker.ANDROID_DATABASE_SQLITE_SQLITE_DATABASE), "db")
                .addParameter(Integer.TYPE, "oldVersion")
                .addParameter(Integer.TYPE, "newVersion")
                .addCode(codeBlock.build())
                .build());
    }

}
