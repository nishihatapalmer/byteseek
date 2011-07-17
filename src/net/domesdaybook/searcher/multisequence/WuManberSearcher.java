/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.searcher.multisequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;
import net.domesdaybook.searcher.Searcher;
import net.domesdaybook.compiler.Compiler;
import net.domesdaybook.compiler.ReversibleCompiler;
import net.domesdaybook.compiler.ReversibleCompiler.Direction;
import net.domesdaybook.compiler.multisequence.TrieMatcherCompiler;
import net.domesdaybook.matcher.multisequence.TrieMatcher;

/**
 *
 * @author matt
 */
public class WuManberSearcher implements Searcher {

    @SuppressWarnings("VolatileArrayField")
    private volatile int[] forwardShifts;
    @SuppressWarnings("VolatileArrayField")
    private volatile int[] backwardShifts;
    private volatile TrieMatcher forwardTrie;
    private volatile TrieMatcher backwardTrie;
    private final List<SequenceMatcher> matcherList;
    private final ReversibleCompiler<TrieMatcher, Collection<SequenceMatcher>> trieCompiler;
    
    
    public WuManberSearcher(final Collection<SequenceMatcher> matchers) {
        this(matchers, null);
    }
    
    
    public WuManberSearcher(final Collection<SequenceMatcher> matchers, 
            final ReversibleCompiler<TrieMatcher, Collection<SequenceMatcher>> compiler) {
        if (matchers == null) {
            throw new IllegalArgumentException("Null sequence matchers passed in to WuManberSearch");
        }
        this.matcherList = new ArrayList(matchers);
        if (compiler == null) {
            this.trieCompiler = new TrieMatcherCompiler();
        } else {
            this.trieCompiler = compiler;
        }

    }
    
    
    
    public long searchForwards(ByteReader reader, long fromPosition, long toPosition) {
        calculateForwardParameters();
        final TrieMatcher validator = forwardTrie;
        final int[] safeShifts = forwardShifts;
        
        
        
        return Searcher.NOT_FOUND;
    }

    
    public int searchForwards(byte[] bytes, int fromPosition, int toPosition) {
        calculateForwardParameters();
        final TrieMatcher validator = forwardTrie;
        final int[] safeShifts = forwardShifts;
        
        return Searcher.NOT_FOUND;
    }

    
    public long searchBackwards(ByteReader reader, long fromPosition, long toPosition) {
        calculateBackwardParameters();
        final TrieMatcher validator = backwardTrie;
        final int[] safeShifts = backwardShifts;
        
        
        return Searcher.NOT_FOUND;
    }

    
    public int searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
        calculateBackwardParameters();
        final TrieMatcher validator = backwardTrie;
        final int[] safeShifts = backwardShifts;
        
        
        return Searcher.NOT_FOUND;

    }
    
    
    private void calculateForwardParameters() {
        if (forwardShifts == null) {
             forwardShifts = createForwardShifts();
             if (forwardTrie == null) {
                 forwardTrie = createTrie(Direction.REVERSED);
             }
        }
    }
    
    
    private void calculateBackwardParameters() {
        if (backwardShifts == null) {
            backwardShifts = createBackwardShifts();
            if (backwardTrie == null) {
                backwardTrie = createTrie(Direction.FORWARDS);
            }
        }
    }

    
    private int[] createForwardShifts() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    
    private int[] createBackwardShifts() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    
    private TrieMatcher createTrie(final Direction direction) {
        try {
            return trieCompiler.compile(matcherList, direction);
        } catch (CompileException ex) {
            return null; 
        }
    }

}
