/*
 * Copyright Matt Palmer 2009-2017, All rights reserved.
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

package net.byteseek.searcher.sequence;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.AnyByteMatcher;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.lazy.DoubleCheckImmutableLazyObject;
import net.byteseek.utils.lazy.LazyObject;
import net.byteseek.utils.factory.ObjectFactory;


/**
 * UnrolledHorspoolSearcher searches for a sequence using the
 * Boyer-Moore-Horspool algorithm, and "unrolls" the main search loop
 * by repeatedly shifting until there is a match to the last pattern position,
 * before attempting verification of the full pattern.  This achieves faster
 * performance than the standard Horspool search as full pattern verification is
 * expensive compared with matching a single byte (avoids loop initialisation overhead).
 * <p>
 * This type of search algorithm does not need to examine every byte in 
 * the bytes being searched.  It is sub-linear, in general needing to
 * examine less bytes than actually occur in the bytes being searched.
 * <p>
 * It pre-computes a table of minimum safe shifts for the search pattern. 
 * Given a byte in the bytes being searched, the shift tells us how many 
 * bytes we can safely shift ahead without missing a possible match.  
 * <p>
 * It proceeds by looking for a match of a byte in the text with the last position
 * in the pattern (or the first, if searching backwards).  If there is no match, 
 * then a safe shift is found by looking up the byte in the safe shift table.
 * If there is a match to the last character, then the rest of the sequence
 * is verified.  If this does not match, then again we shift by the safe shift
 * for the byte at the end of the pattern.
 * <p>
 * A simple example is looking for the bytes 'XYZ' in the sequence 'ABCDEFGXYZ'.
 * The first attempt is to match 'Z', and we find the byte 'C'.  
 * Since 'C' does  not appear anywhere in 'XYZ', we can safely shift 3 bytes ahead
 * and not risk missing a possible match.  In general, the safe shift is either 
 * the length of the pattern, if that byte does not appear in the pattern, 
 * or the shortest distance from the end of the pattern where that byte appears.
 * <p>
 * One initially counter-intuitive consequence of this type of search is that
 * the longer the pattern you are searching for, the better the performance
 * can be, as the possible shifts will be correspondingly bigger. 
 * 
 * @author Matt Palmer
 */
public final class HorspoolUnrolledSearcher extends AbstractWindowSearcher<SequenceMatcher> {

    private final LazyObject<SearchInfo> forwardInfo;
    private final LazyObject<SearchInfo> backwardInfo;

    /**
     * Constructs an UnrolledHorspoolSearcher given a {@link SequenceMatcher}
     * to search for.
     * 
     * @param sequence The SequenceMatcher to search for.
     */
    public HorspoolUnrolledSearcher(final SequenceMatcher sequence) {
        super(sequence);
        forwardInfo  = new DoubleCheckImmutableLazyObject<SearchInfo>(new ForwardInfoFactory());
        backwardInfo = new DoubleCheckImmutableLazyObject<SearchInfo>(new BackwardInfoFactory());
    }

    /**
     * Constructs an UnrolledHorspoolSearcher for the bytes contained in the sequence string,
     * encoded using the platform default character set.
     *
     * @param sequence The string to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public HorspoolUnrolledSearcher(final String sequence) {
        this(sequence, Charset.defaultCharset());
    }

    /**
     * Constructs an UnrolledHorspoolSearcher for the bytes contained in the sequence string,
     * encoded using the charset provided.
     *
     * @param sequence The string to search for.
     * @param charset The charset to encode the string in.
     * @throws IllegalArgumentException if the sequence is null or empty, or the charset is null.
     */
    public HorspoolUnrolledSearcher(final String sequence, final Charset charset) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)));
    }

    /**
     * Constructs an UnrolledHorspoolSearcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public HorspoolUnrolledSearcher(final byte[] sequence) {
        this(sequence == null? null : new ByteSequenceMatcher(sequence));
    }

    @Override
    protected int getSequenceLength() {
        return sequence.length();
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public int searchSequenceForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        
        // Get the objects needed to search:
        final SearchInfo info = forwardInfo.get();
        final int[] safeShifts = info.shifts;
        final ByteMatcher endOfSequence = info.matcher;      
        final SequenceMatcher verifier = info.verifier;
        
        // Determine a safe position to start searching at.
        final int lastMatcherPosition = sequence.length() - 1;
        int searchPosition = addIntegerPositionsAvoidOverflows(fromPosition, lastMatcherPosition);
        
        // Calculate safe bounds for the end of the search:
        final int lastPossiblePosition = bytes.length - 1;
        final int lastPossibleSearchPosition = addIntegerPositionsAvoidOverflows(toPosition, lastMatcherPosition);
        final int finalPosition = Math.min(lastPossibleSearchPosition, lastPossiblePosition);

        // Search forwards:
        while (searchPosition <= finalPosition) {
            
            // Shift forwards until we match the last position in the sequence,
            // or we run out of search space (in which case just return not found).
            byte currentByte = bytes[searchPosition];
            while (!endOfSequence.matches(currentByte)) {
                searchPosition += safeShifts[currentByte & 0xff];
                if (searchPosition > finalPosition) {
                    return finalPosition - searchPosition;
                }
                currentByte = bytes[searchPosition];                
            }
            
            // The last byte matched - verify there is a complete match:
            final int startMatchPosition = searchPosition - lastMatcherPosition;
            if (verifier.matchesNoBoundsCheck(bytes, startMatchPosition)) {
                return startMatchPosition;
            }
            
            // No match was found - shift forward by the shift for the current byte:
            searchPosition += safeShifts[currentByte & 0xff];
        }
        
        return finalPosition - searchPosition;
    }    
        
    
    /**
     * Searches forward using the Boyer Moore Horspool algorithm, using 
     * byte arrays from Windows to handle shifting, and the WindowReader interface
     * on the SequenceMatcher to verify whether a match exists.
     */
    @Override
    protected long doSearchForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
            
        // Get the objects needed to search:
        final SearchInfo info = forwardInfo.get();
        final int[] safeShifts = info.shifts;
        final ByteMatcher endOfSequence = info.matcher;      
        final SequenceMatcher verifier = info.verifier;
        
        // Initialise window search:
        final long endSequencePosition = sequence.length() - 1;
        final long finalPosition = addLongPositionsAvoidOverflows(toPosition, endSequencePosition);
        long searchPosition = addLongPositionsAvoidOverflows(fromPosition, endSequencePosition);
            
        // While there is a window to search in:
        Window window = null;
        while (searchPosition <= finalPosition &&
               (window = reader.getWindow(searchPosition)) != null) {
            
            // Initialise array search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);
            final int arrayEndPosition = window.length() - 1;
            final long distanceToEnd = finalPosition - window.getWindowPosition();
            final int lastSearchPosition = (int) Math.min(distanceToEnd, arrayEndPosition);
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

                // The last byte matched - verify there is a complete match:
                final long arrayBytesSearched = arraySearchPosition - arrayStartPosition;
                final long matchPosition = searchPosition + arrayBytesSearched - endSequencePosition;
                if (verifier.matches(reader, matchPosition)) {
                    return matchPosition; // match found.
                }
                
                // No match was found - shift forward by the shift for the current byte:
                arraySearchPosition += safeShifts[currentByte & 0xff];
            } 
            
            // No match was found in this array - calculate the current search position:
            searchPosition += arraySearchPosition - arrayStartPosition;
        }
        return window == null? NO_MATCH_SAFE_SHIFT                        // we have a null window so we just return a negative value.
                             : finalPosition - searchPosition; // the (negative) shift we can safely make from here.
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int searchSequenceBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        
        // Get objects needed for the search:
        final SearchInfo info = backwardInfo.get();
        final int[] safeShifts = info.shifts;
        final ByteMatcher startOfSequence = info.matcher;
        final SequenceMatcher verifier = info.verifier;
        
        // Calculate safe bounds for the start of the search:
        final int firstPossiblePosition = bytes.length - sequence.length();
        int searchPosition = Math.min(fromPosition, firstPossiblePosition);
        
        // Calculate safe bounds for the end of the search:
        final int lastPosition = Math.max(toPosition, 0);

        // Search backwards:
        while (searchPosition >= lastPosition) {
            
            // Shift backwards until we match the first position in the
            // sequence, or we run out of search space:
            byte currentByte = bytes[searchPosition];
            while (!startOfSequence.matches(currentByte)) {
                searchPosition -= safeShifts[currentByte & 0xFF];
                if (searchPosition < lastPosition) {
                    return searchPosition - lastPosition;
                }
                currentByte = bytes[searchPosition];
            }
            
            // The first byte matched - verify there is a complete match.
            // There is only a verifier if the sequence length was greater than one;
            // if the sequence is only one in length, we have already found it.
            if (verifier == null || verifier.matchesNoBoundsCheck(bytes, searchPosition + 1)) {
                return searchPosition; // match found.
            }

            // No match was found - shift backward by the shift for the current byte:
            searchPosition -= safeShifts[currentByte & 0xff];            
        }
        
        return searchPosition - lastPosition;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected long doSearchBackwards(final WindowReader reader, final long fromPosition, final long toPosition ) throws IOException {
        
        // Initialise:
        final SearchInfo info = backwardInfo.get();
        final int[] safeShifts = info.shifts;
        final ByteMatcher startOfSequence = info.matcher;
        final SequenceMatcher verifier = info.verifier;        
        long searchPosition = fromPosition;
        
        // Search backwards across the windows:
        Window window = null;
        while (searchPosition >= toPosition && 
               (window = reader.getWindow(searchPosition))!= null) {
            
            // Initialise the window search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);   
            final long distanceToEnd = toPosition - window.getWindowPosition();
            final int lastSearchPosition = (int) Math.max(distanceToEnd, 0);
            int arraySearchPosition = arrayStartPosition;
            
            // Search using the byte array for shifts, using the WindowReader
            // for verifiying the sequence with the matcher:          
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
                
                // The first byte matched - verify there is a complete match.
                final int totalShift = arrayStartPosition - arraySearchPosition;
                final long sequencePosition = searchPosition - totalShift;
                if (verifier == null || verifier.matches(reader, sequencePosition + 1)) {
                    return sequencePosition; // match found.
                }
                
                // No match was found - shift backward by the shift for the current byte:
                arraySearchPosition -= safeShifts[currentByte & 0xff];                
            }
            
            // No match was found in this array - calculate the current search position:
            searchPosition -= (arrayStartPosition - arraySearchPosition);
        }
        return window == null? NO_MATCH_SAFE_SHIFT                     // we have a null window, so just return a negative number.
                             : searchPosition - toPosition; // return the (negative) safe shift we can make.
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
    
    
    @Override
    public String toString() {
    	return getClass().getSimpleName() + '(' + sequence + ')';
    }

    
    private static final class SearchInfo {
        private final int[] shifts;
        private final ByteMatcher matcher;
        private final SequenceMatcher verifier;
        
        public SearchInfo(final int[] shifts, final ByteMatcher matcher, final SequenceMatcher verifier) {
        	this.shifts = shifts;
        	this.matcher = matcher;
        	this.verifier = verifier;
        }
    }

    private final static int MAX_BYTES = 1024; // four times the table length fills 98% of positions with random selection.

    private final class ForwardInfoFactory implements ObjectFactory<SearchInfo> {

        /**
         * Calculates the safe shifts to use if searching forwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the end of the matcher.
         */        
        @Override
        public SearchInfo create() {
            // Get info about the matcher:
            final SequenceMatcher localSequence = sequence;
            final int sequenceLength = localSequence.length();
            
            // Create the search info object fields:
            final int lastPosition = sequenceLength - 1;
            final ByteMatcher byteMatcher = localSequence.getMatcherForPosition(lastPosition);
            final SequenceMatcher verifier = (lastPosition == 0)? AnyByteMatcher.ANY_BYTE_MATCHER
            												    : localSequence.subsequence(0, lastPosition);

            // Find the max shift possible by scanning back and counting the bytes matched.
            // If we find 256 bytes in one place then nothing can match past that position.
            // If we exceed four times the table size, 98% of positions would be filled assuming a uniform random distribution.
            // This is not optimal - many complicated patterns could cause a bit more processing than strictly required,
            // but it does avoid denial of service and completely unnecessary processing.
            // Note: the last pattern character doesn't affect the shift table, so we don't care if there
            //       is an Any byte match in that position.  The final position gives the same max shift
            //       as not looking at all, so we don't process that one either.
            //TODO: This explanation doesn't make total sense - copied from a qgram method (table size?)
            //TODO: Also, why does the final position give the same max shift as not looking at all?
            int maxShift = sequenceLength;
            int totalBytes = 0;
            for (int position = sequenceLength - 2; position > 0; position--) {
                final int numBytes = localSequence.getNumBytesAtPosition(position);
                totalBytes += numBytes;
                // Stop if we execeed the max bytes, or the bytes for the current position would overwrite everything after it.
                if (totalBytes > MAX_BYTES || numBytes == 256 ) {
                    maxShift = sequenceLength - position;
                    break;
                }
            }

            // Set the default shift to the length of the sequence for all possible byte values:
            final int[] shifts = new int[256];            
            Arrays.fill(shifts, maxShift);

            // As long as we can shift more than one, work out the other possible shifts:
            if (maxShift > 1) {
                final int processShiftsFromPos = sequenceLength - maxShift;

                // Now set specific shifts for the bytes actually in
                // the sequence itself.  The shift is the distance of a position
                // from the end of the sequence, but we do not create a shift for
                // the very last position.
                for (int sequencePos = processShiftsFromPos; sequencePos < lastPosition; sequencePos++) {
                    final ByteMatcher aMatcher = localSequence.getMatcherForPosition(sequencePos);
                    final byte[] matchingBytes = aMatcher.getMatchingBytes();
                    final int distanceFromEnd = sequenceLength - sequencePos - 1;
                    for (final byte b : matchingBytes) {
                        shifts[b & 0xFF] = distanceFromEnd;
                    }
                }
            }

            return new SearchInfo(shifts, byteMatcher, verifier);
        }
    }
    
    
    private final class BackwardInfoFactory implements ObjectFactory<SearchInfo> {

        /**
         * Calculates the safe shifts to use if searching backwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the beginning of the matcher, with
         * zero being the value of the first position in the sequence.
         */        
        @Override
        public SearchInfo create() {
            // Get info about the matcher:
            final SequenceMatcher localSequence = sequence;
            final int sequenceLength = localSequence.length();
            
            // Create the search info object fields
            final int lastPosition = sequenceLength - 1;
            final ByteMatcher byteMatcher = localSequence.getMatcherForPosition(0);
            final SequenceMatcher verifier = (lastPosition == 0)? null 
            													: localSequence.subsequence(1, sequenceLength);

            // Find the max shift possible by scanning back and counting the bytes matched.
            // If we find 256 bytes in one place then nothing can match past that position.
            // If we exceed four times the table size, 98% of positions would be filled assuming a uniform random distribution.
            // This is not optimal - many complicated patterns could cause a bit more processing than strictly required,
            // but it does avoid denial of service and completely unnecessary processing.
            // Note: the last pattern character doesn't affect the shift table, so we don't care if there
            //       is an Any byte match in that position.  The final position leads to the same max shift
            //       as not looking at all, so we don't process that position either.
            int maxShift = sequenceLength;
            int totalBytes = 0;
            for (int position = 1; position < sequenceLength - 1; position++) {
                final int numBytes = localSequence.getNumBytesAtPosition(position);
                totalBytes += numBytes;
                // Stop if we execeed the max bytes, or the bytes for the current position would overwrite everything after it.
                if (totalBytes > MAX_BYTES || numBytes == 256 ) {
                    maxShift = position + 1;
                    break;
                }
            }

            // Set the default shift to the length of the sequence
            final int[] shifts = new int[256];
            Arrays.fill(shifts, maxShift);

            // As long as we can shift more than one, work out the other possible shifts:
            if (maxShift > 1) {
                final int processShiftsFromPos = maxShift - 1;

                // Now set specific byte shifts for the bytes actually in
                // the sequence itself.  The shift is the position in the sequence,
                // but we do not create a shift for the first position 0.
                for (int sequencePos = processShiftsFromPos; sequencePos > 0; sequencePos--) {
                    final ByteMatcher aMatcher = localSequence.getMatcherForPosition(sequencePos);
                    final byte[] matchingBytes = aMatcher.getMatchingBytes();
                    for (final byte b : matchingBytes) {
                        shifts[b & 0xFF] = sequencePos;
                    }
                }
            }

            return new SearchInfo(shifts, byteMatcher, verifier);
        }
        
    }    

}
