package droidkit.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.Types;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;

import java.io.BufferedWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import droidkit.annotation.SQLiteColumn;
import droidkit.annotation.SQLitePk;

/**
 * @author Daniel Serdyukov
 */
class SQLiteTableMaker implements ClassMaker {

    static final String SQLITE_DATABASE = "SQLiteDatabase";

    private static final String ANDROID_DATABASE = "android.database";

    static final String ANDROID_DATABASE_SQLITE = ANDROID_DATABASE + ".sqlite";

    private static final String CURSOR = "Cursor";

    private static final String ANDROID_CONTENT = "android.content";

    private static final String CONTENT_RESOLVER = "ContentResolver";

    private static final String ANDROID_NET = "android.net";

    private static final String URI = "Uri";

    private static final String CONTENT_PROVIDER_OPERATION = "ContentProviderOperation";

    private static final String CONTENT_VALUES = "ContentValues";

    private static final String DROIDKIT_CONTENT = "droidkit.content";

    private static final String CONTENT_VALUES_COMPAT = "ContentValuesCompat";

    private static final String SUFFIX = "$SQLiteTable";

    private final Set<SQLiteColumnSpec> mColumnsSpecs = new LinkedHashSet<>();

    private final JavacProcessingEnvironment mEnv;

    private final TypeElement mOriginType;

    private final String mTableName;

    private final Type mGenericType;

    private final TypeUtils mTypeUtils;

    public SQLiteTableMaker(ProcessingEnvironment env, Element element, String tableName) {
        mEnv = (JavacProcessingEnvironment) env;
        mOriginType = (TypeElement) element;
        mTableName = tableName;
        mGenericType = Types.get(mOriginType.asType());
        mTypeUtils = new TypeUtils(mEnv);
        collectColumnSpecs();
    }

    @Override
    public JavaFile make() throws Exception {
        mEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generate SQLiteTable<"
                + mOriginType + "> implementation");
        final TypeSpec.Builder builder = TypeSpec.classBuilder(mOriginType.getSimpleName() + SUFFIX)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(Types.parameterizedType(ClassName
                        .get("droidkit.sqlite", "SQLiteTable"), mGenericType));
        brewFields(builder);
        brewMethods(builder);
        final TypeSpec spec = builder.build();
        final JavaFile javaFile = JavaFile.builder(mOriginType.getEnclosingElement().toString(), spec)
                .addFileComment(AUTO_GENERATED)
                .build();
        final JavaFileObject sourceFile = mEnv.getFiler()
                .createSourceFile(javaFile.packageName + "." + spec.name, mOriginType);
        try (final Writer writer = new BufferedWriter(sourceFile.openWriter())) {
            javaFile.emit(writer);
        }
        return javaFile;
    }

    public Type getGenericType() {
        return mGenericType;
    }

    private void collectColumnSpecs() {
        final List<? extends Element> elements = mOriginType.getEnclosedElements();
        for (final Element element : elements) {
            if (ElementKind.FIELD == element.getKind()) {
                final SQLitePk pk = element.getAnnotation(SQLitePk.class);
                if (pk != null) {
                    mColumnsSpecs.add(new SQLiteColumnSpec(element, pk.value()));
                    mTypeUtils.<JCTree.JCVariableDecl>asTree(element).mods.flags &= ~Flags.PRIVATE;
                } else {
                    final SQLiteColumn column = element.getAnnotation(SQLiteColumn.class);
                    if (column != null) {
                        mColumnsSpecs.add(new SQLiteColumnSpec(column.value(), element));
                        mTypeUtils.<JCTree.JCVariableDecl>asTree(element).mods.flags &= ~Flags.PRIVATE;
                    }
                }
            }
        }
        for (final SQLiteColumnSpec spec : mColumnsSpecs) {
            if (spec.pk) {
                return;
            }
        }
        throw new RuntimeException(mGenericType + " has no field annotated with @SQLitePk or @SQLiteColumn(\"_id\")");
    }

    private void brewFields(TypeSpec.Builder builder) {
        builder.addField(FieldSpec.builder(
                Types.parameterizedType(ClassName.get(Map.class), ClassName.get(String.class), mGenericType),
                "CACHE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T<>()", ClassName.get(ConcurrentHashMap.class))
                .build());
    }

    private void brewMethods(TypeSpec.Builder builder) {
        brewGetNameMethod(builder);
        brewCreateMethod(builder);
        brewUpgradeMethod(builder);
        brewGetRowMethod(builder);
        brewInstantiateMethod(builder);
        brewInsert1Method(builder);
        brewInsert2Method(builder);
        brewUpdate1Method(builder);
        brewUpdate2Method(builder);
        brewDelete1Method(builder);
        brewDelete2Method(builder);
        brewToContentValuesMethod(builder);
    }

    private void brewGetNameMethod(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("getName")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(String.class)
                .addCode(CodeBlock.builder()
                        .addStatement("return $S", mTableName)
                        .build())
                .build());
    }

    private void brewCreateMethod(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(ANDROID_DATABASE_SQLITE, SQLITE_DATABASE), "db")
                .addCode("db.execSQL($S);\n", "CREATE TABLE IF NOT EXISTS " + mTableName + "(" +
                        StringUtils.join(", ", mColumnsSpecs) + ");")
                .build());
    }

    private void brewUpgradeMethod(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("upgrade")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(ANDROID_DATABASE_SQLITE, SQLITE_DATABASE), "db")
                .addParameter(Integer.TYPE, "oldVersion")
                .addParameter(Integer.TYPE, "newVersion")
                .addCode("db.execSQL($S);\n", "DROP TABLE IF EXISTS " + mTableName + ";")
                .addCode("create(db);\n")
                .build());
    }

    private void brewGetRowMethod(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("getRow")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(Long.TYPE, "rowId")
                .returns(mGenericType)
                .addCode("return CACHE.get(rowId);\n")
                .build());
    }

    private void brewInstantiateMethod(TypeSpec.Builder builder) {
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.addStatement("final $T object = new $T()", mGenericType, mGenericType);
        for (final SQLiteColumnSpec spec : mColumnsSpecs) {
            if (!StringUtils.isEmpty(spec.field)) {
                codeBlock.addStatement("object.$L = " + SQLiteColumnSpec.toCursorType(spec.type),
                        spec.field, spec.name);
            }
        }
        codeBlock.addStatement("return object");
        builder.addMethod(MethodSpec.methodBuilder("instantiate")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(ANDROID_DATABASE, CURSOR), "cursor")
                .returns(mGenericType)
                .addCode(codeBlock.build())
                .build());
    }

    private void brewInsert1Method(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(ANDROID_CONTENT, CONTENT_RESOLVER), "db")
                .addParameter(ClassName.get(ANDROID_NET, URI), "uri")
                .addParameter(mGenericType, "object")
                .addCode("db.insert(uri, toContentValues(object));\n")
                .build());
    }

    private void brewInsert2Method(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("insert")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(Types.parameterizedType(ArrayList.class,
                        ClassName.get(ANDROID_CONTENT, CONTENT_PROVIDER_OPERATION)), "operations")
                .addParameter(ClassName.get(ANDROID_NET, URI), "uri")
                .addParameter(mGenericType, "object")
                .addCode("operations.add(ContentProviderOperation" +
                        ".newInsert(uri)" +
                        ".withValues(toContentValues(object))" +
                        ".build());\n")
                .build());
    }

    private void brewUpdate1Method(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(ANDROID_CONTENT, CONTENT_RESOLVER), "db")
                .addParameter(ClassName.get(ANDROID_NET, URI), "uri")
                .addParameter(mGenericType, "object")
                .addCode("db.update(uri, toContentValues(object), SQLiteProvider.WHERE_ID_EQ," +
                        " new String[]{String.valueOf(object.$L)});\n", findPk().field)
                .build());
    }

    private void brewUpdate2Method(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(Types.parameterizedType(ArrayList.class,
                        ClassName.get(ANDROID_CONTENT, CONTENT_PROVIDER_OPERATION)), "operations")
                .addParameter(ClassName.get(ANDROID_NET, URI), "uri")
                .addParameter(mGenericType, "object")
                .addCode(CodeBlock.builder()
                        .add("operations.add(ContentProviderOperation\n")
                        .indent()
                        .add(".newUpdate(uri)\n")
                        .add(".withValues(toContentValues(object))\n")
                        .add(".withSelection(SQLiteProvider.WHERE_ID_EQ," +
                                " new String[]{String.valueOf(object.$L)})\n", findPk().field)
                        .add(".build());\n")
                        .unindent()
                        .build())
                .build());
    }

    private void brewDelete1Method(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("delete")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(ANDROID_CONTENT, CONTENT_RESOLVER), "db")
                .addParameter(ClassName.get(ANDROID_NET, URI), "uri")
                .addParameter(mGenericType, "object")
                .addCode("db.delete(uri, SQLiteProvider.WHERE_ID_EQ," +
                        " new String[]{String.valueOf(object.$L)});\n", findPk().field)
                .build());
    }

    private void brewDelete2Method(TypeSpec.Builder builder) {
        builder.addMethod(MethodSpec.methodBuilder("delete")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(Types.parameterizedType(ArrayList.class,
                        ClassName.get(ANDROID_CONTENT, CONTENT_PROVIDER_OPERATION)), "operations")
                .addParameter(ClassName.get(ANDROID_NET, URI), "uri")
                .addParameter(mGenericType, "object")
                .addCode(CodeBlock.builder()
                        .add("operations.add(ContentProviderOperation\n")
                        .indent()
                        .add(".newDelete(uri)\n")
                        .add(".withSelection(SQLiteProvider.WHERE_ID_EQ," +
                                " new String[]{String.valueOf(object.$L)})\n", findPk().field)
                        .add(".build());\n")
                        .unindent()
                        .build())
                .build());
    }

    private void brewToContentValuesMethod(TypeSpec.Builder builder) {
        final ClassName cv = ClassName.get(ANDROID_CONTENT, CONTENT_VALUES);
        final ClassName cvc = ClassName.get(DROIDKIT_CONTENT, CONTENT_VALUES_COMPAT);
        final CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.addStatement("final $T values = new $T()", cv, cv);
        for (final SQLiteColumnSpec spec : mColumnsSpecs) {
            if (!StringUtils.isEmpty(spec.field)) {
                if (spec.pk) {
                    codeBlock.beginControlFlow("if(object.$L > 0)", spec.field);
                    codeBlock.addStatement("$T.put(values, $S, object.$L)", cvc, spec.name, spec.field);
                    codeBlock.endControlFlow();
                } else {
                    codeBlock.addStatement("$T.put(values, $S, object.$L)", cvc, spec.name, spec.field);
                }
            }
        }
        codeBlock.addStatement("return values");
        builder.addMethod(MethodSpec.methodBuilder("toContentValues")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(mGenericType, "object")
                .returns(cv)
                .addCode(codeBlock.build())
                .build());
    }

    private SQLiteColumnSpec findPk() {
        for (final SQLiteColumnSpec spec : mColumnsSpecs) {
            if (spec.pk) {
                return spec;
            }
        }
        throw new RuntimeException(mGenericType + " has no field annotated with @SQLitePk or @SQLiteColumn(\"_id\")");
    }

}
