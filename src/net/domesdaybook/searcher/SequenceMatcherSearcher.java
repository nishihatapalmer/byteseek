/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.searcher;

import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 *
 * @author matt
 */
public abstract class SequenceMatcherSearcher implements Searcher {

    protected final SequenceMatcher matcher;

    public SequenceMatcherSearcher(final SequenceMatcher matcher) {
        this.matcher = matcher;
    }

    public final SequenceMatcher getMatcher() {
        return matcher;
    }

}
