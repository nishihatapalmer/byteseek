/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.compiler.multisequence;

import java.util.Collection;
import java.util.List;
import net.domesdaybook.automata.wrapper.Trie;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.Compiler;
import net.domesdaybook.compiler.automata.TrieCompiler;
import net.domesdaybook.matcher.multisequence.TrieMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 *
 * @author matt
 */
public final class TrieMatcherCompiler implements Compiler<TrieMatcher, Collection<SequenceMatcher>> {

    private static TrieMatcherCompiler defaultCompiler;
    public static TrieMatcher trieMatcherFrom(final Collection<SequenceMatcher> expression) throws CompileException {
        defaultCompiler = new TrieMatcherCompiler();
        return defaultCompiler.compile(expression);
    }
    
    
    private final Compiler<Trie, Collection<SequenceMatcher>> compiler;
   
    
    public TrieMatcherCompiler() {
        this(null);
    }
    
    
    public TrieMatcherCompiler(final Compiler<Trie, Collection<SequenceMatcher>> trieCompiler) {
        if (trieCompiler == null) {
            compiler = new TrieCompiler();
        } else {
            compiler = trieCompiler;
        }
    }    
    
    
    @Override
    public TrieMatcher compile(final Collection<SequenceMatcher> expression) throws CompileException {
        Trie trie = compiler.compile(expression);
        return new TrieMatcher(trie);
    }
    
    
}
