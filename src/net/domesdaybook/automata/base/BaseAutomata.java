/*
 * Copyright Matt Palmer 2011-2012, All rights reserved.
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

package net.domesdaybook.automata.base;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.domesdaybook.automata.Automata;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.walker.StateChildWalker;
import net.domesdaybook.automata.walker.Step;
import net.domesdaybook.automata.walker.StepAction;
import net.domesdaybook.util.object.DeepCopy;

/**
 * A mutable implementation of {@link net.domesdaybook.automata.Automata} interface.
 * 
 * @param <T> The type of object which can be associated with states of the automata.
 * 
 * @author Matt Palmer
 */
public class BaseAutomata<T> implements Automata<T>{

    /**
     * The initial state of the automata.
     */
    protected State<T> initialState;
    
    
    /**
     * Constructs an empty Automata with no states.
     */
    public BaseAutomata() {
    }
    
    
    /**
     * Constructs an Automata with an initial state.
     * 
     * @param initialState The initial state of the automata.
     */
    public BaseAutomata(final State<T> initialState) {
        this.initialState = initialState;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public State<T> getInitialState() {
        return initialState;
    }
    
    
    /**
     * Sets the initial state of this automata.
     * 
     * @param initialState  The initial State of this automata.
     */
    public void setInitialState(final State<T> initialState) {
        this.initialState = initialState;
    }

    
    
    /**
     * {@inheritDoc}
     * 
     * This implementation calculates the final states dynamically by walking
     * the automata states to find the final ones.
     */
    public List<State<T>> getFinalStates() {
        final List<State<T>> finalStates = new ArrayList<State<T>>();
        final StepAction findFinalStates = new StepAction() {
            @Override
            public void take(final Step step) {
                if (step.currentState.isFinal()) {
                    finalStates.add(step.currentState);
                }
            }
        };
        StateChildWalker.walkAutomata(initialState, findFinalStates);
        return finalStates;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public BaseAutomata<T> deepCopy() {
        final Map<DeepCopy, DeepCopy> oldToNew = new IdentityHashMap<DeepCopy, DeepCopy>();
        return deepCopy(oldToNew);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BaseAutomata<T> deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        return new BaseAutomata<T>(initialState.deepCopy(oldToNewObjects));
    }
    
}
