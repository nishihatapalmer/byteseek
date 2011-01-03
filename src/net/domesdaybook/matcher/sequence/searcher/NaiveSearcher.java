/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence.searcher;

import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;

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
        long matchPosition = -1L;
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
        long matchPosition = -1L;
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
