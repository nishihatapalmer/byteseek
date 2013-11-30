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

package net.byteseek.matcher.multisequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;

/**
 * A very simple MultiSequenceMatcher which simply tries all of the
 * sequence matchers in a list in turn.  It is immutable (and so must be all
 * implementations of SequenceMatcher), so it can be safely used in multi-threaded 
 * applications.
 * <p>
 * For large lists of SequenceMatchers, this will not be a very time-efficient
 * way of matching them; using something like a {@link TrieMultiSequenceMatcher}
 * will be much faster.  However, it is space efficient, in that it only incurs
 * the overhead of a list to store the SequenceMatchers.
 * <p>
 * For very short lists of SequenceMatchers, it is possible that it may even be 
 * faster than more complex MultiSequenceMatchers.
 *
 * @author Matt Palmer.
 */
public final class ListMultiSequenceMatcher implements MultiSequenceMatcher {

    private final List<SequenceMatcher> matchers;
    private final int minimumLength;
    private final int maximumLength;

    
    /**
     * Constructs a ListMultiSequenceMatcher from a list of byte arrays.
     * <p>
     * The byte arrays will be cloned when constructing {@link ByteSequenceMatcher}s
     * from them to be used in this matcher.  If the list of byte arrays is empty
     * then a ListMultiSequenceMatcher is constructed which will not match anything.
     * 
     * @param bytesToMatch A list of byte arrays from which to construct the 
     *                     ListMultiSequenceMatcher.
     * @throws IllegalArgumentException if the list passed in is null, any of the
     *         byte arrays in the list is null, or any of the byte arrays in the
     *         list have a length of zero.
     */
    public ListMultiSequenceMatcher(final List<byte[]> bytesToMatch) {
        if (bytesToMatch == null) {
            throw new IllegalArgumentException("Null collection of bytes passed in.");
        }
        matchers = new ArrayList<SequenceMatcher>(bytesToMatch.size());
        for (final byte[] bytes : bytesToMatch) {
            if (bytes == null) {
                throw new IllegalArgumentException("A null byte array was in the list of arrays to match.");
            }
            final SequenceMatcher sequence = new ByteSequenceMatcher(bytes);
            matchers.add(sequence);
        }
        if (matchers.isEmpty()) {
            minimumLength = 0;
            maximumLength = 0;
        } else {
            int minLength = Integer.MAX_VALUE;
            int maxLength = Integer.MIN_VALUE;
            for (final SequenceMatcher matcher : matchers) {
                final int length = matcher.length();
                minLength = Math.min(minLength, length);
                maxLength = Math.max(maxLength, length);
            }
            minimumLength = minLength;
            maximumLength = maxLength;
        }
    }
    
    
    /**
     * Constructs a ListMultiSequenceMatcher from a collection of sequence matchers.
     * The ListMultiSequenceMatcher places the sequence matchers in the collection
     * into its own internal list.  If the collection passed in is empty, 
     * then a MultiSequenceMatcher is constructed which does not match anything.
     * 
     * @param matchersToUse A collection of sequence matchers to construct the
     *        ListMultiSequenceMatcher from.
     * @throws IllegalArgumentException if the collection is null, or any of the
     *         SequenceMatchers in the collection are null.
     */
    public ListMultiSequenceMatcher(final Collection<? extends SequenceMatcher> matchersToUse) {
        if (matchersToUse == null) {
            throw new IllegalArgumentException("Null collection of matchers passed in.");
        }
        matchers = new ArrayList<SequenceMatcher>(matchersToUse);
        for (final SequenceMatcher matcher : matchers) {
            if (matcher == null) {
                throw new IllegalArgumentException("A matcher in the collection was null.");
            }
        }
        if (matchers.isEmpty()) {
            minimumLength = 0;
            maximumLength = 0;
        } else {
            int minLength = Integer.MAX_VALUE;
            int maxLength = Integer.MIN_VALUE;
            for (final SequenceMatcher matcher : matchers) {
                final int length = matcher.length();
                minLength = Math.min(minLength, length);
                maxLength = Math.max(maxLength, length);
            }
            minimumLength = minLength;
            maximumLength = maxLength;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<SequenceMatcher> allMatches(final WindowReader reader, final long matchPosition) 
        throws IOException {
        List<SequenceMatcher> result = Collections.emptyList();         
        final List<SequenceMatcher> localMatchers = matchers;
        for (final SequenceMatcher sequence : localMatchers) {
            if (sequence.matches(reader, matchPosition)) {
                if (result.isEmpty()) {
                    result = new ArrayList<SequenceMatcher>(2);
                }
                result.add(sequence);
            }
        }
        return result;
    }
    
    
    /**    
     * {@inheritDoc}
     */ 
    @Override   
    public Collection<SequenceMatcher> allMatches(final byte[] bytes, 
            final int matchPosition) {
        List<SequenceMatcher> result = Collections.emptyList();         
        final long noOfBytes = bytes.length;
        //FIXME: bounds checking doesn't look right.
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<SequenceMatcher> localMatchers = matchers;
            if (matchPosition + maximumLength < noOfBytes) {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matchesNoBoundsCheck(bytes, matchPosition)) {
                        if (result.isEmpty()) {
                            result = new ArrayList<SequenceMatcher>(2);
                        }
                        result.add(sequence);
                    }
                }
            } else {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matches(bytes, matchPosition)) {
                        if (result.isEmpty()) {
                            result = new ArrayList<SequenceMatcher>(2);
                        }
                        result.add(sequence);
                    }
                }            
            }
        }
        return result;        
    }    
    
    
    /**    
     * {@inheritDoc}
     */ 
    @Override
    public Collection<SequenceMatcher> allMatchesBackwards(final WindowReader reader, 
            final long matchPosition) throws IOException {
        List<SequenceMatcher> result = Collections.emptyList();         
        final List<SequenceMatcher> localMatchers = matchers;
        final long onePastMatchPosition = matchPosition + 1;
        for (final SequenceMatcher sequence : localMatchers) {
            if (sequence.matches(reader, onePastMatchPosition - sequence.length())) {
                if (result.isEmpty()) {
                    result = new ArrayList<SequenceMatcher>(2);
                }
                result.add(sequence);
            }
        }            
        return result;
    }    
    
    
    /**    
     * {@inheritDoc}
     */ 
    @Override
    public Collection<SequenceMatcher> allMatchesBackwards(final byte[] bytes, 
            final int matchPosition) {
        List<SequenceMatcher> result = Collections.emptyList();         
        final int noOfBytes = bytes.length;
        if (matchPosition >= minimumLength - 1 && matchPosition < noOfBytes) {
            final List<SequenceMatcher> localMatchers = matchers;
            final int onePastMatchPosition = matchPosition + 1;
            if (onePastMatchPosition >= maximumLength) {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matchesNoBoundsCheck(bytes, onePastMatchPosition - sequence.length())) {
                        if (result.isEmpty()) {
                            result = new ArrayList<SequenceMatcher>(2);
                        }
                        result.add(sequence);
                    }
                }
            } else {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matches(bytes, onePastMatchPosition - sequence.length())) {
                        if (result.isEmpty()) {
                            result = new ArrayList<SequenceMatcher>(2);
                        }
                        result.add(sequence);
                    }
                }            
            }
        }
        return result; 
    }    
    
    
    /**    
     * {@inheritDoc}
     */   
    @Override
    public SequenceMatcher firstMatch(final WindowReader reader, final long matchPosition) 
            throws IOException {
        final List<SequenceMatcher> localMatchers = matchers;
        for (final SequenceMatcher sequence : localMatchers) {
            if (sequence.matches(reader, matchPosition)) {
                return sequence;
            }
        }            
        return null;
    }    

    
    /**    
     * {@inheritDoc}
     */ 
    @Override      
    public SequenceMatcher firstMatch(final byte[] bytes, final int matchPosition) {
        final long noOfBytes = bytes.length;
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<SequenceMatcher> localMatchers = matchers;
            if (matchPosition + maximumLength < noOfBytes) {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matchesNoBoundsCheck(bytes, matchPosition)) {
                        return sequence;
                    }
                }
            } else {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matches(bytes, matchPosition)) {
                        return sequence;
                    }
                }            
            }
        }
        return null;        
    }
    
    
    /**    
     * {@inheritDoc}
     */ 
    @Override 
    public SequenceMatcher firstMatchBackwards(final WindowReader reader, 
            final long matchPosition) throws IOException {
        final List<SequenceMatcher> localMatchers = matchers;
        final long onePastMatchPosition = matchPosition + 1;
        for (final SequenceMatcher sequence : localMatchers) {
            if (sequence.matches(reader, onePastMatchPosition - sequence.length())) {
                return sequence;
            }
        }            
        return null;    
    }

    
    /**    
     * {@inheritDoc}
     */ 
    @Override 
    public SequenceMatcher firstMatchBackwards(final byte[] bytes, final int matchPosition) {
        final int noOfBytes = bytes.length;
        if (matchPosition >= minimumLength - 1 && matchPosition < noOfBytes) {
            final List<SequenceMatcher> localMatchers = matchers;
            final int onePastMatchPosition = matchPosition + 1;
            if (onePastMatchPosition >= maximumLength) {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matchesNoBoundsCheck(bytes, onePastMatchPosition - sequence.length())) {
                        return sequence;
                    }
                }
            } else {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matches(bytes, onePastMatchPosition - sequence.length())) {
                        return sequence;
                    }
                }            
            }
        }
        return null;   
    }    
    
    
    /**    
     * {@inheritDoc}
     */ 
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) 
            throws IOException {
        final List<SequenceMatcher> localMatchers = matchers;
        for (final SequenceMatcher sequence : localMatchers) {
            if (sequence.matches(reader, matchPosition)) {
                return true;
            }
        }            
        return false;
    }
    
    
    /**    
     * {@inheritDoc}
     */ 
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        final int noOfBytes = bytes.length;
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<SequenceMatcher> localMatchers = matchers;
            if (matchPosition + maximumLength < noOfBytes) {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matchesNoBoundsCheck(bytes, matchPosition)) {
                        return true;
                    }
                }
            } else {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matches(bytes, matchPosition)) {
                        return true;
                    }
                }            
            }
        }
        return false;        
    }

    
    /**    
     * {@inheritDoc}
     */ 
    @Override
    public boolean matchesBackwards(final WindowReader reader, final long matchPosition) throws IOException { 
        final List<SequenceMatcher> localMatchers = matchers;
        final long onePastMatchPosition = matchPosition + 1;
        for (final SequenceMatcher sequence : localMatchers) {
            if (sequence.matches(reader, onePastMatchPosition - sequence.length())) {
                return true;
            }
        }            
        return false; 
    }

    
    /**    
     * {@inheritDoc}
     */ 
    @Override
    public boolean matchesBackwards(final byte[] bytes, final int matchPosition) {
        final int noOfBytes = bytes.length;
        if (matchPosition >= minimumLength - 1 && matchPosition < noOfBytes) {
            final List<SequenceMatcher> localMatchers = matchers;
            final int onePastMatchPosition = matchPosition + 1;
            if (onePastMatchPosition >= maximumLength) {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matchesNoBoundsCheck(bytes, onePastMatchPosition - sequence.length())) {
                        return true;
                    }
                }
            } else {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matches(bytes, onePastMatchPosition - sequence.length())) {
                        return true;
                    }
                }            
            }
        }
        return false; 
    }    
    

    /**    
     * {@inheritDoc}
     */ 
    @Override  
    public int getMinimumLength() {
        return minimumLength;
    }

    
    /**    
     * {@inheritDoc}
     */ 
    @Override  
    public int getMaximumLength() {
        return maximumLength;
    }

    
    /**    
     * {@inheritDoc}
     */ 
    @Override  
    public MultiSequenceMatcher reverse() {
        return new ListMultiSequenceMatcher(
                MultiSequenceUtils.reverseMatchers(matchers));
    }
    
    
    /**    
     * {@inheritDoc}
     */ 
    @Override 
    public MultiSequenceMatcher newInstance(Collection<? extends SequenceMatcher> sequences) {
        return new ListMultiSequenceMatcher(sequences);
    }
    
    
    /**    
     * {@inheritDoc}
     */ 
    @Override  
    public List<SequenceMatcher> getSequenceMatchers() {
        return new ArrayList<SequenceMatcher>(matchers);
    }
    
    
    /**
     * Returns a string representation of this matcher.  The format is subject
     * to change, but it will generally return the name of the matching class
     * and regular expressions defining the sequences matched by the matcher.
     * 
     * @return A string representing this matcher.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[matchers:" + matchers + ']';
    }    
        

}
