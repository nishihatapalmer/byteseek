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

import net.byteseek.io.reader.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.multisequence.MultiSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.SearchResult;
import net.byteseek.searcher.SearchUtils;

/**
 * An implementation of the Wu-Manber search algorithm that works with a block
 * size of two bytes. It extends the {@link AbstractWuManberSearcher} 
 * - see that class for more details of the Wu-Manber algorithm itself.
 * 
 * @author Matt Palmer
 */
public class WuManberTwoByteSearcher extends AbstractWuManberSearcher {
    
    /**
     * Constructs a WuManberTwoByteSearcher.
     * 
     * @param matcher The MultiSequenceMatcher containing the sequences to search for.
     */
    public WuManberTwoByteSearcher(final MultiSequenceMatcher matcher) {
        super(matcher, 2);
        if (matcher.getMinimumLength() < 2) {
            throw new IllegalArgumentException("A minimum sequence length of at least two is required.");
        }
    }        

    
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<SearchResult<SequenceMatcher>> doSearchForwards(final WindowReader reader, 
            final long fromPosition, final long toPosition) throws IOException {
        // Get info needed to search with:
        final SearchInfo info = forwardInfo.get();
        final int[] safeShifts = info.shifts;
        final MultiSequenceMatcher backMatcher = info.matcher;
        final int hashBitMask = safeShifts.length - 1; // safe shifts is a power of two size.            

        // Initialise window search:
        final long finalPosition = toPosition + sequences.getMaximumLength() - 1;
        long searchPosition = fromPosition + sequences.getMinimumLength() - 1;       

        // While there is a window to search in:
        Window window;             
        while (searchPosition <= finalPosition &&
               (window = reader.getWindow(searchPosition)) != null) {

            // Initialise array search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);
            final int arrayEndPosition = window.length() - 1;
            final long distanceToEnd = finalPosition - window.getWindowPosition();     
            final int lastSearchPosition = distanceToEnd < arrayEndPosition?
                                     (int) distanceToEnd : arrayEndPosition;
            int arraySearchPosition = arrayStartPosition;            

            if (arraySearchPosition == 0) {

            }

            // Search forwards in this array:
            // Use the readByte method on the reader to get the first byte of 
            // the block to hash, as it could be in a prior window.
            int firstBlockByte = reader.readByte(searchPosition - 1); 
            while (arraySearchPosition <= lastSearchPosition) {

                // Calculate the hash of the current block:
                final int lastBlockByte = array[arraySearchPosition] & 0xFF;
                if (firstBlockByte < 0) firstBlockByte = array[arraySearchPosition - 1] & 0xFF;
                final int blockHash = (firstBlockByte << 5) - firstBlockByte + lastBlockByte;

                // Get the safe shift for this block:
                final int safeShift = safeShifts[blockHash & hashBitMask];

                if (safeShift == 0) {
                    // see if we have a match:
                    final long matchEndPosition = searchPosition + arraySearchPosition - arrayStartPosition;
                    final Collection<SequenceMatcher> matches =
                            backMatcher.allMatchesBackwards(reader, matchEndPosition);
                    if (!matches.isEmpty()) {
                        // See if any of the matches are within the bounds of the search:
                        final List<SearchResult<SequenceMatcher>> results = 
                            SearchUtils.resultsBackFromPosition(matchEndPosition, matches,
                                                                fromPosition, toPosition);
                        if (!results.isEmpty()) {
                            return results;
                        }
                    }
                    arraySearchPosition++;
                    firstBlockByte = lastBlockByte;
                } else {
                    arraySearchPosition += safeShift;
                    firstBlockByte = -1;
                } 
            } 

            // No match was found in this array - calculate the current search position:
            searchPosition += arraySearchPosition - arrayStartPosition;
        }

        return SearchUtils.noResults();                    
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<SearchResult<SequenceMatcher>> searchForwards(final byte[] bytes, 
            final int fromPosition, final int toPosition) {
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
        // Search position will always be greater than zero, as lastMinimumPosition
        // must be at least one (minimum length of sequences must be at least 2, enforced in constructor).
        int searchPosition = fromPosition > 0 ?
                             fromPosition + lastMinimumPosition : lastMinimumPosition;

        // Search forwards:
        int firstBlockByte = -1;
        while (searchPosition <= lastPosition) {

            // Calculate the hash of the current block:
            final int lastBlockByte = bytes[searchPosition] & 0xFF;
            if (firstBlockByte < 0) firstBlockByte = bytes[searchPosition - 1] & 0xFF;
            final int blockHash = (firstBlockByte << 5) - firstBlockByte + lastBlockByte; 

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
                firstBlockByte = lastBlockByte;

            } else { // we have a safe shift, move on:
                searchPosition += safeShift; 
                firstBlockByte = -1;
            }
        }
        return SearchUtils.noResults();
    }


    /**
     * {@inheritDoc}
     */
    //FIXME: copy of do search forwards gradually evolving to a backwards search...
    @Override
    protected List<SearchResult<SequenceMatcher>> doSearchBackwards(final WindowReader reader,
            final long fromPosition, final long toPosition) throws IOException {
        // Initialise
        final SearchInfo info = forwardInfo.get();
        final int[] safeShifts = info.shifts;
        final MultiSequenceMatcher matcher = info.matcher;
        final int hashBitMask = safeShifts.length - 1; // safe shifts is a power of two size.            
        long searchPosition = fromPosition;       

        // While there is a window to search in:
        Window window;             
        while (searchPosition >= toPosition &&
               (window = reader.getWindow(searchPosition)) != null) {

            // Initialise array search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);   
            final long distanceToEnd = toPosition - window.getWindowPosition();
            final int lastSearchPosition = distanceToEnd > 0?
                                     (int) distanceToEnd : 0;
            int arraySearchPosition = arrayStartPosition;

            // Search backwards in this array:
            // Use the readByte method on the reader to get the first byte of 
            // the block to hash, as it could be in the next window.
            int lastBlockByte = reader.readByte(searchPosition + 1); 
            while (arraySearchPosition >= lastSearchPosition) {

                // Calculate the hash of the current block:
                final int firstBlockByte = array[arraySearchPosition] & 0xFF;
                if (lastBlockByte < 0) lastBlockByte = array[arraySearchPosition + 1];
                final int blockHash = (firstBlockByte << 5) - firstBlockByte + lastBlockByte;

                // Get the safe shift for this block:
                final int safeShift = safeShifts[blockHash & hashBitMask];

                if (safeShift == 0) {
                    // see if we have a match:
                    final long startMatchPosition = searchPosition - (arrayStartPosition - arraySearchPosition);
                    final Collection<SequenceMatcher> matches =
                            matcher.allMatches(reader, startMatchPosition);
                    if (!matches.isEmpty()) {
                        // See if any of the matches are within the bounds of the search:
                        final List<SearchResult<SequenceMatcher>> results = 
                            SearchUtils.resultsAtPosition(startMatchPosition, matches);
                        if (!results.isEmpty()) {
                            return results;
                        }
                    }
                    // No safe shift other than to move back one.
                    arraySearchPosition--;
                    lastBlockByte = firstBlockByte;
                } else { // shift by the safe shift backwards.
                    arraySearchPosition -= safeShift;
                    lastBlockByte = -1;
                } 
            } 

            // No match was found in this array - calculate the current search position:
            searchPosition -= (arrayStartPosition - arraySearchPosition);
        }

        return SearchUtils.noResults();                    
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<SearchResult<SequenceMatcher>> searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
        // Get info needed to search with:
        final SearchInfo info = backwardInfo.get();
        final int[] safeShifts = info.shifts;
        final MultiSequenceMatcher verifier = info.matcher;
        final int hashBitMask = safeShifts.length - 1; // safe shifts is a power of two size.

        // Calculate safe bounds for the search:
        final int finalPosition = toPosition > 0 ?
                                  toPosition : 0;
        final int lastPossiblePosition = bytes.length - sequences.getMinimumLength();
        int searchPosition = fromPosition < lastPossiblePosition ?
                             fromPosition : lastPossiblePosition;

        // Search backwards:
        int lastBlockByte = -1; 
        while (searchPosition >= finalPosition) {

            // Get the safe shift for this byte:
            final int firstBlockByte = bytes[searchPosition] & 0xFF;
            if (lastBlockByte < 0) lastBlockByte = bytes[searchPosition + 1] & 0xFF;
            final int blockHash = (firstBlockByte << 5) - firstBlockByte + lastBlockByte; 
            final int safeShift = safeShifts[blockHash & hashBitMask];

            // Can we shift safely?
            if (safeShift == 0) {

                // No safe shift - see if we have any matches:
                final Collection<SequenceMatcher> matches =
                        verifier.allMatches(bytes, searchPosition);
                if (!matches.isEmpty()) {
                    return SearchUtils.resultsAtPosition(searchPosition, matches);
                }
                searchPosition--; // no safe shift other than to advance one on.
                lastBlockByte = firstBlockByte;

            } else { // we have a safe shift, move on:
                searchPosition -= safeShift; 
                lastBlockByte = -1;
            }
        }
        return SearchUtils.noResults();
    }    
}
