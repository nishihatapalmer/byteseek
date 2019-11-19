/*
 * Copyright Matt Palmer 2009-2016, All rights reserved.
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
 */

package net.byteseek.searcher.sequence;

import java.io.IOException;
import java.nio.charset.Charset;

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;

/**
 * SequenceMatcherSearcher searches for a sequence by trying for a match in each position.
 * In its worst case, where no match is found, if the sequence is m bytes long,
 * and the bytes being searched are n bytes long, it can take O(n * m) to
 * determine there is no match.
 * <p>
 * The algorithm used by this search is exactly the same as that used for the
 * {@link net.byteseek.searcher.MatcherSearcher} searcher.  However, since we know that we are looking for
 * a sequence with a defined length, the search can be more efficiently partitioned
 * between searching directly in byte arrays when the sequence fits, only using
 * the less efficient reader interface when the sequence crosses over windows.
 * <p>
 * Thread safety: this class is immutable, so it is safe to use this
 * searcher in multiple threads simultaneously. However, note that {@link WindowReader}
 * implementations passed in to search methods may not be thread-safe.  If byte
 * arrays are being searched, they must not be modified during searching.
 *
 * @author Matt Palmer
 */
public final class SequenceMatcherSearcher extends AbstractWindowSearcher<SequenceMatcher> {

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set.
     *
     * @param sequence The string to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public SequenceMatcherSearcher(final String sequence) {
        this(sequence, Charset.defaultCharset());
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the charset provided.
     *
     * @param sequence The string to search for.
     * @param charset The charset to encode the string in.
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null.
     */
    public SequenceMatcherSearcher(final String sequence, final Charset charset) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)));
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public SequenceMatcherSearcher(final byte[] sequence) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence));
    }

    /**
     * Constructs a SequenceMatcherSearcher given a {@link SequenceMatcher}.
     * 
     * @param sequence The SequenceMatcher to search for.
     */
    public SequenceMatcherSearcher(final SequenceMatcher sequence) {
        super(sequence);
    }

    @Override
    protected int getSequenceLength() {
        return sequence.length();
    }

    @Override
    public int searchSequenceForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Initialise:
        final SequenceMatcher theSequence = sequence;

        // Calculate safe bounds for the search:
        final int lastPossiblePosition = bytes.length - theSequence.length();
        final int lastPosition = Math.min(toPosition, lastPossiblePosition);
        int searchPosition = Math.max(fromPosition, 0);

        // Search forwards
        while (searchPosition <= lastPosition) {
            if (theSequence.matchesNoBoundsCheck(bytes, searchPosition)) {
                return searchPosition;
            }
            searchPosition++;
        }
        return NO_MATCH_SAFE_SHIFT;
    }

    @Override
    public long doSearchForwards(final WindowReader reader, final long fromPosition,
            final long toPosition) throws IOException {
        // Initialise:
        final SequenceMatcher theSequence = sequence;
        long searchPosition = Math.max(fromPosition, 0);
        
        // While there is data still to search in:
        Window window;
        while (searchPosition <= toPosition &&
               (window = reader.getWindow(searchPosition)) != null) {

            // Calculate bounds for searching over this window:
            final int searchLength = window.length() - reader.getWindowOffset(searchPosition);
            final long endWindowPosition = searchPosition + searchLength - 1;
            final long lastPosition = endWindowPosition < toPosition?
                                      endWindowPosition : toPosition;
            
            // Search forwards up to the end of this window:
            while (searchPosition <= lastPosition) {
                if (theSequence.matches(reader, searchPosition)) {
                    return searchPosition;
                }
                searchPosition++;
            }
        }
        return NO_MATCH_SAFE_SHIFT;
    }
    
    @Override
    public int searchSequenceBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        // Initialise:
        final SequenceMatcher theSequence = sequence;
        
        // Calculate safe bounds for the search:
        final int lastPosition = Math.max(toPosition, 0);
        final int firstPossiblePosition = bytes.length - theSequence.length();
        int searchPosition = Math.min(fromPosition, firstPossiblePosition);
        
        // Search backwards:
        while (searchPosition >= lastPosition) {
            if (theSequence.matchesNoBoundsCheck(bytes, searchPosition)) {
                return searchPosition;
            }
            searchPosition--;
        }
        return NO_MATCH_SAFE_SHIFT;
    }

    @Override
    public long doSearchBackwards(final WindowReader reader, final long fromPosition,
            final long toPosition) throws IOException {
        // Initialise:
        final SequenceMatcher theSequence = sequence;
        //TODO: double check don't need withinLength on doSearchBackwards.  is already called by main method that calls this.
        //long searchPosition = withinLength(reader, fromPosition);
        long searchPosition = fromPosition;
        
        // While there is data to search in:
        Window window;        
        while (searchPosition >= toPosition &&
               (window = reader.getWindow(searchPosition)) != null) {
            
            // Calculate bounds for searching back across this window:
            final long windowStartPosition = window.getWindowPosition();
            final long lastSearchPosition = toPosition > windowStartPosition?
                                            toPosition : windowStartPosition;
            
            // Search backwards:
            while (searchPosition >= lastSearchPosition) {
                if (theSequence.matches(reader, searchPosition)) {
                    return searchPosition;
                }
                searchPosition--;
            }
        }
        return NO_MATCH_SAFE_SHIFT;
    }

    @Override
    public void prepareForwards() {
        // no preparation necessary.
    }

    @Override
    public void prepareBackwards() {
        // no preparation necessary.
    }
    
}
