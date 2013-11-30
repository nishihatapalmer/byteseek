/*
 * Copyright Matt Palmer 2011, All rights reserved.
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

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.byteseek.matcher.sequence.ByteMatcherSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;

/**
 * A simple abstract base class which implements most of the methods required
 * for a {@link ByteMatcher} to also behave as a {@link SequenceMatcher}.
 * 
 * @author Matt Palmer
 */
public abstract class AbstractByteMatcher implements ByteMatcher {
    
    /**
     * {@inheritDoc}
     * 
     * Returns this for position 0, or throws an IndexOutOfBoundsException.
     */
    @Override
    public final ByteMatcher getMatcherForPosition(final int position) {
        if (position != 0) {
            throw new IndexOutOfBoundsException("SingleByteMatchers only have a matcher at position 0.");
        }
        return this;
    }

    
    /**
     * {@inheritDoc}
     *
     * Always returns 1.
     */ 
    @Override
    public final int length() {
        return 1;
    }
    

    /**
     * {@inheritDoc}
     *
     * Always returns this.
     */ 
    @Override
    public final SequenceMatcher reverse() {
        return this;
    }    
    
    
    /**
     * Throws an IndexOutOfBoundsException if the begin index is not zero or
     * the endIndex is not one, otherwise it returns this.
     * ByteMatchers by definition only match one byte, 
     * so there can be no other possible subsequences.
     * 
     * @param beginIndex The beginning index, which must be 0.
     * @param endIndex The ending index, which must be 1.
     * @return SequenceMatcher this sequence matcher.
     * @throws IndexOutOfBoundsException if the begin index is not zero or the 
     *         end index is not one.
     */
    @Override
    public final SequenceMatcher subsequence(final int beginIndex, final int endIndex) {
        if (beginIndex != 0 || endIndex != 1) {
            throw new IndexOutOfBoundsException("SingleByteMatchers only support a sequence starting at zero with a length of one.");
        }
        return this;
    }
    
    
    /**
     * Throws an IndexOutOfBoundsException if the begin index is not zero,
     * otherwise it returns this.
     * ByteMatcher by definition only match one byte, 
     * so there can be no other possible subsequences.
     * 
     * @param beginIndex The beginning index, which must be 0.
     * @return SequenceMatcher this sequence matcher.
     * @throws IndexOutOfBoundsException if the begin index is not zero 
     */    
    @Override
    public final SequenceMatcher subsequence(final int beginIndex) {
        if (beginIndex != 0) {
            throw new IndexOutOfBoundsException("SingleByteMatchers only support a sequence starting at zero with a length of one.");
        }
        return this;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override    
    public SequenceMatcher repeat(int numberOfRepeats) {
        if (numberOfRepeats < 1) {
            throw new IllegalArgumentException("Number of repeats must be at least one.");
        }
        if (numberOfRepeats == 1) {
            return this;
        }   
        return new ByteMatcherSequenceMatcher(this, numberOfRepeats);
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
        return getClass().getSimpleName() + "(" + toRegularExpression(true) + ")";
    }    
    
    @Override
    public Iterator<ByteMatcher> iterator() {
    	return new ByteMatcherIterator();
    }
    
    public class ByteMatcherIterator implements Iterator<ByteMatcher> {

    	private boolean iterated = false;
    	
		@Override
		public boolean hasNext() {
			return iterated;
		}

		@Override
		public ByteMatcher next() {
			if (!iterated) {
				iterated = true;
				return AbstractByteMatcher.this;
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Byte matcher iterators cannot remove the ByteMatcher");
		}
    	
    }
    
    
}
