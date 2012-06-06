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

package net.domesdaybook.searcher.sequence;

import net.domesdaybook.util.object.LazyObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.domesdaybook.matcher.bytes.AnyByteMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.bytes.ByteMatcher;
import net.domesdaybook.reader.Window;
import net.domesdaybook.searcher.SearchUtils;
import net.domesdaybook.searcher.SearchResult;


/**
 * HorspoolFinalFlagSearcher searches for a sequence using a slightly modified form
 * of the Boyer-Moore-Horspool algorithm. 
 * <p>
 * The basic idea consists of flagging the bytes which appear at the end of 
 * a pattern in the shift table, while preserving the shift values of the 
 * existing byte values, without needing additional storage.  It does this by
 * making any shift values that exist for bytes of the final pattern position
 * negative.  Other methods of flagging the final bytes could also be used,
 * (e.g. using bit masking) but making a signed integer negative is quite natural
 * and uses less operations, since we can simply subtract a negative value to 
 * shift forwards (or add the negative value to shift backwards). This concept
 * can also be applied to other shift-based search algorithms.
 * <p>
 * In some ways, it is similar to the Tuned-Boyer-Moore-Horspool algorithm, which
 * makes the final shift value zero and preserves the original shift value in another 
 * variable.  However, this cannot handle byte classes (multiple bytes at one position)
 * without requiring another independent shift table for the lookup (although it
 * does permit a nice loop unrolling which isn't possible for my modification).
 * <p>
 * This modification was invented by the author of this code (Matt Palmer) 
 * in February 2012. I have not seen this idea applied before, although
 * it is possible that it has been independently discovered by other people.
 * Profiling indicates this implementation is about twice as fast as the 
 * Boyer-Moore-Horspool implementation, probably due to a simpler shift-loop
 * and not needing a special matcher object to see if the last bytes matched).
 * <p>
 * This type of search algorithm does not need to examine every byte in 
 * the bytes being searched.  It is sub-linear, in general needing to
 * examine less bytes than actually occur in the bytes being searched.
 * <p>
 * It pre-computes a table of minimum safe shifts for the search pattern. 
 * Given a byte in the bytes being searched, the shift tells us how many 
 * bytes we can safely shift ahead without missing a possible match.  It also
 * tells us which values appear at the end of a pattern, by making these negative.
 * <p>
 * It proceeds by searching for the search pattern backwards,
 * from the last position in the pattern to the first.  The safe shift is looked 
 * up in the table using the value of the byte in the search text at the current
 * position.  If the shift is greater than zero, we know that we can move the
 * current position along by that amount.  If the shift is less than zero, this means 
 * there may be a match at this position.  If there is no match at that position,
 * then we shift by the negative value of the shift (e.g. we subtract the negative
 * shift to move forwards, or vice versa if searching backwards).
 * <p>
 * A simple example is looking for the bytes 'XYZ' in the sequence 'ABCDEFGXYZ'.
 * The first attempt is to match 'Z', and we find the byte 'C'.  Since 'C' does
 * not appear anywhere in 'XYZ', we can safely shift 3 bytes ahead and not risk
 * missing a possible match.  In general, the safe shift is either the length of
 * the pattern, if that byte does not appear in the pattern, or the shortest 
 * distance from the end of the pattern where that byte appears.  The shift values
 * may be negative to flag that this byte also appears at the end of a pattern.
 * <p>
 * One initially counter-intuitive consequence of this type of search is that
 * the longer the pattern you are searching for, the better the performance
 * usually is, as the possible shifts will be correspondingly bigger.
 * 
 * @author Matt Palmer
 */
public final class HorspoolFinalFlagSearcher extends AbstractSequenceSearcher {

    private final LazyObject<SearchInfo> forwardInfo;
    private final LazyObject<SearchInfo> backwardInfo;

    /**
     * Constructs a BoyerMooreHorspool searcher given a {@link SequenceMatcher}
     * to search for.
     * 
     * @param sequence The SequenceMatcher to search for.
     */
    public HorspoolFinalFlagSearcher(final SequenceMatcher sequence) {
        super(sequence);
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
        final SequenceMatcher verifier = info.verifier;
        
        // Calculate safe bounds for the start of the search:
        final int lastMatcherPosition = getMatcher().length() - 1;                
        int searchPosition = fromPosition > 0?
                             fromPosition + lastMatcherPosition : lastMatcherPosition;
        
        // Calculate safe bounds for the end of the search:
        final int lastPossiblePosition = bytes.length - 1;
        final int lastPossibleSearchPosition = toPosition + lastMatcherPosition;
        final int finalPosition = lastPossibleSearchPosition < lastPossiblePosition?
                                  lastPossibleSearchPosition : lastPossiblePosition;
        
        // Search forwards:
        while (searchPosition <= finalPosition) {
            
            // Shift forward until there is a negative shift or we run out of
            // search space.
            int shift = safeShifts[bytes[searchPosition] & 0xFF];
            while (shift > 0) {
                searchPosition += shift;
                if (searchPosition > finalPosition) {
                    return SearchUtils.noResults();
                }
                shift = safeShifts[bytes[searchPosition] & 0xFF];
            }
            
            // The last byte matched - verify there is a complete match:
            final int startMatchPosition = searchPosition - lastMatcherPosition;
            if (verifier.matchesNoBoundsCheck(bytes, startMatchPosition)) {
                return SearchUtils.singleResult(startMatchPosition, matcher); // match found.
            }
            
            // No match was found - shift forward by the next closest shift for
            // the current byte. Subtract because the shift is negative.
            searchPosition -= shift;
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
        final SequenceMatcher verifier = info.verifier;
        
        // Initialise window search:
        final long endSequencePosition = matcher.length() - 1;
        final long finalPosition = toPosition + endSequencePosition;
        long searchPosition = fromPosition + endSequencePosition;        
        
        // While there is a window to search in:
        Window window;      
        while (searchPosition <= finalPosition &&
               (window = reader.getWindow(searchPosition)) != null) {
            
            // Initialise array search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);
            final int arrayEndPosition = window.length() - 1;
            final int lastMatcherPosition = matcher.length() - 1;
            final long distanceToEnd = finalPosition - window.getWindowPosition() + lastMatcherPosition;     
            final int lastSearchPosition = distanceToEnd < arrayEndPosition?
                                     (int) distanceToEnd : arrayEndPosition;
            int arraySearchPosition = arrayStartPosition;            
                        
            // Search forwards in this array:
            ARRAY_SEARCH: while (arraySearchPosition <= lastSearchPosition) {

                // Shift forward until there is a negative shift or we run out of
                // search space.
                int shift = safeShifts[array[arraySearchPosition] & 0xFF];
                while (shift > 0) {
                    arraySearchPosition += shift;
                    if (arraySearchPosition > lastSearchPosition) {
                        break ARRAY_SEARCH; // outside the array, move on.
                    }
                    shift = safeShifts[array[arraySearchPosition] & 0xFF];
                }
                 
                // The last byte matched - verify there is a complete match:
                final long totalShift = arraySearchPosition - arrayStartPosition;
                final long matchPosition = searchPosition + totalShift - endSequencePosition;
                if (verifier.matches(reader, matchPosition)) {
                    return SearchUtils.singleResult(matchPosition, matcher); // match found.
                }
                
                // No match was found - shift forward by the next closest shift for
                // the current byte. Subtract because the shift is negative.
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
        final SequenceMatcher verifier = info.verifier;
        
        // Calculate safe bounds for the start of the search:
        final int firstPossiblePosition = bytes.length - getMatcher().length();        
        int searchPosition = fromPosition < firstPossiblePosition?
                             fromPosition : firstPossiblePosition;
        
        // Calculate safe bounds for the end of the search:
        final int lastPosition = toPosition > 0?
                                 toPosition : 0;
        
        // Search backwards:
        while (searchPosition >= lastPosition) {
            
            // Shift backwards until there is a negative shift or we run out of
            // search space.
            int shift = safeShifts[bytes[searchPosition] & 0xFF];
            while (shift > 0) {
                searchPosition -= shift;
                if (searchPosition < lastPosition) {
                    return SearchUtils.noResults();
                }
                shift = safeShifts[bytes[searchPosition] & 0xFF];
            }
            
            // The first byte matched - verify there is a complete match:
            // A null verifier means we don't need a verifier, as the sequence
            // is only one byte long - which we have just matched above.
            if (verifier == null || verifier.matchesNoBoundsCheck(bytes, searchPosition + 1)) {
                return SearchUtils.singleResult(searchPosition, matcher); // match found.
            }

            // No match was found - shift backward by the shift for the current byte.
            // We add the shift, because it is negative.
            searchPosition += shift;     
        }
        
        return SearchUtils.noResults();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    protected List<SearchResult<SequenceMatcher>> doSearchBackwards(final Reader reader, 
            final long fromPosition, final long toPosition ) throws IOException {
        
        // Initialise search:
        final SearchInfo info = backwardInfo.get();
        final int[] safeShifts = info.shifts;
        final SequenceMatcher verifier = info.verifier;        
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
            // for verifiying the sequence with the matcher:          
            ARRAY_SEARCH: while (arraySearchPosition >= lastSearchPosition) {
                
                // Shift backward until there is a negative shift or we run out of
                // search space.
                int shift = safeShifts[array[arraySearchPosition] & 0xFF];
                while (shift > 0) {
                    arraySearchPosition -= shift;
                    if (arraySearchPosition < lastSearchPosition) {
                        break ARRAY_SEARCH; // outside the array, move on.
                    }
                    shift = safeShifts[array[arraySearchPosition] & 0xFF];
                }
                
                // The first byte matched - verify there is a complete match:
                // A null verifier means we don't need a verifier, as the sequence
                // is only one byte long - which we have just matched above.
                final int totalShift = arrayStartPosition - arraySearchPosition;
                final long startMatchPosition = searchPosition - totalShift;
                if (verifier == null || verifier.matches(reader, startMatchPosition + 1)) {
                    return SearchUtils.singleResult(startMatchPosition, matcher); // match found.
                }
                
                // No match was found - shift backward by the shift for the current byte.
                // We add the shift, because it is negative.
                arraySearchPosition += shift;                
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
    public void prepareForwards() {
        forwardInfo.get();
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareBackwards() {
        backwardInfo.get();
    }
    
    
    private static class SearchInfo {
        public int[] shifts;
        public SequenceMatcher verifier;
    }
    
    
    private class ForwardSearchInfo extends LazyObject<SearchInfo> {

        public ForwardSearchInfo() {
        }
        
        /**
         * Calculates the safe shifts to use if searching forwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the end of the matcher.
         */        
        @Override
        protected SearchInfo create() {
            // Get info about the matcher:
            final SequenceMatcher sequence = getMatcher();            
            final int sequenceLength = sequence.length();            
            
            // Create the search info object:
            final SearchInfo info = new SearchInfo();
            final int lastPosition = sequenceLength - 1;
            if (lastPosition == 0) {
                info.verifier = AnyByteMatcher.ANY_BYTE_MATCHER;
            } else {
                info.verifier = sequence.subsequence(0, lastPosition);
            }
            info.shifts = new int[256];            

            // Set the default shift to the length of the sequence
            Arrays.fill(info.shifts, sequenceLength);

            // Now set specific shifts for the bytes actually in
            // the sequence itself.  The shift is the distance of a position
            // from the end of the sequence, but we do not create a shift for
            // the very last position.
            for (int sequencePos = 0; sequencePos < lastPosition; sequencePos++) {
                final ByteMatcher aMatcher = sequence.getMatcherForPosition(sequencePos);
                final byte[] matchingBytes = aMatcher.getMatchingBytes();
                final int distanceFromEnd = sequenceLength - sequencePos - 1;
                for (final byte b : matchingBytes) {
                    info.shifts[b & 0xFF] = distanceFromEnd;
                }
            }
            // Make last position byte values negative, to flag that these
            // bytes appear at the final position of the pattern, but without
            // destroying the shift value that already exists for those bytes.
            final ByteMatcher lastMatcher = sequence.getMatcherForPosition(lastPosition);
            final byte[] matchingBytes = lastMatcher.getMatchingBytes();
            for (final byte b : matchingBytes) {
                info.shifts[b & 0xFF] = -info.shifts[b & 0xFF];
            }

            return info;
        }
    }
    
    
    private class BackwardSearchInfo extends LazyObject<SearchInfo> {

        public BackwardSearchInfo() {
        }
        
        /**
         * Calculates the safe shifts to use if searching backwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the beginning of the matcher, with
         * zero being the value of the first position in the sequence.
         * If a shift is negative, it indicates that those bytes are in the
         * first position in the sequence, as well as giving the next closest
         * safe shift for those bytes (the positive value).
         */        
        @Override
        protected SearchInfo create() {
            // Get info about the matcher:
            final SequenceMatcher sequence = getMatcher();            
            final int sequenceLength = sequence.length();            
            
            // Create the search info object:
            final SearchInfo info = new SearchInfo();
            final int lastPosition = sequenceLength - 1;
            if (lastPosition == 0) {
                info.verifier = null;
            } else {
                info.verifier = sequence.subsequence(1, sequenceLength);
            }
            info.shifts = new int[256];            

            // Set the default shift to the length of the sequence
            Arrays.fill(info.shifts, sequenceLength);

            // Now set specific byte shifts for the bytes actually in
            // the sequence itself.  The shift is the position in the sequence,
            // but we do not create a shift for the first position 0.
            for (int sequencePos = lastPosition; sequencePos > 0; sequencePos--) {
                final ByteMatcher aMatcher = sequence.getMatcherForPosition(sequencePos);
                byte[] matchingBytes = aMatcher.getMatchingBytes();
                for (final byte b : matchingBytes) {
                    info.shifts[b & 0xFF] = sequencePos;
                }
            }
            // Make first position byte values negative, to flag that these
            // bytes appear at the "final" position of the pattern, but without
            // destroying the shift value that already exists for those bytes.
            final ByteMatcher firstMatcher = sequence.getMatcherForPosition(0);
            final byte[] matchingBytes = firstMatcher.getMatchingBytes();
            for (final byte b : matchingBytes) {
                info.shifts[b & 0xFF] = -info.shifts[b & 0xFF];
            }
            
            return info;
        }
        
    }    

}
