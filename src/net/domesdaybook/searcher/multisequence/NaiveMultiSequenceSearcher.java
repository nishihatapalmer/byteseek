/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.searcher.multisequence;

import net.domesdaybook.matcher.multisequence.MultiSequenceMatcher;
import net.domesdaybook.reader.ByteReader;
import net.domesdaybook.searcher.Searcher;

/**
 *
 * @author matt
 */
public class NaiveMultiSequenceSearcher implements Searcher {
    
    private final MultiSequenceMatcher matcher;

    NaiveMultiSequenceSearcher(final MultiSequenceMatcher matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("Null matcher passed in.");
        }
        this.matcher = matcher;
    }
    
    
    public long searchForwards(ByteReader reader, long fromPosition, long toPosition) {
        final long lastPossiblePosition = reader.length() - 1;
        final long upToPosition = toPosition < lastPossiblePosition? toPosition : lastPossiblePosition;
        long currentPosition = fromPosition > 0? fromPosition : 0;
        while (currentPosition <= upToPosition) {
            if (matcher.matches(reader, currentPosition)) {
                return currentPosition;
            }
            currentPosition++;
        }
        return Searcher.NOT_FOUND;
    }

    
    public long searchBackwards(ByteReader reader, long fromPosition, long toPosition) {
        final long lastPossiblePosition = reader.length() - 1;
        final long upToPosition = toPosition > 0? toPosition : 0;
        long currentPosition = fromPosition < lastPossiblePosition? fromPosition : lastPossiblePosition;
        while (currentPosition >= upToPosition) {
            if (matcher.matches(reader, currentPosition)) {
                return currentPosition;
            }
            currentPosition--;
        }
        
        return Searcher.NOT_FOUND;
    }
    
    
    
}
