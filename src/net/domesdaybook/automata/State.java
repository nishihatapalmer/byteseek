/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata;

import java.util.List;
import java.util.Map;

/**
 *
 * @author matt
 */
public interface State extends DeepCopy {

    public static boolean FINAL = true;
    public static boolean NON_FINAL = false;
    
    public String getLabel();

    public void setLabel(final String label);

    public void addTransition(final Transition transition);

    public void addAllTransitions(final List<Transition> transitions);

    public List<Transition> getTransitions();

    public boolean isFinal();

    public State deepCopy();

    public State deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects);

}

