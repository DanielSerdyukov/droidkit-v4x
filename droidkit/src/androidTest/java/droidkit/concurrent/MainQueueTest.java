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
public class MainQueueTest extends AndroidTestCase {

    private static final long DELAY = 350;

    public void testInvoke() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        MainQueue.get().invoke(new Runnable() {
            @Override
            public void run() {
                Assert.assertEquals(Looper.getMainLooper(), Looper.myLooper());
                latch.countDown();
            }
        });
        latch.await(1, TimeUnit.SECONDS);
    }

    public void testInvokeWithDelay() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final long start = SystemClock.uptimeMillis();
        MainQueue.get().invoke(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
            }
        }, DELAY);
        latch.await(1, TimeUnit.SECONDS);
        final long time = SystemClock.uptimeMillis() - start;
        Assert.assertTrue(time >= DELAY);
        Assert.assertTrue(time < 1000);
    }

}
