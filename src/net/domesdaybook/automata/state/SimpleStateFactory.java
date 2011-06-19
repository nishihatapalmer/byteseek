/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata.state;

import net.domesdaybook.automata.State;

/**
 *
 * @author matt
 */
public class SimpleStateFactory implements StateFactory {

    @Override
    public State create(boolean isFinal) {
        return new SimpleState(isFinal);
    }
    
}

