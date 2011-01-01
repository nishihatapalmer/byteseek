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

    //FIXME: replace these with an enum and wherever they are currently used.
    public static boolean FINAL = true;
    public static boolean NON_FINAL = false;

    /**
     *
     * @return The label of the state.
     */
    public String getLabel();


    /**
     *
     * @param label The label to give the state.
     */
    public void setLabel(final String label);


    /**
     *
     * @param transition A transition to some state to add to this state.
     */
    public void addTransition(final Transition transition);


    /**
     *
     * @param transitions A list of transitions to add to this state.
     */
    public void addAllTransitions(final List<Transition> transitions);


    /**
     *
     * @return A list of transitions from this state.
     */
    public List<Transition> getTransitions();


    /**
     *
     * @return true if this state is final (meaning, the automata matches at this state).
     */
    public boolean isFinal();


    /**
     * This is a convenience method, providing initial values to:
     * <CODE>deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects)</CODE>
     *
     * @return State a deep copy of this object.
     */
    public State deepCopy();


    /**
     * This method is inherited from the <CODE>DeepCopy</COPY> interface,
     * and is redeclared here with a return type of State (rather than DeepCopy),
     * to make using the method easier.
     *
     * @param oldToNewObjects
     * @return State
     */
    public State deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects);

}

