/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.compiler.automata;

import net.domesdaybook.automata.State;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.Compiler;

/**
 *
 * @author matt
 */
public class DfaExpressionCompiler implements Compiler<State, String> {

    private static DfaExpressionCompiler defaultCompiler;
    public static State dfaFrom(String expression) throws CompileException {
        defaultCompiler = new DfaExpressionCompiler();
        return defaultCompiler.compile(expression);
    }
    
    private final Compiler<State, String> nfaCompiler;
    private final Compiler<State, State> dfaFromNfaCompiler;
    
    public DfaExpressionCompiler() {
        this(null, null);
    }

    /* can't include this method as the the following constructor has the
     * same type erasure as this one.
    public DfaExpressionCompiler(Compiler<State, String> nfaCompilerToUse) {
        this(nfaCompilerToUse, null);
    }
     *
     */
    
    public DfaExpressionCompiler(Compiler<State, State> dfaFromNfaCompilerToUse) {
        this(null, dfaFromNfaCompilerToUse);
    }    
    
    public DfaExpressionCompiler(Compiler<State, String> nfaCompilerToUse, Compiler<State, State> dfaFromNfaCompilerToUse) {
        if (nfaCompilerToUse == null) {
            this.nfaCompiler = new NfaExpressionCompiler();
        } else {
            this.nfaCompiler = nfaCompilerToUse;
        }
        if (dfaFromNfaCompilerToUse == null) {
            this.dfaFromNfaCompiler = new DfaFromNfaCompiler();
        } else {
            this.dfaFromNfaCompiler = dfaFromNfaCompilerToUse;
        }
    }
    
    @Override
    public State compile(String expression) throws CompileException {
        State initialNfaState = nfaCompiler.compile(expression);
        return dfaFromNfaCompiler.compile(initialNfaState);
    }
    
}
