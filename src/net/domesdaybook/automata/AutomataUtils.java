/*
 * Copyright Matt Palmer 2009-2012, All rights reserved.
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

package net.domesdaybook.automata;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class to do useful things with automata.
 * 
 * @author Matt Palmer
 */
public final class AutomataUtils {

	private static final String DOT_HEADER = "digraph {\n";
	private static final String DOT_TITLE = "label=\"%s\"\n";
	private static final String DOT_FOOTER = "\n}";
	private static final String FINAL_STATE_SHAPE = "doublecircle";
	private static final String NON_FINAL_STATE_SHAPE = "circle";
	private static final String STATE_DEFINITION = "%s [label=\"%s\", shape=\"%s\"]\n";
	private static final String TRANSITION_DEFINITION = "%s->%s [label=\"%s\"]\n";

	/**
	 * A private constructor to prevent instantiation of this static utility
	 * class.
	 */
	private AutomataUtils() {
	}

	/**
	 * Builds a text representation of the automata in Graphviz dot format.
	 * http://www.graphviz.org/
	 * <p/>
	 * Graphviz can then render the automata using a variety of graph layout
	 * algorithms, outputting the render to many common formats.
	 * 
	 * @param automata
	 *            The automata to produce a DOT graph from.
	 * @param title
	 *            The title of the DOT graph.
	 * @return A String containing the automata serialised in DOT format.
	 */
	public static <T> String toDot(final Automata<T> automata,
			final String title) {
		final StringBuilder builder = new StringBuilder();
		builder.append(DOT_HEADER);
		final String onelineTitle = title.replaceAll("\\s", " ");
		builder.append(String.format(DOT_TITLE, onelineTitle));
		final Map<State<T>, Integer> visitedStates = new IdentityHashMap<State<T>, Integer>();
		buildDot(automata.getInitialState(), visitedStates, 0, builder);
		builder.append(DOT_FOOTER);
		return builder.toString();
	}

	private static <T> int buildDot(final State<T> state,
			final Map<State<T>, Integer> visitedStates, int nextStateNumber,
			final StringBuilder builder) {
		if (!visitedStates.containsKey(state)) {
			visitedStates.put(state, nextStateNumber);
			final String label = Integer.toString(nextStateNumber);
			final String shape = state.isFinal() ? FINAL_STATE_SHAPE
					: NON_FINAL_STATE_SHAPE;
			builder.append(String.format(STATE_DEFINITION, label, label, shape));

			// process its transitions:
			final List<Transition<T>> transitions = state.getTransitions();
			for (final Transition<T> transition : transitions) {
				final State<T> toState = transition.getToState();
				final int processedNumber = buildDot(toState, visitedStates,
						nextStateNumber + 1, builder);
				nextStateNumber = processedNumber > nextStateNumber ? processedNumber
						: nextStateNumber;
				final String toStateLabel = Integer.toString(visitedStates
						.get(toState));
				final String transitionLabel = transition.toString();
				builder.append(String.format(TRANSITION_DEFINITION, label,
						toStateLabel, transitionLabel));
			}
		}
		return nextStateNumber;
	}

}
