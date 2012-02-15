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
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;

/**
 *
 * @author Matt Palmer
 */
public final class MultiSequenceReverseMatcher implements MultiSequenceMatcher {

    private final MultiSequenceMatcher reversed;
    private final Map<SequenceMatcher, SequenceMatcher> reverseToOriginals;
    
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
    
    
    public Collection<SequenceMatcher> allMatches(Reader reader, long matchPosition) throws IOException {
        return getOriginalSequences(reversed.allMatches(reader, matchPosition));
    }

    public Collection<SequenceMatcher> allMatches(byte[] bytes, int matchPosition) {
        return getOriginalSequences(reversed.allMatches(bytes, matchPosition));
    }

    public Collection<SequenceMatcher> allMatchesBackwards(Reader reader, long matchPosition) throws IOException {
        return getOriginalSequences(reversed.allMatchesBackwards(reader, matchPosition));
    }

    public Collection<SequenceMatcher> allMatchesBackwards(byte[] bytes, int matchPosition) {
        return getOriginalSequences(reversed.allMatchesBackwards(bytes, matchPosition));
    }

    public SequenceMatcher firstMatch(Reader reader, long matchPosition) throws IOException {
        return getOriginalSequence(reversed.firstMatch(reader, matchPosition));
    }

    public SequenceMatcher firstMatch(byte[] bytes, int matchPosition) {
        return getOriginalSequence(reversed.firstMatch(bytes, matchPosition));
    }

    public SequenceMatcher firstMatchBackwards(Reader reader, long matchPosition) throws IOException {
        return getOriginalSequence(reversed.firstMatchBackwards(reader, matchPosition));
    }

    public SequenceMatcher firstMatchBackwards(byte[] bytes, int matchPosition) {
        return getOriginalSequence(reversed.firstMatchBackwards(bytes, matchPosition));
    }

    public boolean matchesBackwards(Reader reader, long matchPosition) throws IOException {
        return reversed.matchesBackwards(reader, matchPosition);
    }

    public boolean matchesBackwards(byte[] bytes, int matchPosition) {
        return reversed.matchesBackwards(bytes, matchPosition);
    }

    public int getMinimumLength() {
        return reversed.getMinimumLength();
    }

    public int getMaximumLength() {
        return reversed.getMaximumLength();
    }

    public MultiSequenceMatcher reverse() {
        return new MultiSequenceReverseMatcher(reversed);
    }
    

    public MultiSequenceMatcher newInstance(Collection<? extends SequenceMatcher> sequences) {
        return new MultiSequenceReverseMatcher(reversed.newInstance(sequences));
    }
    

    public List<SequenceMatcher> getSequenceMatchers() {
        return reversed.getSequenceMatchers();
    }
    

    public boolean matches(Reader reader, long matchPosition) throws IOException {
        return reversed.matches(reader, matchPosition);
    }
    

    public boolean matches(byte[] bytes, int matchPosition) {
        return reversed.matches(bytes, matchPosition);
    }
    
    
    private Collection<SequenceMatcher> getOriginalSequences(Collection<SequenceMatcher> reversed) {
        final List<SequenceMatcher> originals = new ArrayList<SequenceMatcher>(reversed.size());
        for (final SequenceMatcher reverse : reversed) {
            originals.add(reverseToOriginals.get(reverse));
        }
        return originals;
    }
    
    
    private SequenceMatcher getOriginalSequence(SequenceMatcher reversed) {
        return reverseToOriginals.get(reversed);
    }

    
}
