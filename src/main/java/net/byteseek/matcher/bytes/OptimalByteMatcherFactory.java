/*
 * Copyright Matt Palmer 2009-2019, All rights reserved.
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

import java.util.*;

import net.byteseek.incubator.matcher.bytes.SetBinarySearchMatcher;
import net.byteseek.utils.ByteUtils;
import net.byteseek.utils.ArgUtils;
import net.byteseek.utils.MathUtils;

/**
 * A fairly simple implementation of {@link ByteMatcherFactory}.  It attempts to build the
 * most efficient {@link ByteMatcher} which matches the set of bytes passed in (and whether or
 * not that set of bytes should be inverted).  It tries to avoid large costs in analysing the set
 * passed in.  Most simple matchers are created just looking at the size of the set.  It will
 * also usually need to iterate across all the bytes in the set at least once, and if more than once,
 * not many times more.
 *
 * @author Matt Palmer
 */
public final class OptimalByteMatcherFactory implements ByteMatcherFactory {

    public static final ByteMatcherFactory FACTORY = new OptimalByteMatcherFactory();


    private static final int[] EMPTY_ARRAY = new int[0]; // used to avoid creating arrays if not necessary.

    /**
     * Creates an efficient {@link ByteMatcher} from a collection of bytes passed in.
     * 
     * @param bytes A set of bytes which a ByteMatcher must match.
     * @return A ByteMatcher which matches that set of bytes.
     */
    @Override
    public ByteMatcher create(final Collection<Byte> bytes) {
        return create(bytes, NOT_INVERTED);
    }

    /**
     * Creates an efficient {@link ByteMatcher} from a set of bytes passed in.
     *
     * @param bytes A set of bytes which a ByteMatcher must match.
     * @return A ByteMatcher which matches that set of bytes.
     */
    @Override
    public ByteMatcher create(final Set<Byte> bytes) {
        return create(bytes, NOT_INVERTED);
    }

    /**
     * Creates an efficient {@link ByteMatcher} from a collection of bytes passed in (
     * and whether that the set of bytes in the collection should be inverted or not).
     * <p>
     * Duplicate values are permitted in the collection passed in.
     *
     * @param bytes  The collection of bytes to match (or their inverse).
     * @param matchInverse   Whether the set values are inverted or not
     * @return A ByteMatcher which is optimal for that set of bytes.
     * @throws IllegalArgumentException if the collection is null.
     * @throws NullPointerException if any of the Bytes in the collection are null.
     */
    @Override
    public ByteMatcher create(final Collection<Byte> bytes, final boolean matchInverse) {
        ArgUtils.checkNullCollection(bytes, "bytes"); // an empty collection is fine if we match inverse...
        return create(new LinkedHashSet<Byte>(bytes), matchInverse);
    }

    @Override
    public ByteMatcher create(final Set<Byte> bytes, final boolean matchInverse) {
        ArgUtils.checkNullCollection(bytes, "bytes"); // an empty collection is fine if we match inverse.

        //Note: We use the pattern of trying different approaches and only returning a result if it wasn't null.
        //      Although a bit inelegant, it avoids deeply nested blocks and makes it simple to extend.

        // Are there any obvious matchers or inverted matchers for a particular size of set?
        ByteMatcher result = createSizeMatchers(bytes, matchInverse);
        if (result != null) { return result; }

        // Are there any matchers for ranges or bitmasks?
        result = createRangeOrBitmaskMatchers(bytes, matchInverse);
        if (result != null) { return result; }

        // Are there any matchers for ranges or bitmasks for the inverse of the set provided?
        result = createInvertedRangeOrBitmaskMatchers(bytes, matchInverse);
        if (result != null) { return result; }

        // No more specialised or efficient matcher exists for this set of bytes - use a bitmap matcher, which has
        // efficient O(1) lookup for any byte
        return new SetBitmapMatcher(bytes, matchInverse);
    }

    private ByteMatcher createRangeOrBitmaskMatchers(final Set<Byte> bytes, boolean matchInverse) {

        // Set some useful constants
        final int setSize = bytes.size();
        final boolean isPowerTwoSize = MathUtils.isPowerOfTwo(setSize);
        final boolean isInversePowerTwoSize = MathUtils.isPowerOfTwo(256-setSize);
        final boolean needToCountBits = isPowerTwoSize | isInversePowerTwoSize;

        // Initialize the variables we need to process the set of bytes.
        int minValue = 255;
        int maxValue = 0;
        final int[] bit0Counts, bit1Counts;
        if (needToCountBits) { // If we might have a bitmask, count the bits as we go.
            bit0Counts = new int[8]; // a count of how many zero bits we have.
            bit1Counts = new int[8]; // a count of how many one bits we have.
        } else {
            bit0Counts = EMPTY_ARRAY;
            bit1Counts = EMPTY_ARRAY;
        }

        // Find the minimum and maximum byte values in the set, and a count of each bit position.
        for (final Byte b : bytes) {
            // Turn the signed byte into an unsigned int, yet again. Whoever made bytes signed should reflect on their sins.
            final int value = b & 0xFF;
            if (needToCountBits) { // count the bits if we have a power of two, or inverse power two size.
                addBitCounts(value, bit0Counts, bit1Counts);
            }
            if (value < minValue) {
                minValue = value;
            }
            if (value > maxValue) {
                maxValue = value;
            }
        }

        // Create any range or bitmask matcher which fits these stats:
        return createRangeOrBitmaskMatchers(minValue, maxValue, setSize, bit0Counts, bit1Counts, matchInverse);
    }

    private ByteMatcher createInvertedRangeOrBitmaskMatchers(final Set<Byte> bytes, boolean matchInverse) {
        // Set some useful constants
        final int setSize = 256 - bytes.size();
        final boolean isPowerTwoSize = MathUtils.isPowerOfTwo(setSize);
        //TODO: don't need this if we aren't doing wildbitany.
        final boolean isInversePowerTwoSize = MathUtils.isPowerOfTwo(256-setSize);
        final boolean needToCountBits = isPowerTwoSize | isInversePowerTwoSize;

        // Initialize the variables we need to process the set of bytes.
        int minValue = 255;
        int maxValue = 0;
        final int[] bit0Counts, bit1Counts;
        if (needToCountBits) { // If we might have a bitmask, count the bits as we go.
            bit0Counts = new int[8]; // a count of how many zero bits we have.
            bit1Counts = new int[8]; // a count of how many one bits we have.
        } else {
            bit0Counts = EMPTY_ARRAY;
            bit1Counts = EMPTY_ARRAY;
        }

        // Find the minimum and maximum byte values in the set, and a count of each bit position.
        for (int value = 0, numFound = 0; numFound < setSize && value < 256; value++) {
            if (!bytes.contains(Byte.valueOf((byte) value))) {
                numFound++;
                if (needToCountBits) { // count the bits if we have a power of two, or inverse power two size.
                    addBitCounts(value, bit0Counts, bit1Counts);
                }
                if (value < minValue) {
                    minValue = value;
                }
                if (value > maxValue) {
                    maxValue = value;
                }
            }
        }

        // Return any range or bitmask matchers that match these stats (or null if they don't).
        return createRangeOrBitmaskMatchers(minValue, maxValue, setSize, bit0Counts, bit1Counts, !matchInverse);
    }

    private void addBitCounts(final int value, final int[] bit0Counts, final int[] bit1Counts) {
        for (int bit = 1, bitIndex = 0; bit < 256; bit <<= 1, bitIndex++) {
            if ((value & bit) == bit) {
                bit1Counts[bitIndex]++;
            } else {
                bit0Counts[bitIndex]++;
            }
        }
    }

    private ByteMatcher createRangeOrBitmaskMatchers(final int minValue, final int maxValue, final int setSize,
                                                     final int[] bit0Counts, final int[] bit1Counts,
                                                     final boolean matchInverse) {
        // Test for whether the set as provided has a range of bytes in it:
        // If the number of bytes between the max and min is the same as the entire size, then it's a range.
        if (maxValue - minValue + 1 == setSize) {
            return new ByteRangeMatcher(minValue, maxValue, matchInverse);
        }

        final boolean isPowerTwoSize = MathUtils.isPowerOfTwo(setSize);
        final boolean isInversePowerTwoSize = MathUtils.isPowerOfTwo(256-setSize);


        final int halfSetSize = setSize >> 1;
        // Test for whether a bitmask and a WildBitMatcher could match this set of bytes:
        // All WildBitMask matchers match a power of two number of bytes
        // (each wildbit we don't care about gives two more possibilities to match).
        if (isPowerTwoSize) {

            // Bits which have a count of setSize had that bit set permanently in the set, so it's a value bit.
            // Bits which have an equal count of zero and one at half the set size each have all combinations with both values, so it's a don't care bit.
            int mask  = 0xFF;
            int value = 0x00;
            for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
                final int bit0Count = bit0Counts[bitIndex];
                final int bit1Count = bit1Counts[bitIndex];

                // We have a don't care bit - zero and one are both present at half the set size.
                if (bit0Count == bit1Count && bit0Count == halfSetSize) {
                    mask &= ~(1 << bitIndex); // unset the bit in the mask
                } else if (bit1Count == setSize) { // If all the bytes had this bit set to one:
                    value |= (1 << bitIndex); // set the bit in value to the same as the bitIndex.
                } else if (bit0Count != setSize) { // If all the bytes do not have this bit as zero:
                    // If at this point, we don't have a bit0count of setSize, then this can't be a valid bitmask.
                    // All bits will either be at the set size, or have zero/one in equal numbers of half the set size.
                    // If they don't it can't be a valid wildbitmatch.
                    mask = 0xFF; // Set the mask to a "no wildbits mask" and exit the loop.
                    break;
                } // If we did have a valid bit0Count, there's nothing to do, as the value is already set to zer0 for that position.
            }

            // If we exit the loop with some wildbits,
            //   Then any non-wild bits are the same for all bytes (as the loop tests that this is the case).
            // If also the size of such a wildbit set is the same as the set we have,
            //   Then we have a valid wild bit matcher:
            if (mask != 0xFF && (1 << ByteUtils.countUnsetBits((byte) mask)) == setSize) {
                return new WildBitMatcher((byte) value, (byte) mask, matchInverse);
            }
        }

        return null;
    }

    private ByteMatcher createSizeMatchers(final Set<Byte> bytes, boolean matchInverse) {
        if (matchInverse) {
            switch (bytes.size()) {
                case 0: {
                    return AnyByteMatcher.ANY_BYTE_MATCHER;
                }
                case 1: {
                    return OneByteInvertedMatcher.valueOf(bytes.iterator().next());
                }
                case 2: {
                    final Iterator<Byte> iterator = bytes.iterator();
                    return TwoByteInvertedMatcher.valueOf(iterator.next(), iterator.next());
                }
                case 254: {
                    final byte[] twobytes = getTwoBytesNotInSet(bytes); //TODO: put in ByteUtils?
                    return TwoByteMatcher.valueOf(twobytes[0], twobytes[1]);
                }
                case 255: {
                    return OneByteMatcher.valueOf(getByteNotInSet(bytes)); //TODO: put in ByteUtils?
                }
                case 256: { //TODO: matches nothing - throw exception instead?
                    return new AnyBitmaskMatcher((byte) 0, false);
                }
            }
        } else {
            switch (bytes.size()) {
                case 0: { //TODO: matches nothing - throw exception instead?
                    return new AnyBitmaskMatcher((byte) 0, false);
                }
                case 1: {
                    return OneByteMatcher.valueOf(bytes.iterator().next());
                }
                case 2: {
                    final Iterator<Byte> iterator = bytes.iterator();
                    return TwoByteMatcher.valueOf(iterator.next(), iterator.next());
                }
                case 254: {
                    final byte[] twobytes = getTwoBytesNotInSet(bytes); //TODO: put in ByteUtils?
                    return TwoByteInvertedMatcher.valueOf(twobytes[0], twobytes[1]);
                }
                case 255: {
                    return OneByteInvertedMatcher.valueOf(getByteNotInSet(bytes)); //TODO: put in ByteUtils?
                }
                case 256: {
                    return AnyByteMatcher.ANY_BYTE_MATCHER;
                }
            }
        }
        return null;
    }

    private byte[] getTwoBytesNotInSet(final Set<Byte> values) {
        final byte[] results = new byte[2];
        for (int byteValue = 0, resultIndex = 0; byteValue < 256 && resultIndex < 2; byteValue++) {
            final Byte theByte = Byte.valueOf((byte) (byteValue & 0xFF));
            if (!values.contains(theByte)) {
                results[resultIndex++] = theByte.byteValue();
            }
        }
        return results;
    }

    private byte getByteNotInSet(final Set<Byte> values) {
        for (int byteValue = 0; byteValue < 256; byteValue++) {
            if (!values.contains((byte) (byteValue & 0xFF))) {
                return (byte) (byteValue & 0xFF);
            }
        }
        throw new RuntimeException("Must always be able to find a byte not in the set.");
    }

}
