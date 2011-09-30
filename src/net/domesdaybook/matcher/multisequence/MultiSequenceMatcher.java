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
 *  * Neither the "byteseek" name nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission. 
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

package net.domesdaybook.matcher.multisequence;

import java.io.IOException;
import java.util.Collection;
import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;

/**
 * An interface for matchers which can match more than one
 * {@link SequenceMatcher} at the same time.
 *
 * @author Matt Palmer
 */
public interface MultiSequenceMatcher extends Matcher {

    /**
     * Returns all the SequenceMatcher objects which matched.
     * Should never return null - always returns a collection, even if empty.
     *
     * @param reader The {@link Reader} to read from.
     * @param matchPosition The position to test for a match.
     * @return A collection of matching SequenceMatchers or an empty collection if none matched.
     */
    Collection<SequenceMatcher> allMatches(final Reader reader, final long matchPosition)
            throws IOException;

    
    /**
     * Returns all the SequenceMatcher objects which matched.
     * Should never return null - always returns a collection, even if empty.
     *
     * @param bytes The byte array to read from.
     * @param matchPosition The position to test for a match.
     * @return A collection of matching SequenceMatchers or an empty collection if none matched.
     */
    Collection<SequenceMatcher> allMatches(final byte[] bytes, final int matchPosition);
    
    
   
    /**
     * Returns all the SequenceMatcher objects which matched backwards from
     * the matchPosition.
     * 
     * Should never return null - always returns a collection, even if empty.
     *
     * @param reader The {@link Reader} to read from.
     * @param matchPosition The position to test for a match.
     * @return A collection of matching SequenceMatchers or an empty collection if none matched.
     */
    Collection<SequenceMatcher> allMatchesBackwards(final Reader reader, 
            final long matchPosition) throws IOException;;

    
    /**
     * Returns all the SequenceMatcher objects which matched backwards from
     * the matchPosition.
     * 
     * Should never return null - always returns a collection, even if empty.
     *
     * @param reader The byte array to read from.
     * @param matchPosition The position to test for a match.
     * @return A collection of matching SequenceMatchers or an empty collection if none matched.
     */
    Collection<SequenceMatcher> allMatchesBackwards(final byte[] bytes, final int matchPosition);    
        
     
    /**
     * Returns the first matching sequence, or null if no sequence matched.
     * 
     * @param reader The {@link Reader} to read from.
     * @param matchPosition matchPosition The position to test for a match.
     * @return The SequenceMatcher which matched at that position, or null if none matched.
     */
    SequenceMatcher firstMatch(final Reader reader, final long matchPosition)
       throws IOException;
  
    
    
    /**
     * Returns the first matching sequence, or null if no sequence matched.
     * 
     * @param bytes The byte array to read from.
     * @param matchPosition matchPosition The position to test for a match.
     * @return The SequenceMatcher which matched at that position, or null if none matched.
     */
    SequenceMatcher firstMatch(final byte[] bytes, final int matchPosition);   
    
    
    /**
     * Returns the first matching sequence backwards from the matchPosition,
     * or null if no sequence matched.
     * 
     * @param reader The {@link Reader} to read from.
     * @param matchPosition matchPosition The position to test for a match.
     * @return The SequenceMatcher which matched at that position, or null if none matched.
     */
    SequenceMatcher firstMatchBackwards(final Reader reader, final long matchPosition)
        throws IOException;
    

    /**
     * Returns the first matching sequence backwards from the matchPosition,
     * or null if no sequence matched.
     * 
     * @param bytes The byte array to read from.
     * @param matchPosition matchPosition The position to test for a match.
     * @return The SequenceMatcher which matched at that position, or null if none matched.
     */
    SequenceMatcher firstMatchBackwards(final byte[] bytes, final int matchPosition);       
    
    
    /**
     * Returns whether or not there is a match backwards from the matchPosition
     * 
     * @param reader The {@link Reader} to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     */    
    boolean matchesBackwards(final Reader reader, final long matchPosition)
         throws IOException;;
    
    
    /**
     * Returns whether or not there is a match backwards from the matchPosition
     * 
     * @param reader The {@link Reader} to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     */    
    boolean matchesBackwards(final byte[] bytes, final int matchPosition);
    
    
    /**
     * 
     * @return int The minimum length of the sequences that can match.
     */
    int getMinimumLength();
    
    
    
    /**
     * 
     * @return int The maximum length of the sequences that can match.
     */
    int getMaximumLength();
}
