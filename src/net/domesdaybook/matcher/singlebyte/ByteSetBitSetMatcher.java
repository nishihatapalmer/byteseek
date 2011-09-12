/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.bytes.ByteUtilities;
import java.util.BitSet;
import java.util.Set;
import net.domesdaybook.reader.Reader;

/**
 * A ByteSetBitSetMatcher is a {@link SingleByteMatcher  which
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
public final class ByteSetBitSetMatcher extends InvertibleMatcher {

    private static final String ILLEGAL_ARGUMENTS = "Null or empty Byte set passed in to ByteSetMatcher.";
    private final BitSet byteValues = new BitSet(256);

    /**
     * Constructs a ByteSetBitSetMatcher from a set of bytes.
     *
     * @param values A set of bytes
     * @param inverted Whether matching is on the set of bytes or their inverse.
     * @throws {@link IllegalArgumentException} if the set is null or empty.
     */
    public ByteSetBitSetMatcher(final Set<Byte> values, final boolean inverted) {
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
    public boolean matches(final Reader reader, final long matchFrom) {
        return (matchFrom >= 0 && matchFrom < reader.length()) &&
                (byteValues.get((int) reader.readByte(matchFrom) & 0xFF) ^ inverted);
    }  


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
        return (matchFrom >= 0 && matchFrom < bytes.length) &&
                (byteValues.get((int) bytes[matchFrom] & 0xFF) ^ inverted);
    }  
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final Reader reader, final long matchFrom) {
        return byteValues.get((int) reader.readByte(matchFrom) & 0xFF) ^ inverted;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchFrom) {
        return byteValues.get((int) bytes[matchFrom] & 0xFF) ^ inverted;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        return byteValues.get((int) theByte & 0xFF) ^ inverted;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
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
            // If we have a range of more than 1 contiguous set positions,
            // represent this as a range of values:
            if ( lastBitSetPosition - firstBitSetPosition > 1 ) {
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
    public byte[] getMatchingBytes() {
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
    public int getNumberOfMatchingBytes() {
        return inverted ? 256 - byteValues.cardinality() : byteValues.cardinality();
    }

}
