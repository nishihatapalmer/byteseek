/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata;

import java.util.List;

/**
 *
 * @author matt
 */
public interface State {

    public static boolean FINAL = true;
    public static boolean NON_FINAL = false;
    
    public String getLabel();

    public void addTransition(final Transition transition);

    public void addAllTransitions(final List<Transition> transitions);

    public List<Transition> getTransitions();

    public boolean isFinal();

}

