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
public final class CaseInsensitiveByteMatcher implements SingleByteMatcher {

    private final Character value;
    private final byte[] caseValues;


    public CaseInsensitiveByteMatcher(Character value) {
        this.value = value;
        caseValues = new byte[2];
        caseValues[0] = (byte) Character.toLowerCase(value);
        caseValues[1] = (byte) Character.toUpperCase(value);
    }


    @Override
    public final boolean matches(final ByteReader reader, long matchPosition) {
        return matches(reader.readByte(matchPosition));
    }


    @Override
    public final boolean matches(final byte theByte) {
        return (theByte == caseValues[0] || theByte == caseValues[1]);
    }


    @Override
    public final byte[] getMatchingBytes() {
        return caseValues;
    }


    @Override
    public final String toRegularExpression(final boolean prettyPrint) {
        return prettyPrint? " `" + value.toString() + "` " : '`' + value.toString() + '`';
    }

    
    @Override
    public final int getNumberOfMatchingBytes() {
        return caseValues[0] == caseValues[1] ? 1 : 2;
    }


}
