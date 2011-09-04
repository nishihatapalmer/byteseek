/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.automata.walker;

import net.domesdaybook.automata.State;

/**
 *
 * @author matt
 */
public interface Walker {
    
    void walk(final State startState, final StepObserver observer);
}
