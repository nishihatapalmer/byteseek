/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.reader.ByteReader;

/**
 * An immutable {@link SingleByteMatcher} which matches ASCII bytes case insensitively.
 *
 * <p>It will only work for ASCII characters in the range 0 - 127.
 * Other Unicode characters will not work, as all of the SingleByteMatcher
 * classes work at the byte level, so cannot deal with multi-byte characters.
 *
 * @author Matt Palmer
 */
public final class CaseInsensitiveByteMatcher implements SingleByteMatcher {

    private final static String ILLEGAL_ARGUMENTS = "Non-ASCII char passed in to CaseInsensitiveByteMatcher: %s";

    private final Character value;
    private final byte[] caseValues;


    /**
     * Constructs a CaseInsensitiveByteMatcher from the character provided.
     *
     * @param asciiChar The ASCII character to match in a case insensitive way.
     * @throws {@link IllegalArgumentException} if the character is not ASCII.
     *
     */
    public CaseInsensitiveByteMatcher(Character asciiChar) {
        // Precondition: must be an ASCII char:
        final long val = (long) asciiChar;
        if (val > 127 || val < 0) {
            final String message = String.format(ILLEGAL_ARGUMENTS, asciiChar);
            throw new IllegalArgumentException(message);
        }
        this.value = asciiChar;
        caseValues = new byte[2];
        caseValues[0] = (byte) Character.toLowerCase(asciiChar);
        caseValues[1] = (byte) Character.toUpperCase(asciiChar);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final ByteReader reader, final long matchPosition) {
        final byte theByte = reader.readByte(matchPosition);
        return (theByte == caseValues[0] || theByte == caseValues[1]);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        final byte theByte = bytes[matchPosition];
        return (theByte == caseValues[0] || theByte == caseValues[1]);
    }    

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        return (theByte == caseValues[0] || theByte == caseValues[1]);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        final byte firstByte = caseValues[0];
        final byte secondByte = caseValues[1];
        if (firstByte == secondByte) {
            final byte[] singleValue = new byte[1];
            singleValue[0] = firstByte;
            return singleValue;
        } else {
            return caseValues;
        }
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        return prettyPrint? " `" + value.toString() + "` " : '`' + value.toString() + '`';
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return caseValues[0] == caseValues[1] ? 1 : 2;
    }


}
