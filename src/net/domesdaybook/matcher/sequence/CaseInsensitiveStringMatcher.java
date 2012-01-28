/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
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
 */

package net.domesdaybook.matcher.sequence;

import java.io.IOException;
import java.util.Arrays;
import net.domesdaybook.matcher.bytes.CaseInsensitiveByteMatcher;
import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.matcher.bytes.OneByteMatcher;
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
    private final ByteMatcher[] charMatchList;


    /**
     * Constructs an immutable CaseSensitiveStringMatcher from an ASCII string.
     *
     * @param caseInsensitiveASCIIString The string to match.
     */
    public CaseInsensitiveStringMatcher(final String caseInsensitiveASCIIString) {
        this(caseInsensitiveASCIIString, 1);
    }

    
    public CaseInsensitiveStringMatcher(final CaseInsensitiveByteMatcher matcher) {
        this(matcher, 1);
    }
    
    
    /**
     * Constructs an immutable CaseInsensitiveStringMatcher from a repeated number
     * of CaseInsensitiveByteMatchers.
     * 
     * @param matcher The CaseInsensitiveByteMatcher to build this matcher from.
     * @param numberOfRepeats The number of times to repeat the matcher.
     */
    public CaseInsensitiveStringMatcher(final CaseInsensitiveByteMatcher matcher, final int numberOfRepeats) {
        if (matcher == null) {
            throw new IllegalArgumentException("Null matcher passed in.");
        }
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("Number of repeats must be at least one.");
        }
        length = numberOfRepeats;
        caseInsensitiveString = repeat(matcher, length);
        charMatchList = new ByteMatcher[length];
        for (int charIndex = 0; charIndex < length; charIndex++) {
            charMatchList[charIndex] = matcher;
        }
    }
    

    /**
     * Constructs an immutable CaseInsensitiveStringMatcher from a repeated
     * number of ASCII strings.
     *
     * @param caseInsensitiveASCIIString The (repeatable) string to match.
     * @param numberToRepeat The number of repeats.
     */
    public CaseInsensitiveStringMatcher(final String caseInsensitiveASCIIString, final int numberToRepeat) {
        if (caseInsensitiveASCIIString == null || caseInsensitiveASCIIString.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string passed in to CaseInsensitiveStringMatcher.");
        }
        if (numberToRepeat < 1) {
            throw new IllegalArgumentException("Number of repeats must be at least one.");
        }
        caseInsensitiveString = repeatString(caseInsensitiveASCIIString, numberToRepeat);
        length = caseInsensitiveString.length();
        charMatchList = new ByteMatcher[length];
        for (int charIndex = 0; charIndex < length; charIndex++) {
            charMatchList[charIndex] = getByteMatcherForChar(caseInsensitiveString.charAt(charIndex));
        }
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
     */
    @Override
    public boolean matches(final Reader reader, final long matchPosition) throws IOException {
        final int localLength = length;
        final ByteMatcher[] matchList = charMatchList;   
        Window window = reader.getWindow(matchPosition);
        int checkPos = 0;
        while (window != null) {
            final int offset = reader.getWindowOffset(matchPosition + checkPos);
            final int endPos = Math.min(window.length(), offset + localLength - checkPos);
            final byte[] array = window.getArray();
            for (int windowPos = offset; windowPos < endPos; windowPos++) {
                final ByteMatcher byteMatcher = matchList[checkPos++];
                if (!byteMatcher.matches(array[windowPos])) {
                    return false;
                }
            }
            if (checkPos == localLength) {
                return true;
            } else {
                window = reader.getWindow(matchPosition + checkPos);
            }
        }
        return false;
    }   
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        if (matchPosition + length < bytes.length && matchPosition >= 0) {
            int position = matchPosition;
            final ByteMatcher[] localList = charMatchList;
            for (final ByteMatcher charMatcher: localList) {
                if (!charMatcher.matches(bytes[position++])) {
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
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        int position = matchPosition;
        final ByteMatcher[] localList = charMatchList;        
        for (final ByteMatcher charMatcher : localList) {
            if (!charMatcher.matches(bytes[position++])) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ByteMatcher getMatcherForPosition(final int position) {
        return charMatchList[position];
    }


    /**
     * Returns the string which is matched case insensitively.
     * 
     * @return The string which is matched by this class.
     */
    public String getCaseInsensitiveString() {
        //TODO: do we need this member variable, or can we reconstruct it from 
        //      the matcher list?  Performance issues?
        //      We do not seem to be enforcing ASCII text anywhere either.
        return caseInsensitiveString;
    }
    

    /**
     * Returns a OneByteMatcher for bytes which are not alphabetic characters,
     * and a CaseInsensitiveByteMatcher for alphabetic characters.
     * 
     * @param theChar the character to get a byte matcher for.
     * @return A ByteMatcher optimised for the character.
     */
    private ByteMatcher getByteMatcherForChar(final char theChar) {
        if ((theChar >= 'a' && theChar <= 'z') ||
            (theChar >= 'A' && theChar <= 'Z')) {
            return new CaseInsensitiveByteMatcher(theChar);
        } else {
            //FIXME: if the char is not an ASCII char, this will not be correct.
            return new OneByteMatcher((byte) theChar);
        }
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public CaseInsensitiveStringMatcher reverse() {
        final String reversed = new StringBuilder(caseInsensitiveString).reverse().toString();
        return new CaseInsensitiveStringMatcher(reversed);
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceMatcher subsequence(final int beginIndex, final int endIndex) {
        if (beginIndex < 0 || endIndex > length || beginIndex >= endIndex) {
            final String message = "Subsequence index %d to %d is out of bounds in a sequence of length %d";
            throw new IndexOutOfBoundsException(String.format(message, beginIndex, endIndex, length));
        }
        if (endIndex - beginIndex == 1) {
            return charMatchList[beginIndex];
        }
        return new CaseInsensitiveStringMatcher(caseInsensitiveString.substring(beginIndex, endIndex));
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceMatcher repeat(final int numberOfRepeats) {
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("Number of repeats must be at least one.");
        }
        if (numberOfRepeats == 1) {
            return this;
        }        
        return new CaseInsensitiveStringMatcher(repeatString(caseInsensitiveString, numberOfRepeats));
    }

    
    private String repeat(CaseInsensitiveByteMatcher matcher, int numberOfRepeats) {
        final char charToRepeat = (char) matcher.getMatchingBytes()[0];
        final char[] repeated = new char[numberOfRepeats];
        Arrays.fill(repeated, charToRepeat);
        return new String(repeated);
    }
    

    private String repeatString(final String stringToRepeat, final int numberToRepeat) {
        if (numberToRepeat == 1) {
            return stringToRepeat;
        }
        final StringBuilder builder = new StringBuilder(stringToRepeat.length() * numberToRepeat);
        for (int count = 0; count < numberToRepeat; count++) {
            builder.append(stringToRepeat);
        }
        return builder.toString();
    }

}
