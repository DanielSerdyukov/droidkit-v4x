package droidkit.unit.content;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import droidkit.content.BoolValue;
import droidkit.content.FloatValue;
import droidkit.content.IntValue;
import droidkit.content.LongValue;
import droidkit.content.StringValue;
import droidkit.content.TypedPrefs;

/**
 * @author Daniel Serdyukov
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class TypedPrefsTest {

    private SharedPreferences mPrefs;

    @Before
    public void setUp() throws Exception {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(Robolectric.application);
    }

    @Test
    public void testStringValue() throws Exception {
        final MockPrefs mock = TypedPrefs.from(mPrefs, MockPrefs.class);
        mock.stringValue().set("droidkit.unit.content");
        Assert.assertEquals("droidkit.unit.content", mPrefs.getString("stringValue", StringValue.EMPTY));
    }

    @Test
    public void testIntValue() throws Exception {
        final MockPrefs mock = TypedPrefs.from(mPrefs, MockPrefs.class);
        mock.intValue().set(100500);
        Assert.assertEquals(100500, mPrefs.getInt("intValue", IntValue.EMPTY));
    }

    @Test
    public void testLongValue() throws Exception {
        final MockPrefs mock = TypedPrefs.from(mPrefs, MockPrefs.class);
        mock.longValue().set(100500L);
        Assert.assertEquals(100500L, mPrefs.getLong("longValue", LongValue.EMPTY));
    }

    @Test
    public void testFloatValue() throws Exception {
        final MockPrefs mock = TypedPrefs.from(mPrefs, MockPrefs.class);
        mock.floatValue().set(9.99f);
        Assert.assertEquals(9.99f, mPrefs.getFloat("floatValue", FloatValue.EMPTY), 0.0f);
    }

    @Test
    public void testBoolValue() throws Exception {
        final MockPrefs mock = TypedPrefs.from(mPrefs, MockPrefs.class);
        mock.boolValue().set(true);
        Assert.assertTrue(mPrefs.getBoolean("boolValue", BoolValue.EMPTY));
    }

    @After
    public void tearDown() throws Exception {
        mPrefs.edit().clear().apply();
    }

    private interface MockPrefs {

        StringValue stringValue();

        IntValue intValue();

        LongValue longValue();

        FloatValue floatValue();

        BoolValue boolValue();

    }

}
