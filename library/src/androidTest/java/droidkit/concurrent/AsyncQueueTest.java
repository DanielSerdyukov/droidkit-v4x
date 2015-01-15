package droidkit.concurrent;

import android.os.Looper;
import android.os.SystemClock;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Daniel Serdyukov
 */
public class AsyncQueueTest extends AndroidTestCase {

    private static final int DELAY = 1000;

    public void testInvoke() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        AsyncQueue.get().invoke(new Runnable() {
            @Override
            public void run() {
                Assert.assertFalse(Looper.getMainLooper().equals(Looper.myLooper()));
                latch.countDown();
            }
        });
        latch.await(1, TimeUnit.SECONDS);
    }

    public void testInvokeWithDelay() throws Exception {
        final long start = SystemClock.uptimeMillis();
        final CountDownLatch latch = new CountDownLatch(1);
        AsyncQueue.get().invoke(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
            }
        }, 1000);
        latch.await(5, TimeUnit.SECONDS);
        final long time = SystemClock.uptimeMillis() - start;
        Assert.assertTrue(time >= DELAY);
    }

}
