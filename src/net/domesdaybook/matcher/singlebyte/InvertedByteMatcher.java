/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.io.IOException;
import net.domesdaybook.bytes.ByteUtilities;
import net.domesdaybook.reader.Reader;

/**
 *
 * @author matt
 */
public class InvertedByteMatcher extends AbstractSingleByteSequence {

    private final byte byteToMiss;


    /**
     * Constructs an immutable InvertedByteMatcher.
     *
     * @param byteToMiss The only byte not to match.
     */
    public InvertedByteMatcher(final byte byteToMiss) {
        this.byteToMiss = byteToMiss;
    }
    
    
    /**
     * Constructs an immutable InvertedByteMatcher from a hex representation of a byte.
     * 
     * @param hexByte 
     * @throws IllegalArgumentException if the string is not a valid 2-digit hex byte.
     */
    public InvertedByteMatcher(final String hexByte) {
        this.byteToMiss = ByteUtilities.byteFromHex(hexByte);
    }    


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final Reader reader, final long matchFrom)
            throws IOException{
        return matchFrom >= 0 && matchFrom < reader.length() &&
                reader.readByte(matchFrom) != byteToMiss;
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
        return matchFrom >= 0 && matchFrom < bytes.length &&
                bytes[matchFrom] != byteToMiss;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final Reader reader, final long matchFrom) 
            throws IOException{
        return reader.readByte(matchFrom) != byteToMiss;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchFrom) {
        return bytes[matchFrom] != byteToMiss;
    }    

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        return theByte != byteToMiss;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        byte[] matchingBytes = new byte[255];
        int byteIndex = 0;
        for (int byteValue = 0; byteValue < 256; byteValue++) {
            final byte theByte = (byte) byteValue;
            if (theByte != byteToMiss) {
                matchingBytes[byteIndex++] = theByte;
            }
        }
        return matchingBytes;
    }


     /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        StringBuilder builder = new StringBuilder();
        builder.append(prettyPrint? " [^ " : "[^");
        builder.append(ByteUtilities.byteToString(prettyPrint, byteToMiss & 0xFF));
        builder.append(prettyPrint? " ] " : "]");
        return builder.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return 255;
    }


}
