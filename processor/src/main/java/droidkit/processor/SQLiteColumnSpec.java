package droidkit.processor;

import com.squareup.javapoet.Types;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;

/**
 * @author Daniel Serdyukov
 */
final class SQLiteColumnSpec {

    static final String ROWID = "_id";

    private static final Map<Type, String> JAVA_TO_SQLITE_TYPE;

    static {
        JAVA_TO_SQLITE_TYPE = new HashMap<>();
        JAVA_TO_SQLITE_TYPE.put(Integer.TYPE, "INTEGER");
        JAVA_TO_SQLITE_TYPE.put(Long.TYPE, "INTEGER");
        JAVA_TO_SQLITE_TYPE.put(Float.TYPE, "REAL");
        JAVA_TO_SQLITE_TYPE.put(Double.TYPE, "REAL");
        JAVA_TO_SQLITE_TYPE.put(Boolean.TYPE, "INTEGER");
    }

    private static final Map<Type, String> CURSOR_TO_JAVA_TYPE;

    static {
        CURSOR_TO_JAVA_TYPE = new HashMap<>();
        CURSOR_TO_JAVA_TYPE.put(Integer.TYPE, "droidkit.database.CursorUtils.getInt(cursor, $S)");
        CURSOR_TO_JAVA_TYPE.put(Long.TYPE, "droidkit.database.CursorUtils.getLong(cursor, $S)");
        CURSOR_TO_JAVA_TYPE.put(Float.TYPE, "droidkit.database.CursorUtils.getFloat(cursor, $S)");
        CURSOR_TO_JAVA_TYPE.put(Double.TYPE, "droidkit.database.CursorUtils.getDouble(cursor, $S)");
        CURSOR_TO_JAVA_TYPE.put(Boolean.TYPE, "droidkit.database.CursorUtils.getBoolean(cursor, $S)");
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

    final Type type;

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
        this.type = element == null ? Long.TYPE : Types.get(element.asType());
        this.pk = ROWID.equals(this.name);
        this.conflictClause = conflictClause;
    }

    static String toSQLiteType(Type type) {
        String sqliteType = JAVA_TO_SQLITE_TYPE.get(type);
        if (StringUtils.isEmpty(sqliteType)) {
            if (type.getTypeName().equals("byte[]")) {
                return "BLOB";
            }
            return "TEXT";
        }
        return sqliteType;
    }

    static String toCursorType(Type type) {
        String sqliteType = CURSOR_TO_JAVA_TYPE.get(type);
        if (StringUtils.isEmpty(sqliteType)) {
            if (type.getTypeName().equals("java.lang.String")) {
                return "droidkit.database.CursorUtils.getString(cursor, $S)";
            } else if (type.getTypeName().equals("byte[]")) {
                return "droidkit.database.CursorUtils.getBlob(cursor, $S)";
            }
            throw new RuntimeException("Unsupported column type '" + type + "'");
        }
        return sqliteType;
    }

    private static void checkPkType(boolean pk, Type type) {
        if (pk && !Long.TYPE.equals(type)) {
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
