/*
 * 
 * Copyright Matt Palmer 2011, All rights reserved.
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
 * 
 */
package net.domesdaybook.automata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.domesdaybook.automata.walker.StateChildWalker;
import net.domesdaybook.automata.walker.Step;
import net.domesdaybook.automata.walker.StepAction;
import net.domesdaybook.object.DeepCopy;

/**
 *
 * @author Matt Palmer
 */
public class BaseAutomata<T> implements Automata<T>{

    protected final State<T> initialState;
   
    
    public BaseAutomata(final State<T> initialState) {
        this.initialState = initialState;
    }

    
    public State<T> getInitialState() {
        return initialState;
    }

    
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
    

    public DeepCopy deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
