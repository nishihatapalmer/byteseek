/*
 * Copyright Matt Palmer 2009-2012, All rights reserved.
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


package net.domesdaybook.matcher.bytes;

import java.io.IOException;

import net.domesdaybook.io.WindowReader;
import net.domesdaybook.io.Window;
import net.domesdaybook.matcher.sequence.ByteArrayMatcher;
import net.domesdaybook.matcher.sequence.CaseInsensitiveSequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.util.bytes.ByteUtilities;

/**
 * An immutable {@link ByteMatcher} which matches ASCII bytes case insensitively.
 *
 * <p>It will only work for ASCII characters in the range 0 - 127.
 * Other Unicode characters will not work, as all of the ByteMatcher
 * classes work at the byte level, so cannot deal with multi-byte characters.
 *
 * @author Matt Palmer
 */
public final class CaseInsensitiveByteMatcher extends AbstractByteMatcher {

    private final static String ILLEGAL_ARGUMENTS = "Non-ASCII char passed in to CaseInsensitiveByteMatcher: %s";

    private final char value;
    private final byte[] caseValues;


    /**
     * Constructs a CaseInsensitiveByteMatcher from the character provided.
     *
     * @param asciiChar The ASCII character to match in a case insensitive way.
     */
    public CaseInsensitiveByteMatcher(final char asciiChar) {
        // Precondition: must be an ASCII char:
        if (asciiChar > 127 || asciiChar < 0) {
            final String message = String.format(ILLEGAL_ARGUMENTS, asciiChar);
            throw new IllegalArgumentException(message);
        }
        this.value = asciiChar;
        caseValues = new byte[2];
        caseValues[0] = (byte) Character.toLowerCase(asciiChar);
        caseValues[1] = (byte) Character.toUpperCase(asciiChar);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException{
        final Window window = reader.getWindow(matchPosition);     
        if (window != null) {
            final byte theByte = window.getByte(reader.getWindowOffset(matchPosition));
            return (theByte == caseValues[0] || theByte == caseValues[1]);
        }
        return false;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        if (matchPosition >= 0 && matchPosition < bytes.length) {
            final byte theByte = bytes[matchPosition];
            return (theByte == caseValues[0] || theByte == caseValues[1]);
        }
        return false;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        final byte theByte = bytes[matchPosition];
        return (theByte == caseValues[0] || theByte == caseValues[1]);
    }    

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        return (theByte == caseValues[0] || theByte == caseValues[1]);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        final byte firstByte = caseValues[0];
        final byte secondByte = caseValues[1];
        if (firstByte == secondByte) {
            return new byte[] {firstByte};
        }
        return caseValues.clone();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        return prettyPrint? " `" + Character.toString(value) + "` " : '`'
                + Character.toString(value) + '`';
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return caseValues[0] == caseValues[1] ? 1 : 2;
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
        if (getNumberOfMatchingBytes() == 1) {
            return new ByteArrayMatcher(ByteUtilities.repeat(caseValues[0], numberOfRepeats));
        }
        return new CaseInsensitiveSequenceMatcher(this, numberOfRepeats);
    }        


}
