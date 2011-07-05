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

    
    public NaiveMultiSequenceMatcher(Collection<SequenceMatcher> matchersToUse) {
        matchers = new ArrayList(matchersToUse);
    }


    /**
     * 
     * @inheritDoc
     * 
     * Note: will return false if access is outside the byte reader.
     *       It will not throw an IndexOutOfBoundsException.
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
     * 
     * Note: will return false if access is outside the byte reader.
     *       It will not throw an IndexOutOfBoundsException.
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
     * 
     * Note: will return false if access is outside the byte reader.
     *       It will not throw an IndexOutOfBoundsException.
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
     * 
     * Note: will return false if access is outside the byte array.
     *       It will not throw an IndexOutOfBoundsException.
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
     * 
     * Note: will return false if access is outside the byte array.
     *       It will not throw an IndexOutOfBoundsException.
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
     * 
     * @inheritDoc
     * 
     * Note: will return false if access is outside the byte array.
     *       It will not throw an IndexOutOfBoundsException.     * 
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
    
}
