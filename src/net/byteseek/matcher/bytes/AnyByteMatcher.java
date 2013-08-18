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

package net.byteseek.matcher.bytes;

import java.io.IOException;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.FixedGapMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.util.bytes.ByteUtilities;

/**
 * A {@link ByteMatcher} which matches any byte at all.
 *
 * @author Matt Palmer
 */
public final class AnyByteMatcher extends AbstractByteMatcher {

    /**
     * A static AnyByteMatcher to return - there only needs to be one
     * AnyByteMatcher.
     */
    public static final AnyByteMatcher ANY_BYTE_MATCHER = new AnyByteMatcher();
    
    
    // A static 256-element array containing all the bytes.
    private static final byte[] ALL_BYTES =  ByteUtilities.getAllByteValues();


    /**
     * Constructs an immutable AnyByteMatcher.
     */
    public AnyByteMatcher() {
    }


    /**
     * Always returns true.
     */
    @Override
    public boolean matches(final byte theByte) {
        return true;
    }
    

    /**
     * Returns a 256-element array of all the possible byte values.
     */
    @Override
    public byte[] getMatchingBytes() {
        return ALL_BYTES.clone();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        return prettyPrint ? " . " : ".";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
        return reader.readByte(matchPosition) >= 0;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return matchPosition >= 0 && matchPosition < bytes.length;
    }    


    /**
     * Always returns 256.
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return 256;
    }

    
    /**
     * Always returns true
     */ 
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return true;
    }

    
    /**
     * Returns a FixedGapMatcher as long as the number of repeats.
     * @throws IllegalArgumentException if the number of repeats is less than one.
     */     
    @Override
    public SequenceMatcher repeat(int numberOfRepeats) {
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("Number of repeats must be at least one.");
        }
        if (numberOfRepeats == 1) {
            return this;
        }           
        return new FixedGapMatcher(numberOfRepeats);
    }
    
    @Override
    public String toString() {
    	return getClass().getSimpleName();
    }


}
