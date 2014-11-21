package net.byteseek.io.reader.cache;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * The purpose of this test is to test all the functionality which the abstract AbstractCache
 * class provides to its implementing classes.  This is all the subscription, un-subscription
 * and window-free notification mechanisms.
 * <p>
 * Since we can't test an AbstractCache directly, we'll test it using the NoCache,
 * which is the most minimal implementation of AbstractCache and which does not
 * override any functionality provided by AbstractCache.
 */
public class AbstractCacheTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testSubscribe() throws Exception {

    }

    @Test
    public void testUnsubscribe() throws Exception {

    }

    @Test
    public void testNotifyWindowFree() throws Exception {

    }
}