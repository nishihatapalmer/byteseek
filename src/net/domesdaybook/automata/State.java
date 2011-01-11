/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.automata;

import net.domesdaybook.copy.DeepCopy;
import java.util.List;
import java.util.Map;

/**
 * State is an interface representing a state of an automata.
 * A state can have a label, transitions to other states (or to itself),
 * and be a final or non-final state.  A final state is one which indicates
 * a match if it is reached processing the automata.
 * 
 * It extends the DeepCopy interface, to ensure that all states can provide
 * deep copies of themselves.
 *
 * @author Matt Palmer
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
     * @param transition A transition to add to this state.
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
     * @return true if this state is a final state.
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

