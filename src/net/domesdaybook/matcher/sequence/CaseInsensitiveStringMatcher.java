/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.singlebyte.CaseInsensitiveByteMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 * An immutable class which matches an ASCII string case insensitively.
 * 
 * @author Matt Palmer
 */
public final class CaseInsensitiveStringMatcher implements SequenceMatcher {

    private final int length;
    private final String caseInsensitiveString;
    private final SingleByteMatcher[] charMatchList;


    /**
     * Constructs an immutable CaseSensitiveStringMatcher from an ASCII string.
     *
     * @param caseInsensitiveASCIIString The string to match.
     */
    public CaseInsensitiveStringMatcher(final String caseInsensitiveASCIIString) {
        this(caseInsensitiveASCIIString, 1);
    }



    /**
     * Constructs an immutable CaseSensitiveStringMatcher from a repeated
     * number of ASCII strings.
     *
     * @param caseInsensitiveASCIIString The (repeatable) string to match.
     * @param numberToRepeat The number of repeats.
     */
    public CaseInsensitiveStringMatcher(final String caseInsensitiveASCIIString, final int numberToRepeat) {
        if (caseInsensitiveASCIIString == null || caseInsensitiveASCIIString.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string passed in to CaseInsensitiveStringMatcher.");
        }
        caseInsensitiveString = repeatString(caseInsensitiveASCIIString, numberToRepeat);
        length = caseInsensitiveString.length();
        charMatchList = new SingleByteMatcher[length];
        for (int charIndex = 0; charIndex < length; charIndex++) {
            charMatchList[charIndex] = getByteMatcherForChar(caseInsensitiveString.charAt(charIndex));
        }
    }


    private String repeatString(final String stringToRepeat, final int numberToRepeat) {
        if (numberToRepeat == 1) {
            return stringToRepeat;
        }
        StringBuilder builder = new StringBuilder();
        for (int count = 0; count < numberToRepeat; count++) {
            builder.append(stringToRepeat);
        }
        return builder.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final int length() {
        return length;
    }

    
     /**
     * {@inheritDoc}
     */
    @Override
    public final String toRegularExpression(final boolean prettyPrint) {
        if (prettyPrint) {
            return " `" + caseInsensitiveString + "` ";
        }
        return "`" + caseInsensitiveString + "`";
    }

    
    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public final boolean matches(final ByteReader reader, final long matchFrom) {
        final int localLength = length;        
        if (matchFrom + localLength < reader.length() && matchFrom >= 0) {
            final SingleByteMatcher[] matchList = charMatchList;
            for (int byteIndex = 0; byteIndex < localLength; byteIndex++) {
                final SingleByteMatcher charMatcher = matchList[byteIndex];
                final byte theByte = reader.readByte(matchFrom + byteIndex);
                if (!charMatcher.matches(theByte)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public final boolean matches(final byte[] bytes, final int matchFrom) {
        final int localLength = length;
        if (matchFrom + localLength < bytes.length && matchFrom >= 0) {
            final SingleByteMatcher[] matchList = charMatchList;
            for (int byteIndex = 0; byteIndex < localLength; byteIndex++) {
                final SingleByteMatcher charMatcher = matchList[byteIndex];
                final byte theByte = bytes[matchFrom + byteIndex];
                if (!charMatcher.matches(theByte)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }   
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final ByteReader reader, final long matchFrom) {
        final int localLength = length;        
        final SingleByteMatcher[] matchList = charMatchList;
        for (int byteIndex = 0; byteIndex < localLength; byteIndex++) {
            final SingleByteMatcher charMatcher = matchList[byteIndex];
            final byte theByte = reader.readByte(matchFrom + byteIndex);
            if (!charMatcher.matches(theByte)) {
                return false;
            }
        }
        return true;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchFrom) {
        final int localLength = length;
        final SingleByteMatcher[] matchList = charMatchList;
        for (int byteIndex = 0; byteIndex < localLength; byteIndex++) {
            final SingleByteMatcher charMatcher = matchList[byteIndex];
            final byte theByte = bytes[matchFrom + byteIndex];
            if (!charMatcher.matches(theByte)) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final SingleByteMatcher getByteMatcherForPosition(int position) {
        return charMatchList[position];
    }


    /**
     *
     * @return The string which is matched by this class.
     */
    public String getCaseInsensitiveString() {
        return caseInsensitiveString;
    }
    

    /**
     * Returns a ByteMatcher for bytes which are not alphabetic characters,
     * and a CaseInsensitiveByteMatcher for alphabetic characters.
     * 
     * @param theChar the character to get a byte matcher for.
     * @return A SingleByteMatcher optimised for the character.
     */
    private SingleByteMatcher getByteMatcherForChar(char theChar) {
        SingleByteMatcher result;
        if ((theChar >= 'a' && theChar <= 'z') ||
            (theChar >= 'A' && theChar <= 'Z')) {
            result = new CaseInsensitiveByteMatcher(theChar);
        } else {
            result = new ByteMatcher((byte) theChar);
        }
        return result;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public CaseInsensitiveStringMatcher reverse() {
        final String reversed = new StringBuffer(caseInsensitiveString).reverse().toString();
        return new CaseInsensitiveStringMatcher(reversed);
    }


}
