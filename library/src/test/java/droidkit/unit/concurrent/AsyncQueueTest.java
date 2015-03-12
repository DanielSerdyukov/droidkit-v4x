package droidkit.unit.concurrent;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import droidkit.concurrent.AsyncQueue;

/**
 * @author Daniel Serdyukov
 */
public class AsyncQueueTest {

    @Test
    public void testInvokeCallable() throws Exception {
        final Future<String> future = AsyncQueue.get().invoke(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "droidkit.unit.concurrent";
            }
        });
        Assert.assertEquals("droidkit.unit.concurrent", future.get(1, TimeUnit.SECONDS));
    }

    @Test
    public void testInvokeCallableWithDelay() throws Exception {
        final long start = System.currentTimeMillis();
        final Future<Long> future = AsyncQueue.get().invoke(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return System.currentTimeMillis();
            }
        }, 500);
        Assert.assertTrue((future.get(1, TimeUnit.SECONDS) - start) >= 500);
    }

    @Test
    public void testInvokeRunnable() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        AsyncQueue.get().invoke(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
            }
        });
        Assert.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testInvokeRunnableWithDelay() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicLong time = new AtomicLong(System.currentTimeMillis());
        AsyncQueue.get().invoke(new Runnable() {
            @Override
            public void run() {
                time.set(System.currentTimeMillis() - time.get());
                latch.countDown();
            }
        }, 500);
        Assert.assertTrue(latch.await(1, TimeUnit.SECONDS));
        Assert.assertTrue(time.get() >= 500);
    }

}
