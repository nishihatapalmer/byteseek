/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.matcher.sequence.Utilities;


/**
 *
 * @author matt
 */
public final class ByteMatcher implements SingleByteMatcher {

    private final byte byteToMatch;

    
    public ByteMatcher(final byte value) {
        byteToMatch = value;
    }


    @Override
    public final boolean matchesByte(final byte theByte) {
        return theByte == byteToMatch;
    }

    
    @Override
    public final byte[] getMatchingBytes() {
        return new byte[] {byteToMatch};
    }


    @Override
    public final String toRegularExpression(boolean prettyPrint) {
        final String regex = Utilities.bytesToString(prettyPrint, getMatchingBytes());
        return prettyPrint? regex + " " : regex;
    }

}
