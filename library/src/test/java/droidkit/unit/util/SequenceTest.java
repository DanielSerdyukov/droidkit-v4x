package droidkit.unit.util;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import droidkit.util.Sequence;

/**
 * @author Daniel Serdyukov
 */
public class SequenceTest {

    private Sequence mSequence;

    @Before
    public void setUp() throws Exception {
        mSequence = new Sequence(1000L);
    }

    @Test
    public void testNextInt() throws Exception {
        Assert.assertEquals(1001, mSequence.nextInt());
    }

    @Test
    public void testNextLong() throws Exception {
        Assert.assertEquals(1001L, mSequence.nextLong());
    }

}
