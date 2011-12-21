/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.List;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class ByteRangeMatcherTest {

    /**
     * 
     */
    public ByteRangeMatcherTest() {
    }


    /**
     * Test of matches method, of class ByteRangeMatcher.
     */
    @Test
    public void testByteRange() {
        for (int start = 0; start < 256; start++) {
            System.out.println(String.format("Testing byte ranges starting with %d", start));
            for (int end = 0; end < 256; end++) {
                ByteRangeMatcher matcherNotInverted = new ByteRangeMatcher(start, end, InvertibleMatcher.NOT_INVERTED);
                ByteRangeMatcher matcherInverted = new ByteRangeMatcher(start, end, InvertibleMatcher.INVERTED);
                validateMatcher(matcherNotInverted, start, end);
                validateMatcher(matcherInverted, start, end);
            }
        }
    }

    private void validateMatcher(ByteRangeMatcher matcher, int start, int end) {
        int startValue, endValue;
        if (start > end) {
            startValue = end;
            endValue = start;
        } else {
            startValue = start;
            endValue = end;
        }
        String regex = String.format("[%s%02x-%02x]", matcher.isInverted()? "^" : "", startValue, endValue);
        assertEquals(regex, regex, matcher.toRegularExpression(false));
        String isInverted = matcher.isInverted()? "is" : "is not";
        int numberOfBytes = matcher.isInverted()? 255 - endValue + startValue : endValue - startValue + 1;
        assertEquals(String.format("Number of bytes for %d-%d, matcher %s inverted\t", start, end, isInverted), numberOfBytes, matcher.getNumberOfMatchingBytes());
        List<Byte> byteList = new ArrayList<Byte>();
        String message = "Testing value %d on range %d-%d, matcher %s inverted\t";
        for (int testvalue = 0; testvalue < startValue; testvalue++) {
            String testmessage = String.format(message, testvalue, start, end, isInverted);
            boolean matched = matcher.matches((byte) testvalue);
            if (matched) { byteList.add((byte) testvalue); }
            assertEquals(testmessage, matcher.isInverted(), matched);
        }
        for (int testvalue = startValue; testvalue <= endValue; testvalue++) {
            String testmessage = String.format(message, testvalue, start, end, isInverted);
            boolean matched = matcher.matches((byte) testvalue);
            if (matched) { byteList.add((byte) testvalue); }
            assertEquals(testmessage, !matcher.isInverted(), matched);
        }
        for (int testvalue = endValue+1; testvalue < 256; testvalue++) {
            String testmessage = String.format(message, testvalue, start, end, isInverted);
            boolean matched = matcher.matches((byte) testvalue);
            if (matched) { byteList.add((byte) testvalue); }
            assertEquals(testmessage, matcher.isInverted(), matched);
        }
        byte[] bytes = new byte[byteList.size()];
        int pos = 0;
        for (Byte b : byteList) {
            bytes[pos++] = b;
        }
        message = String.format("Testing byte array on range %d-%d, matcher %s inverted", start, end, matcher.isInverted()? "" : " not");
        assertArrayEquals(message, bytes, matcher.getMatchingBytes() );
    }

}