/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import java.util.Arrays;
import java.util.Set;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public class ByteSetBinarySearchMatcher extends NegatableMatcher implements SingleByteMatcher {

    private byte[] bytes;

    
    public ByteSetBinarySearchMatcher(final Set<Byte> bytes, final boolean negated) {
        super(negated);
        this.bytes = new byte[bytes.size()];
        int byteIndex = 0;
        for (Byte b : bytes) {
            this.bytes[byteIndex++] = b;
        }
        Arrays.sort(this.bytes);
    }

    @Override
    public final boolean matches(byte theByte) {
        return Arrays.binarySearch(bytes, theByte) >= 0 ^ negated;
    }


    @Override
    public final byte[] getMatchingBytes() {
        return bytes;
    }


    @Override
    public final int getNumberOfMatchingBytes() {
        return bytes.length;
    }


    @Override
    public final String toRegularExpression(final boolean prettyPrint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public final boolean matches(final ByteReader reader, final long matchFrom) {
        return matches(reader.getByte(matchFrom));
    }

}
