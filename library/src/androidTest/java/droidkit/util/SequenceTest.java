package droidkit.util;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Serdyukov
 */
@RunWith(AndroidJUnit4.class)
public class SequenceTest {

    private Sequence mSequence;

    @Before
    public void setUp() throws Exception {
        mSequence = new Sequence(100);
    }

    @Test
    public void nextLong() throws Exception {
        Assert.assertEquals(101L, mSequence.nextLong());
    }

    @Test
    public void nextInt() throws Exception {
        Assert.assertEquals(101, mSequence.nextInt());
    }

}
