/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 * http://www.opensource.org/licenses/BSD-3-Clause
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * Neither the "byteseek" name nor the names of its contributors 
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission. 
 *  
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.domesdaybook.automata;

import net.domesdaybook.automata.strategy.AllMatchingTransitions;
import net.domesdaybook.automata.strategy.FirstMatchingTransition;
import net.domesdaybook.automata.strategy.NoTransition;
import java.util.Collection;
import net.domesdaybook.object.copy.DeepCopy;
import java.util.List;
import java.util.Map;

/**
 * State is an interface representing a state of an automata.
 * A state can have transitions to other states (or to itself),
 * and be a final or non-final state.  A final state is one which indicates
 * a match if it is reached processing the automata.
 * 
 * It extends the {@link DeepCopy} interface, to ensure that all states
 * can provide deep copies of themselves.
 *
 * @author Matt Palmer
 */
public interface State extends DeepCopy {

    //FIXME: replace these with an enum and wherever they are currently used?
    public static boolean FINAL = true;
    public static boolean NON_FINAL = false;
    
    public static final TransitionStrategy FIRST_MATCHING_TRANSITION = new FirstMatchingTransition();
    public static final TransitionStrategy ALL_MATCHING_TRANSITIONS = new AllMatchingTransitions();
    public static final TransitionStrategy NO_TRANSITION = new NoTransition();
   
    
    /**
     * 
     * @param value The byte value to find the next states for.
     * @param states A collection to which the next states (if any) will be added.
     */
    public void appendNextStatesForByte(final Collection<State> states, final byte value);

    
    /**
     *
     * @return true if this state is a final state.
     */
    public boolean isFinal();


    /**
     *
     * @return A list of transitions from this state.
     */
    public List<Transition> getTransitions();

    
    /**
     * 
     * @param strategy Sets a transition strategy to use for this state.
     */
    public void setTransitionStrategy(final TransitionStrategy strategy);
    
    
    /**
     * 
     * @return The transition strategy used by this state.
     */
    public TransitionStrategy getTransitionStrategy();
    
   
    /**
     * 
     * @param isFinal Whether the state is final or not.
     */
    public void setIsFinal(final boolean isFinal);

    
    
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
     * @param transition The transition to remove from this state.
     */
    public void removeTransition(final Transition transition);    
    
    
    
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
    public State deepCopy(final Map<DeepCopy, DeepCopy> oldToNewObjects);

}

