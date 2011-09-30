/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
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

package net.domesdaybook.matcher.automata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.automata.State;
import net.domesdaybook.reader.Reader;

/**
 *
 * @author Matt Palmer
 */
//public class NfaMatcher implements Matcher {
public class NfaMatcher {

    private final State firstState;


    public NfaMatcher(State firstState) {
        this.firstState = firstState;
    }
    
    
//    @Override
    public final boolean matches(final Reader reader, final long fromPosition) 
        throws IOException {
        // If the first state is final, this will always match.
        // Most Nfas won't have a first state which matches, but this is possible
        // For example, the expression "A?" matches A, or nothing at all.  
        // It can make sense for an Nfa to have an initial final state, but only 
        // if the matching algorithm is trying to locate all possible matches,
        // in which case you don't stop matching until there are no further options.
        
        //FIX: can have duplicate states from nfa or dfa's.
        //     An nfa can return more than one state for the same byte,
        //     and we are processing more than one state in any case.
        
        if (!firstState.isFinal()) {
                
            final List<State> nextStates = new ArrayList<State>();                
            List<State> currentStates = new ArrayList<State>();
            currentStates.add(firstState);
            long currentPosition = fromPosition;            
            while (!currentStates.isEmpty()) {
                
                // Get the next byte to match on:
                final byte currentByte = reader.readByte(currentPosition++);

                // Get the next set of active states from the current states:
                nextStates.clear();
                for (State currentState : currentStates) {
                    if (currentState.isFinal()) {
                        return true;
                    }
                    currentState.appendNextStatesForByte(nextStates, currentByte);
                }

                currentStates = nextStates;
            }
        }
        return firstState.isFinal();
    }
}

