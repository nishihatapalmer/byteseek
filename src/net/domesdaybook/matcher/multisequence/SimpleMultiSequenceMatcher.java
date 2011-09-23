/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.multisequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.domesdaybook.matcher.sequence.ByteSequenceMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;

/**
 * A very simple MultiSequenceMatcher which simply tries all of the
 * sequence matchers in turn.
 *
 * @author Matt Palmer.
 */
public final class SimpleMultiSequenceMatcher implements MultiSequenceMatcher {

    private final List<SequenceMatcher> matchers;
    private final int minimumLength;
    private final int maximumLength;

    
    public SimpleMultiSequenceMatcher(List<byte[]> bytesToMatch) {
        if (bytesToMatch == null) {
            throw new IllegalArgumentException("Null collection of bytes passed in.");
        }
        matchers = new ArrayList<SequenceMatcher>(bytesToMatch.size());
        for (final byte[] bytes : bytesToMatch) {
            final SequenceMatcher sequence = new ByteSequenceMatcher(bytes);
            matchers.add(sequence);
        }
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
    
    
    public SimpleMultiSequenceMatcher(Collection<SequenceMatcher> matchersToUse) {
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
    public List<SequenceMatcher> allMatches(final Reader reader, final long matchPosition) 
        throws IOException {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();         
        final List<SequenceMatcher> localMatchers = matchers;
        for (final SequenceMatcher sequence : localMatchers) {
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
    public Collection<SequenceMatcher> allMatches(final byte[] bytes, final int matchPosition) {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();         
        final long noOfBytes = bytes.length;
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<SequenceMatcher> localMatchers = matchers;
            if (matchPosition + maximumLength < noOfBytes) {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matchesNoBoundsCheck(bytes, matchPosition)) {
                        result.add(sequence);
                    }
                }
            } else {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matches(bytes, matchPosition)) {
                        result.add(sequence);
                    }
                }            
            }
        }
        return result;        
    }    
    
    
    /**
     * 
     * @inheritDoc
     */
    @Override
    public Collection<SequenceMatcher> allMatchesBackwards(final Reader reader, 
            final long matchPosition) throws IOException {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();         
        final List<SequenceMatcher> localMatchers = matchers;
        final long onePastMatchPosition = matchPosition + 1;
        for (final SequenceMatcher sequence : localMatchers) {
            if (sequence.matches(reader, onePastMatchPosition - sequence.length())) {
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
    public Collection<SequenceMatcher> allMatchesBackwards(final byte[] bytes, final int matchPosition) {
        final List<SequenceMatcher> result = new ArrayList<SequenceMatcher>();         
        final int noOfBytes = bytes.length;
        if (matchPosition >= minimumLength - 1 && matchPosition < noOfBytes) {
            final List<SequenceMatcher> localMatchers = matchers;
            final int onePastMatchPosition = matchPosition + 1;
            if (onePastMatchPosition >= maximumLength) {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matchesNoBoundsCheck(bytes, onePastMatchPosition - sequence.length())) {
                        result.add(sequence);
                    }
                }
            } else {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matches(bytes, onePastMatchPosition - sequence.length())) {
                        result.add(sequence);
                    }
                }            
            }
        }
        return result; 
    }    
    
    
    /**    
     * 
     * @inheritDoc
     */    
    @Override
    public SequenceMatcher firstMatch(final Reader reader, final long matchPosition) 
            throws IOException {
        final List<SequenceMatcher> localMatchers = matchers;
        for (final SequenceMatcher sequence : localMatchers) {
            if (sequence.matches(reader, matchPosition)) {
                return sequence;
            }
        }            
        return null;
    }    

    
    /**    
     * @inheritDoc 
     */ 
    @Override      
    public SequenceMatcher firstMatch(final byte[] bytes, final int matchPosition) {
        final long noOfBytes = bytes.length;
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<SequenceMatcher> localMatchers = matchers;
            if (matchPosition + maximumLength < noOfBytes) {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matchesNoBoundsCheck(bytes, matchPosition)) {
                        return sequence;
                    }
                }
            } else {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matches(bytes, matchPosition)) {
                        return sequence;
                    }
                }            
            }
        }
        return null;        
    }
    
    
    /**    
     * @inheritDoc 
     */ 
    @Override 
    public SequenceMatcher firstMatchBackwards(final Reader reader, 
            final long matchPosition) throws IOException {
        final List<SequenceMatcher> localMatchers = matchers;
        final long onePastMatchPosition = matchPosition + 1;
        for (final SequenceMatcher sequence : localMatchers) {
            if (sequence.matches(reader, onePastMatchPosition - sequence.length())) {
                return sequence;
            }
        }            
        return null;    
    }

    
    /**    
     * @inheritDoc 
     */ 
    @Override 
    public SequenceMatcher firstMatchBackwards(final byte[] bytes, final int matchPosition) {
        final int noOfBytes = bytes.length;
        if (matchPosition >= minimumLength - 1 && matchPosition < noOfBytes) {
            final List<SequenceMatcher> localMatchers = matchers;
            final int onePastMatchPosition = matchPosition + 1;
            if (onePastMatchPosition >= maximumLength) {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matchesNoBoundsCheck(bytes, onePastMatchPosition - sequence.length())) {
                        return sequence;
                    }
                }
            } else {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matches(bytes, onePastMatchPosition - sequence.length())) {
                        return sequence;
                    }
                }            
            }
        }
        return null;   
    }    
    
    
    /**    
     * 
     * @inheritDoc
     */ 
    @Override
    public boolean matches(final Reader reader, final long matchPosition) 
            throws IOException {
        final List<SequenceMatcher> localMatchers = matchers;
        for (final SequenceMatcher sequence : localMatchers) {
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
        final int noOfBytes = bytes.length;
        if (matchPosition >= minimumLength - 1 && matchPosition + minimumLength < noOfBytes) {
            final List<SequenceMatcher> localMatchers = matchers;
            if (matchPosition + maximumLength < noOfBytes) {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matchesNoBoundsCheck(bytes, matchPosition)) {
                        return true;
                    }
                }
            } else {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matches(bytes, matchPosition)) {
                        return true;
                    }
                }            
            }
        }
        return false;        
    }

    

    /**    
     * 
     * @inheritDoc
     */ 
    @Override
    public boolean matchesBackwards(final Reader reader, 
            final long matchPosition) throws IOException { 
        final List<SequenceMatcher> localMatchers = matchers;
        final long onePastMatchPosition = matchPosition + 1;
        for (final SequenceMatcher sequence : localMatchers) {
            if (sequence.matches(reader, onePastMatchPosition - sequence.length())) {
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
    public boolean matchesBackwards(final byte[] bytes, final int matchPosition) {
        final int noOfBytes = bytes.length;
        if (matchPosition >= minimumLength - 1 && matchPosition < noOfBytes) {
            final List<SequenceMatcher> localMatchers = matchers;
            final int onePastMatchPosition = matchPosition + 1;
            if (onePastMatchPosition >= maximumLength) {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matchesNoBoundsCheck(bytes, onePastMatchPosition - sequence.length())) {
                        return true;
                    }
                }
            } else {
                for (final SequenceMatcher sequence : localMatchers) {
                    if (sequence.matches(bytes, onePastMatchPosition - sequence.length())) {
                        return true;
                    }
                }            
            }
        }
        return false; 
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
