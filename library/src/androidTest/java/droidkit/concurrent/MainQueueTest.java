package droidkit.concurrent;

import android.os.Looper;
import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidJUnit4.class)
public class MainQueueTest {

    private static final long DELAY = 350;

    @Test
    public void invoke() throws Exception {
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

    @Test
    public void invokeWithDelay() throws Exception {
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
    }

}
