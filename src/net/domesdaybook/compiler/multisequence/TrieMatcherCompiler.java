/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.compiler.multisequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.automata.wrapper.Trie;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.compiler.ReversibleCompiler;
import net.domesdaybook.compiler.automata.TrieCompiler;
import net.domesdaybook.matcher.multisequence.TrieMatcher;
import net.domesdaybook.matcher.sequence.ByteSequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 *
 * @author matt
 */
public final class TrieMatcherCompiler implements ReversibleCompiler<TrieMatcher, Collection<SequenceMatcher>> {

    private static TrieMatcherCompiler defaultCompiler;
    
    public static TrieMatcher trieMatcherFrom(final Collection<SequenceMatcher> expression) throws CompileException {
        return trieMatcherFrom(expression, Direction.FORWARDS);
    }
    
    public static TrieMatcher trieMatcherFrom(final List<byte[]> bytes) throws CompileException {
        return trieMatcherFrom(bytes, Direction.FORWARDS);
    }
    
    public static TrieMatcher trieMatcherFrom(final Collection<SequenceMatcher> expression,
                                              final Direction direction) throws CompileException {
        defaultCompiler = new TrieMatcherCompiler();
        return defaultCompiler.compile(expression, direction);
    }
    
    
    public static TrieMatcher trieMatcherFrom(final List<byte[]> bytes,
                                              final Direction direction) throws CompileException {
        defaultCompiler = new TrieMatcherCompiler();
        List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>(bytes.size());
        for (final byte[] bytesToUse : bytes) {
            matchers.add(new ByteSequenceMatcher(bytesToUse));
        }
        return defaultCompiler.compile(matchers, direction);
    }    
    
    
    private final ReversibleCompiler<Trie, Collection<SequenceMatcher>> compiler;
   
    
    public TrieMatcherCompiler() {
        this(null);
    }
    
    
    public TrieMatcherCompiler(final ReversibleCompiler<Trie, Collection<SequenceMatcher>> trieCompiler) {
        if (trieCompiler == null) {
            compiler = new TrieCompiler();
        } else {
            compiler = trieCompiler;
        }
    }    
    
    
    @Override
    public TrieMatcher compile(final Collection<SequenceMatcher> expression) throws CompileException {
        final Trie trie = compiler.compile(expression);
        return new TrieMatcher(trie);
    }
    

    @Override
    public TrieMatcher compile(Collection<SequenceMatcher> expression, Direction direction) throws CompileException {
        final Trie trie = compiler.compile(expression, direction);
        return new TrieMatcher(trie);
    }
    
    
}
