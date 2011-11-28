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
import net.domesdaybook.matcher.singlebyte.AnyMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.Reader;

/**
 * An immutable object which matches a gap of unknown bytes.
 * 
 * @author matt
 */
public final class FixedGapMatcher implements SequenceMatcher {

    private static final SingleByteMatcher ANY_MATCHER = new AnyMatcher();

    private final int gapLength;

   
    /**
     * Constructs a FixedGapMatcher of a given length.
     *
     * @param gapLength The length of the gap to match.
     * @throws IllegalArgumentException if the gap is less than one.
     */
    public FixedGapMatcher(final int gapLength) {
        if (gapLength < 1) {
            throw new IllegalArgumentException("FixedGapMatcher requires a gap greater than zero.");
        }
        this.gapLength = gapLength;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SingleByteMatcher getByteMatcherForPosition(final int position) {
        if (position < 0 || position >= gapLength) {
            final String message = String.format("Position %d out of bounds, length is %d", position, gapLength);
            throw new IndexOutOfBoundsException(message);
        }
        return ANY_MATCHER;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int length() {
        return gapLength;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        return prettyPrint ? String.format(" .{%d} ", gapLength) : String.format(".{%d}", gapLength);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final Reader reader, final long matchPosition) throws IOException {
        return reader.getWindow(matchPosition) != null && 
               reader.getWindow(matchPosition + gapLength) != null;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return matchPosition + gapLength < bytes.length && matchPosition >= 0;
    }    

    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        return true;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override    
    public FixedGapMatcher reverse() {
        return this;
    }

}
