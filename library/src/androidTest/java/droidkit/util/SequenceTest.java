package droidkit.util;

import junit.framework.Assert;
import junit.framework.TestCase;

import droidkit.log.Logger;

/**
 * @author Daniel Serdyukov
 */
public class SequenceTest extends TestCase {

    private Sequence mSequence;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mSequence = new Sequence(100);
    }

    public void testNextLong() throws Exception {
        Assert.assertEquals(101L, mSequence.nextLong());
        Logger.debug("olololo");
    }

    public void testNextInt() throws Exception {
        Assert.assertEquals(101, mSequence.nextInt());
    }

}
