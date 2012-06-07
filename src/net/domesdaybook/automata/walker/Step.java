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
 
package net.domesdaybook.automata.walker;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;

/**
 * A class which encapsulates a step of a walk of an automata.  It is often
 * not enough to know which state is being visited. Some walks may entail 
 * visiting each step more than once, as every transition is followed.  
 * Alternatively, not all transitions may be followed if each State will only
 * be visited once.  
 * <p>
 * It may be as important to know the details of where you have come from, and how you
 * got there, as where you currently are in the walk.
 * <p>
 * Note: the very first step of a walk will probably have a null previousState and a null
 * transitionFollowed, as it did not come from a previous state by any transition.
 * 
 * @author Matt Palmer
 */
public final class Step<T> {
    
    /** 
     * The previous State from which the step was taken.  
     * Can be null if the first state is being processed, as it did not originate
     * from another State.
     */
    public final State<T> previousState;
    
    /**
     * The transition followed to arrive at the current State.
     * Can be null if the first state is being processed, as it did not originate
     * by following a transition.
     */
    public final Transition<T> transitionFollowed;
    
    /**
     * The current State being observed.  If this is the first state being
     * processing in a walk of an automata, then the other parameters will be null.
     */
    public final State<T> currentState;
    
    
    /**
     * Constructor for a Step.
     * 
     * @param previous The previous state
     * @param followed The transition followed to arrive at the current state.
     * @param currentState The current state in the walk.
     */
    public Step(final State<T> previous, final Transition<T> followed, final State<T> currentState) {
        this.previousState = previous;
        this.transitionFollowed = followed;
        this.currentState = currentState;
    }
    
}
