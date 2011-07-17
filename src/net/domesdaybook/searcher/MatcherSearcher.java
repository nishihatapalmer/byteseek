/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.searcher;

import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public class MatcherSearcher implements Searcher {

    private final Matcher matcher;
    
    MatcherSearcher(final Matcher matcher) {
        this.matcher = matcher;
    }
    
    
    /**
     * @inheritDoc
     */
    @Override
    public long searchForwards(ByteReader reader, long fromPosition, long toPosition) {
        final long lastPossiblePosition = reader.length() - 1;
        final long upToPosition = toPosition < lastPossiblePosition? toPosition : lastPossiblePosition;
        long currentPosition = fromPosition > 0? fromPosition : 0;
        final Matcher localMatcher = matcher;
        while (currentPosition <= upToPosition) {
            if (localMatcher.matches(reader, currentPosition)) {
                return currentPosition;
            }
            currentPosition++;
        }
        return Searcher.NOT_FOUND;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public int searchForwards(byte[] bytes, int fromPosition, int toPosition) {
        final int lastPossiblePosition = bytes.length - 1;
        final int upToPosition = toPosition < lastPossiblePosition? toPosition : lastPossiblePosition;
        int currentPosition = fromPosition > 0? fromPosition : 0;
        final Matcher localMatcher = matcher;
        while (currentPosition <= upToPosition) {
            if (localMatcher.matches(bytes, currentPosition)) {
                return currentPosition;
            }
            currentPosition++;
        }
        return Searcher.NOT_FOUND;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public long searchBackwards(ByteReader reader, long fromPosition, long toPosition) {
        final long lastPossiblePosition = reader.length() - 1;
        final long upToPosition = toPosition > 0? toPosition : 0;
        long currentPosition = fromPosition < lastPossiblePosition? fromPosition : lastPossiblePosition;
        final Matcher localMatcher = matcher;
        while (currentPosition >= upToPosition) {
            if (localMatcher.matches(reader, currentPosition)) {
                return currentPosition;
            }
            currentPosition--;
        }
        
        return Searcher.NOT_FOUND;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public int searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
        final int lastPossiblePosition = bytes.length - 1;
        final int upToPosition = toPosition > 0? toPosition : 0;
        int currentPosition = fromPosition < lastPossiblePosition? fromPosition : lastPossiblePosition;
        final Matcher localMatcher = matcher;
        while (currentPosition >= upToPosition) {
            if (localMatcher.matches(bytes, currentPosition)) {
                return currentPosition;
            }
            currentPosition--;
        }
        
        return Searcher.NOT_FOUND;
    }
    
}
