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
package net.byteseek.searcher.multisequence.wu_manber;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.multisequence.MultiSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.SearchResult;
import net.byteseek.searcher.SearchUtils;

/**
 * THIS CLASS IS NOT YET FULLY IMPLEMENTED.
 * <p>
 * An implementation of the Wu-Manber search algorithm that works with any block
 * size. It extends the {@link AbstractWuManberSearcher} 
 * - see that class for more details of the Wu-Manber algorithm itself.
 * <p>
 * Note that there are optimised implementations of this algorithm for a block size of
 * one (@link WuManberOneByteSearcher} and with a block size of two {@link WuManberTwoByteSearcher}.
 * 
 * @author Matt Palmer
 */

public class WuManberMultiByteSearcher extends AbstractWuManberSearcher {

    /**
     * Constructs a WuManberMultiByteSearcher.
     * 
     * @param matcher The MultiSequenceMatcher containing the sequences to search for.
     * @param blockSize The block size to use when searching.
     */
    public WuManberMultiByteSearcher(final MultiSequenceMatcher matcher,
                                      final int blockSize) {
        super(matcher, blockSize);
        if (matcher.getMinimumLength() < blockSize) {
            final String message = String.format(
                    "Minimum sequence length (%d) cannot be smaller than the block size: %d",
                     matcher.getMinimumLength(), blockSize);
            throw new IllegalArgumentException(message);
        }            
    }        

    @Override
    protected List<SearchResult<SequenceMatcher>> doSearchForwards(WindowReader reader, long searchPosition, long lastSearchPosition) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<SearchResult<SequenceMatcher>> doSearchBackwards(WindowReader reader, long searchPosition, long lastSearchPosition) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<SearchResult<SequenceMatcher>> searchForwards(byte[] bytes, int fromPosition, int toPosition) {
        // Get info needed to search with:
        final SearchInfo info = forwardInfo.get();
        final int[] safeShifts = info.shifts;
        final int hashBitMask = safeShifts.length - 1; // safe shifts is a power of two size.
        final MultiSequenceMatcher backMatcher = info.matcher;

        // Calculate safe bounds for the search:
        final int lastPossiblePosition = bytes.length - 1;
        final int lastPosition = toPosition < lastPossiblePosition ?
                                 toPosition : lastPossiblePosition;
        final int lastMinimumPosition = sequences.getMinimumLength() - 1;
        int searchPosition = fromPosition > 0 ?
                             fromPosition + lastMinimumPosition : lastMinimumPosition;


        // Search forwards:
        while (searchPosition <= lastPosition) {

            // Calculate the hash of the current block:
            int blockHash = 0;
            for (int blockPosition = searchPosition - blockSize + 1; 
                     blockPosition <= searchPosition; blockPosition++) {
                final int value = bytes[blockPosition] & 0xFF;
                blockHash = ((blockHash << 5) - blockHash) * value;
            }

            // Get the safe shift for this block:
            final int safeShift = safeShifts[blockHash & hashBitMask];

            // Can we shift safely?
            if (safeShift == 0) {

                // No safe shift - see if we have any matches:
                final Collection<SequenceMatcher> matches =
                        backMatcher.allMatchesBackwards(bytes, searchPosition);
                if (!matches.isEmpty()) {

                    // See if any of the matches are within the bounds of the search:
                    final List<SearchResult<SequenceMatcher>> results = 
                        SearchUtils.resultsBackFromPosition(searchPosition, matches, 
                                                            fromPosition, toPosition);
                    if (!results.isEmpty()) {
                        return results;
                    }
                }
                searchPosition++; // no safe shift other than to advance one on.

            } else { // we have a safe shift, move on:
                searchPosition += safeShift; 
            }
        }
        return SearchUtils.noResults();
    }

    @Override
	public List<SearchResult<SequenceMatcher>> searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
}
