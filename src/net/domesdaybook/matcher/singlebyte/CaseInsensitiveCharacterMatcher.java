/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Bytes;

/**
 *
 * @author matt
 */
public final class CaseInsensitiveCharacterMatcher implements SingleByteMatcher {

    private final Character value;
    private final byte[] caseValues;


    public CaseInsensitiveCharacterMatcher(Character value) {
        this.value = value;
        caseValues = new byte[2];
        caseValues[0] = (byte) Character.toLowerCase(value);
        caseValues[1] = (byte) Character.toUpperCase(value);
    }
    

    @Override
    public final boolean matchesByte(final byte theByte) {
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


}
