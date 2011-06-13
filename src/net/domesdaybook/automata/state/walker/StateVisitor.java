/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.automata.state.walker;

import net.domesdaybook.automata.State;

/**
 *
 * @author matt
 */
public interface StateVisitor {
    
    void visit(State state);
    
}
