/*
 * Copyright Matt Palmer 2013, All rights reserved.
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

import net.byteseek.automata.serializer.DotSerializer;
import net.byteseek.automata.walker.CountStateAction;
import net.byteseek.automata.walker.StateChildWalker;
import net.byteseek.automata.walker.Walker;

/**
 * A static utility class containing methods which are useful when processing Automata.
 * 
 * @author Matt Palmer
 */
public final class AutomataUtils {
	
	private AutomataUtils() {
	}
	
	public static <T> String toDot(Automata<T> automata, boolean includeAssociatedObjects, String title) {
		return new DotSerializer<T>().serialize(automata, includeAssociatedObjects, title);
	}
	
	public static <T> int countStates(Automata<T> automata) {
		final Walker<T> stateWalker = new StateChildWalker<T>();
		final CountStateAction<T> stateCounter = new CountStateAction<T>();
		stateWalker.walk(automata.getInitialState(), stateCounter);
		return stateCounter.getStateCount();
	}

}
