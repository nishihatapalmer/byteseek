/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.expression;

/**
 *
 * @author matt
 */
public class MatchResult {

    private long matchPosition;
    private int matchLength;

    public MatchResult(final long matchPosition, final int matchLength) {
        this.matchPosition = matchPosition;
        this.matchLength = matchLength;
    }

    public long getMatchPosition() {
        return matchPosition;
    }

    public int getMatchLength() {
        return matchLength;
    }


    public void setMatchPosition(final long matchPosition) {
        this.matchPosition = matchPosition;
    }

    public void setMatchLength(final int matchLength) {
        this.matchLength = matchLength;
    }
}
