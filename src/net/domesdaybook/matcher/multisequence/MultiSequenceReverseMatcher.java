/*
 * Copyright Matt Palmer 2012, All rights reserved.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.domesdaybook.io.WindowReader;
import net.domesdaybook.matcher.sequence.SequenceMatcher;

//TODO: should this simply be a translation class with no knowledge of the
//      reversal process?  Mapping the sequences in one multisequencematcher
//      to those in another?  Might need a transformation object (verb...!)
//      or leave transformation to an external process, and just pass in the 
//      map of old to new sequences.

/**
 * This class matches the reverse of a set of sequences matched by another MultiSequenceMatcher,
 * but on a match, returns the original non-reversed sequences instead of the reversed ones.
 * <p>
 * It wraps a new MultiSequenceMatcher of the type originally passed in, but
 * with the sequences matched by the original reversed.  Since it is possible to 
 * reverse any MultiSequenceMatcher by calling its {@link #reverse()} method, 
 * there would seem little use for a class like this.  However, it has one further
 * trick: the sequences returned by a match on this class are translated back into
 * the non-reversed sequences used by the original MultiSequenceMatcher passed in.
 * <p>
 * This may seem like a strange ability to need.  However, it is very useful when
 * using shift-based multi-sequence search techniques 
 * (see @link net.domesdaybook.searcher.multisequence}) 
 * to be able to match the reverse of a set of sequences, but to know which of the 
 * original un-reversed sequences were "matched" in reverse.
 *
 * @author Matt Palmer
 */
public final class MultiSequenceReverseMatcher implements MultiSequenceMatcher {

    private final MultiSequenceMatcher reversed;
    private final Map<SequenceMatcher, SequenceMatcher> reverseToOriginals;
    
    /**
     * Constructs a MultiSequenceReverseMatcher from an original MultiSequenceMatcher.
     * 
     * @param original The original MultiSequenceMatcher.
     */
    public MultiSequenceReverseMatcher(final MultiSequenceMatcher original) {
        reverseToOriginals = new IdentityHashMap<SequenceMatcher, SequenceMatcher>();
        final List<SequenceMatcher> originalSequences = original.getSequenceMatchers();
        List<SequenceMatcher> reverseSequences = new ArrayList<SequenceMatcher>();
        for (final SequenceMatcher originalSequence : originalSequences) {
            final SequenceMatcher reverseSequence = originalSequence.reverse();
            reverseSequences.add(reverseSequence);
            reverseToOriginals.put(reverseSequence, originalSequence);
        }
        reversed = original.newInstance(reverseSequences);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<SequenceMatcher> allMatches(WindowReader reader, long matchPosition) throws IOException {
        return getOriginalSequences(reversed.allMatches(reader, matchPosition));
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<SequenceMatcher> allMatches(byte[] bytes, int matchPosition) {
        return getOriginalSequences(reversed.allMatches(bytes, matchPosition));
    }

    
    /**
     * {@inheritDoc}
     */    
    @Override
    public Collection<SequenceMatcher> allMatchesBackwards(WindowReader reader, long matchPosition) throws IOException {
        return getOriginalSequences(reversed.allMatchesBackwards(reader, matchPosition));
    }

    
    /**
     * {@inheritDoc}
     */    
    @Override
    public Collection<SequenceMatcher> allMatchesBackwards(byte[] bytes, int matchPosition) {
        return getOriginalSequences(reversed.allMatchesBackwards(bytes, matchPosition));
    }

    
    /**
     * {@inheritDoc}
     */    
    @Override
    public SequenceMatcher firstMatch(WindowReader reader, long matchPosition) throws IOException {
        return getOriginalSequence(reversed.firstMatch(reader, matchPosition));
    }

    
    /**
     * {@inheritDoc}
     */    
    @Override
    public SequenceMatcher firstMatch(byte[] bytes, int matchPosition) {
        return getOriginalSequence(reversed.firstMatch(bytes, matchPosition));
    }

    
    /**
     * {@inheritDoc}
     */    
    @Override
    public SequenceMatcher firstMatchBackwards(WindowReader reader, long matchPosition) throws IOException {
        return getOriginalSequence(reversed.firstMatchBackwards(reader, matchPosition));
    }

    
    /**
     * {@inheritDoc}
     */    
    @Override
    public SequenceMatcher firstMatchBackwards(byte[] bytes, int matchPosition) {
        return getOriginalSequence(reversed.firstMatchBackwards(bytes, matchPosition));
    }

    
    /**
     * {@inheritDoc}
     */    
    @Override
    public boolean matchesBackwards(WindowReader reader, long matchPosition) throws IOException {
        return reversed.matchesBackwards(reader, matchPosition);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesBackwards(byte[] bytes, int matchPosition) {
        return reversed.matchesBackwards(bytes, matchPosition);
    }

    
    /**
     * {@inheritDoc}
     */    
    @Override
    public int getMinimumLength() {
        return reversed.getMinimumLength();
    }

    
    /**
     * {@inheritDoc}
     */    
    @Override
    public int getMaximumLength() {
        return reversed.getMaximumLength();
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MultiSequenceMatcher reverse() {
        return new MultiSequenceReverseMatcher(reversed);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiSequenceMatcher newInstance(Collection<? extends SequenceMatcher> sequences) {
        return new MultiSequenceReverseMatcher(reversed.newInstance(sequences));
    }
    

    /**
     * {@inheritDoc}
     */    
    @Override
    public List<SequenceMatcher> getSequenceMatchers() {
        return reversed.getSequenceMatchers();
    }
    

    /**
     * {@inheritDoc}
     */    
    @Override
    public boolean matches(WindowReader reader, long matchPosition) throws IOException {
        return reversed.matches(reader, matchPosition);
    }
    
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public boolean matches(byte[] bytes, int matchPosition) {
        return reversed.matches(bytes, matchPosition);
    }
    
    
    /**
     * Translates a collection of reversed sequence matchers back into the original
     * non-reversed ones they were created from.
     * 
     * @param toTranslate A collection of reversed sequence matchers.
     * @return A collection of the corresponding non-reversed sequence matchers.
     */
    private Collection<SequenceMatcher> getOriginalSequences(final Collection<SequenceMatcher> toTranslate) {
        final List<SequenceMatcher> originals = new ArrayList<SequenceMatcher>(toTranslate.size());
        for (final SequenceMatcher reverse : toTranslate) {
            originals.add(reverseToOriginals.get(reverse));
        }
        return originals;
    }
    
    
    private SequenceMatcher getOriginalSequence(final SequenceMatcher toTranslate) {
        return reverseToOriginals.get(toTranslate);
    }

    
}
