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

package net.domesdaybook.matcher.multisequence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
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
     * @throws IOException  
     */
    public Collection<SequenceMatcher> allMatches(Reader reader, long matchPosition)
            throws IOException;

    
    /**
     * Returns all the SequenceMatcher objects which matched.
     * Should never return null - always returns a collection, even if empty.
     *
     * @param bytes The byte array to read from.
     * @param matchPosition The position to test for a match.
     * @return A collection of matching SequenceMatchers or an empty collection if none matched.
     */
    public Collection<SequenceMatcher> allMatches(byte[] bytes, int matchPosition);
    
    
   
    /**
     * Returns all the SequenceMatcher objects which matched backwards from
     * the matchPosition.
     * 
     * Should never return null - always returns a collection, even if empty.
     *
     * @param reader The {@link Reader} to read from.
     * @param matchPosition The position to test for a match.
     * @return A collection of matching SequenceMatchers or an empty collection if none matched.
     * @throws IOException  
     */
    public Collection<SequenceMatcher> allMatchesBackwards(Reader reader, 
            long matchPosition) throws IOException;

    
    /**
     * Returns all the SequenceMatcher objects which matched backwards from
     * the matchPosition.
     * 
     * Should never return null - always returns a collection, even if empty.
     *
     * @param bytes 
     * @param matchPosition The position to test for a match.
     * @return A collection of matching SequenceMatchers or an empty collection if none matched.
     */
    public Collection<SequenceMatcher> allMatchesBackwards(byte[] bytes, int matchPosition);    
        
     
    /**
     * Returns the first matching sequence, or null if no sequence matched.
     * 
     * @param reader The {@link Reader} to read from.
     * @param matchPosition matchPosition The position to test for a match.
     * @return The SequenceMatcher which matched at that position, or null if none matched.
     * @throws IOException  
     */
    public SequenceMatcher firstMatch(Reader reader, long matchPosition)
       throws IOException;
  
    
    
    /**
     * Returns the first matching sequence, or null if no sequence matched.
     * 
     * @param bytes The byte array to read from.
     * @param matchPosition matchPosition The position to test for a match.
     * @return The SequenceMatcher which matched at that position, or null if none matched.
     */
    public SequenceMatcher firstMatch(byte[] bytes, int matchPosition);   
    
    
    /**
     * Returns the first matching sequence backwards from the matchPosition,
     * or null if no sequence matched.
     * 
     * @param reader The {@link Reader} to read from.
     * @param matchPosition matchPosition The position to test for a match.
     * @return The SequenceMatcher which matched at that position, or null if none matched.
     * @throws IOException  
     */
    public SequenceMatcher firstMatchBackwards(Reader reader, long matchPosition)
        throws IOException;
    

    /**
     * Returns the first matching sequence backwards from the matchPosition,
     * or null if no sequence matched.
     * 
     * @param bytes The byte array to read from.
     * @param matchPosition matchPosition The position to test for a match.
     * @return The SequenceMatcher which matched at that position, or null if none matched.
     */
    public SequenceMatcher firstMatchBackwards(byte[] bytes, int matchPosition);       
    
    
    /**
     * Returns whether or not there is a match backwards from the matchPosition
     * 
     * @param reader The {@link Reader} to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     * @throws IOException  
     */    
    public boolean matchesBackwards(Reader reader, long matchPosition)
         throws IOException;;
    
    
    /**
     * Returns whether or not there is a match backwards from the matchPosition
     * 
     * @param bytes 
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     */    
    public boolean matchesBackwards(byte[] bytes, int matchPosition);
    
    
    /**
     * Returns the minimum length of the sequences that can match
     * 
     * @return int The minimum length of the sequences that can match.
     */
    public int getMinimumLength();
    
    
    
    /**
     *  Returns the maximum length of the sequences that can match.
     * 
     * @return int The maximum length of the sequences that can match.
     */
    public int getMaximumLength();
    
    
    /**
     * Returns a multi sequence matcher which matches the reversed
     * sequences used to construct this MultiSequenceMatcher.
     * 
     * @return A MultiSequenceMatcher which recognises the reversed sequences.
     */
    public MultiSequenceMatcher reverse();
    
    
    public MultiSequenceMatcher newInstance(Collection<? extends SequenceMatcher> sequences);

    
    /**
     * Returns a collection of all the sequences matched by this matcher.
     * 
     * @return A collection of the sequence matchers this multi sequence matcher matches.
     */
    public List<SequenceMatcher> getSequenceMatchers();
}
