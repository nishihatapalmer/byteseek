/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.multisequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.ByteReader;

/**
 * A very simple MultiSequenceMatcher which simply tries all of the
 *  sequence matchers in turn, returning the first matching sequence, if any.
 *
 * @author Matt Palmer.
 */
public final class NaiveMultiSequenceMatcher implements MultiSequenceMatcher {

    private final List<SequenceMatcher> matchers;
    private final int minimumLength;
    private final int maximumLength;

    
    public NaiveMultiSequenceMatcher(Collection<SequenceMatcher> matchersToUse) {
        if (matchersToUse == null) {
            throw new IllegalArgumentException("Null collection of matchers passed in.");
        }
        matchers = new ArrayList(matchersToUse);
        if (matchers.isEmpty()) {
            minimumLength = 0;
            maximumLength = 0;
        } else {
            int minLength = Integer.MAX_VALUE;
            int maxLength = Integer.MIN_VALUE;
            for (final SequenceMatcher matcher : matchers) {
                final int length = matcher.length();
                minLength = Math.min(minLength, length);
                maxLength = Math.max(maxLength, length);
            }
            minimumLength = minLength;
            maximumLength = maxLength;
        }
    }


    /**
     * 
     * @inheritDoc
     */
    @Override
    public List<SequenceMatcher> allMatches(final ByteReader reader, final long matchPosition) {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();
        for (final SequenceMatcher sequence : matchers) {
            if (sequence.matches(reader, matchPosition)) {
                result.add(sequence);
            }
        }
        return result;
    }
    
    
    /**    
     * 
     * @inheritDoc
     */    
    @Override
    public SequenceMatcher firstMatch(final ByteReader reader, final long matchPosition) {
        for (final SequenceMatcher sequence : matchers) {
            if (sequence.matches(reader, matchPosition)) {
                return sequence;
            }
        }
        return null;
    }    

    
    /**    
     * 
     * @inheritDoc
     */ 
    @Override
    public boolean matches(final ByteReader reader, final long matchPosition) {
        for (final SequenceMatcher sequence : matchers) {
            if (sequence.matches(reader, matchPosition)) {
                return true;
            }
        }
        return false;
    }
    
    
    /**    
     * 
     * @inheritDoc
     */ 
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        for (final SequenceMatcher sequence : matchers) {
            if (sequence.matches(bytes, matchPosition)) {
                return true;
            }
        }
        return false;
    }

    
    /**    
     * 
     * @inheritDoc
     */ 
    @Override   
    public Collection<SequenceMatcher> allMatches(final byte[] bytes, final int matchPosition) {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();
        for (final SequenceMatcher sequence : matchers) {
            if (sequence.matches(bytes, matchPosition)) {
                result.add(sequence);
            }
        }
        return result;        
    }


    /**    
     * @inheritDoc 
     */ 
    @Override      
    public SequenceMatcher firstMatch(byte[] bytes, int matchPosition) {
        for (final SequenceMatcher sequence : matchers) {
            if (sequence.matches(bytes, matchPosition)) {
                return sequence;
            }
        }
        return null;
    }

    
    /**    
     * @inheritDoc 
     */ 
    @Override  
    public int getMinimumLength() {
        return minimumLength;
    }

    
    /**    
     * @inheritDoc 
     */ 
    @Override  
    public int getMaximumLength() {
        return maximumLength;
    }
    
}
