/*
 * Copyright Matt Palmer 2012-2013, All rights reserved.
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
package net.domesdaybook.compiler.regex;

import net.domesdaybook.automata.State;
import net.domesdaybook.automata.Transition;
import net.domesdaybook.automata.TransitionFactory;
import net.domesdaybook.automata.base.ByteMatcherTransition;
import net.domesdaybook.compiler.matcher.CompilerUtils;
import net.domesdaybook.matcher.bytes.ByteMatcherFactory;
import net.domesdaybook.matcher.bytes.SetAnalysisByteMatcherFactory;
import net.domesdaybook.parser.ParseException;
import net.domesdaybook.parser.tree.ParseTree;


public final class ParseTreeTransitionFactory<T>
  implements TransitionFactory<T, ParseTree> {

  private final ByteMatcherFactory matcherFactory;
  
  public ParseTreeTransitionFactory() {
    this(new SetAnalysisByteMatcherFactory());
  }
  
  public ParseTreeTransitionFactory(final ByteMatcherFactory matcherFactory) {
    this.matcherFactory = matcherFactory;
  }
  
  public Transition<T> create(final ParseTree source,
                              final boolean invert,
                              final State<T> toState) {
    try {
      switch (source.getParseTreeType()) {
        case BYTE:          return createByteTransition(source, toState);
        case ALL_BITMASK:   return createAllBitmaskTransition(source, toState);
        case ANY_BITMASK:   return createAnyBitmaskTransition(source, toState);
        case ANY:           return createAnyTransition(source, toState);
        case RANGE:			return createRangeTransition(source, toState);
        case SET:           return createSetTransition(source, toState);
      }
    } catch (final ParseException justReturnNull) {
      //TODO: Should a factory throw an exception?  If so, it involves some
      //      extensive refactoring in the classes that use the factories, as
      //      they currently don't expect to fail at creating transitions.
      //      Maybe they should - there are inputs from which a transition can't be
      //      created, for example, the empty set of bytes.  However, this will also
      //      cascade into exceptions for building other kinds of things.  But I guess,
      //      if it's possible to fail due to the arguments passed in, we can only choose 
      //      a checked exception, or throw a runtime IllegalArgumentException...
    }
    return null;
  }

  
  private Transition<T> createByteTransition(final ParseTree ast, final State<T> toState) throws ParseException {
    return new ByteMatcherTransition<T>(CompilerUtils.createByteMatcher(ast), toState);
  }
  
  private Transition<T> createAllBitmaskTransition(final ParseTree ast, final State<T> toState) throws ParseException {
    return new ByteMatcherTransition<T>(CompilerUtils.createAllBitmaskMatcher(ast), toState);
  }

  private Transition<T> createAnyBitmaskTransition(final ParseTree ast, final State<T> toState) throws ParseException {
   return new ByteMatcherTransition<T>(CompilerUtils.createAnyBitmaskMatcher(ast), toState);
  }
  
  private Transition<T> createAnyTransition(final ParseTree ast, final State<T> toState) throws ParseException {
    return new ByteMatcherTransition<T>(CompilerUtils.createAnyMatcher(ast), toState);
  }
  
  private Transition<T> createRangeTransition(final ParseTree ast, final State<T> toState) throws ParseException {
	  return new ByteMatcherTransition<T>(CompilerUtils.createRangeMatcher(ast), toState);
  }
  
  private Transition<T> createSetTransition(final ParseTree ast, final State<T> toState) throws ParseException {
     return new ByteMatcherTransition<T>(CompilerUtils.createMatcherFromSet(ast, matcherFactory), toState);
  }

}
