/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.compiler.multisequence;

import java.util.List;
import net.domesdaybook.automata.AssociatedState;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.Compiler;
import net.domesdaybook.compiler.automata.TrieCompiler;
import net.domesdaybook.matcher.multisequence.TrieMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 *
 * @author matt
 */
public final class TrieMatcherCompiler implements Compiler<TrieMatcher, List<SequenceMatcher>> {

    private static TrieMatcherCompiler defaultCompiler;
    public static TrieMatcher trieMatcherFrom(List<SequenceMatcher> expression) throws CompileException {
        defaultCompiler = new TrieMatcherCompiler();
        return defaultCompiler.compile(expression);
    }
    
    
    final Compiler<AssociatedState<SequenceMatcher>, List<SequenceMatcher>> compiler;
   
    
    public TrieMatcherCompiler() {
        this(null);
    }
    
    
    public TrieMatcherCompiler(Compiler<AssociatedState<SequenceMatcher>, List<SequenceMatcher>> trieCompiler) {
        if (trieCompiler == null) {
            compiler = new TrieCompiler();
        } else {
            compiler = trieCompiler;
        }
    }    
    
    
    @Override
    public TrieMatcher compile(List<SequenceMatcher> expression) throws CompileException {
        AssociatedState<SequenceMatcher> initialTrieState = compiler.compile(expression);
        return new TrieMatcher(initialTrieState);
    }
    
    
}
