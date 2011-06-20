/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class NfaExpressionsCompiler implements Compiler<State, List<String>> {

    private static NfaExpressionsCompiler defaultCompiler;
    public static State nfaFrom(List<String> expressions) throws CompileException {
        defaultCompiler = new NfaExpressionsCompiler();
        return defaultCompiler.compile(expressions);
    }
    
    private final Compiler<State, String> nfaCompiler;
    
    public NfaExpressionsCompiler() {
        this(null);
    }
    
    public NfaExpressionsCompiler(final Compiler<State, String> nfaCompilerToUse) {
        if (nfaCompilerToUse == null) {
            this.nfaCompiler = new NfaExpressionCompiler();
        } else {
            this.nfaCompiler = nfaCompilerToUse;
        }
    }
    
    @Override
    public State compile(List<String> expressions) throws CompileException {
        return nfaCompiler.compile(getExpressionsAsAlternatives(expressions));
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
