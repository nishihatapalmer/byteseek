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
public class NaiveSearcher extends MatcherSearcher {

    public NaiveSearcher(CombinedSequenceMatcher sequence) {
        super(sequence);
    }

    @Override
    public long searchForwards(Bytes reader, long fromPosition, long toPosition ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long searchBackwards(Bytes reader, long fromPosition, long toPosition ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
