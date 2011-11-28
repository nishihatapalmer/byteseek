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
 * 
 */

package net.domesdaybook.matcher.sequence;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.matcher.singlebyte.ByteMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;

/**
 * An immutable class which matches ASCII text case sensitively.
 * 
 * @author Matt Palmer
 */
public final class CaseSensitiveStringMatcher implements SequenceMatcher {

    private final byte[] byteArray;
    private final int length;

    /**
     * Constructs a case sensitive matcher for a given ASCII string.
     *
     * @param caseSensitiveASCIIString The ASCII string to match case sensitively.
     */
    public CaseSensitiveStringMatcher(final String caseSensitiveASCIIString) {
        this(caseSensitiveASCIIString, 1);
    }


    /**
     * Constructs a case sensitive matcher for a number of repeated ASCII strings.
     *
     * @param caseSensitiveASCIIString The (repeated) ASCII string to match case sensitively.
     * @param numberToRepeat The number of times to repeat the ASCII string.
     * @throws IllegalArgumentException if the string is null or empty, or the
     *         number to repeat is less than one.
     */
    public CaseSensitiveStringMatcher(final String caseSensitiveASCIIString, final int numberToRepeat) {
        if (caseSensitiveASCIIString == null || caseSensitiveASCIIString.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string passed in to CaseSensitiveStringMatcher.");
        }
        if (numberToRepeat < 1) {
            throw new IllegalArgumentException("CaseSensitiveStringMatcher requires a positive number of repeats.");
        }
        String caseSensitiveString = repeatString(caseSensitiveASCIIString, numberToRepeat);
        final int byteSequenceLength = caseSensitiveString.length();
        byteArray = new byte[byteSequenceLength];
        //FIXME: does not work with non-ASCII characters properly.
        for (int byteIndex = 0; byteIndex < byteSequenceLength; byteIndex++) {
            byteArray[byteIndex] = (byte) (caseSensitiveString.charAt(byteIndex));
        }
        length = byteArray.length;
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
    public boolean matches(final Reader reader, final long matchPosition)
            throws IOException {
        final int localLength = length;
        final byte[] localArray = byteArray;          
        Window window = reader.getWindow(matchPosition);
        int checkPos = 0;
        while (window != null) {
            final int offset = reader.getWindowOffset(matchPosition + checkPos);
            final int endPos = Math.min(window.getLimit(), offset + localLength - checkPos);
            final byte[] array = window.getArray();
            for (int windowPos = offset; windowPos < endPos; windowPos++) {
                if (array[windowPos] != localArray[checkPos++]) {
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
        final int localLength = length;
        if (matchPosition + localLength <= bytes.length && matchPosition >= 0) {
            final byte[] localArray = byteArray;
            for (int byteIndex = 0; byteIndex < localLength; byteIndex++) {
                if (localArray[byteIndex] != bytes[matchPosition + byteIndex]) {
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
        final int localLength = length;
        final byte[] localArray = byteArray;
        for (int byteIndex = 0; byteIndex < localLength; byteIndex++) {
            if (!(localArray[byteIndex] == bytes[matchPosition + byteIndex])) {
                return false;
            }
        }
        return true;
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
            return " '" + getCaseSensitiveString() + "' ";
        }
        return "'" + getCaseSensitiveString() + "'";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SingleByteMatcher getByteMatcherForPosition(final int position) {
        return new ByteMatcher(byteArray[position]);
    }

    
    /**
     * Returns the string which is matched case sensitively.
     * 
     * @return The string this matcher matches case sensitively.
     */
    public String getCaseSensitiveString() {
        try {
            return new String(byteArray, "US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

   
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CaseSensitiveStringMatcher reverse() {
        final String reversed = new StringBuffer(getCaseSensitiveString()).reverse().toString();
        return new CaseSensitiveStringMatcher(reversed);
    }

}

