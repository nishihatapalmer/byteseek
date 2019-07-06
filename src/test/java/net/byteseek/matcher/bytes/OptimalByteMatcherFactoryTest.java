/*
 * Copyright Matt Palmer 2019, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.byteseek.matcher.bytes;

import net.byteseek.utils.ByteUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class OptimalByteMatcherFactoryTest {

    static Random random = new Random();
    ByteMatcherFactory factory;

    @BeforeClass
    public static void setUpClass() throws Exception {
        final long seed = System.currentTimeMillis();
        // final long seed = ?
        random.setSeed(seed);
        System.out.println("Seeding random number generator with: " + Long.toString(seed));
        System.out.println("To repeat these exact tests, set the seed to the value above.");
    }

    @Before
    public void setUp() throws Exception {
        factory = new OptimalByteMatcherFactory();
    }

    @Test
    public void testAllBytes() throws Exception {
        Set<Byte> allBytes = getAllBytes();
        ByteMatcher matcher = factory.create(allBytes);
        assertEquals(256, matcher.getNumberOfMatchingBytes());

        matcher = factory.create(allBytes, InvertibleMatcher.INVERTED);
        assertEquals(0, matcher.getNumberOfMatchingBytes());
    }

    @Test
    public void testEmptySet() throws Exception {
        Set<Byte> empty = new HashSet<Byte>();
        ByteMatcher matcher = factory.create(empty);
        //TODO: should throw exception?  matching zero bytes will never match anything.
        assertEquals(0, matcher.getNumberOfMatchingBytes());

        matcher = factory.create(empty, InvertibleMatcher.INVERTED);
        assertEquals(256, matcher.getNumberOfMatchingBytes());
    }

    @Test
    public void testCreateOneByteMatcher() throws Exception {
        for (int i = 0; i < 256; i++) {
            // Create as a single byte in a set, not inverted
            Set<Byte> byteSet= new HashSet<Byte>();
            byteSet.add(Byte.valueOf((byte) i));

            ByteMatcher matcher = factory.create(byteSet);
            testCreateOneByteMatcher(matcher, (byte) i);

            // Create as an inversion of 255 bytes:
            byteSet = getAllBytes();
            byteSet.remove((byte) i);
            matcher = factory.create(byteSet, InvertibleMatcher.INVERTED);
            testCreateOneByteMatcher(matcher, (byte) i);
        }
    }

    private void testCreateOneByteMatcher(ByteMatcher matcher, byte i) {
        assertEquals(1, matcher.getNumberOfMatchingBytes());
        assertEquals(OneByteMatcher.class, matcher.getClass());
        assertEquals((byte) i, matcher.getMatchingBytes()[0]);
    }

    @Test
    public void testCreateInvertedOneByteMatcher() throws Exception {
        for (int i = 0; i < 256; i++) {
            // Create with 255 byte set
            Set<Byte> byteSet = getAllBytes();
            byteSet.remove((byte) i);
            ByteMatcher matcher = factory.create(byteSet);
            testCreateInvertedOneByteMatcher(matcher, (byte) i);

            // Create with an inverted single byte:
            byteSet.clear();
            byteSet.add(Byte.valueOf((byte) i));
            matcher = factory.create(byteSet, InvertibleMatcher.INVERTED);
            testCreateInvertedOneByteMatcher(matcher, (byte) i);
        }
    }

    private void testCreateInvertedOneByteMatcher(ByteMatcher matcher, byte i) {
        assertEquals(255, matcher.getNumberOfMatchingBytes());
        assertEquals(OneByteInvertedMatcher.class, matcher.getClass());
        byte[] matching = matcher.getMatchingBytes();
        assertEquals(255, matching.length);
        byte toFind = (byte) i;
        for (int j = 0; j < 255; j++) {
            assertNotEquals(toFind, matching[j]);
        }
    }

    @Test
    public void testCreateTwoByteMatcher() throws Exception {
        for (int i = 0; i < 256; i++) {
            // Create with a 2 byte set:
            Set<Byte> byteSet= new HashSet<Byte>();
            byteSet.add(Byte.valueOf((byte) i));
            byteSet.add(Byte.valueOf((byte) ((i + 57) % 256)));

            ByteMatcher matcher = factory.create(byteSet);
            testCreateTwoByteMatcher(matcher, byteSet, InvertibleMatcher.NOT_INVERTED);

            // Create with an inverted 254 byte set:
            byteSet = getAllBytes();
            byteSet.remove((byte) i);
            byteSet.remove((byte) (((i + 57) % 256)));

            matcher = factory.create(byteSet, InvertibleMatcher.INVERTED);
            testCreateTwoByteMatcher(matcher, byteSet, InvertibleMatcher.INVERTED);
        }
    }

    private void testCreateTwoByteMatcher(ByteMatcher matcher, Set<Byte> byteSet, boolean inverted) {
        assertEquals(2, matcher.getNumberOfMatchingBytes());
        assertEquals(TwoByteMatcher.class, matcher.getClass());
        byte[] matching = matcher.getMatchingBytes();
        assertEquals(2, matching.length);
        assertTrue(byteSet.contains(matching[0]) ^ inverted);
        assertTrue(byteSet.contains(matching[1]) ^ inverted);
    }

    @Test
    public void testCreateTwoByteInvertedMatcher() throws Exception {
        for (int i = 0; i < 256; i++) {
            // Create with a 254 byte set (not inverted):
            Set<Byte> byteSet= getAllBytes();
            byteSet.remove(Byte.valueOf((byte) i));
            byteSet.remove(Byte.valueOf((byte) ((i + 57) % 256)));

            ByteMatcher matcher = factory.create(byteSet);
            testCreateTwoByteInvertedMatcher(matcher, byteSet, InvertibleMatcher.NOT_INVERTED);

            // Create with a 2 byte set inverted:
            byteSet.clear();
            byteSet.add((byte) i);
            byteSet.add((byte) ((i + 57) % 256));
            matcher = factory.create(byteSet, InvertibleMatcher.INVERTED);
            testCreateTwoByteInvertedMatcher(matcher, byteSet, InvertibleMatcher.INVERTED);
        }
    }

    private void testCreateTwoByteInvertedMatcher(ByteMatcher matcher, Set<Byte> byteSet, boolean inverted) {
        assertEquals(254, matcher.getNumberOfMatchingBytes());
        assertEquals(TwoByteInvertedMatcher.class, matcher.getClass());
        byte[] matching = matcher.getMatchingBytes();
        assertEquals(254, matching.length);
        assertTrue(byteSet.contains(matching[0]) ^ inverted);
        assertTrue(byteSet.contains(matching[1]) ^ inverted);
    }

    @Test
    public void testCreateByteRangeMatcher() throws Exception {
        for (int i = 0; i < 256; i++) {
            // Get a difference of at least 3, but no more than 253 - otherwise we
            // we will run into matchers for sizes of 1, 2, 254 and 255 which supercede range matchers.
            int end = (i + random.nextInt(250) + 3) % 256;

            // Create with a range:
            Set<Byte> byteSet= getRangeBytes(i, end);
            ByteMatcher matcher = factory.create(byteSet);
            testCreateRangeMatcher(matcher, byteSet, InvertibleMatcher.NOT_INVERTED);

            // Create range by inverting an inverted range:
            Set<Byte> invertedSet = getAllBytes();
            invertedSet.removeAll(byteSet);
            testCreateRangeMatcher(matcher, invertedSet, InvertibleMatcher.INVERTED);
        }
    }

    private void testCreateRangeMatcher(ByteMatcher matcher, Set<Byte> bytes, boolean inverted) {
        assertEquals(ByteRangeMatcher.class, matcher.getClass());

        int num = inverted? 256 - matcher.getNumberOfMatchingBytes() : matcher.getNumberOfMatchingBytes();
        assertEquals(num, bytes.size());

        for (int i = 0; i < 256; i++) {
            byte b = (byte) i;
            if (bytes.contains(b)) {
                assertTrue(matcher.matches(b) ^ inverted);
            } else {
                assertFalse(matcher.matches(b) ^ inverted);
            }
        }
    }

    @Test
    public void testCreateByteRangeInvertedMatcher() throws Exception {
        for (int i = 1; i < 256; i++) {
            int end = (i + random.nextInt(248) + 3) % 256;

            // Create a range and an inverted version of the range:
            Set<Byte> byteSet= getRangeBytes(i, end);
            Set<Byte> invertedSet = getAllBytes();
            invertedSet.removeAll(byteSet);

            // Create a matcher which is an inverse range from that set, and check it matches the set only.
            ByteMatcher matcher = factory.create(invertedSet);
            testCreateRangeInvertedMatcher(matcher, invertedSet);
        }
    }

    private void testCreateRangeInvertedMatcher(ByteMatcher matcher, Set<Byte> bytes) {
        assertEquals(ByteRangeMatcher.class, matcher.getClass());

        //int num = inverted? 256 - matcher.getNumberOfMatchingBytes() : matcher.getNumberOfMatchingBytes();
        int num = matcher.getNumberOfMatchingBytes();
        assertEquals(num, bytes.size());

        for (int i = 0; i < 256; i++) {
            byte b = (byte) i;
            if (bytes.contains(b)) {
                assertTrue(matcher.matches(b));
            } else {
                assertFalse(matcher.matches(b));
            }
        }
    }

    @Test
    public void testCreateWildbitMatcher() {
        for (int test = 0; test < 256; test++) {
            byte mask = (byte) random.nextInt(256);
            byte value = (byte) random.nextInt(256);
            WildBitMatcher matcher = new WildBitMatcher(value, mask);

            int numberMatchingBytes = 1 << ByteUtils.countUnsetBits(mask);

            // Avoid set sizes which the factory will just produce specialised matchers for:
            if (numberMatchingBytes > 2 && numberMatchingBytes < 254) {

                Set<Byte> bytes = new HashSet<Byte>();
                ByteUtils.addBytesMatchedByWildBit(mask, value, bytes, false);

                assertEquals(bytes.size(), numberMatchingBytes);

                ByteMatcher matcher2 = factory.create(bytes);
                // sometimes a wildbit with contiguous wild bits is more efficiently matched by a range,
                // so we don't always get a wildbitmatcher back from the factory, or we get a different
                // wildbit matcher (e.g. inverted differently), that should still match the same bytes.
                if (matcher2.getClass() == WildBitMatcher.class) {
                    assertTrue(matcher2.equals(matcher));
                    assertTrue(matcher.equals(matcher2));
                    for (int byteValue = 0; byteValue < 256; byteValue++) {
                        byte b = (byte) byteValue;
                        if (bytes.contains(b)) {
                            assertTrue(matcher2.matches(b));
                        } else {
                            assertFalse(matcher2.matches(b));
                        }
                    }
                } else { // At least check that the different matcher matches the same bytes.
                    checkMatchesTheSameBytes(matcher, matcher2);
                }
            }
        }
    }

    private void checkMatchesTheSameBytes(ByteMatcher matcher1, ByteMatcher matcher2) {
        assertEquals(matcher1.getNumberOfMatchingBytes(), matcher2.getNumberOfMatchingBytes());
        byte[] b1 = matcher1.getMatchingBytes();
        byte[] b2 = matcher2.getMatchingBytes();
        assertEquals(matcher1.getNumberOfMatchingBytes(), b1.length);
        assertEquals(matcher2.getNumberOfMatchingBytes(), b2.length);
        for (int i = 0; i < b1.length; i++) {
            assertTrue(arrayContains(b1[i], b2));
        }
    }

    private boolean arrayContains(byte b, byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == b) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testCreateInvertedWildbitMatcher() {
        for (int test = 0; test < 256; test++) {
            byte mask = (byte) random.nextInt(256);
            byte value = (byte) random.nextInt(256);
            WildBitMatcher matcher = new WildBitMatcher(value, mask, InvertibleMatcher.INVERTED);

            int numberMatchingBytes = 256 - (1 << ByteUtils.countUnsetBits(mask));

            // Avoid set sizes which the factory will just produce specialised matchers for:
            if (numberMatchingBytes > 2 && numberMatchingBytes < 254) {

                Set<Byte> bytes = new HashSet<Byte>();
                //TODO: sort out order of mask / value parameters to make things consistent.
                ByteUtils.addBytesMatchedByWildBit(mask, value, bytes, false);
                Set<Byte> inverse = ByteUtils.invertedSet(bytes);

                assertEquals(inverse.size(), numberMatchingBytes);

                ByteMatcher matcher2 = factory.create(inverse);
                for (int byteValue = 0; byteValue < 256; byteValue++) {
                    byte b = (byte) byteValue;
                    if (inverse.contains(b)) {
                        assertTrue(matcher2.matches(b));
                    } else {
                        assertFalse(matcher2.matches(b));
                    }
                }

            }
        }
    }


    @Test
    public void testRandomSets() {
        List<Byte> bytes = new ArrayList<Byte>();
        for (int test = 0; test < 1024; test++) {
            // Create a random set of bytes:
            int numInSet = random.nextInt(256);
            bytes.clear();
            for (int num = 0; num < numInSet; num++) {
                bytes.add((byte) random.nextInt(256));
            }

            // Test random list (possibly has duplicates)
            testRandomSet(bytes);
        }
    }

    private void testRandomSet(Collection<Byte> bytes) {
        Set<Byte> byteSet = new HashSet<Byte>(bytes);

        // Test by creating with the collection of bytes
        ByteMatcher matcher = factory.create(bytes);
        testRandomMatcher(matcher, byteSet);

        // Test by creating with the set of bytes
        matcher = factory.create(byteSet);
        testRandomMatcher(matcher, byteSet);
    }

    private void testRandomMatcher(ByteMatcher matcher, Set<Byte> bytes) {
        assertEquals(bytes.size(), matcher.getNumberOfMatchingBytes());
        for (int i = 0; i < 256; i++) {
            byte b = (byte) i;
            if (bytes.contains(b)) {
                assertTrue(matcher.matches(b));
            } else {
                assertFalse(matcher.matches(b));
            }
        }
    }


    private Set<Byte> getAllBytes() {
        Set<Byte> bytes = new HashSet<Byte>();
        for (int i = 0; i < 256; i++) {
            bytes.add((byte) i);
        }
        return bytes;
    }

    private Set<Byte> getRangeBytes(int start, int end) {
        if (start > end) {
            int temp = start;
            start = end;
            end   = temp;
        }
        Set<Byte> bytes = new HashSet<Byte>();
        for (int i = start; i <= end; i++) {
            bytes.add((byte) i);
        }
        return bytes;
    }

}