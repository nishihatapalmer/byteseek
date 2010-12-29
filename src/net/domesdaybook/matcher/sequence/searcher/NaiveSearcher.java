/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence.searcher;

import net.domesdaybook.reader.ByteReader;
import net.domesdaybook.matcher.sequence.CombinedSequenceMatcher;

/**
 *
 * @author matt
 */
public final class NaiveSearcher extends SequenceMatcherSearcher {

    public NaiveSearcher(CombinedSequenceMatcher sequence) {
        super(sequence);
    }

    @Override
    public final long searchForwards(final ByteReader reader, final long fromPosition, final long toPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public final long searchBackwards(final ByteReader reader, final long fromPosition, final long toPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
