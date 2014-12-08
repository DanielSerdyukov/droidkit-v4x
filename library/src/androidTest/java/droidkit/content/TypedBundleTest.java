package droidkit.content;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Serdyukov
 */
public class TypedBundleTest extends AndroidTestCase {

    private Bundle mBundle;

    private Extra mExtra;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mBundle = new Bundle();
        mExtra = TypedBundle.from(mBundle, Extra.class);
    }

    public void testIntValue() throws Exception {
        final IntValue version = mExtra.version();
        Assert.assertNotNull(version);
        Assert.assertEquals(IntValue.EMPTY, version.get());
        version.set(123);
        Assert.assertEquals(123, version.get());
        Assert.assertEquals(mBundle.getInt("version", IntValue.EMPTY), version.get());
    }

    public void testStringValue() throws Exception {
        final StringValue name = mExtra.name();
        Assert.assertNotNull(name);
        Assert.assertEquals(StringValue.EMPTY, name.get());
        name.set("John");
        Assert.assertEquals("John", name.get());
        Assert.assertEquals(mBundle.getString("name", StringValue.EMPTY), name.get());
    }

    public void testBoolValue() throws Exception {
        final BoolValue enabled = mExtra.enabled();
        Assert.assertNotNull(enabled);
        Assert.assertEquals(BoolValue.EMPTY, enabled.get());
        enabled.set(true);
        Assert.assertTrue(enabled.get());
        Assert.assertEquals(mBundle.getBoolean("enabled", BoolValue.EMPTY), enabled.get());
        Assert.assertTrue(enabled.toggle());
        Assert.assertFalse(enabled.get());
        Assert.assertEquals(mBundle.getBoolean("enabled", BoolValue.EMPTY), enabled.get());
    }

    public void testLongValue() throws Exception {
        final LongValue time = mExtra.time();
        Assert.assertNotNull(time);
        Assert.assertEquals(LongValue.EMPTY, time.get());
        final long expected = SystemClock.uptimeMillis();
        time.set(expected);
        Assert.assertEquals(expected, time.get());
        Assert.assertEquals(mBundle.getLong("time", LongValue.EMPTY), time.get());
    }

    public void testDoubleValue() throws Exception {
        final DoubleValue lat = mExtra.lat();
        Assert.assertNotNull(lat);
        Assert.assertEquals(DoubleValue.EMPTY, lat.get());
        lat.set(60.39856);
        Assert.assertEquals(60.39856, lat.get());
        Assert.assertEquals(mBundle.getDouble("lat", DoubleValue.EMPTY), lat.get());
    }

    public void testFloatValue() throws Exception {
        final FloatValue distance = mExtra.distance();
        Assert.assertNotNull(distance);
        Assert.assertEquals(FloatValue.EMPTY, distance.get());
        distance.set(100f);
        Assert.assertEquals(100f, distance.get());
        Assert.assertEquals(mBundle.getFloat("distance", FloatValue.EMPTY), distance.get());
    }

    public void testStringListValue() throws Exception {
        final StringListValue lines = mExtra.lines();
        Assert.assertNotNull(lines);
        final List<String> newLines = new ArrayList<>();
        newLines.add("1");
        newLines.add("2");
        newLines.add("3");
        lines.set(newLines);
        Assert.assertEquals(newLines, lines.get());
        Assert.assertEquals(mBundle.getStringArrayList("lines"), lines.get());
    }

    public void testParcelableValue() throws Exception {
        final ParcelableValue location = mExtra.location();
        final Location latLng = new Location(LocationManager.PASSIVE_PROVIDER);
        location.set(latLng);
        Assert.assertEquals(latLng, location.<Location>get());
        Assert.assertEquals(mBundle.<Location>getParcelable("location"), location.<Location>get());
    }

    public void testWrapper() throws Exception {
        Assert.assertSame(mBundle, mExtra.pack());
        Assert.assertSame(mBundle, mExtra.create());
        Assert.assertSame(mBundle, mExtra.build());
    }

    public void testPack() throws Exception {
        final Extra extra = TypedBundle.from(Extra.class);
        extra.version().set(1);
        extra.name().set("John");
        extra.enabled().toggle();
        extra.time().set(2014);
        extra.lat().set(60.334455);
        extra.distance().set(50f);
        final Bundle pack = extra.pack();
        Assert.assertEquals(1, pack.getInt("version"));
        Assert.assertEquals("John", pack.getString("name"));
        Assert.assertTrue(pack.getBoolean("enabled"));
        Assert.assertEquals(2014, pack.getLong("time"));
        Assert.assertEquals(60.334455, pack.getDouble("lat"));
        Assert.assertEquals(50f, pack.getFloat("distance"));
    }

    private static interface Extra {

        IntValue version();

        StringValue name();

        BoolValue enabled();

        LongValue time();

        DoubleValue lat();

        FloatValue distance();

        StringListValue lines();

        ParcelableValue location();

        Bundle pack();

        Bundle create();

        Bundle build();

    }

}
