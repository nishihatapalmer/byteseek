package net.byteseek.matcher;

import org.junit.Test;

import java.util.Arrays;

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
    public void messingAboutRemoveThis() {
        int[] startPositions = new int[] {0, 4, 9, 18};
        for (int i = 0; i < startPositions.length; i++) {
            System.out.println("Array index: " + i + "\t Value: " + startPositions[i]);
        }
        for (int i = 0; i < 20; i++) {
            int result = Arrays.binarySearch(startPositions, i);
            int matcherIndex, offset;
            if (result < 0) {
                matcherIndex = -(result + 2);
                offset       = i - startPositions[matcherIndex];
            } else {
                matcherIndex = result;
                offset       = 0;
            }
            System.out.println("Test: " + i + "\t Result: " + result + "\t  Matcher Index: " + matcherIndex + "\t Offset: " + offset);
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

            Object something = new Object();
            assertFalse(res1.equals(something));
            assertFalse(res1.equals(null));
        }
    }

    @Test
    public void testToString() throws Exception {
        MatchResult result = new MatchResult(0,0);
        assertTrue(result.toString().contains(MatchResult.class.getSimpleName()));
    }

}