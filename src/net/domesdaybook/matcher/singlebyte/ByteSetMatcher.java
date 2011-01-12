/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.domesdaybook.reader.ByteReader;

/**
 * A ByteSetMatcher is a {@link SingleByteMatcher  which
 * matches an arbitrary set of bytes.
 *
 * It uses a BitSet as the underlying representation of the set of bytes,
 * so is not memory efficient for small numbers of sets of bytes.
 *
 * <p>Use the static {@code buildOptimalMatcher()} factory method to
 * construct a more memory efficient matcher where possible.
 *
 * @author Matt Palmer
 */
public final class ByteSetMatcher extends InvertibleMatcher implements SingleByteMatcher {

    private static final String ILLEGAL_ARGUMENTS = "Null or empty Byte set passed in to ByteSetMatcher.";
    private static final int BINARY_SEARCH_THRESHOLD = 16;

    private final BitSet byteValues = new BitSet(256);


    /**
     * Builds an optimal matcher from a set of bytes.
     *
     * <p>If the set is a single, non-inverted byte, then a {@link ByteMatcher}
     * is returned. If the values lie in a contiguous range, then a
     * {@link ByteSetRangeMatcher} is returned.  If the number of bytes in the
     * set are below a threshold value (16), then a {@link ByteSetBinarySearchMatcher}
     * is returned, otherwise a {@link ByteSetMatcher} is returned.
     * 
     * @param setValues The set of byte values to match.
     * @param inverted   Whether the set values are inverted or not
     * @return A SingleByteMatcher which is optimal for that set of bytes.
     * @throws {@link IllegalArgumentException} if the set is null or empty.
     */
    public static SingleByteMatcher buildOptimalMatcher(final Set<Byte> setValues, final boolean inverted) {
        if (setValues == null || setValues.isEmpty()) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENTS);
        }
        SingleByteMatcher result = null;
        final int numberOfValues = setValues.size();
        if (numberOfValues == 1 && !inverted) {
            for (Byte byteToMatch : setValues) {
                result = new ByteMatcher(byteToMatch);
            }
        } else if (numberOfValues == 255 && inverted) {
            for (byte byteValue = Byte.MIN_VALUE; byteValue < Byte.MAX_VALUE; byteValue++) {
                if (!setValues.contains(byteValue)) {
                    result = new ByteMatcher(byteValue);
                    break;
                }
            }
        } else if (numberOfValues > 0) {

            // Determine if all the values lie in a single range:
            final List<Byte> bytes = new ArrayList<Byte>(setValues);
            Collections.sort(bytes);
            final int lastValuePosition = numberOfValues - 1;
            final int firstValue = bytes.get(0);
            final int lastValue = bytes.get(lastValuePosition);

            // Construct an optimal byte set matcher:
            if (lastValue - firstValue == lastValuePosition) {  // values lie in a contiguous range
                result = new ByteSetRangeMatcher(firstValue, lastValue, inverted);
            } else  // values do not lie in a contiguous range.
            if (bytes.size() < BINARY_SEARCH_THRESHOLD) { // small number of bytes in set - use binary searcher:
                result = new ByteSetBinarySearchMatcher(setValues, inverted);
            } else {
                result = new ByteSetMatcher(setValues, inverted);
            }
        }
        return result;
    }


    /**
     * Constructs a ByteSetMatcher from a set of bytes.
     *
     * @param values A set of bytes
     * @param inverted Whether matching is on the set of bytes or their inverse.
     * @throws {@link IllegalArgumentException} if the set is null or empty.
     */
    public ByteSetMatcher(final Set<Byte> values, final boolean inverted) {
        super(inverted);
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENTS);
        }
        for (Byte b : values) {
            byteValues.set((int) b & 0xFF);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean matches(final ByteReader reader, final long matchFrom) {
        return matches(reader.readByte(matchFrom));
    }  


    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean matches(final byte theByte) {
        return byteValues.get((int) theByte & 0xFF) ^ inverted;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final String toRegularExpression(final boolean prettyPrint) {
        StringBuilder regularExpression = new StringBuilder();
        if ( prettyPrint ) {
            regularExpression.append(' ');
        }
        regularExpression.append("[");
        if ( inverted ) {
            regularExpression.append("^");
        }
        int firstBitSetPosition = byteValues.nextSetBit(0);
        while ( firstBitSetPosition >= 0 && firstBitSetPosition < 256 ) {
            int lastBitSetPosition = byteValues.nextClearBit(firstBitSetPosition)-1;
            // If the next clear position doesn't exist, then all remaining values are set:
            if ( lastBitSetPosition < 0 ) {
                lastBitSetPosition = 255;
            }
            // If we have a range of more than 2 contiguous set positions,
            // represent this as a range of values:
            if ( lastBitSetPosition - firstBitSetPosition > 2 ) {
                final String minValue = ByteUtilities.byteToString(prettyPrint, firstBitSetPosition);
                final String maxValue = ByteUtilities.byteToString(prettyPrint, lastBitSetPosition);
                regularExpression.append( String.format("%s-%s", minValue, maxValue));
            } else { // less than 2 contiguous set positions - just write out a single byte:
                final String byteVal = ByteUtilities.byteToString(prettyPrint, firstBitSetPosition);
                regularExpression.append( byteVal );
                lastBitSetPosition = firstBitSetPosition;
            }
            firstBitSetPosition = byteValues.nextSetBit(lastBitSetPosition+1);
        }
        regularExpression.append("]");
        if ( prettyPrint ) {
            regularExpression.append(' ');
        }
        return regularExpression.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] getMatchingBytes() {
        byte[] values = new byte[getNumberOfMatchingBytes()];
        int byteIndex = 0;
        for (int value = 0; value < 256; value++) {
            if (byteValues.get(value) ^ inverted) {
                values[byteIndex++] = (byte) value;
            }
        }
        return values;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final int getNumberOfMatchingBytes() {
        return inverted ? 256 - byteValues.cardinality() : byteValues.cardinality();
    }

}
