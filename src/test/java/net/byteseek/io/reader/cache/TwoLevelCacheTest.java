package net.byteseek.io.reader.cache;

import org.junit.Test;

import static org.junit.Assert.fail;

public class TwoLevelCacheTest {

    @Test(expected=IllegalArgumentException.class)
    public void testCreateNullCaches() throws Exception {
        TwoLevelCache.create(null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateNullPrimaryCache() throws Exception {
        TwoLevelCache.create(null, new NoCache());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateNullSecondaryCache() throws Exception {
        TwoLevelCache.create(new NoCache(),null);
    }

    public void testCreateNoPrimarySecondaryOK() throws Exception {
        TwoLevelCache.create(new NoCache(),new NoCache());
    }


    @Test
    public void testGetWindow() throws Exception {
        fail("TODO");

    }

    @Test
    public void testAddWindow() throws Exception {
        fail("TODO");

    }

    @Test
    public void testClear() throws Exception {
        fail("TODO");

    }

    @Test
    public void testWindowFree() throws Exception {
        fail("TODO");

    }

    @Test
    public void testGetPrimaryCache() throws Exception {
        fail("TODO");

    }

    @Test
    public void testGetSecondaryCache() throws Exception {
        fail("TODO");

    }

    @Test
    public void testToString() throws Exception {
        fail("TODO");

    }
}