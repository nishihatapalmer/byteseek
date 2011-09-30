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


package net.domesdaybook.automata.strategy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.TransitionStrategy;
import net.domesdaybook.object.copy.DeepCopy;

/**
 *
 * @author matt
 */
public final class AllMatchingTransitions implements TransitionStrategy {

    @Override
    public void getDistinctStatesForByte(Collection<State> states, byte value, Collection<Transition> transitions) {
        // Do not know whether there will be:
        // (1) more than one state for the same byte value, or
        // (2) whether the same state will appear more than once against the
        //     same byte value. 
        // Ensure that only distinct states are returned which match the byte value.
        Set<State> matchingStates = new HashSet<State>();
        for (Transition transition : transitions) {
            State nextState = transition.getStateForByte(value);
            if (nextState != null && !matchingStates.contains(nextState)) {
                matchingStates.add(nextState);
                states.add(nextState);
            }
        }
    }

    @Override
    public void initialise(State state) {
    }

    public TransitionStrategy deepCopy(Map<DeepCopy, DeepCopy> oldToNewObjects) {
        return this;
    }
    
}
