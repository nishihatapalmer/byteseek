/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence.searcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author matt palmer
 */
public final class WuManberSearch {

    private int shortestPatternLength = 0;
    private int shiftTableSize = 0;
    private int shiftTableHashBitMask = 0;
    private int verifierTableSize = 0;
    private int blockSize = 1;
    private int[] shifts;
    private Map<String, List<String>> verifiers;

    public enum searchType {
        FIND_ALL,
        FIND_NEXT;
    }

    public class MatchResult {
        private long matchPosition;
        private String matchingPattern;

        public MatchResult(long matchPosition, String matchingPattern) {
            this.matchPosition = matchPosition;
            this.matchingPattern = matchingPattern;
        }

        public long getMatchPosition() {
            return matchPosition;
        }

        public String getMatchingPattern() {
            return matchingPattern;
        }
    }


    public WuManberSearch(List<String> patterns) {
        compile(patterns);
    }


    public WuManberSearch(String pattern) {
        List<String> patterns = new ArrayList<String>();
        patterns.add(pattern);
        compile(patterns);
    }

    public void compile(List<String> patterns) {
        calculateParameters(patterns);
        buildTables(patterns);
    }


    public List<MatchResult> find(String text, searchType type) {
        return findFrom(text, 0, type);
    }


    public List<MatchResult> findFrom(String text, int fromPosition, searchType type) {
        return findWithin(text, fromPosition, text.length(), type);
    }


    public List<MatchResult> findWithin(String text, int fromPosition, int toPosition, searchType type) {
        if (fromPosition >= text.length() || toPosition >= text.length()) {
            throw new IllegalArgumentException("Search positions outside length of text.");
        }
        if (fromPosition > toPosition) {
            throw new IllegalArgumentException("Search from position greater than to position.");
        }
        return search(text, fromPosition, toPosition, type);
    }


    private List<MatchResult> search(String text, int fromPosition, int toPosition, searchType type) {
        List<MatchResult> result = new ArrayList<MatchResult>();
        int pos = fromPosition + shortestPatternLength - 1;
        while (pos <= toPosition) {
            final String textBlock = text.substring(pos-blockSize+1, pos+1);
            final int shiftValue = shifts[getBlockHash(textBlock)];
            if (shiftValue == 0) { // if there is no possible shift, the text to the left may be a match for a pattern.
                List<MatchResult> matches = verifyMatches(text, pos, verifiers.get(textBlock));
                if ( matches.size() > 0 ) {
                    result.addAll(matches);
                    if (type == searchType.FIND_NEXT) {
                        break;
                    }
                }
                pos += 1;
            } else {
                pos += shiftValue;
            }
        }
        return result;
    }


    // Naive verifier, checking each possible match one by one.
    // A reverse trie structure would work much more efficiently.
    private List<MatchResult> verifyMatches(String text, int pos, List<String> possibleMatches) {
        List<MatchResult> matches = new ArrayList<MatchResult>();
        for (String match : possibleMatches) {
            final int matchLength = match.length();
            final int matchStart = pos - matchLength + 1;
            if ( text.regionMatches(matchStart, match, 0, matchLength)) {
                matches.add(new MatchResult(matchStart, match));
            }
        }
        return matches;
    }

    
    private void calculateParameters(List<String> patterns) {
        // calculate basic parameters:
        shortestPatternLength = getShortestPatternLength(patterns);
        blockSize = calculateBlockSize(patterns);
        shiftTableSize = 1 << calculateShiftTablePowerTwoSize(patterns);
        shiftTableHashBitMask = shiftTableSize - 1;
        verifierTableSize = calculateVerifierTableSize(patterns);
    }

    
    private void buildTables(List<String> patterns) {
        shifts = new int[shiftTableSize];
        verifiers = new HashMap<String,List<String>>(verifierTableSize);

        initialiseDefaultShift();

        // For each pattern, calculate pattern-specific shifts and add a verifier:
        for (String pattern : patterns) {
             final int patternLength = pattern.length();
             String block = "";

             // for each block in a pattern, calculate its shift:
             for (int blockIndex = blockSize; blockIndex <= patternLength; blockIndex++) {
                 block = pattern.substring(blockIndex-blockSize, blockIndex);
                 final int hashValue = getBlockHash(block);
                 final int shiftValue = patternLength - blockIndex;
                 shifts[hashValue] = Math.min(shifts[hashValue], shiftValue);
             }

             addVerifier(block,pattern);
        }
    }


    private void initialiseDefaultShift() {
        final int defaultShift = shortestPatternLength - blockSize + 1;
        Arrays.fill(shifts, defaultShift);
    }


    private void addVerifier(String block, String pattern) {
         List<String> verifiersForBlock = verifiers.get(block);
         if (verifiersForBlock == null) {
             verifiersForBlock = new ArrayList<String>();
             verifiers.put(block, verifiersForBlock);
         }
         verifiersForBlock.add(pattern);
    }


    private int getBlockHash(String block) {
        return block.hashCode() & shiftTableHashBitMask;
    }


    private int getShortestPatternLength(List<String> patterns) {
        int shortestLength = Integer.MAX_VALUE;
        for (String pattern : patterns) {
            shortestLength = Math.min(shortestLength, pattern.length());
        }
        return shortestLength;
    }


    private int calculateBlockSize(List<String> patterns) {
        //TODO: use wu-manber heuristic for block size.
        return shortestPatternLength > 1 ? 2 : 1;
    }


    private int calculateShiftTablePowerTwoSize(List<String> patterns) {
        return 12; //TODO: calculate size based on number of patterns
    }

    private int calculateVerifierTableSize(List<String> patterns) {
        return 1024; //TODO: calculate verifier table size based on number of patterns.
    }

    
}
