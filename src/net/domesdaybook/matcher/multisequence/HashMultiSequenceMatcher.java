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
import java.util.List;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;

/**
 *
 * @author Matt Palmer
 */
public class HashMultiSequenceMatcher implements MultiSequenceMatcher {

    private final List<SequenceMatcher> matchers;
    private final List<SequenceMatcher>[] hashTable;
    private final int minimumLength;
    private final int maximumLength;
    
    public HashMultiSequenceMatcher(final Collection<? extends SequenceMatcher> matchers) {
        // Store matcher list:
        if (matchers == null || matchers.isEmpty()) {
            throw new IllegalArgumentException("Null or empty matchers passed in.");
        }
        this.matchers = new ArrayList<SequenceMatcher>(matchers.size());
        
        //Calculate min length, max length and the hash of each matcher:
        hashTable = createHashTableFor(this.matchers);
        int currentMin = Integer.MAX_VALUE;
        int currentMax = Integer.MIN_VALUE;
        for (final SequenceMatcher matcher : matchers) {
            final int length = matcher.length();
            if (length < currentMin) currentMin = length;
            if (length > currentMax) currentMax = length;
            addHashFor(matcher);
        }
        minimumLength = currentMin;
        maximumLength = currentMax;
    }
    
    public Collection<SequenceMatcher> allMatches(final Reader reader, final long matchPosition) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<SequenceMatcher> allMatches(final byte[] bytes, final int matchPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<SequenceMatcher> allMatchesBackwards(final Reader reader, final long matchPosition) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<SequenceMatcher> allMatchesBackwards(final byte[] bytes, final int matchPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SequenceMatcher firstMatch(final Reader reader, final long matchPosition) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SequenceMatcher firstMatch(final byte[] bytes, final int matchPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SequenceMatcher firstMatchBackwards(final Reader reader, final long matchPosition) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SequenceMatcher firstMatchBackwards(final byte[] bytes, final int matchPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean matchesBackwards(final Reader reader, final long matchPosition) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean matchesBackwards(final byte[] bytes, final int matchPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMinimumLength() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMaximumLength() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MultiSequenceMatcher reverse() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public MultiSequenceMatcher newInstance(final Collection<? extends SequenceMatcher> sequences) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<SequenceMatcher> getSequenceMatchers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean matches(Reader reader, long matchPosition) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean matches(byte[] bytes, int matchPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void addHashFor(final SequenceMatcher matcher) {
        //TODO: this is only for a block size of one, what about higher block sizes?
        final byte[] matchingBytes = matcher.getMatcherForPosition(0).getMatchingBytes();
        for (final byte b : matchingBytes) {
           
        }
    }

    private List<SequenceMatcher>[] createHashTableFor(final List<SequenceMatcher> matchers) {
        //TODO: determine appropriate table size.
        int tableSize = 32;
        return new List[tableSize];
    }
    
}
