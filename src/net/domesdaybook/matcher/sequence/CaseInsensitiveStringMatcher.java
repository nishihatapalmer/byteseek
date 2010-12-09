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

    private int stringLength;
    private String caseInsensitiveString;
    private SequenceMatcher[] charMatchList;

    public CaseInsensitiveStringMatcher(final String caseInsensitiveASCIIString) {
        caseInsensitiveString = caseInsensitiveASCIIString;
        charMatchList = new SequenceMatcher[caseInsensitiveASCIIString.length()];
        stringLength = caseInsensitiveASCIIString.length();
        for ( int charIndex = 0; charIndex < stringLength; charIndex++) {
            charMatchList[charIndex] = getByteMatcherForChar(caseInsensitiveASCIIString.charAt(charIndex));
        }
    }


    @Override
    public final int length() {
        return stringLength;
    }

    
    private SequenceMatcher getByteMatcherForChar(char theChar) {
        SequenceMatcher result;
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
    public boolean matchesBytes(Bytes reader, long matchFrom) {
        boolean result = true;
        final SequenceMatcher[] matchList = charMatchList;
        final int localStop = stringLength;
        for ( int byteIndex = 0; result && byteIndex < localStop; byteIndex++) {
            final SequenceMatcher charMatcher = matchList[byteIndex];
            result = charMatcher.matchesBytes(reader, matchFrom + byteIndex);
        }
        return result;
    }


    @Override
    public SingleByteMatcher getByteMatcherForPosition(int position) {
        return (SingleByteMatcher) charMatchList[position];
    }

}
