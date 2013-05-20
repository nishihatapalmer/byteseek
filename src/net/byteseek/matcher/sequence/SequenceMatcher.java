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

package net.byteseek.matcher.sequence;

import net.byteseek.matcher.Matcher;
import net.byteseek.matcher.bytes.ByteMatcher;

/**
 * An extension to the {@link Matcher} interface to support sequences of 
 * Matchers. 
 *
 * @author Matt Palmer
 */
 public interface SequenceMatcher extends Matcher {

    /**
     * Returns a {@link ByteMatcher} which matches all the bytes at
     * the requested position in the sequence.
     *
     * @param position The position in the byte matcher to return a dedicated byte matcher for.
     * @return A ByteMatcher for the position in the sequence provided.
     * @throws IndexOutOfBoundsException if an attempt is made to get a ByteMatcher
     *                                   for a position outside of the sequence.
     * 
     */
    public ByteMatcher getMatcherForPosition(int position);

    
    /**
     * Returns whether there is a match or not at the given position in a byte array.
     * <p/>
     * It does not perform any bounds checking, so an IndexOutOfBoundsException
     * can be thrown by this method if matching is outside the bounds of the array.
     * <p>
     * This method is useful when searching in byte arrays, where the bounds checking
     * of the search algorithm has already assured a safe usage, hence it is more
     * efficient in those circumstances.  
     * <p>
     * It is not recommended to use this method for normal matching without some
     * additional bounds checking, or unless you would rather accept the overhead
     * of an IndexOutOfBoundsException on an improper access.  
     * 
     * @param bytes An array of bytes to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     * @throws IndexOutOfBoundsException if a match is made outside the bounds of 
     *                                   the array.
     */
    public boolean matchesNoBoundsCheck(byte[] bytes, int matchPosition);    
    
    
    /**
     * Returns the length of the matching sequence.
     *
     * @return Returns the length of a matching byte sequence.
     */
    public int length();

    
    /**
     * Returns a new SequenceMatcher which matches the reverse sequence of the
     * original sequence matcher.
     * 
     * @return A SequenceMatcher which matches the reverse sequence.
     */
    public SequenceMatcher reverse();
    
    
    /**
     * Returns a new SequenceMatcher which matches a sub-sequence of the 
     * original sequence.  The subsequence returned must match at least one
     * byte (an empty SequenceMatcher is prohibited).
     * 
     * @param beginIndex The beginning index, starting at zero, inclusive.
     * @param endIndex The ending index, exclusive.
     * @return SequenceMatcher the specified sub-sequence.
     * @throws IndexOutOfBoundsException if the beginIndex is negative, 
     *         or the endIndex is greater than the length of the sequence,
     *         or the beginIndex is greater than or equal to the endIndex.
     */
    public SequenceMatcher subsequence(int beginIndex, int endIndex);
    
    
    /**
     * Returns a new SequenceMatcher which matches a sub-sequence of the original
     * sequence starting from the begin index.  The subsequence returned must match
     * at least one byte (and empty SequenceMatcher is prohibited).
     * 
     * @param beginIndex The beginning index, starting at zero.
     * @return SequenceMatcher the specified sub-sequence.
     * @throws IndexOutOfBoundsException if the beginIndex is negative, 
     *         or the endIndex is greater than the length of the sequence,
     *         or the beginIndex is greater than or equal to the endIndex.
     */
    public SequenceMatcher subsequence(int beginIndex);
    
    
    /**
     * Returns a SequenceMatcher which matches this SequenceMatcher repeated
     * a number of times.
     * 
     * @param numberOfRepeats The number of times to repeat this SequenceMatcher.
     * @return A SequenceMatcher which matches the same as this sequence repeated
     *         a number of times.
     */
    public SequenceMatcher repeat(final int numberOfRepeats);
    
    
    /**
     * Returns a regular expression representation of the matching sequence.
     * 
     * @param prettyPrint whether to pretty print the regular expression with spacing.
     * @return A string containing a regular expression of the byte matcher.
     */
    public String toRegularExpression(boolean prettyPrint);

}
