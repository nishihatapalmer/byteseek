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

package net.byteseek.searcher.multisequence.set_horspool;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.byteseek.io.reader.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.multisequence.MultiSequenceMatcher;
import net.byteseek.matcher.multisequence.MultiSequenceReverseMatcher;
import net.byteseek.matcher.multisequence.MultiSequenceUtils;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.SearchResult;
import net.byteseek.searcher.SearchUtils;
import net.byteseek.searcher.multisequence.AbstractMultiSequenceSearcher;
import net.byteseek.util.object.LazyObject;

/**
 * A class implementing the Set-Horspool Final-Flag algorithm. This is a variant
 * of the {@link SetHorspoolSearcher}, with an optimisation invented by Matt Palmer,
 * and dubbed "final flag" searching.
 * <p>
 * Final flag searching optimises for the case where a match is not found.  In
 * the original algorithm, all that can be done is to shift on by one byte, as
 * we lost any previous bigger shift when we set the final shift to a value of one.
 * Final flag searching allows a bigger shift to occur in this case, by not overwriting the
 * shift for the last byte in a sequence with one.  Instead, it takes the shift
 * that existed previously, and just makes it negative.  A negative shift now indicates
 * that a verification step must be undertaken - but if that verification fails, we still
 * have the bigger shift which existed (albeit negative, but we can simply subtract it
 * rather than add it).
 * <p>
 * Tests indicate that this is often quite a bit faster than the original version, 
 * partly because we get a bigger shift in the case of a mismatch, but also because
 * testing for a final byte match can be done entirely using the shift table, rather
 * than relying on a dedicated class to match the last byte of the sequence.
 * 
 * @author Matt Palmer
 */
public class SetHorspoolFinalFlagSearcher extends AbstractMultiSequenceSearcher {

    private final LazyObject<SearchInfo> forwardInfo;
    private final LazyObject<SearchInfo> backwardInfo;
    
    
    /**
     * Constructs a SetHorspoolFinalFlagSearcher.
     * 
     * @param sequences A MultiSequenceMatcher containing the sequences to be searched for.
     */
    public SetHorspoolFinalFlagSearcher(final MultiSequenceMatcher sequences) {
        super(sequences);
        forwardInfo = new ForwardSearchInfo();
        backwardInfo = new BackwardSearchInfo();
    }
    
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public List<SearchResult<SequenceMatcher>> searchForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        
        // Get the objects needed to search:
        final SearchInfo info = forwardInfo.get();
        final int[] safeShifts = info.shifts;
        final MultiSequenceMatcher verifier = info.verifier;
        
        // Calculate safe bounds for the start of the search:
        final int safeStartPosition = fromPosition > 0? 
                                          fromPosition : 0;
        int searchPosition = safeStartPosition + sequences.getMinimumLength() - 1;
        
        // Calculate safe bounds for the end of the search:
        final int lastPossiblePosition = bytes.length - 1;
        final int lastToPosition = toPosition + sequences.getMaximumLength() - 1;
        final int finalPosition = lastToPosition < lastPossiblePosition?
                                  lastToPosition : lastPossiblePosition;
        
        // Search forwards:
        while (searchPosition <= finalPosition) {
            
            // Shift forwards until we match the last position in the sequence,
            // or we run out of search space (in which case just return not found).
            int shift = safeShifts[bytes[searchPosition] & 0xFF];
            while (shift > 0) {
                searchPosition += shift;
                if (searchPosition > finalPosition) {
                    return SearchUtils.noResults();
                }
                shift = safeShifts[bytes[searchPosition] & 0xFF];
            }
            
            // The last bytes matched - verify the rest of the sequences.
            final Collection<SequenceMatcher> matches = verifier.allMatchesBackwards(bytes, searchPosition);
            if (!matches.isEmpty()) {
                final List<SearchResult<SequenceMatcher>> results = 
                    SearchUtils.resultsBackFromPosition(searchPosition, matches, 
                                                       fromPosition, toPosition);
                if (!results.isEmpty()) {
                    return results;
                }
            }
            
            // No match was found - shift forward by the shift for the current byte:
            // We subtract the shift to add it, as it is negative.
            searchPosition -= shift;
        }
        
        return SearchUtils.noResults();
    }    
        
    
    /**
     * {@inheritDoc}
     */ 
    @Override
    protected List<SearchResult<SequenceMatcher>> doSearchForwards(final WindowReader reader, final long fromPosition, 
        final long toPosition) throws IOException {
            
        // Get the objects needed to search:
        final SearchInfo info = forwardInfo.get();
        final int[] safeShifts = info.shifts;
        final MultiSequenceMatcher verifier = info.verifier;
        
        // Initialise window search:
        final long finalPosition = toPosition + sequences.getMaximumLength() - 1;
        long searchPosition = fromPosition + getMatcher().getMinimumLength() - 1;        
        
        // While there is a window to search in:
        Window window;
        while (searchPosition <= finalPosition &&
               (window = reader.getWindow(searchPosition))!= null) {
            
            // Initialise array search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);
            final int arrayEndPosition = window.length() - 1;
            final long distanceToEnd = finalPosition - window.getWindowPosition();     
            final int lastSearchPosition = distanceToEnd < arrayEndPosition?
                                     (int) distanceToEnd : arrayEndPosition;
            int arraySearchPosition = arrayStartPosition;            
                        
            // Search forwards in this array:
            ARRAY_SEARCH: while (arraySearchPosition <= lastSearchPosition) {

                // Shift forwards until we match the last position in the sequence,
                // or we run out of search space.
                int shift = safeShifts[array[arraySearchPosition] & 0xFF];
                while (shift > 0) {
                    arraySearchPosition += shift;
                    if (arraySearchPosition > lastSearchPosition) {
                        break ARRAY_SEARCH;
                    }
                    shift = safeShifts[array[arraySearchPosition] & 0xFF];
                }

                // The last bytes matched - verify the rest of the sequences.
                final long totalShift = arraySearchPosition - arrayStartPosition;
                final long matchEndPosition = searchPosition + totalShift;
                final Collection<SequenceMatcher> matches = 
                        verifier.allMatchesBackwards(reader, matchEndPosition);
                if (!matches.isEmpty()) {
                    final List<SearchResult<SequenceMatcher>> results = 
                        SearchUtils.resultsBackFromPosition(matchEndPosition, matches, 
                                                            fromPosition, toPosition);
                    if (!results.isEmpty()) {
                        return results;
                    }
                }
                
                // No match was found - shift forward by the shift for the current byte:
                // We subtract the shift to add it, as it is negative.
                arraySearchPosition -= shift;
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
    public List<SearchResult<SequenceMatcher>> searchBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        
        // Get objects needed for the search:
        final SearchInfo info = backwardInfo.get();
        final int[] safeShifts = info.shifts;
        final MultiSequenceMatcher verifier = info.verifier;
        
        // Calculate safe bounds for the start of the search:
        final int firstPossiblePosition = bytes.length - getMatcher().getMinimumLength();        
        int searchPosition = fromPosition < firstPossiblePosition?
                             fromPosition : firstPossiblePosition;
        
        // Calculate safe bounds for the end of the search:
        final int lastPosition = toPosition > 0?
                                 toPosition : 0;
        
        // Search backwards:
        while (searchPosition >= lastPosition) {
            
            // Shift backwards until we match the first position in the
            // sequence, or we run out of search space:
            int shift = safeShifts[bytes[searchPosition] & 0xFF];
            while (shift > 0) {
                searchPosition -= shift;
                if (searchPosition < lastPosition) {
                    return SearchUtils.noResults();
                }
                shift = safeShifts[bytes[searchPosition] & 0xFF];
            }
            
            // The first bytes matched - verify the rest of the sequences:
            final Collection<SequenceMatcher> matches = verifier.allMatches(bytes, searchPosition);
            if (!matches.isEmpty()) {
                return SearchUtils.resultsAtPosition(searchPosition, matches);
            }

            // No match was found - shift backward by the shift for the current byte:
            // We add the shift to subtract it, as it is negative.
            searchPosition += shift;            
        }
        
        return SearchUtils.noResults();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<SearchResult<SequenceMatcher>> doSearchBackwards(final WindowReader reader, 
            final long fromPosition, final long toPosition ) throws IOException {
        
        // Get the objects needed to search:
        final SearchInfo info = backwardInfo.get();
        final int[] safeShifts = info.shifts;
        final MultiSequenceMatcher verifier = info.verifier;        
        
        // Initialise window search:
        long searchPosition = fromPosition;
        
        // Search backwards across the windows:
        Window window;        
        while (searchPosition >= toPosition &&
               (window = reader.getWindow(searchPosition)) != null) {
            
            // Initialise the window search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);   
            final long distanceToEnd = toPosition - window.getWindowPosition();
            final int lastSearchPosition = distanceToEnd > 0?
                                     (int) distanceToEnd : 0;
            int arraySearchPosition = arrayStartPosition;
            
            // Search using the byte array for shifts, using the WindowReader
            // for verifiying the sequence with the sequences:          
            ARRAY_SEARCH: while (arraySearchPosition >= lastSearchPosition) {
                
                // Shift backwards until we match the first position in the sequence,
                // or we run out of search space.
                int shift = safeShifts[array[arraySearchPosition] & 0xFF];
                while (shift > 0) {
                    arraySearchPosition -= shift;
                    if (arraySearchPosition < lastSearchPosition) {
                        break ARRAY_SEARCH;
                    }
                    shift = safeShifts[array[arraySearchPosition] & 0xFF];
                }
                
                // The first byte matched - verify the rest of the sequences.
                final long startMatchPosition = searchPosition - (arrayStartPosition - arraySearchPosition);
                final Collection<SequenceMatcher> matches = verifier.allMatches(reader, startMatchPosition);
                if (!matches.isEmpty()) {
                    return SearchUtils.resultsAtPosition(startMatchPosition, matches); // match found.
                }
                
                // No match was found - shift backward by the shift for the current byte:
                // We add the shift to subtract it, as it is negative.
                arraySearchPosition += shift;                
            }
            
            // No match was found in this array - calculate the current search position:
            searchPosition -= (arrayStartPosition - arraySearchPosition);
        }

        return SearchUtils.noResults();
    }

    
    /**
     * Forces the calculation of the forward search information needed to search forwards.
     */
    @Override
    public void prepareForwards() {
        forwardInfo.get();
    }

    
    /**
     * Forces the calculation of the backwards search information needed to search backwards.
     */
    @Override
    public void prepareBackwards() {
        backwardInfo.get();
    }
    
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + "[sequences:" + sequences + ']'; 
    }


    /**
     * A class holding information needed to search.
     */
    private static class SearchInfo {
        public int[] shifts;
        public MultiSequenceMatcher verifier;
    }    
    
    
    /**
     * A factory class implementing the {@link LazyObject}, creating a 
     * {@SearchInfo} for searching forwards.
     */
    private class ForwardSearchInfo extends LazyObject<SearchInfo> {

        public ForwardSearchInfo() {
        }
        
        /**
         * Calculates the safe shifts to use if searching forwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the end of the sequences.
         */        
        @Override
        protected SearchInfo create() {
            // Get info about the multi sequence sequences:
            final MultiSequenceMatcher matcher = getMatcher();            
            final int minLength = matcher.getMinimumLength();            
            
            // Create the search info object:
            final SearchInfo info = new SearchInfo();
            
            // Create a verifier which works on the reverse sequences of the
            // multi sequence sequences (they will be matched backwards from the 
            // end of the sequences - if they are also reversed they will match
            // the original sequences).
            info.verifier = new MultiSequenceReverseMatcher(matcher);
            
            // Create the array of shifts and set the default shift to the
            // minimum length of all the sequences:
            info.shifts = new int[256];            
            Arrays.fill(info.shifts, minLength);

            // Now set specific shifts for the bytes actually in
            // the sequences.  The shift is the distance of a position
            // from the end of the sequence, but we do not create a shift for
            // the very last position (which would have been zero).  
            // We only create shifts of a distance less than the minimum length
            // of all the sequences to be matched (as we cannot have a shift 
            // bigger than that, or we might miss a smaller sequence).
            for (int distanceFromEnd = minLength - 1; distanceFromEnd > 0; distanceFromEnd--) {
                final Set<Byte> bytesForPosition =
                        MultiSequenceUtils.bytesAlignedRight(distanceFromEnd, matcher);
                for (final byte b : bytesForPosition) {
                    info.shifts[b & 0xFF] = distanceFromEnd;
                }
            }
            
            // Now set the shifts for the final matching bytes to be negative:
            final Set<Byte> bytesForPosition =
                    MultiSequenceUtils.bytesAlignedRight(0, matcher);
            for (final byte b: bytesForPosition) {
                int currentShift = info.shifts[b & 0xFF];
                if (currentShift > 0) {
                    info.shifts[b & 0xFF] = -currentShift;
                }
            }

            return info;
        }
    }
    
    
    /**
     * A factory class implementing the {@link LazyObject}, creating a 
     * {@SearchInfo} for searching backwards.
     */
    private class BackwardSearchInfo extends LazyObject<SearchInfo> {

        public BackwardSearchInfo() {
        }
        
        /**
         * Calculates the safe shifts to use if searching backwards.
         * A safe shift is either the minimum length of all the sequences, if the
         * byte does not appear in the {@link MultiSequenceMatcher}, or
         * the shortest distance it appears from the beginning of the sequences, with
         * zero being the value of the first position in the sequence.
         */        
        @Override
        protected SearchInfo create() {
            // Get info about the multi sequence sequences:
            final MultiSequenceMatcher matcher = getMatcher();            
            final int minLength = matcher.getMinimumLength();            
            
            // Create the search info object:
            final SearchInfo info = new SearchInfo();
            info.verifier = matcher;
            
            // Create the array of shifts and set the default shift to the
            // minimum length of all the sequences:
            info.shifts = new int[256];            
            Arrays.fill(info.shifts, minLength);

            // Now set specific shifts for the bytes actually in
            // the sequences.  The shift is the distance of a position
            // from the start of the sequence, but we do not create a shift for
            // the very first position (which would have a distance of zero).
            // We only create shifts of a distance less than the minimum length
            // of all the sequences to be matched (as we cannot have a shift 
            // bigger than that, or we might miss a smaller sequence).
            for (int distanceFromStart = minLength - 1; distanceFromStart > 0; distanceFromStart--) {
                final Set<Byte> bytesForPosition =
                        MultiSequenceUtils.bytesAlignedLeft(distanceFromStart, matcher);
                for (final byte b : bytesForPosition) {
                    info.shifts[b & 0xFF] = distanceFromStart;
                }
            }
            
            // Now set the shifts for the final matching bytes to be negative:
            final Set<Byte> bytesForPosition =
                    MultiSequenceUtils.bytesAlignedLeft(0, matcher);
            for (final byte b: bytesForPosition) {
                int currentShift = info.shifts[b & 0xFF];
                if (currentShift > 0) {
                    info.shifts[b & 0xFF] = -currentShift;
                }                
            }

            return info;
        }
        
    }        
    
}
