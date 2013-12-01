/*
 * Copyright Matt Palmer 2009-2013, All rights reserved.
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

package net.byteseek.matcher.sequence;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.AnyByteMatcher;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.object.ArgUtils;

/**
 * An immutable object which matches a gap of unknown bytes.
 * <p>
 * This is equivalent to a sequence of Any matchers (a ByteMatcher which matches all bytes), 
 * but it's a lot quicker just to check that the match is within bounds, than to actually
 * check the bytes themselves, since by definition it will match any bytes it sees.
 * 
 * @author Matt Palmer
 */
public final class FixedGapMatcher implements SequenceMatcher {

    private final int length;

   
    /**
     * Constructs a FixedGapMatcher of a given length.
     *
     * @param gapLength The length of the gap to match.
     * @throws IllegalArgumentException if the gap is less than one.
     */
    public FixedGapMatcher(final int gapLength) {
    	ArgUtils.checkPositiveInteger(gapLength);
        this.length = gapLength;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ByteMatcher getMatcherForPosition(final int position) {
    	ArgUtils.checkIndexOutOfBounds(length, position);
        return AnyByteMatcher.ANY_BYTE_MATCHER;
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
    	switch (length) {
	    	case 1:  return prettyPrint ? ". "   : ".";
	    	case 2:  return prettyPrint ? ".. "  : "..";
	    	case 3:  return prettyPrint ? "... " : "...";
	    	default: return prettyPrint ? String.format(".{%d} ", length) 
	    								: String.format(".{%d}",  length); 
    	}
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
        return reader.getWindow(matchPosition) != null && 
               reader.getWindow(matchPosition + length - 1) != null;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return matchPosition + length < bytes.length && matchPosition >= 0;
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

    
    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceMatcher subsequence(int startIndex, int endIndex) {
        ArgUtils.checkIndexOutOfBounds(length, startIndex, endIndex);
        if (endIndex - startIndex == 1) {
            return AnyByteMatcher.ANY_BYTE_MATCHER;
        }
        return new FixedGapMatcher(endIndex - startIndex);
    }

    
    /**
     * {@inheritDoc}
     */  
    @Override
    public SequenceMatcher subsequence(final int beginIndex) {
        return subsequence(beginIndex, length());
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceMatcher repeat(int numberOfRepeats) {
    	ArgUtils.checkPositiveInteger(numberOfRepeats);
        if (numberOfRepeats == 1) {
            return this;
        }        
        return new FixedGapMatcher(length * numberOfRepeats);
    }
    
    
    /**
     * Returns a string representation of this matcher.  The format is subject
     * to change, but it will generally return the name of the matching class
     * and a regular expression defining the bytes matched by the matcher.
     * 
     * @return A string representing this matcher.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + toRegularExpression(true) + ']';
    }


	@Override
	public Iterator<ByteMatcher> iterator() {
		return new FixedGapIterator();
	}    
	
	private class FixedGapIterator implements Iterator<ByteMatcher> {

		private int count;
		
		@Override
		public boolean hasNext() {
			return count < length;
		}

		@Override
		public ByteMatcher next() {
			if (hasNext()) {
				return AnyByteMatcher.ANY_BYTE_MATCHER;
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Byte matchers cannot be removed from a FixedGapMatcher");
		}
		
	}

}
