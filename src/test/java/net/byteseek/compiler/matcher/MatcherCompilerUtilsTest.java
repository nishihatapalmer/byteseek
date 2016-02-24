package net.byteseek.compiler.matcher;

import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.bytes.InvertedByteMatcher;
import net.byteseek.matcher.bytes.OneByteMatcher;
import net.byteseek.parser.tree.node.ByteNode;
import org.junit.Test;

import static org.junit.Assert.*;

public class MatcherCompilerUtilsTest {

    @Test
    public void testIsInverted() throws Exception {
        ByteNode node = new ByteNode((byte) 00, false);
        assertFalse(MatcherCompilerUtils.isInverted(node, false));
        assertTrue(MatcherCompilerUtils.isInverted(node, true));

        node = new ByteNode((byte) 00, true);
        assertTrue(MatcherCompilerUtils.isInverted(node, false));
        assertFalse(MatcherCompilerUtils.isInverted(node, true));
    }

    @Test
    public void testCreateByteMatcher() throws Exception {
        for (int i = 0; i < 256; i++) {
            ByteNode node = new ByteNode((byte) i, false);
            ByteMatcher matcher = MatcherCompilerUtils.createByteMatcher(node);
            assertEquals("Class is a OneByteMatcher", OneByteMatcher.class, matcher.getClass());
            assertEquals("Matches only one byte", 1, matcher.getNumberOfMatchingBytes());
            assertEquals("Value is " + i, i, matcher.getMatchingBytes()[0] & 0xFF);

            node = new ByteNode((byte) i, true);
            matcher = MatcherCompilerUtils.createByteMatcher(node);
            assertEquals("Class is an InvertedByteMatcher", InvertedByteMatcher.class, matcher.getClass());
            assertEquals("Matches 255 bytes", 255, matcher.getNumberOfMatchingBytes());
            byte[] matching = matcher.getMatchingBytes();
            for (byte by : matching) {
                if (((int) by & 0xFF) == i) {
                    fail("The byte value " + i + " was found in the inverted matcher.");
                }
            }
        }
    }

    @Test
    public void testCreateByteMatcher1() throws Exception {
        for (int i = 0; i < 256; i++) {
            ByteNode node = new ByteNode((byte) i, false);
            ByteMatcher matcher = MatcherCompilerUtils.createByteMatcher(node, false);
            assertEquals("Class is a OneByteMatcher", OneByteMatcher.class, matcher.getClass());
            assertEquals("Matches only one byte", 1, matcher.getNumberOfMatchingBytes());
            assertEquals("Value is " + i, i, matcher.getMatchingBytes()[0] & 0xFF);

            node = new ByteNode((byte) i, true);
            matcher = MatcherCompilerUtils.createByteMatcher(node, false);
            assertEquals("Class is an InvertedByteMatcher", InvertedByteMatcher.class, matcher.getClass());
            assertEquals("Matches 255 bytes", 255, matcher.getNumberOfMatchingBytes());
            byte[] matching = matcher.getMatchingBytes();
            for (byte by : matching) {
                if (((int) by & 0xFF) == i) {
                    fail("The byte value " + i + " was found in the inverted matcher.");
                }
            }

            node = new ByteNode((byte) i, false);
            matcher = MatcherCompilerUtils.createByteMatcher(node, true);
            assertEquals("Class is an InvertedByteMatcher", InvertedByteMatcher.class, matcher.getClass());
            assertEquals("Matches 255 bytes", 255, matcher.getNumberOfMatchingBytes());
            matching = matcher.getMatchingBytes();
            for (byte by : matching) {
                if (((int) by & 0xFF) == i) {
                    fail("The byte value " + i + " was found in the inverted matcher.");
                }
            }

            node = new ByteNode((byte) i, true);
            matcher = MatcherCompilerUtils.createByteMatcher(node, true);
            assertEquals("Class is a OneByteMatcher", OneByteMatcher.class, matcher.getClass());
            assertEquals("Matches only one byte", 1, matcher.getNumberOfMatchingBytes());
            assertEquals("Value is " + i, i, matcher.getMatchingBytes()[0] & 0xFF);
        }
    }

    @Test
    public void testCreateAnyMatcher() throws Exception {

    }

    @Test
    public void testCreateAnyMatcher1() throws Exception {

    }

    @Test
    public void testCreateAllBitmaskMatcher() throws Exception {

    }

    @Test
    public void testCreateAllBitmaskMatcher1() throws Exception {

    }

    @Test
    public void testCreateAnyBitmaskMatcher() throws Exception {

    }

    @Test
    public void testCreateAnyBitmaskMatcher1() throws Exception {

    }

    @Test
    public void testCreateRangeMatcher() throws Exception {

    }

    @Test
    public void testCreateRangeMatcher1() throws Exception {

    }

    @Test
    public void testCreateMatcherFromSet() throws Exception {

    }

    @Test
    public void testCreateMatcherFromSet1() throws Exception {

    }

    @Test
    public void testCreateCaseInsensitiveMatcher() throws Exception {

    }

    @Test
    public void testCreateCaseInsensitiveMatcher1() throws Exception {

    }
}