/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.compiler.automata;

import java.util.List;
import net.domesdaybook.automata.State;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.Compiler;

/**
 *
 * @author matt
 */
public class DfaExpressionsCompiler implements Compiler<State, List<String>> {
    
    private static DfaExpressionsCompiler defaultCompiler;
    public static State nfaFrom(List<String> expressions) throws CompileException {
        defaultCompiler = new DfaExpressionsCompiler();
        return defaultCompiler.compile(expressions);
    }
    
    private final Compiler<State, List<String>> multiNfaCompiler;
    private final Compiler<State, State> dfaCompiler;
    
    public DfaExpressionsCompiler() {
        this(null, null);
    }
    
    public DfaExpressionsCompiler(final Compiler<State, List<String>> multiNfaCompilerToUse, Compiler<State, State> dfaCompilerToUse) {
        if (multiNfaCompilerToUse == null) {
            this.multiNfaCompiler = new NfaExpressionsCompiler();
        } else {
            this.multiNfaCompiler = multiNfaCompilerToUse;
        }
        if (dfaCompilerToUse == null) {
            this.dfaCompiler = new DfaFromNfaCompiler();
        } else {
            this.dfaCompiler = dfaCompilerToUse;
        }
    }
    
    @Override
    public State compile(List<String> expressions) throws CompileException {
        State nfaState = multiNfaCompiler.compile(expressions);
        return dfaCompiler.compile(nfaState);
    }

    /**
     * Turns a list of expression strings into a single expression string
     * by putting each expression in round brackets and separating them
     * with the | alternative symbol.
     * 
     * @param expressions a list of expressions
     * @return A single expression matching any of them.
     */
    private String getExpressionsAsAlternatives(List<String> expressions) {
        final StringBuilder builder = new StringBuilder();
        char separator = ' ';
        for (String expression : expressions) {
            builder.append(separator);
            builder.append("(");
            builder.append(expression);
            builder.append(")");
            separator = '|';
        }
        return builder.toString();
    }

    
}
