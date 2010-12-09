/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.matcher.sequence.Utilities;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Bytes;


/**
 *
 * @author matt
 */
public class ByteMatcher implements SequenceMatcher, SingleByteMatcher {

    byte byteToMatch;

    public ByteMatcher(byte value) {
        byteToMatch = value;
    }

    @Override
    public boolean matchesBytes(Bytes reader, long matchFrom) {
        return reader.getByte(matchFrom) == byteToMatch;
    }

    @Override
    public boolean matchesByte(byte theByte) {
        return theByte == byteToMatch;
    }

    @Override
    public byte[] getMatchingBytes() {
        byte[] value = new byte[1];
        value[0] = byteToMatch;
        return value;
    }

    @Override
    public SingleByteMatcher getByteMatcherForPosition(int position) {
        return this;
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public String toRegularExpression(boolean prettyPrint) {
        final String regex = Utilities.bytesToString(prettyPrint, getMatchingBytes());
        return prettyPrint? regex + " " : regex;
    }



}
