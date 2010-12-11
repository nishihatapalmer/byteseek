/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.singlebyte.CaseInsensitiveCharacterMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.reader.Bytes;

/**
 *
 * @author matt
 */
public class CaseInsensitiveStringMatcher implements SequenceMatcher {

    private final int length;
    private final String caseInsensitiveString;
    private final SingleByteMatcher[] charMatchList;

    
    public CaseInsensitiveStringMatcher(final String caseInsensitiveASCIIString) {
        caseInsensitiveString = caseInsensitiveASCIIString;
        length = caseInsensitiveASCIIString.length();
        charMatchList = new SingleByteMatcher[length];
        for (int charIndex = 0; charIndex < length; charIndex++) {
            charMatchList[charIndex] = getByteMatcherForChar(caseInsensitiveASCIIString.charAt(charIndex));
        }
    }


    @Override
    public final int length() {
        return length;
    }

    
    private final SingleByteMatcher getByteMatcherForChar(char theChar) {
        SingleByteMatcher result;
        if ((theChar >= 'a' && theChar <= 'z') ||
            (theChar >= 'A' && theChar <= 'Z')) {
            result = new CaseInsensitiveCharacterMatcher(theChar);
        } else {
            result = new ByteMatcher((byte) theChar);
        }
        return result;
    }
    

    @Override
    public final String toRegularExpression( final boolean prettyPrint ) {
        if (prettyPrint) {
            return " `" + caseInsensitiveString + "` ";
        }
        return "`" + caseInsensitiveString + "`";
    }

    
    @Override
    public final boolean matches(Bytes reader, long matchFrom) {
        boolean result = true;
        final SingleByteMatcher[] matchList = charMatchList;
        final int localStop = length;
        for ( int byteIndex = 0; result && byteIndex < localStop; byteIndex++) {
            final SingleByteMatcher charMatcher = matchList[byteIndex];
            final byte theByte = reader.getByte(matchFrom + byteIndex);
            result = charMatcher.matches(theByte);
        }
        return result;
    }


    @Override
    public final SingleByteMatcher getByteMatcherForPosition(int position) {
        return (SingleByteMatcher) charMatchList[position];
    }

}
