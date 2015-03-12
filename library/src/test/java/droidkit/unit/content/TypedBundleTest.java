package droidkit.unit.content;

import android.os.Bundle;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import droidkit.content.BoolValue;
import droidkit.content.DoubleValue;
import droidkit.content.FloatValue;
import droidkit.content.IntValue;
import droidkit.content.LongValue;
import droidkit.content.StringValue;
import droidkit.content.TypedBundle;

/**
 * @author Daniel Serdyukov
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class TypedBundleTest {

    @Test
    public void testStringValue() throws Exception {
        final Bundle bundle = new Bundle();
        final MockBundle mock = TypedBundle.from(bundle, MockBundle.class);
        mock.stringValue().set("droidkit.unit.content");
        Assert.assertEquals("droidkit.unit.content", bundle.getString("stringValue"));
    }

    @Test
    public void testIntValue() throws Exception {
        final Bundle bundle = new Bundle();
        final MockBundle mock = TypedBundle.from(bundle, MockBundle.class);
        mock.intValue().set(100500);
        Assert.assertEquals(100500, bundle.getInt("intValue"));
    }

    @Test
    public void testLongValue() throws Exception {
        final Bundle bundle = new Bundle();
        final MockBundle mock = TypedBundle.from(bundle, MockBundle.class);
        mock.longValue().set(100500L);
        Assert.assertEquals(100500L, bundle.getLong("longValue"));
    }

    @Test
    public void testFloatValue() throws Exception {
        final Bundle bundle = new Bundle();
        final MockBundle mock = TypedBundle.from(bundle, MockBundle.class);
        mock.floatValue().set(9.99f);
        Assert.assertEquals(9.99f, bundle.getFloat("floatValue"), 0.0f);
    }

    @Test
    public void testDoubleValue() throws Exception {
        final Bundle bundle = new Bundle();
        final MockBundle mock = TypedBundle.from(bundle, MockBundle.class);
        mock.doubleValue().set(9.99);
        Assert.assertEquals(9.99, bundle.getDouble("doubleValue"), 0.0);
    }

    @Test
    public void testBoolValue() throws Exception {
        final Bundle bundle = new Bundle();
        final MockBundle mock = TypedBundle.from(bundle, MockBundle.class);
        mock.boolValue().set(true);
        Assert.assertTrue(bundle.getBoolean("boolValue"));
    }

    private interface MockBundle {

        StringValue stringValue();

        IntValue intValue();

        LongValue longValue();

        FloatValue floatValue();

        DoubleValue doubleValue();

        BoolValue boolValue();

    }

}
