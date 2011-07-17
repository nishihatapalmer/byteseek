/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.searcher.sequence;

import java.util.Arrays;
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
 * the shift able tells us how many bytes we can safely shift ahead without
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
public final class BoyerMooreHorspoolSearcher implements Searcher {

    // volatile arrays are usually a bad idea, as volatile applies to the array
    // reference, not to the contents of the array.  However, we will never change
    // the array contents once it is initialised, so this is safe.
    @SuppressWarnings("VolatileArrayField")
    private volatile int[] shiftForwardFunction;
    @SuppressWarnings("VolatileArrayField")
    private volatile int[] shiftBackwardFunction;
    private volatile SingleByteMatcher firstSingleMatcher;
    private volatile SingleByteMatcher lastSingleMatcher;
    private final SequenceMatcher matcher;


    /**
     * Constructs a BoyerMooreHorspool searcher given a {@link SequenceMatcher}
     * to search for.
     * 
     * @param matcher A {@link SequenceMatcher} to search for.
     */
    public BoyerMooreHorspoolSearcher(final SequenceMatcher sequence) {
        if (sequence == null) {
            throw new IllegalArgumentException("Null sequence passed in to BoyerMooreHorspoolSearcher.");
        }        
        this.matcher = sequence;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final long searchForwards(final ByteReader reader, 
            final long fromPosition, final long toPosition ) {
        
        // Get the objects needed to search:
        final int[] safeShifts = getForwardShifts();
        final SingleByteMatcher lastMatcher = getLastSingleMatcher();
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final int lastBytePositionInSequence = theMatcher.length() - 1;
        final long lastPossiblePosition = reader.length() - lastBytePositionInSequence - 1;
        final long lastPosition = toPosition < lastPossiblePosition?
                toPosition : lastPossiblePosition;
        long searchPosition = fromPosition < 0?
                lastBytePositionInSequence : fromPosition + lastBytePositionInSequence;
        
        // Search forwards:
        while (searchPosition <= lastPosition) {

            // Scan forwards to find a match to the last byte in the sequence:
            byte lastByte = reader.readByte(searchPosition);
            while (!lastMatcher.matches(lastByte)) {
                searchPosition += safeShifts[(int) lastByte & 0xFF];
                if (searchPosition > lastPosition) {
                    return Searcher.NOT_FOUND;
                }
                lastByte = reader.readByte(searchPosition);
            }

            // Do we have a match?
            final long startMatchPosition = searchPosition - lastBytePositionInSequence;
            if (theMatcher.matchesNoBoundsCheck(reader, startMatchPosition)) {
                return startMatchPosition;
            }

            // Move on to the next safe position.
            searchPosition += safeShifts[(int) lastByte & 0xFF];
        }

        return Searcher.NOT_FOUND;
    }



    /**
     * {@inheritDoc}
     */    
    @Override
    public int searchForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        
        // Get the objects needed to search:
        final int[] safeShifts = getForwardShifts();
        final SingleByteMatcher lastMatcher = getLastSingleMatcher();
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final int lastBytePositionInSequence = theMatcher.length() - 1;
        final int lastPossiblePosition = bytes.length - lastBytePositionInSequence - 1;
        final int lastPosition = toPosition < lastPossiblePosition?
                toPosition : lastPossiblePosition;
        int searchPosition = fromPosition < 0?
                lastBytePositionInSequence : fromPosition + lastBytePositionInSequence;
        
        // Search forwards:
        while (searchPosition <= lastPosition) {

            // Scan forwards to find a match to the last byte in the sequence:
            byte lastByte = bytes[searchPosition];
            while (!lastMatcher.matches(lastByte)) {
                searchPosition += safeShifts[(int) lastByte & 0xFF];
                if (searchPosition > lastPosition) {
                    return Searcher.NOT_FOUND;
                }
                lastByte = bytes[searchPosition];
            }

            // Do we have a match?
            final int startMatchPosition = searchPosition - lastBytePositionInSequence;
            if (theMatcher.matchesNoBoundsCheck(bytes, startMatchPosition)) {
                return startMatchPosition;
            }

            // Move on to the next safe position.
            searchPosition += safeShifts[(int) lastByte & 0xFF];
        }

        return Searcher.NOT_FOUND;
    }    

    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long searchBackwards(final ByteReader reader, 
            final long fromPosition, final long toPosition ) {
        
        // Get objects needed to search:
        final int[] safeShifts = getBackwardShifts();
        final SingleByteMatcher firstMatcher = getFirstSingleMatcher();
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final long lastPosition = toPosition > 0? toPosition : 0;
        final long firstPossiblePosition = reader.length() - theMatcher.length();        
        long searchPosition = fromPosition < firstPossiblePosition?
                fromPosition : firstPossiblePosition;
        
        // Search backwards:
        while (searchPosition >= lastPosition) {

            // Scan backwards for a match to the first byte in the sequence.
            byte firstByte = reader.readByte(searchPosition);
            while (!firstMatcher.matches(firstByte)) {
                // Note: shifts for backwards matching are already negative, so we add them.
                searchPosition += safeShifts[(int) firstByte & 0xFF]; // shifts always add - if the search is backwards, the shift values are already negative.
                if (searchPosition < lastPosition) {
                    return Searcher.NOT_FOUND;
                }
                firstByte = reader.readByte(searchPosition);
            }

            // Do we have a match?
            if (theMatcher.matchesNoBoundsCheck(reader, searchPosition)) {
                return searchPosition;
            }

            // Move on to the next safe position.
            // Note: shifts for backwards matching are already negative, so we add them.
            searchPosition += safeShifts[(int) firstByte & 0xFF];
        }

        return Searcher.NOT_FOUND;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int searchBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        
        // Get objects needed for the search:
        final int[] safeShifts = getBackwardShifts();
        final SingleByteMatcher firstMatcher = getFirstSingleMatcher();
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final int lastPosition = toPosition > 0? toPosition : 0;
        final int firstPossiblePosition = bytes.length - theMatcher.length();        
        int searchPosition = fromPosition < firstPossiblePosition?
                fromPosition : firstPossiblePosition;
        
        // Search backwards:
        while (searchPosition >= lastPosition) {

            // Scan backwards for a match to the first byte in the sequence.
            byte firstByte = bytes[searchPosition];
            while (!firstMatcher.matches(firstByte)) {
                // Note: shifts for backwards matching are already negative, so we add them.
                searchPosition += safeShifts[(int) firstByte & 0xFF]; // shifts always add - if the search is backwards, the shift values are already negative.
                if (searchPosition < lastPosition) {
                    return Searcher.NOT_FOUND;
                }
                firstByte = bytes[searchPosition];
            }

            // Do we have a match?
            if (theMatcher.matchesNoBoundsCheck(bytes, searchPosition)) {
                return searchPosition;
            }

            // Move on to the next safe position.
            // Note: shifts for backwards matching are already negative, so we add them.
            searchPosition += safeShifts[(int) firstByte & 0xFF];
        }

        return Searcher.NOT_FOUND;
    }    

    /**
     *
     * Uses Single-Check lazy initialisation.  This can result in the field
     * being initialised more than once, but this doesn't really matter.
     * 
     * @return A 256-element array of integers, giving the safe shift
     * for a given byte when searching forwards.
     */
    private int[] getForwardShifts() {
        int[] result = shiftForwardFunction;
        if (result == null) {
            shiftForwardFunction = result = createForwardShifts();
        }
        return result;
    }


    /**
     *
     * Uses Single-Check lazy initialisation.  This can result in the field
     * being initialised more than once, but this doesn't really matter.
     * 
     * @return A 256-element array of integers, giving the safe shift
     * for a given byte when searching backwards.
     */
    private int[] getBackwardShifts() {
        int[] result = shiftBackwardFunction;
        if (result == null) {
            shiftBackwardFunction = result = createBackwardShifts();
        }
        return result;
    }
    
    
    /**
     * Uses Single-Check lazy initialisation.  This can result in the field
     * being initialised more than once, but this doesn't really matter.
     * 
     * @return The last single byte matcher in the matcher sequence.
     */
    private SingleByteMatcher getLastSingleMatcher() {
        SingleByteMatcher result = lastSingleMatcher;
        if (result == null) {
            final SequenceMatcher theMatcher = getMatcher();
            lastSingleMatcher = result = theMatcher.getByteMatcherForPosition(theMatcher.length()-1);
        }
        return result;
    }
    
    
    /**
     * Uses Single-Check lazy initialisation.  This can result in the field
     * being initialised more than once, but this doesn't really matter.
     * 
     * @return The first single byte matcher in the matcher sequence.
     */
    private SingleByteMatcher getFirstSingleMatcher() {
        SingleByteMatcher result = firstSingleMatcher;
        if (result == null) {
            firstSingleMatcher = result = getMatcher().getByteMatcherForPosition(0);
        }
        return result;        
    }


    
    
    /**
     * Calculates the safe shifts to use if searching backwards.
     * A safe shift is either the length of the sequence, if the
     * byte does not appear in the {@link SequenceMatcher}, or
     * the shortest distance it appears from the beginning of the matcher.
     */
    private int[] createBackwardShifts() {
        // First set the default shift to the length of the sequence
        // (negative if search direction is reversed)
        final int[] shifts = new int[256];
        final SequenceMatcher theMatcher = getMatcher();
        final int numBytes = theMatcher.length();

        final int defaultShift =  numBytes * -1;
        Arrays.fill(shifts, defaultShift);

        // Now set specific byte shifts for the bytes actually in
        // the sequence itself.  The shift is the distance of each character
        // from the end of the sequence, as a zero-indexed offset.
        // Each position can match more than one byte (e.g. if a byte class appears).
        for (int sequenceByteIndex = numBytes-1; sequenceByteIndex > 0; sequenceByteIndex--) {
            final SingleByteMatcher aMatcher = theMatcher.getByteMatcherForPosition(sequenceByteIndex);
            final byte[] matchingBytes = aMatcher.getMatchingBytes();
            for (int byteIndex = 0; byteIndex < matchingBytes.length; byteIndex++)  {
                final int byteSequenceValue = (matchingBytes[byteIndex] & 0xFF);
                shifts[byteSequenceValue] = -sequenceByteIndex; // 1 - numBytes + sequenceByteIndex;
            }
        }
        return shifts;
    }


    /**
     * Calculates the safe shifts to use if searching forwards.
     * A safe shift is either the length of the sequence, if the
     * byte does not appear in the {@link SequenceMatcher}, or
     * the shortest distance it appears from the end of the matcher.
     */
    private int[] createForwardShifts() {
        // First set the default shift to the length of the sequence
        final int[] shifts = new int[256];
        final SequenceMatcher theMatcher = getMatcher();
        final int numBytes = theMatcher.length();

        final int defaultShift =  numBytes;
        Arrays.fill(shifts, defaultShift);

        // Now set specific byte shifts for the bytes actually in
        // the sequence itself.  The shift is the distance of each character
        // from the end of the sequence, as a zero-indexed offset.
        // Each position can match more than one byte (e.g. if a byte class appears).
        for (int sequenceByteIndex = 0; sequenceByteIndex < numBytes -1; sequenceByteIndex++) {
            final SingleByteMatcher aMatcher = theMatcher.getByteMatcherForPosition(sequenceByteIndex);
            final byte[] matchingBytes = aMatcher.getMatchingBytes();
            for (int byteIndex = 0; byteIndex < matchingBytes.length; byteIndex++)  {
                final int byteSequenceValue = ( matchingBytes[byteIndex] & 0xFF );
                shifts[byteSequenceValue] = numBytes-sequenceByteIndex-1;
            }
        }
        
        return shifts;
    }

    /**
     *
     * @return The underlying {@link SequenceMatcher} to search for.
     */
    public final SequenceMatcher getMatcher() {
        return matcher;
    }

}
