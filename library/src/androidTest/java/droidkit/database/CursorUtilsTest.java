package droidkit.database;

import android.database.Cursor;
import android.database.MatrixCursor;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Arrays;

import droidkit.database.CursorUtils;

/**
 * @author Daniel Serdyukov
 */
public class CursorUtilsTest extends TestCase {

    private Cursor mCursor;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        final MatrixCursor cursor = new MatrixCursor(new String[]{
                "long_field",
                "int_field",
                "sort_field",
                "double_field",
                "float_field",
                "string_field",
                "blob_field"
        });
        cursor.addRow(new Object[]{1000L, 100, (short) 10, 99.99, 66.66f, "test", new byte[]{1, 2, 3}});
        mCursor = cursor;
        mCursor.moveToFirst();
    }

    public void testGetLong() throws Exception {
        Assert.assertEquals(mCursor.getLong(mCursor.getColumnIndex("long_field")),
                CursorUtils.getLong(mCursor, "long_field"));
    }

    public void testGetInt() throws Exception {
        Assert.assertEquals(mCursor.getInt(mCursor.getColumnIndex("int_field")),
                CursorUtils.getInt(mCursor, "int_field"));
    }

    public void testGetShort() throws Exception {
        Assert.assertEquals(mCursor.getShort(mCursor.getColumnIndex("sort_field")),
                CursorUtils.getShort(mCursor, "sort_field"));
    }

    public void testGetDouble() throws Exception {
        Assert.assertEquals(mCursor.getDouble(mCursor.getColumnIndex("double_field")),
                CursorUtils.getDouble(mCursor, "double_field"));
    }

    public void testGetFloat() throws Exception {
        Assert.assertEquals(mCursor.getFloat(mCursor.getColumnIndex("float_field")),
                CursorUtils.getFloat(mCursor, "float_field"));
    }

    public void testGetString() throws Exception {
        Assert.assertEquals(mCursor.getString(mCursor.getColumnIndex("string_field")),
                CursorUtils.getString(mCursor, "string_field"));
    }

    public void testGetBlob() throws Exception {
        final byte[] expected = mCursor.getBlob(mCursor.getColumnIndex("blob_field"));
        final byte[] actual = CursorUtils.getBlob(mCursor, "blob_field");
        Assert.assertTrue(Arrays.equals(expected, actual));
    }

}
