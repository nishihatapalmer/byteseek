/*
 * Copyright Matt Palmer 2011-12, All rights reserved.
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

package net.domesdaybook.searcher.multisequence;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.matcher.bytes.ByteMatcherFactory;
import net.domesdaybook.matcher.bytes.SimpleByteMatcherFactory;
import net.domesdaybook.matcher.multisequence.MultiSequenceMatcher;
import net.domesdaybook.matcher.multisequence.MultiSequenceUtils;
import net.domesdaybook.matcher.multisequence.MultiSequenceReverseMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.util.object.LazyObject;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;
import net.domesdaybook.searcher.SearchUtils;
import net.domesdaybook.searcher.SearchResult;

/**
 *
 * @author Matt Palmer
 */
public class SetHorspoolSearcher extends AbstractMultiSequenceSearcher {

    private final ByteMatcherFactory byteMatcherFactory;
    private final LazyObject<SearchInfo> forwardInfo;
    private final LazyObject<SearchInfo> backwardInfo;
    
    
    public SetHorspoolSearcher(final MultiSequenceMatcher sequences) {
        super(sequences);
        forwardInfo = new ForwardSearchInfo();
        backwardInfo = new BackwardSearchInfo();
        
        //TODO: provide constructors to allow different byte sequences factories.
        byteMatcherFactory = new SimpleByteMatcherFactory();
    }
    
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public List<SearchResult<SequenceMatcher>> searchForwards(final byte[] bytes, 
            final int fromPosition, final int toPosition) {
        
        // Get the objects needed to search:
        final SearchInfo info = forwardInfo.get();
        final int[] safeShifts = info.shifts;
        final ByteMatcher endOfSequence = info.matcher;      
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
            byte currentByte = bytes[searchPosition];
            while (!endOfSequence.matches(currentByte)) {
                searchPosition += safeShifts[currentByte & 0xff];
                if (searchPosition > finalPosition) {
                    return SearchUtils.noResults();
                }
                currentByte = bytes[searchPosition];                
            }
            
            // The last bytes matched - verify the rest of the sequences.
            final Collection<SequenceMatcher> matches = verifier.allMatchesBackwards(bytes, searchPosition);
            if (!matches.isEmpty()) {
                // Build a result list, filtering out any which don't fall within
                // the "from" or "to" positions of the search.
                final List<SearchResult<SequenceMatcher>> results = 
                    SearchUtils.resultsBackFromPosition(searchPosition, matches, 
                                                        fromPosition, toPosition);
                if (!results.isEmpty()) {
                    return results;
                }
            }
            
            // No match was found - shift forward by the shift for the current byte:
            searchPosition += safeShifts[currentByte & 0xff];
        }
        
        return SearchUtils.noResults();
    }    
        
    
    /**
     * Searches forward using the Boyer Moore Horspool algorithm, using 
     * byte arrays from Windows to handle shifting, and the Reader interface
     * on the SequenceMatcher to verify whether a match exists.
     */
    @Override
    protected List<SearchResult<SequenceMatcher>> doSearchForwards(final Reader reader, final long fromPosition, 
        final long toPosition) throws IOException {
            
        // Get the objects needed to search:
        final SearchInfo info = forwardInfo.get();
        final int[] safeShifts = info.shifts;
        final ByteMatcher endOfSequence = info.matcher;      
        final MultiSequenceMatcher verifier = info.verifier;
        
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
                        
            // Search forwards in this array:
            ARRAY_SEARCH: while (arraySearchPosition <= lastSearchPosition) {

                // Shift forwards until we match the last position in the sequence,
                // or we run out of search space.
                byte currentByte = array[arraySearchPosition];
                while (!endOfSequence.matches(currentByte)) {
                    arraySearchPosition += safeShifts[currentByte & 0xff];
                    if (arraySearchPosition > lastSearchPosition) {
                        break ARRAY_SEARCH; // outside the array, move on.
                    }
                    currentByte = array[arraySearchPosition];                
                }

                // The last bytes matched - verify the rest of the sequences.
                final long matchEndPosition = searchPosition + arraySearchPosition - arrayStartPosition;
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
                arraySearchPosition += safeShifts[currentByte & 0xff];
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
        final ByteMatcher startOfSequence = info.matcher;
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
            byte currentByte = bytes[searchPosition];
            while (!startOfSequence.matches(currentByte)) {
                searchPosition -= safeShifts[currentByte & 0xFF];
                if (searchPosition < lastPosition) {
                    return SearchUtils.noResults();
                }
                currentByte = bytes[searchPosition];
            }
            
            // The first bytes matched - verify the rest of the sequences:
            final Collection<SequenceMatcher> matches = verifier.allMatches(bytes, searchPosition);
            if (!matches.isEmpty()) {
                return SearchUtils.resultsAtPosition(searchPosition, matches);
            }

            // No match was found - shift backward by the shift for the current byte:
            searchPosition -= safeShifts[currentByte & 0xff];            
        }
        
        return SearchUtils.noResults();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<SearchResult<SequenceMatcher>> doSearchBackwards(final Reader reader, 
            final long fromPosition, final long toPosition ) throws IOException {
        
        // Initialise
        final SearchInfo info = backwardInfo.get();
        final int[] safeShifts = info.shifts;
        final ByteMatcher startOfSequence = info.matcher;
        final MultiSequenceMatcher verifier = info.verifier;        
        long searchPosition = fromPosition;
        
        // Search backwards across the windows:
        Window window;        
        while (searchPosition >= toPosition &&
               (window = reader.getWindow(searchPosition))!= null) {
            
            // Initialise the window search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);   
            final long distanceToEnd = toPosition - window.getWindowPosition();
            final int lastSearchPosition = distanceToEnd > 0?
                                     (int) distanceToEnd : 0;
            int arraySearchPosition = arrayStartPosition;
            
            // Search using the byte array for shifts, using the Reader
            // for verifiying the sequence with the sequences:          
            ARRAY_SEARCH: while (arraySearchPosition >= lastSearchPosition) {
                
                // Shift backwards until we match the first position in the sequence,
                // or we run out of search space.
                byte currentByte = array[arraySearchPosition];
                while (!startOfSequence.matches(currentByte)) {
                    arraySearchPosition -= safeShifts[currentByte & 0xff];
                    if (arraySearchPosition < lastSearchPosition) {
                        break ARRAY_SEARCH;
                    }
                    currentByte = array[arraySearchPosition];
                }
                
                // The first byte matched - verify the rest of the sequences.
                final long startMatchPosition = searchPosition - (arrayStartPosition - arraySearchPosition);
                final Collection<SequenceMatcher> matches = verifier.allMatches(reader, startMatchPosition);
                if (!matches.isEmpty()) {
                    return SearchUtils.resultsAtPosition(startMatchPosition, matches); // match found.
                }
                
                // No match was found - shift backward by the shift for the current byte:
                arraySearchPosition -= safeShifts[currentByte & 0xff];                
            }
            
            // No match was found in this array - calculate the current search position:
            searchPosition -= (arrayStartPosition - arraySearchPosition);
        }

        return SearchUtils.noResults();
    }

    
    @Override
    public void prepareForwards() {
        forwardInfo.get();
    }

    
    @Override
    public void prepareBackwards() {
        backwardInfo.get();
    }
    

    private static class SearchInfo {
        public int[] shifts;
        public ByteMatcher matcher;
        public MultiSequenceMatcher verifier;
    }    
    
    
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
            final MultiSequenceMatcher sequences = getMatcher();            
            final int minLength = sequences.getMinimumLength();            
            
            // Create the search info object:
            final SearchInfo info = new SearchInfo();
            
            // Create a byte sequences for the last position of all the sequences:
            final Set<Byte> allLastBytes =
                    MultiSequenceUtils.bytesAlignedRight(0, sequences);
            info.matcher = byteMatcherFactory.create(allLastBytes);
            
            // Create a verifier which works on the reverse sequences of the
            // multi sequence sequences (they will be matched backwards from the 
            // end of the sequences - if they are also reversed they will match
            // the original sequences).
            info.verifier = new MultiSequenceReverseMatcher(sequences);
            
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
                        MultiSequenceUtils.bytesAlignedRight(distanceFromEnd, sequences);
                for (final byte b : bytesForPosition) {
                    info.shifts[b & 0xFF] = distanceFromEnd;
                }
            }

            return info;
        }
    }
    
    
    
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
            final MultiSequenceMatcher sequences = getMatcher();            
            final int minLength = sequences.getMinimumLength();            
            
            // Create the search info object:
            final SearchInfo info = new SearchInfo();
            
            // Create a byte sequences for the first position of all the sequences:
            final Set<Byte> allFirstBytes =
                    MultiSequenceUtils.bytesAlignedLeft(0, sequences);
            info.matcher = byteMatcherFactory.create(allFirstBytes);
            
            info.verifier = sequences;
            
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
                        MultiSequenceUtils.bytesAlignedLeft(distanceFromStart, sequences);
                for (final byte b : bytesForPosition) {
                    info.shifts[b & 0xFF] = distanceFromStart;
                }
            }

            return info;
        }
        
    }        
    
}
