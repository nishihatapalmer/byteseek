/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 * A Glushkov Non-deterministic Finite-state Automata (NFA) is an automata which
 * is constructed to have one initial state, and additionally, a state for each
 * position in the regular expression that defines a byte or set of bytes.
 *
 * Transitions exist from each position to every other reachable position,
 * on the byte or bytes of the position being transitioned to.
 *
 * Being non-deterministic means that from any given state,
 * you can have transitions to more than one other state on the same byte value.
 * By way of constrast, Deterministic Finite-state Automata (DFAs) have at most one
 * state they can transition to on a given byte value (or none, if there is no match).
 *
 * Unlike the classic Thompson construction (the most common regular expression NFA)
 * Glushkov automata have no "empty" transitions - that is, transitions to another
 * state without reading a byte.  The Thompson NFA uses empty transitions to
 * simplify constructing the automata, by wiring up states together as the need
 * arises during construction, and to make mathematically proving certain properties
 * of the automata easier.  However, they seem to have no value other than this.
 *
 * Having no empty transitions makes the automata smaller, more peformant,
 * and easier to transform it further (e.g. building a DFA from it)
 * but makes constructing it a little more difficult in the first place.
 * 
 */

package net.domesdaybook.expression.compiler;

import net.domesdaybook.automata.nfa.NfaState;
import net.domesdaybook.expression.parser.regularExpressionParser;
import org.antlr.runtime.tree.CommonTree;

/**
 *
 * @author matt
 */
public class GlushkovNfaCompiler implements NfaCompiler {


    @Override
    public NfaState compile(final CommonTree ast) {
       return buildGlushkovAutomata(ast).initialState;
    }


    private InitialAndFinalNfaStates buildGlushkovAutomata(final CommonTree ast) {
       
        switch (ast.getToken().getType()) {
            
            // recursive part of building:
            case (regularExpressionParser.SEQUENCE): {

            }

            case (regularExpressionParser.ALT): {

            }

            case (regularExpressionParser.REPEAT): {
                
            }

            // non-recursive part of building:
            case (regularExpressionParser.BYTE): {

            }


        }
        return null;
    }

    private class InitialAndFinalNfaStates {

        public NfaState initialState;
        public NfaState finalState;

    }



}
