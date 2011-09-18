/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.searcher.sequence;

import java.io.IOException;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.searcher.AbstractSearcher;
import net.domesdaybook.searcher.Searcher;

/**
 * SequenceSearcher searches for a sequence by trying for a match in each position.
 * In its worst case, where no match is found, if the sequence is m bytes long,
 * and the bytes being searched are n bytes long, it will take O(n * m) to
 * determine there is no match.
 *
 * @author Matt Palmer
 */
public final class SequenceSearcher extends AbstractSearcher {
    
    private final SequenceMatcher matcher;


    public SequenceSearcher(final SequenceMatcher sequence) {
        if (sequence == null) {
            throw new IllegalArgumentException("Null sequence passed in to SequenceMatcherSearcher.");
        }        
        this.matcher = sequence;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final long searchForwards(final Reader reader, final long fromPosition, 
            final long toPosition) throws IOException {
        // Get objects needed for the search:
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final long lastPossiblePosition = reader.length() - theMatcher.length();
        final long lastPosition = toPosition < lastPossiblePosition?
                toPosition : lastPossiblePosition;
        long searchPosition = fromPosition < 0? 0 : fromPosition;
        
        // Search forwards
        while (searchPosition <= lastPosition) {
            if (theMatcher.matchesNoBoundsCheck(reader, searchPosition)) {
                return searchPosition;
            }
            searchPosition++;
        }
        return Searcher.NOT_FOUND;
    }
    
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public int searchForwards(byte[] bytes, int fromPosition, int toPosition) {
        // Get objects needed for the search:
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final int lastPossiblePosition = bytes.length - theMatcher.length();
        final int lastPosition = toPosition < lastPossiblePosition?
                toPosition : lastPossiblePosition;
        int searchPosition = fromPosition < 0? 0 : fromPosition;        
        
        // Search forwards
        while (searchPosition <= lastPosition) {
            if (theMatcher.matchesNoBoundsCheck(bytes, searchPosition)) {
                return searchPosition;
            }
            searchPosition++;
        }
        return Searcher.NOT_FOUND;    
    }    

    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long searchBackwards(final Reader reader, final long fromPosition, 
            final long toPosition) throws IOException {
        // Get objects needed for the search:
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final long lastPosition = toPosition < 0? 0 : toPosition;        
        final long firstPossiblePosition = reader.length() - theMatcher.length();
        long searchPosition = fromPosition < firstPossiblePosition?
                fromPosition : firstPossiblePosition;
        
        // Search backwards:
        while (searchPosition >= lastPosition) {
            if (theMatcher.matchesNoBoundsCheck(reader, searchPosition)) {
                return searchPosition;
            }
            searchPosition--;
        }
        return Searcher.NOT_FOUND;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
        // Get objects needed for the search:
        final SequenceMatcher theMatcher = getMatcher();
        
        // Calculate safe bounds for the search:
        final int lastPosition = toPosition < 0? 0 : toPosition;
        final int firstPossiblePosition = bytes.length - theMatcher.length();
        int searchPosition = fromPosition < firstPossiblePosition?
                fromPosition : firstPossiblePosition;
        
        // Search backwards:
        while (searchPosition >= lastPosition) {
            if (theMatcher.matchesNoBoundsCheck(bytes, searchPosition)) {
                return searchPosition;
            }
            searchPosition--;
        }
        return Searcher.NOT_FOUND;
    }

    /**
     *
     * @return The underlying {@link SequenceMatcher} to search for.
     */
    /**
     *
     * @return The underlying {@link SequenceMatcher} to search for.
     */
    public final SequenceMatcher getMatcher() {
        return matcher;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public void prepareForwards() {
        // no preparation necessary.
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public void prepareBackwards() {
        // no preparation necessary.
    }

}
