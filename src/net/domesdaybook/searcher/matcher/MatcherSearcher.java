/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.searcher.matcher;

import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.searcher.AbstractSearcher;
import net.domesdaybook.searcher.Searcher;

/**
 *
 * @author matt
 */
public class MatcherSearcher extends AbstractSearcher {

    private final Matcher matcher;
    
    MatcherSearcher(final Matcher matcher) {
        this.matcher = matcher;
    }
    
    
   
    /**
     * @inheritDoc
     */
    @Override
    public long searchForwards(final Reader reader, final long fromPosition, final long toPosition) {
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
    public int searchForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
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
    public long searchBackwards(final Reader reader, final long fromPosition, final long toPosition) {
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
    public int searchBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
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
