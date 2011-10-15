/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 *  
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.domesdaybook.matcher.sequence;

import java.io.IOException;
import net.domesdaybook.matcher.singlebyte.CaseInsensitiveByteMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;

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
        final StringBuilder builder = new StringBuilder();
        for (int count = 0; count < numberToRepeat; count++) {
            builder.append(stringToRepeat);
        }
        return builder.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int length() {
        return length;
    }

    
     /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
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
    public boolean matches(final Reader reader, final long matchFrom) throws IOException {
        final int localLength = length;
        final SingleByteMatcher[] matchList = charMatchList;   
        Window window = reader.getWindow(matchFrom);
        int checkPos = 0;
        while (window != null) {
            final int offset = reader.getWindowOffset(matchFrom + checkPos);
            final int endPos = Math.min(window.getLimit(), offset + localLength - checkPos);
            final byte[] array = window.getArray();
            for (int windowPos = offset; windowPos < endPos; windowPos++) {
                final SingleByteMatcher byteMatcher = matchList[checkPos++];
                if (!byteMatcher.matches(array[windowPos])) {
                    return false;
                }
            }
            if (checkPos == localLength) {
                return true;
            } else {
                window = reader.getWindow(matchFrom + checkPos);
            }
        }
        return false;
    }   
    

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
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
    public SingleByteMatcher getByteMatcherForPosition(final int position) {
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
    private SingleByteMatcher getByteMatcherForChar(final char theChar) {
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
