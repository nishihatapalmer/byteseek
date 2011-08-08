/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.searcher.matcher;

import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.reader.BridgingArrayReader;
import net.domesdaybook.reader.ByteReader;
import net.domesdaybook.reader.Array;
import net.domesdaybook.reader.ArrayProvider;
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
    
    
    //TODO: experiment with ArrayProvider
    public long searchForwards(final ArrayProvider provider, final long fromPosition, final long toPosition) {
        final Array bytes = provider.getByteArray(fromPosition);
        final byte[] array = bytes.getArray();
        final int lastPossiblePosition = array.length - 1;
        long result = searchForwards(array, bytes.getOffset(), lastPossiblePosition);
        if (result >= 0) {
            return result;
        }
        Array nextBytes = provider.getByteArray(fromPosition + lastPossiblePosition);
        BridgingArrayReader bridge = new BridgingArrayReader(bytes.getArray(), nextBytes.getArray());
        
        return Searcher.NOT_FOUND;
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public long searchForwards(final ByteReader reader, final long fromPosition, final long toPosition) {
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

    

    public long searchBackwards(final ArrayProvider provider, final long fromPosition, long toPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
    
    
    /**
     * @inheritDoc
     */
    @Override
    public long searchBackwards(final ByteReader reader, final long fromPosition, final long toPosition) {
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
