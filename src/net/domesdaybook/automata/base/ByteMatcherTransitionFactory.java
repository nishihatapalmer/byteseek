package net.domesdaybook.automata.base;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.TransitionFactory;
import net.domesdaybook.matcher.bytes.ByteMatcher;


/**
 * @author Matt Palmer
 */
public class ByteMatcherTransitionFactory implements TransitionFactory<ByteMatcher, ByteMatcher>{

  public Transition<ByteMatcher> create(final ByteMatcher source,
                                        final boolean ignoreInversion,
                                        final State<ByteMatcher> toState) {
    return new ByteMatcherTransition(source, toState);
  }

}
