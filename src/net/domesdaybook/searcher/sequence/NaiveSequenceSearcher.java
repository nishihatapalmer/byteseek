/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.searcher.sequence;

import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;
import net.domesdaybook.searcher.Searcher;

/**
 * NaiveSequenceSearcher searches for a sequence by trying for a match in each position.
 * In its worst case, where no match is found, if the sequence is m bytes long,
 * and the bytes being searched are n bytes long, it will take O(n * m) to
 * determine there is no match.
 *
 * @author Matt Palmer
 */
public final class NaiveSequenceSearcher extends SequenceMatcherSearcher {

    /**
     * {@inheritDoc}
     */
    public NaiveSequenceSearcher(SequenceMatcher sequence) {
        super(sequence);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final long searchForwards(final ByteReader reader, final long fromPosition, final long toPosition) {
        final SequenceMatcher theMatcher = matcher;
        long searchPosition = fromPosition < 0? 0 : fromPosition;
        final long lastPosition = toPosition < reader.length()?
                  toPosition - theMatcher.length() + 1
                : reader.length() - theMatcher.length();
        while (searchPosition <= lastPosition) {
            if (theMatcher.matches(reader, searchPosition)) {
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
        final SequenceMatcher theMatcher = matcher;
        int searchPosition = fromPosition < 0? 0 : fromPosition;
        final int lastPosition = toPosition < bytes.length?
                  toPosition - theMatcher.length() + 1
                : bytes.length - theMatcher.length();
        while (searchPosition <= lastPosition) {
            if (theMatcher.matches(bytes, searchPosition)) {
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
    public final long searchBackwards(final ByteReader reader, final long fromPosition, final long toPosition) {
        final SequenceMatcher theMatcher = matcher;
        long searchPosition = fromPosition < reader.length()?
                  fromPosition - theMatcher.length() + 1
                : reader.length() - theMatcher.length();
        final long lastPosition = toPosition < 0? 0 : toPosition;
        while (searchPosition >= lastPosition) {
            if (theMatcher.matches(reader, searchPosition)) {
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
        final SequenceMatcher theMatcher = matcher;
        int searchPosition = fromPosition < bytes.length?
                  fromPosition - theMatcher.length() + 1
                : bytes.length - theMatcher.length();
        final int lastPosition = toPosition < 0? 0 : toPosition;
        while (searchPosition >= lastPosition) {
            if (theMatcher.matches(bytes, searchPosition)) {
                return searchPosition;
            }
            searchPosition--;
        }
        return Searcher.NOT_FOUND;
    }

}
