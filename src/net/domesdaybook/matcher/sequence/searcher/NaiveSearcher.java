/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence.searcher;

import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;
import net.domesdaybook.searcher.Searcher;

/**
 * NaiveSearcher searches for a sequence by trying for a match in each position.
 * In its worst case, where no match is found, if the sequence is m bytes long,
 * and the bytes being searched are n bytes long, it will take O(n * m) to
 * determine there is no match.
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
