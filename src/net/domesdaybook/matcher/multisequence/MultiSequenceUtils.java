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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.domesdaybook.bytes.ByteUtilities;
import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 *
 * @author Matt Palmer
 */
public final class MultiSequenceUtils {
    
    /**
     * This is a static utility class, so a private constructor prevents
     * instantiating it.
     */
    private MultiSequenceUtils() {
    }
    
    
    public static List<SequenceMatcher> reverseMatchers(Collection<? extends SequenceMatcher> matchers) {
        final List<SequenceMatcher> reversedMatchers = new ArrayList<SequenceMatcher>(matchers.size());
        for (final SequenceMatcher matcher : matchers) {
            reversedMatchers.add(matcher.reverse());
        }
        return reversedMatchers;
    }
    
    
    public static Map<SequenceMatcher, SequenceMatcher> mapReverseMatchers(Collection<? extends SequenceMatcher> matchers) {
        final Map<SequenceMatcher, SequenceMatcher> mapReversedMatchers = new IdentityHashMap<SequenceMatcher, SequenceMatcher>();
        for (final SequenceMatcher matcher : matchers) {
            final SequenceMatcher reversed = matcher.reverse();
            mapReversedMatchers.put(reversed, matcher);
        }
        return mapReversedMatchers;
    }
    
    
    public static Set<Byte> bytesAlignedLeft(final int atPosition, 
                                             final MultiSequenceMatcher matcher) {
        final Set<Byte> bytes = new HashSet<Byte>();
        if (atPosition >= 0 && atPosition < matcher.getMaximumLength()) {
            for (final SequenceMatcher sequence : matcher.getSequenceMatchers()) {
                if (atPosition < sequence.length()) {
                    final ByteMatcher lastMatcher = sequence.getMatcherForPosition(atPosition);
                    final byte[] bytesForPosition = lastMatcher.getMatchingBytes();
                    ByteUtilities.addAll(bytesForPosition, bytes);
                }
            }
        }
        return bytes;
    }


    public static Set<Byte> bytesAlignedRight(final int atPosition,
                                              final MultiSequenceMatcher matcher) {
        final Set<Byte> bytes = new HashSet<Byte>();
        if (atPosition >= 0 && atPosition < matcher.getMaximumLength()) {
            for (final SequenceMatcher sequence : matcher.getSequenceMatchers()) {
                final int sequencePosition = sequence.length() - atPosition - 1;
                if (sequencePosition >= 0) {
                    final ByteMatcher lastMatcher = sequence.getMatcherForPosition(sequencePosition);
                    final byte[] bytesForPosition = lastMatcher.getMatchingBytes();
                    ByteUtilities.addAll(bytesForPosition, bytes);
                }
            }
        }
        return bytes;        
    }    
    
    
}
