/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
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
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
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
 */

package net.domesdaybook.automata.wrapper;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.domesdaybook.automata.State;
import net.domesdaybook.object.DeepCopy;

/**
 * A simple class which wraps the initial and final states of an automata.
 * This is useful when building automata, as we don't have to keep
 * recalculating the final states from the automata each time we need them.
 * <p>
 * Also implements the {@link DeepCopy} interface, so we can easily
 * make deep copies of the wrapper and its wrapped automata.
 *
 * @author Matt Palmer
 */
public final class InitialFinalStates implements DeepCopy {

    public State initialState;
    public List<State> finalStates = new ArrayList<State>();

    /**
     * A method which makes a state final or not, and ensures that
     * the internal list of final states is kept up to date.
     *
     * @param state the state to make final or not.
     * @param isFinal Whether the state should be final.
     */
    public void setIsFinal(final State state, final boolean isFinal) {
        state.setIsFinal(isFinal);
        if (isFinal) {
            if (!finalStates.contains(state)) {
                finalStates.add(state);
            }
        } else {
            finalStates.remove(state);
        }
    }


    /**
     * Makes a deep copy of this object and its wrapped automata.
     * This is a convenience method which sets up an initial map
     * before calling the main deepCopy method that performs the
     * actual copy.
     * 
     * @return A deep copy of this object and its wrapped automata.
     */
    public InitialFinalStates deepCopy() {
        final Map<DeepCopy, DeepCopy> oldToNewObjects = new IdentityHashMap<DeepCopy, DeepCopy>();
        return deepCopy(oldToNewObjects);
    }    


    /**
     * Makes a deep copy of this object and its wrapped automata.
     *
     * @param oldToNewObjects A map of old objects to their deep copies.
     * @return A deep copy of this object and its wrapped automata.
     */
    @Override
    public InitialFinalStates deepCopy(final Map<DeepCopy, DeepCopy> oldToNewObjects) {
        InitialFinalStates copy = new InitialFinalStates();
        oldToNewObjects.put(this, copy);
        if (initialState == null) {
            copy.initialState = null;
        } else {
            copy.initialState = (State) initialState.deepCopy(oldToNewObjects);
        }
        if (finalStates == null) {
            copy.finalStates = null;
        } else {
            copy.finalStates = new ArrayList<State>();
            for (State finalState : finalStates) {
                final State finalStateCopy = (State) finalState.deepCopy(oldToNewObjects);
                copy.finalStates.add(finalStateCopy);
            }
        }
        return copy;
    }
        
}