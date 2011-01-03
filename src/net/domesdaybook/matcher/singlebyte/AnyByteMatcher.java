/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author Matt Palmer
 */
public class AnyByteMatcher implements SingleByteMatcher {

    public AnyByteMatcher() {
    }


    @Override
    public final boolean matches(byte theByte) {
        return true;
    }


    @Override
    public final byte[] getMatchingBytes() {
        byte[] bytes = new byte[256];
        for (int count = 255; count >= 0; count--) {
            bytes[count] = (byte) count;
        }
        return bytes;
    }


    @Override
    public final String toRegularExpression(boolean prettyPrint) {
        return prettyPrint ? " . " : ".";
    }


    @Override
    public final boolean matches(ByteReader reader, long matchFrom) {
        return matches(reader.readByte(matchFrom));
    }


    @Override
    public int getNumberOfMatchingBytes() {
        return 256;
    }

}
