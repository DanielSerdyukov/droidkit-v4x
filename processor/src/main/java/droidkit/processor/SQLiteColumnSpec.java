package droidkit.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;

/**
 * @author Daniel Serdyukov
 */
final class SQLiteColumnSpec {

    static final String ROWID = "_id";

    private static final Map<String, String> JAVA_TO_SQLITE_TYPE;

    private static final Map<String, String> CURSOR_TO_JAVA_TYPE;

    private static final String BYTE_ARRAY = "byte[]";

    private static final String INTEGER = "INTEGER";

    private static final String REAL = "REAL";

    private static final String TEXT = "TEXT";

    private static final String BLOB = "BLOB";

    static {
        CURSOR_TO_JAVA_TYPE = new HashMap<>();
        CURSOR_TO_JAVA_TYPE.put(TypeName.INT.toString(), "$T.getInt(cursor, $S)");
        CURSOR_TO_JAVA_TYPE.put(TypeName.LONG.toString(), "$T.getLong(cursor, $S)");
        CURSOR_TO_JAVA_TYPE.put(TypeName.FLOAT.toString(), "$T.getFloat(cursor, $S)");
        CURSOR_TO_JAVA_TYPE.put(TypeName.DOUBLE.toString(), "$T.getDouble(cursor, $S)");
        CURSOR_TO_JAVA_TYPE.put(TypeName.BOOLEAN.toString(), "$T.getBoolean(cursor, $S)");
        CURSOR_TO_JAVA_TYPE.put(ClassName.get(String.class).toString(), "$T.getString(cursor, $S)");
        CURSOR_TO_JAVA_TYPE.put(BYTE_ARRAY, "$T.getBlob(cursor, $S)");
    }

    static {
        JAVA_TO_SQLITE_TYPE = new HashMap<>();
        JAVA_TO_SQLITE_TYPE.put(TypeName.INT.toString(), INTEGER);
        JAVA_TO_SQLITE_TYPE.put(TypeName.LONG.toString(), INTEGER);
        JAVA_TO_SQLITE_TYPE.put(TypeName.FLOAT.toString(), REAL);
        JAVA_TO_SQLITE_TYPE.put(TypeName.DOUBLE.toString(), REAL);
        JAVA_TO_SQLITE_TYPE.put(TypeName.BOOLEAN.toString(), INTEGER);
        JAVA_TO_SQLITE_TYPE.put(ClassName.get(String.class).toString(), TEXT);
        JAVA_TO_SQLITE_TYPE.put(BYTE_ARRAY, BLOB);
    }

    private static final String[] CONFLICT_VALUES = new String[]{
            "",
            " ON CONFLICT ROLLBACK",
            " ON CONFLICT ABORT",
            " ON CONFLICT FAIL",
            " ON CONFLICT IGNORE",
            " ON CONFLICT REPLACE"
    };

    final String field;

    final String name;

    final TypeName type;

    final boolean pk;

    final int conflictClause;

    SQLiteColumnSpec() {
        this(ROWID, null, 5 /* SQLiteDatabase.CONFLICT_REPLACE */);
    }

    SQLiteColumnSpec(Element element, int conflictClause) {
        this(ROWID, element, conflictClause);
    }

    SQLiteColumnSpec(String name, Element element) {
        this(name, element, 0);
    }

    SQLiteColumnSpec(String name, Element element, int conflictClause) {
        this.field = element != null ? element.getSimpleName().toString() : null;
        this.name = StringUtils.nonEmpty(name, field);
        this.type = element == null ? TypeName.LONG : TypeName.get(element.asType());
        this.pk = ROWID.equals(this.name);
        this.conflictClause = conflictClause;
    }

    static String toSQLiteType(TypeName typeName) {
        final String type = typeName.toString();
        final String sqliteType = JAVA_TO_SQLITE_TYPE.get(type);
        if (StringUtils.isEmpty(sqliteType)) {
            throw new RuntimeException("Unsupported column type '" + type + "'");
        }
        return sqliteType;
    }

    static String toCursorType(TypeName typeName) {
        final String type = typeName.toString();
        final String sqliteType = CURSOR_TO_JAVA_TYPE.get(type);
        if (StringUtils.isEmpty(sqliteType)) {
            /*if (typeName.getTypeName().equals("java.lang.String")) {
                return "droidkit.database.CursorUtils.getString(cursor, $S)";
            }*/
            /*if (BYTE_ARRAY.equals(type)) {
                return "$T.getBlob(cursor, $S)";
            }*/
            throw new RuntimeException("Unsupported cursor type '" + type + "'");
        }
        return sqliteType;
    }

    private static void checkPkType(boolean pk, TypeName type) {
        if (pk && TypeName.LONG != type) {
            throw new RuntimeException("@SQLitePk must be long type");
        }
    }

    @Override
    public String toString() {
        checkPkType(pk, type);
        final StringBuilder sb = new StringBuilder(40)
                .append(name)
                .append(" ")
                .append(toSQLiteType(type));
        if (pk) {
            sb.append(" PRIMARY KEY");
        }
        return sb.append(CONFLICT_VALUES[conflictClause]).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SQLiteColumnSpec that = (SQLiteColumnSpec) o;
        return !(name != null ? !name.equals(that.name) : that.name != null);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

}
