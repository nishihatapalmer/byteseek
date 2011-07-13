/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public class InvertedByteMatcher implements SingleByteMatcher {

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
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final ByteReader reader, final long matchFrom) {
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
        for (byte byteToMatch = Byte.MIN_VALUE; byteToMatch <= Byte.MAX_VALUE; byteToMatch++) {
            if (matches(byteToMatch)) {
                matchingBytes[byteIndex++]=byteToMatch;
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
