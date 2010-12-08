/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.searcher;

import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 *
 * @author matt
 */
public abstract class MatcherSearcher implements Searcher {

    protected SequenceMatcher matcher;

    public MatcherSearcher(final SequenceMatcher matcher) {
        this.matcher = matcher;
    }

    public SequenceMatcher getMatcher() {
        return matcher;
    }

}
