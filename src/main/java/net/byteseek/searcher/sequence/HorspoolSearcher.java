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
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.utils.lazy.DoubleCheckImmutableLazyObject;
import net.byteseek.utils.lazy.LazyObject;
import net.byteseek.utils.factory.ObjectFactory;

/**
 * HorspoolSearcher searches for a sequence using the Boyer-Moore-Horspool algorithm.
 * Although a simplification of the more sophisticated Boyer-Moore algorithm, it is normally
 * faster than its better known parent due to a simpler structure and better memory cache hits.
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
public final class HorspoolSearcher extends AbstractWindowSearcher<SequenceMatcher> {

    private final LazyObject<int[]> forwardInfo;  // forwards searching shift table, calculated on demand.
    private final LazyObject<int[]> backwardInfo; // backwards searching shift table, calculated on demand.

    /**
     * Constructs a BoyerMooreHorspool searcher given a {@link SequenceMatcher}
     * to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     * @throws IllegalArgumentException if the sequence is null.
     */
    public HorspoolSearcher(final SequenceMatcher sequence) {
        super(sequence);
        forwardInfo  = new DoubleCheckImmutableLazyObject<int[]>(new ForwardInfoFactory());
        backwardInfo = new DoubleCheckImmutableLazyObject<int[]>(new BackwardInfoFactory());
    }

    /**
     * Constructs a searcher for the bytes contained in the sequence string,
     * encoded using the platform default character set.
     *
     * @param sequence The string to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public HorspoolSearcher(final String sequence) {
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
    public HorspoolSearcher(final String sequence, final Charset charset) {
        this(sequence == null? null : charset == null? null : new ByteSequenceMatcher(sequence.getBytes(charset)));
    }

    /**
     * Constructs a searcher for the byte array provided.
     *
     * @param sequence The byte sequence to search for.
     * @throws IllegalArgumentException if the sequence is null or empty.
     */
    public HorspoolSearcher(final byte[] sequence) {
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
        final int[] safeShifts = forwardInfo.get();
        final SequenceMatcher toMatch = sequence;

        // Determine a safe position to start searching at.
        final int lastMatcherPosition = toMatch.length() - 1;
        int searchPosition = fromPosition > 0? fromPosition + lastMatcherPosition : lastMatcherPosition;

        // Calculate safe bounds for the end of the search:
        final int lastPossiblePosition = bytes.length - 1;
        final int lastPossibleSearchPosition = toPosition + lastMatcherPosition;
        final int finalPosition = lastPossibleSearchPosition < lastPossiblePosition?
                                  lastPossibleSearchPosition : lastPossiblePosition;

        // Search forwards:
        while (searchPosition <= finalPosition) {

            // Check if there is a match at the current position:
            final int startMatchPosition = searchPosition - lastMatcherPosition;
            if (toMatch.matchesNoBoundsCheck(bytes, startMatchPosition)) {
                return startMatchPosition;
            }

            // Shift forward by the shift for the current byte:
            searchPosition += safeShifts[bytes[searchPosition] & 0xff];
        }

        return finalPosition - searchPosition; // return next safe shift as a negative number.
    }


    /**
     * Searches forward using the Boyer Moore Horspool algorithm, using 
     * byte arrays from Windows to handle shifting, and the WindowReader interface
     * on the SequenceMatcher to verify whether a match exists.
     */
    @Override
    protected long doSearchForwards(final WindowReader reader, final long fromPosition,
                                    final long toPosition) throws IOException {

        // Get the objects needed to search:
        final int[] safeShifts = forwardInfo.get();
        final SequenceMatcher toMatch = sequence;

        // Initialise window search:
        final long endSequencePosition = toMatch.length() - 1;
        final long finalPosition = toPosition + endSequencePosition;
        long searchPosition = fromPosition + endSequencePosition;

        // While there is a window to search in:
        Window window = null;
        while (searchPosition <= finalPosition &&
                (window = reader.getWindow(searchPosition)) != null) {

            // Initialise array search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);
            final int arrayEndPosition = window.length() - 1;
            final long distanceToEnd = finalPosition - window.getWindowPosition(); // difference between last search position and start of current window.
            final int lastSearchPosition = distanceToEnd < arrayEndPosition?
                                     (int) distanceToEnd : arrayEndPosition;
            int arraySearchPosition = arrayStartPosition;

            // Search forwards in this array:
            while (arraySearchPosition <= lastSearchPosition) {

                // Verify if there is a match:
                final long arrayBytesSearched = arraySearchPosition - arrayStartPosition;
                final long matchPosition = searchPosition + arrayBytesSearched - endSequencePosition;
                if (toMatch.matches(reader, matchPosition)) {
                    return matchPosition; // match found.
                }

                // Shift forward by the shift for the current byte:
                arraySearchPosition += safeShifts[array[arraySearchPosition] & 0xff];
            }

            // No match was found in this array - calculate the current search position:
            searchPosition += arraySearchPosition - arrayStartPosition;
        }
        return window == null? NO_MATCH_SAFE_SHIFT             // we have a null window so we just return a negative safe shift.
                             : finalPosition - searchPosition; // the (negative) shift we can safely make from here.
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int searchSequenceBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {

        // Get objects needed for the search:
        final int[] safeShifts = backwardInfo.get();
        final SequenceMatcher toMatch = sequence;

        // Calculate safe bounds for the start of the search:
        final int firstPossiblePosition = bytes.length - toMatch.length();
        int searchPosition = fromPosition < firstPossiblePosition? fromPosition : firstPossiblePosition;

        // Calculate safe bounds for the end of the search:
        final int lastPosition = toPosition > 0? toPosition : 0;

        // Search backwards:
        while (searchPosition >= lastPosition) {

             // The first byte matched - verify there is a complete match.
            // There is only a verifier if the sequence length was greater than one;
            // if the sequence is only one in length, we have already found it.
            if (toMatch.matchesNoBoundsCheck(bytes, searchPosition)) {
                return searchPosition; // match found.
            }

            // No match was found - shift backward by the shift for the current byte:
            searchPosition -= safeShifts[bytes[searchPosition] & 0xff];
        }

        return searchPosition - lastPosition; // return next safe shift as a negative number.
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected long doSearchBackwards(final WindowReader reader,
                                     final long fromPosition, final long toPosition ) throws IOException {

        // Initialise:
        final int[] safeShifts = backwardInfo.get();
        final SequenceMatcher toMatch = sequence;

        // Search backwards across the windows:
        Window window = null;
        long searchPosition = fromPosition;
        while (searchPosition >= toPosition &&
                (window = reader.getWindow(searchPosition))!= null) {

            // Initialise the window search:
            final byte[] array = window.getArray();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);
            final long distanceFromWindowStart = toPosition - window.getWindowPosition();
            final int lastSearchPosition = distanceFromWindowStart > 0?
                                     (int) distanceFromWindowStart : 0;
            int arraySearchPosition = arrayStartPosition;

            // Search using the byte array for shifts, using the WindowReader
            // for verifiying the sequence with the matcher:          
            while (arraySearchPosition >= lastSearchPosition) {

                // Verify if there is a match.
                final int totalShift = arrayStartPosition - arraySearchPosition;
                final long sequencePosition = searchPosition - totalShift;
                if (toMatch.matches(reader, sequencePosition)) {
                    return sequencePosition; // match found.
                }

                // Shift backward by the shift for the current byte:
                arraySearchPosition -= safeShifts[array[arraySearchPosition] & 0xff];
            }

            // No match was found in this array - calculate the current search position:
            searchPosition -= (arrayStartPosition - arraySearchPosition);
        }
        return window == null? NO_MATCH_SAFE_SHIFT          // we have a null window, so just return a negative safe shift.
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
        return getClass().getSimpleName() + "[sequence:" + sequence + ']';
    }

    private final static int MAX_BYTES = 1024; // four times the table length fills 98% of positions with random selection.

    private final class ForwardInfoFactory implements ObjectFactory<int[]> {

        /**
         * Calculates the safe shifts to use if searching forwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the end of the matcher.
         */
        @Override
        public int[] create() {
            // Get info about the matcher:
            final SequenceMatcher localSequence = sequence;
            final int sequenceLength = localSequence.length();
            final int lastPosition = sequenceLength - 1;

            // Find the max shift possible by scanning back and counting the bytes matched.
            // If we find 256 bytes in one place then nothing can match past that position.
            // If we exceed four times the table size, 98% of positions would be filled assuming a uniform random distribution.
            // This is not optimal - many complicated patterns could cause a bit more processing than strictly required,
            // but it does avoid denial of service and completely unnecessary processing.
            // Note: the last pattern character doesn't affect the shift table, so we don't care if there
            //       is an Any byte match in that position.  The final position gives the same max shift
            //       as not looking at all, so we don't process that one either.
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

            return shifts;
        }
    }

    private final class BackwardInfoFactory implements ObjectFactory<int[]> {

        /**
         * Calculates the safe shifts to use if searching backwards.
         * A safe shift is either the length of the sequence, if the
         * byte does not appear in the {@link SequenceMatcher}, or
         * the shortest distance it appears from the beginning of the matcher, with
         * zero being the value of the first position in the sequence.
         */
        @Override
        public int[] create() {
            // Get info about the matcher:
            final SequenceMatcher localSequence = sequence;
            final int sequenceLength = localSequence.length();

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

            // Set the default shift to the maximum shift which can be made.
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

            return shifts;
        }

    }

}
