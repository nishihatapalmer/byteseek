/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.io.IOException;
import net.domesdaybook.reader.Reader;

/**
 * An immutable {@link SingleByteMatcher} which matches ASCII bytes case insensitively.
 *
 * <p>It will only work for ASCII characters in the range 0 - 127.
 * Other Unicode characters will not work, as all of the SingleByteMatcher
 * classes work at the byte level, so cannot deal with multi-byte characters.
 *
 * @author Matt Palmer
 */
public final class CaseInsensitiveByteMatcher extends AbstractSingleByteSequence {

    private final static String ILLEGAL_ARGUMENTS = "Non-ASCII char passed in to CaseInsensitiveByteMatcher: %s";

    private final char value;
    private final byte[] caseValues;


    /**
     * Constructs a CaseInsensitiveByteMatcher from the character provided.
     *
     * @param asciiChar The ASCII character to match in a case insensitive way.
     * @throws {@link IllegalArgumentException} if the character is not ASCII.
     *
     */
    public CaseInsensitiveByteMatcher(final char asciiChar) {
        // Precondition: must be an ASCII char:
        if (asciiChar > 127 || asciiChar < 0) {
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
    public boolean matches(final Reader reader, final long matchPosition) 
            throws IOException{
        if (matchPosition >= 0 && matchPosition < reader.length()) {
            final byte theByte = reader.readByte(matchPosition);
            return (theByte == caseValues[0] || theByte == caseValues[1]);
        }
        return false;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        if (matchPosition >= 0 && matchPosition < bytes.length) {
            final byte theByte = bytes[matchPosition];
            return (theByte == caseValues[0] || theByte == caseValues[1]);
        }
        return false;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final Reader reader, final long matchPosition) 
            throws IOException{
        final byte theByte = reader.readByte(matchPosition);
        return (theByte == caseValues[0] || theByte == caseValues[1]);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
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
        return prettyPrint? " `" + Character.toString(value) + "` " : '`'
                + Character.toString(value) + '`';
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return caseValues[0] == caseValues[1] ? 1 : 2;
    }


}
