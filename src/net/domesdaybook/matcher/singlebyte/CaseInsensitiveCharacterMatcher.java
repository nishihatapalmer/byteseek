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
public class CaseInsensitiveCharacterMatcher implements SequenceMatcher, SingleByteMatcher {

    Character value;
    byte[] caseValues;
    
    public CaseInsensitiveCharacterMatcher(Character value) {
        this.value = value;
        caseValues = new byte[2];
        caseValues[0] = (byte) Character.toLowerCase(value);
        caseValues[1] = (byte) Character.toUpperCase(value);
    }
    
    @Override
    public boolean matchesBytes(Bytes reader, long matchFrom) {
        final byte theByte = reader.getByte(matchFrom);
        return (theByte == caseValues[0] || theByte == caseValues[1]);
    }

    @Override
    public boolean matchesByte(byte theByte) {
        return (theByte == caseValues[0] || theByte == caseValues[1]);
    }


    @Override
    public byte[] getMatchingBytes() {
        return caseValues;
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
        return prettyPrint? " `" + value.toString() + "` " : '`' + value.toString() + '`';
    }


}
