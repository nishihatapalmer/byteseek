/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.searcher.sequence;

import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.searcher.Searcher;

/**
 * A SequenceMatcherSearcher is an abstract class implementing the {@link Searcher}
 * interface, using {@link SequenceMatcher} objects as the specification for what
 * to search for.
 *
 * SequenceMatcherSearchers should be thread-safe, either by being immutable,
 * or by some other way of ensuring shared access to the searcher is safe.
 *
 * @author Matt Palmer
 */
public abstract class SequenceMatcherSearcher implements Searcher {


    private final SequenceMatcher matcher;

    /**
     * Constructs an immutable SequenceMatcherSearcher.
     *
     * @param matcher The {@link SequenceMatcher} to search for.
     */
    public SequenceMatcherSearcher(final SequenceMatcher matcher) {
        if (matcher == null) {
            throw new IllegalArgumentException("Null matcher passed in to SequenceMatcherSearcher.");
        }
        this.matcher = matcher;
    }


    /**
     * 
     * @return The underlying {@link SequenceMatcher} to search for.
     */
    public final SequenceMatcher getMatcher() {
        return matcher;
    }

}
