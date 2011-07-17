/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.searcher.multisequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.automata.wrapper.Trie;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;
import net.domesdaybook.searcher.Searcher;
import net.domesdaybook.compiler.Compiler;
import net.domesdaybook.compiler.automata.TrieCompiler;

/**
 *
 * @author matt
 */
public class WuManberSearcher implements Searcher {

    @SuppressWarnings("VolatileArrayField")
    private volatile int[] forwardShifts;
    @SuppressWarnings("VolatileArrayField")
    private volatile int[] backwardShifts;
    private volatile Trie forwardTrie;
    private volatile Trie backwardTrie;
    private final List<SequenceMatcher> matcherList;
    private final Compiler<Trie, Collection<SequenceMatcher>> trieCompiler;
    
    
    public WuManberSearcher(final Collection<SequenceMatcher> matchers) {
        this.matcherList = new ArrayList(matchers);
        this.trieCompiler = new TrieCompiler();
    }
    
    
    public WuManberSearcher(final Collection<SequenceMatcher> matchers, 
            final Compiler<Trie, Collection<SequenceMatcher>> compiler) {
        this.matcherList = new ArrayList(matchers);
        this.trieCompiler = compiler;
    }
    
    
    public long searchForwards(ByteReader reader, long fromPosition, long toPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public int searchForwards(byte[] bytes, int fromPosition, int toPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public long searchBackwards(ByteReader reader, long fromPosition, long toPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    public int searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
