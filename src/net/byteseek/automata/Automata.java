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

package net.byteseek.automata;

import java.util.Collection;

import net.byteseek.util.object.DeepCopy;

/**
 * An interface representing an automata, containing an initial {@link State}
 * and a collection of final States. A final State is one which represents a
 * match in the automata.
 * 
 * @param <T>
 *            The type of object associated with a State.
 * 
 * @author Matt Palmer
 */
public interface Automata<T> extends DeepCopy {

	/**
	 * Returns the initial {@link State} of the Automata.
	 * 
	 * @return State<T> the initial State of the automata.
	 */
	public State<T> getInitialState();

	/**
	 * Returns a collection of {@link State}s which are final in the Automata.
	 * Implementations should return an empty collection if there are no final
	 * states.
	 * 
	 * @return A collection of final States.
	 */
	public Collection<State<T>> getFinalStates();

	/**
	 * Returns true if the automata is deterministic.
	 * 
	 * @return true if the automata is deterministic.
	 */
	public boolean isDeterministic();

	/**
	 * Produces a deep copy of the automata, its' States and Transitions. It
	 * will not produce deep copies of any objects associated with a State,
	 * although the automata copy will link to the same associated objects as
	 * the original.
	 * 
	 * @return A deep copy of the automata.
	 */
	public Automata<T> deepCopy();

}
