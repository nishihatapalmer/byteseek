/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.temp;

import java.util.List;
import net.domesdaybook.expression.MatchResult;
import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public abstract class State  {

    protected List<State> nextStates;
    protected List<State> previousStates;

    public List<State> getNextStates() {
        return nextStates;
    }

    public List<State> getPreviousStates() {
        return previousStates;
    }

    public void linkWithState(final State nextState) {
        nextStates.add(nextState);
        nextState.previousStates.add(this);
    }
    
    public abstract MatchResult findForwards(final ByteReader reader, final long fromPosition, final long toPosition);

    public abstract MatchResult findBackwards(final ByteReader reader, final long fromPosition,final long toPosition);

}
