package net.byteseek.matcher;

import org.junit.Test;

import static org.junit.Assert.*;

public class MatchResultTest {

    @Test
    public void getMatchPosition() throws Exception {
        for (int i = 0; i < 1000; i++) {
            MatchResult result = new MatchResult(i, 1);
            assertEquals(i, result.getMatchPosition());
        }
    }

    @Test
    public void getMatchLength() throws Exception {
        for (int i = 0; i < 1000; i++) {
            MatchResult result = new MatchResult(i, i);
            assertEquals(i, result.getMatchLength());
        }
    }

    @Test
    public void testHashCode() throws Exception {
        for (int i = 0; i < 1000; i++) {
            MatchResult res1 = new MatchResult(i, i);
            MatchResult res2 = new MatchResult(i, i);
            assertEquals(res1.hashCode(), res2.hashCode());
        }
    }

    @Test
    public void equals() throws Exception {
        for (int i = 0; i < 1000; i++) {
            MatchResult res1 = new MatchResult(i, i);
            assertEquals(res1, res1);
            MatchResult res2 = new MatchResult(i, i);
            assertEquals(res2, res2);
            assertEquals(res1, res2);
            assertEquals(res2, res1);
            MatchResult res3 = new MatchResult(i, i+1);
            assertNotEquals(res1, res3);
            assertNotEquals(res3, res1);
            MatchResult res4 = new MatchResult(i+1, i);
            assertNotEquals(res1, res4);
            assertNotEquals(res3, res4);
        }
    }

    @Test
    public void testToString() throws Exception {
        MatchResult result = new MatchResult(0,0);
        assertTrue(result.toString().contains(MatchResult.class.getSimpleName()));
    }

}