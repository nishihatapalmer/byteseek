/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence.searcher;

import net.domesdaybook.reader.ByteReader;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.searcher.Searcher;


/**
 * BoyerMooreHorspoolSearcher searches for a sequence using the
 * Boyer-Moore-Horspool algorithm.
 * <p>
 * This type of search algorithm does not need to examine every byte in 
 * the bytes being searched.  It is sub-linear, in general needing to
 * examine less bytes than actually occur in the bytes being searched.
 * <p>
 * It proceeds by searching for the search pattern backwards, from the last byte
 * in the pattern to the first.  It pre-computes a table of minimum safe shifts
 * for the search pattern.  Given a byte in the bytes being searched,
 * the shift table tells us how many bytes we can safely shift ahead without
 * missing a possible match.  If the shift is zero, then we must validate that
 * the pattern actually occurs at this position (the last byte of pattern matches
 * the current position in the bytes being searched).
 * <p>
 * A simple example is looking for the bytes 'XYZ' in the sequence 'ABCDEFGXYZ'.
 * The first attempt is to match 'Z', and we find the byte 'C'.  Since 'C' does
 * not appear anywhere in 'XYZ', we can safely shift 3 bytes ahead and not risk
 * missing a possible match.  In general, the safe shift is either the length of
 * the pattern, if that byte does not appear in the pattern, or the shortest 
 * distance from the end of the pattern where that byte appears.
 * <p>
 * One initially counter-intuitive consequence of this type of search is that
 * the longer the pattern you are searching for, the better the performance
 * usually is, as the possible shifts will be correspondingly bigger.
 * 
 * @author Matt Palmer
 */
public final class BoyerMooreHorspoolSearcher extends SequenceMatcherSearcher {

    private int[] shiftForwardFunction;
    private int[] shiftBackwardFunction;
    private SingleByteMatcher firstSingleMatcher;
    private SingleByteMatcher lastSingleMatcher;


    /**
     * Constructs a BoyerMooreHorspool searcher given a {@link SequenceMatcher}
     * to search for.
     * 
     * @param matcher A {@link SequenceMatcher} to search for.
     */
    public BoyerMooreHorspoolSearcher(final SequenceMatcher matcher) {
        super(matcher);
        // Prepopulate the forward and backward shifts, making the class
        // effectively immutable once constructed.
        //
        // This will increase construction time, and probably build
        // shifts which aren't used for one direction, but it makes the
        // class thread-safe.
        //
        // Another way would be to synchronise access to the shift and
        // and last and first matcher objects, which would mean we would
        // use less memory and construct faster, at the expense of
        // synchronising access.
        getForwardShifts();
        getBackwardShifts();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final long searchForwards(final ByteReader reader, final long fromPosition, final long toPosition ) {

        final int[] safeShifts = getForwardShifts();
        final SingleByteMatcher lastMatcher = lastSingleMatcher;
        final int lastBytePositionInSequence = matcher.length() - 1;
        long matchPosition = fromPosition;
        boolean matchFound = false;
        while (matchPosition <= toPosition) {

            // Scan forwards to find a match to the last byte in the sequence:
            byte lastByte = reader.readByte(matchPosition);
            while (!lastMatcher.matches(lastByte)) {
                matchPosition += safeShifts[(int) lastByte & 0xFF];
                if ( matchPosition <= toPosition ) {
                    lastByte = reader.readByte(matchPosition);
                } else {
                    break;
                }
            }

            // If we're still inside the search window, we have a matching last byte.
            // Verify whether the rest of the sequence matches at this position:
            if (matchPosition <= toPosition ) {
                matchFound = matcher.matches(reader, matchPosition - lastBytePositionInSequence);
                if (matchFound) {
                     break;
                }
            }

            // No match was found.
            // Shift the match position according to the value of the last byte
            // observed at the end of the sequence so far in the file.

            // "Sunday" variant of Boyer-Moore-Horspool sometimes gets better average performance
            // by shifting based on the next byte of the file, rather than the last byte checked.
            // This isn't always faster, as it has less "locality of reference":
            if (matchPosition < toPosition) {
                matchPosition +=1;
                lastByte = reader.readByte(matchPosition);
            }

            matchPosition += safeShifts[(int) lastByte & 0xFF];
        }

        return matchFound ? matchPosition : Searcher.NOT_FOUND;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final long searchBackwards(final ByteReader reader, final long fromPosition, final long toPosition ) {
        
        final int[] safeShifts = getBackwardShifts();
        final SingleByteMatcher firstMatcher = firstSingleMatcher;
        long matchPosition = fromPosition;
        boolean matchFound = false;
        while (matchPosition >= toPosition) {

            // Scan for a match to the first byte in the sequence, scanning backwards from the starting position:
            byte firstByte = reader.readByte(matchPosition);
            while (!firstMatcher.matches(firstByte)) {
                matchPosition += safeShifts[(int) firstByte & 0xFF]; // shifts always add - if the search is backwards, the shift values are already negative.
                if ( matchPosition >= toPosition ) {
                    firstByte = reader.readByte(matchPosition);
                } else {
                    break;
                }
            }

            // As long as we're still inside the search window
            // (greater than the last position we can scan backwards to)
            // we have a matching first byte - verify that the rest of the sequence matches too.
            if (matchPosition >= toPosition) {
                matchFound = matcher.matches(reader, matchPosition);
                if (matchFound) {
                    break;
                }
            }

            // No match was found.
            // Shift the match position according to the value of the last byte
            // observed at the end of the sequence so far in the file.
            // Note: always add shifts - if the search is backwards, the shifts are precomputed with negative values.

            // "Sunday" variant of Boyer-Moore-Horspool sometimes gets better average performance
            // by shifting based on the next byte of the file, rather than the last byte checked.
            // This isn't always faster, as it has less "locality of reference":
            if ( matchPosition > toPosition ) {
                matchPosition -=1;
                firstByte = reader.readByte(matchPosition);
            }

            matchPosition += safeShifts[(int) firstByte & 0xFF];
        }

        return matchFound ? matchPosition : Searcher.NOT_FOUND;
    }


    /**
     *
     * @return A 256-element array of integers, giving the safe shift
     * for a given byte when searching forwards.
     */
    private int[] getForwardShifts() {
        if (this.shiftForwardFunction == null) {
            calculateForwardShifts();
            this.lastSingleMatcher = matcher.getByteMatcherForPosition(matcher.length()-1);
        }
        return this.shiftForwardFunction;
    }


    /**
     *
     * @return A 256-element array of integers, giving the safe shift
     * for a given byte when searching backwards.
     */
    private int[] getBackwardShifts() {
        if (this.shiftBackwardFunction == null) {
            calculateBackwardShifts();
            this.firstSingleMatcher = matcher.getByteMatcherForPosition(0);
        }
        return this.shiftBackwardFunction;
    }


    /**
     * Calculates the safe shifts to use if searching backwards.
     * A safe shift is either the length of the sequence, if the
     * byte does not appear in the {@link SequenceMatcher}, or
     * the shortest distance it appears from the beginning of the matcher.
     */
    private void calculateBackwardShifts() {
        // First set the default shift to the length of the sequence
        // (negative if search direction is reversed)
        this.shiftBackwardFunction = new int[256];
        final int[] shifts = this.shiftBackwardFunction;
        final int numBytes = matcher.length();

        final int defaultShift =  numBytes * -1;
        for (int charValueIndex=255; charValueIndex>=0; charValueIndex--) {
            shifts[charValueIndex] = defaultShift;
        }

        // Now set specific byte shifts for the bytes actually in
        // the sequence itself.  The shift is the distance of each character
        // from the end of the sequence, as a zero-indexed offset.
        // Each position can match more than one byte (e.g. if a byte class appears).
        for ( int sequenceByteIndex = numBytes-1; sequenceByteIndex > 0; sequenceByteIndex--) {
            final SingleByteMatcher aMatcher = matcher.getByteMatcherForPosition(sequenceByteIndex);
            final byte[] matchingBytes = aMatcher.getMatchingBytes();
            for (int byteIndex = 0; byteIndex < matchingBytes.length; byteIndex++)  {
                final int byteSequenceValue = (matchingBytes[byteIndex] & 0xFF);
                shifts[byteSequenceValue] = -sequenceByteIndex; // 1 - numBytes + sequenceByteIndex;
            }
        }
    }


    /**
     * Calculates the safe shifts to use if searching forwards.
     * A safe shift is either the length of the sequence, if the
     * byte does not appear in the {@link SequenceMatcher}, or
     * the shortest distance it appears from the end of the matcher.
     */
    private void calculateForwardShifts() {
        // First set the default shift to the length of the sequence
        this.shiftForwardFunction = new int[256];
        final int[] shifts = this.shiftForwardFunction;
        final int numBytes = matcher.length();

        final int defaultShift =  numBytes;
        for (int charValueIndex=255; charValueIndex>=0; charValueIndex--) {
            shifts[charValueIndex] = defaultShift;
        }

        // Now set specific byte shifts for the bytes actually in
        // the sequence itself.  The shift is the distance of each character
        // from the end of the sequence, as a zero-indexed offset.
        // Each position can match more than one byte (e.g. if a byte class appears).
        for ( int sequenceByteIndex = 0; sequenceByteIndex < numBytes -1; sequenceByteIndex++ ) {
            final SingleByteMatcher aMatcher = matcher.getByteMatcherForPosition(sequenceByteIndex);
            final byte[] matchingBytes = aMatcher.getMatchingBytes();
            for (int byteIndex = 0; byteIndex < matchingBytes.length; byteIndex++)  {
                final int byteSequenceValue = ( matchingBytes[byteIndex] & 0xFF );
                shifts[byteSequenceValue]=numBytes-sequenceByteIndex-1;
            }
        }
    }

}
