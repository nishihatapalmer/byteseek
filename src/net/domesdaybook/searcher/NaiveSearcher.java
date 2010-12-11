/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.searcher;

import net.domesdaybook.reader.Bytes;
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
    public final long searchForwards(final Bytes reader, final long fromPosition, final long toPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public final long searchBackwards(final Bytes reader, final long fromPosition, final long toPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
