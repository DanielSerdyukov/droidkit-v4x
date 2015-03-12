package droidkit.unit.concurrent;

import android.os.Looper;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import droidkit.concurrent.MainQueue;

/**
 * @author Daniel Serdyukov
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class MainQueueTest {

    @Test
    public void testInvokeCallable() throws Exception {
        final AtomicBoolean invoked = new AtomicBoolean();
        MainQueue.get().invoke(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Assert.assertSame(Looper.getMainLooper(), Looper.myLooper());
                invoked.set(true);
                return null;
            }
        });
        Assert.assertTrue(invoked.get());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInvokeCallableGet() throws Exception {
        MainQueue.get().invoke(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return null;
            }
        }).get(1, TimeUnit.SECONDS);
    }

    @Test
    public void testInvokeRunnable() throws Exception {
        final AtomicBoolean invoked = new AtomicBoolean();
        MainQueue.get().invoke(new Runnable() {
            @Override
            public void run() {
                Assert.assertSame(Looper.getMainLooper(), Looper.myLooper());
                invoked.set(true);
            }
        });
        Assert.assertTrue(invoked.get());
    }

}
