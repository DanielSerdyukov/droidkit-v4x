package droidkit.content;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Daniel Serdyukov
 */
public class TypedPrefsTest extends AndroidTestCase {

    private SharedPreferences mPrefs;

    private Settings mSettings;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mPrefs.edit().clear().apply();
        mSettings = TypedPrefs.from(getContext(), Settings.class);
    }

    public void testIntValue() throws Exception {
        final IntValue version = mSettings.version();
        Assert.assertNotNull(version);
        Assert.assertEquals(IntValue.EMPTY, version.get());
        version.set(123);
        Assert.assertEquals(123, version.get());
        Assert.assertEquals(mPrefs.getInt("version", IntValue.EMPTY), version.get());
    }

    public void testStringValue() throws Exception {
        final StringValue name = mSettings.name();
        Assert.assertNotNull(name);
        Assert.assertEquals(StringValue.EMPTY, name.get());
        name.set("John");
        Assert.assertEquals("John", name.get());
        Assert.assertEquals(mPrefs.getString("name", StringValue.EMPTY), name.get());
    }

    public void testBoolValue() throws Exception {
        final BoolValue enabled = mSettings.enabled();
        Assert.assertNotNull(enabled);
        Assert.assertEquals(BoolValue.EMPTY, enabled.get());
        enabled.set(true);
        Assert.assertTrue(enabled.get());
        Assert.assertEquals(mPrefs.getBoolean("enabled", BoolValue.EMPTY), enabled.get());
        Assert.assertTrue(enabled.toggle());
        Assert.assertFalse(enabled.get());
        Assert.assertEquals(mPrefs.getBoolean("enabled", BoolValue.EMPTY), enabled.get());
    }

    public void testLongValue() throws Exception {
        final LongValue time = mSettings.time();
        Assert.assertNotNull(time);
        Assert.assertEquals(LongValue.EMPTY, time.get());
        final long expected = SystemClock.uptimeMillis();
        time.set(expected);
        Assert.assertEquals(expected, time.get());
        Assert.assertEquals(mPrefs.getLong("time", LongValue.EMPTY), time.get());
    }

    public void testFloatValue() throws Exception {
        final FloatValue distance = mSettings.distance();
        Assert.assertNotNull(distance);
        Assert.assertEquals(FloatValue.EMPTY, distance.get());
        distance.set(100f);
        Assert.assertEquals(100f, distance.get());
        Assert.assertEquals(mPrefs.getFloat("distance", FloatValue.EMPTY), distance.get());
    }

    public void testStringSetValue() throws Exception {
        final StringSetValue lines = mSettings.lines();
        Assert.assertNotNull(lines);
        final Set<String> newLines = new HashSet<>();
        newLines.add("1");
        newLines.add("2");
        newLines.add("3");
        lines.set(newLines);
        Assert.assertEquals(newLines, lines.get());
        Assert.assertEquals(mPrefs.getStringSet("lines", Collections.<String>emptySet()), lines.get());
    }

    private static interface Settings {

        IntValue version();

        StringValue name();

        BoolValue enabled();

        LongValue time();

        FloatValue distance();

        StringSetValue lines();

    }

}
