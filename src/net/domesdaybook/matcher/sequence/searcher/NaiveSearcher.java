/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence.searcher;

import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;
import net.domesdaybook.searcher.Searcher;

/**
 *
 * @author matt
 */
public final class NaiveSearcher extends SequenceMatcherSearcher {

    public NaiveSearcher(SequenceMatcher sequence) {
        super(sequence);
    }

    
    @Override
    public final long searchForwards(final ByteReader reader, final long fromPosition, final long toPosition) {
        long matchPosition = Searcher.NOT_FOUND;
        long searchPosition = fromPosition;
        final long lastPosition = toPosition - matcher.length() - 1;
        while (searchPosition <= lastPosition) {
            if (matcher.matches(reader, searchPosition)) {
                matchPosition = searchPosition;
                break;
            }
            searchPosition++;
        }
        return matchPosition;
    }


    @Override
    public final long searchBackwards(final ByteReader reader, final long fromPosition, final long toPosition) {
        long matchPosition = Searcher.NOT_FOUND;
        long searchPosition = fromPosition - matcher.length() + 1;
        while (searchPosition >= toPosition) {
            if (matcher.matches(reader, searchPosition)) {
                matchPosition = searchPosition;
                break;
            }
            searchPosition--;
        }
        return matchPosition;
    }

}
