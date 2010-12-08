/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.matcher.singlebyte.BitMaskMatcher;
import net.domesdaybook.matcher.singlebyte.ByteClassMatcher;
import net.domesdaybook.reader.Bytes;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author matt
 */
public class CombinedSequenceMatcher implements SequenceMatcher {

    private class ByteMatcherIndex {
        public SequenceMatcher matcher;
        public int offset;
        public ByteMatcherIndex(final SequenceMatcher matcher, final int offset) {
            this.matcher = matcher;
            this.offset= offset;
        }
    }

    private List<SequenceMatcher> matchers;
    private List<ByteMatcherIndex> byteMatcherForPosition;
    private int length;

    public CombinedSequenceMatcher(final String expression) {
        fromExpression(expression);
        calculatePositions();
    }
    
    @Override
    public boolean matchesBytes(Bytes reader, long matchFrom) {
       boolean result = true;
        long matchAt = matchFrom;
        final List<SequenceMatcher> localList=matchers;
        for ( int matchIndex = 0, stop=localList.size(); matchIndex < stop; matchIndex++ ) {
            final SequenceMatcher matcher = localList.get( matchIndex );
            if (matcher.matchesBytes(reader, matchAt)) {
                matchAt += matcher.length();
            } else {
                result = false;
                break;
            }
        }
        return result;
    }


    @Override
    public int length() {
        return length;
    }

    @Override
    public String toRegularExpression(boolean prettyPrint) {
        StringBuilder regularExpression = new StringBuilder();
        for ( int matcherIndex = 0, lastMatcher = matchers.size();
            matcherIndex < lastMatcher; matcherIndex++ ) {
           final SequenceMatcher matcher = matchers.get(matcherIndex);
           regularExpression.append( matcher.toRegularExpression( prettyPrint ) );
        }
        return regularExpression.toString();
    }

    private void calculatePositions() {
        length = 0;
        byteMatcherForPosition = new ArrayList<ByteMatcherIndex>();
        for ( int seqIndex = 0, stop=matchers.size(); seqIndex < stop; seqIndex++ ) {
            final SequenceMatcher matcher = matchers.get(seqIndex);
            final int numberOfBytes = matcher.length();
            for (int matcherPos = 0; matcherPos < numberOfBytes; matcherPos++) {
                ByteMatcherIndex index = new ByteMatcherIndex(matcher,matcherPos);
                byteMatcherForPosition.add(index);
            }
            length += numberOfBytes;
        }
    }



    @Override
    public final SingleByteMatcher getByteMatcherForPosition(int position) {
        final ByteMatcherIndex index = byteMatcherForPosition.get(position);
        final SequenceMatcher matcher = index.matcher;
        return matcher.getByteMatcherForPosition(index.offset);
    }


    public List<SequenceMatcher> getMatchers() {
        return matchers;
    }

    private void fromExpression(final String byteSequenceSpec) {
        matchers = new ArrayList<SequenceMatcher>();
        int stringPos = 0;
        final int byteSequenceLength = byteSequenceSpec.length();
        while ( stringPos < byteSequenceLength ) {
            final String currentChar = byteSequenceSpec.substring( stringPos, stringPos + 1 );
            SequenceMatcher matcher = null;

            // byte class?
            if (currentChar.equals("[")) { // Is it a byte class?
                final int endSquareBracketPos = getClosingSetPosition(byteSequenceSpec, stringPos + 1);
                if (endSquareBracketPos > 0) {
                    final String byteClassSpec = byteSequenceSpec.substring(stringPos, endSquareBracketPos+1);
                    matcher = ByteClassMatcher.fromExpression(byteClassSpec);
                    stringPos = endSquareBracketPos + 1;
                } else {
                    throw new IllegalArgumentException( "No closing square bracket for byte class.");
                }
            }


            // ASCII case-sensitive string?
            else if (currentChar.equals("'")) {
                final int closingQuote = byteSequenceSpec.indexOf("'", stringPos + 1);
                if (closingQuote > 0 ) {
                    final String caseSensitiveASCIIString = byteSequenceSpec.substring(stringPos+1,closingQuote);
                    matcher = new CaseSensitiveStringMatcher(caseSensitiveASCIIString);
                    stringPos = closingQuote + 1;
                } else {
                    throw new IllegalArgumentException( "No closing quote ' for case-sensitive string.");
                }
            }

            // ASCII case-insensitive string?
            else if (currentChar.equals("`")) {
                final int closingQuote = byteSequenceSpec.indexOf("`", stringPos + 1);
                if ( closingQuote > 0 ) {
                    final String caseInsensitiveASCIIString = byteSequenceSpec.substring(stringPos+1,closingQuote);
                    matcher = new CaseInsensitiveStringMatcher(caseInsensitiveASCIIString);
                    stringPos = closingQuote + 1;
                } else {
                    throw new IllegalArgumentException( "No closing quote ` for case-insensitive string.");
                }
            }


            // bitmask?
            else if (currentChar.equals("&")) {
                if ( stringPos + 2 < byteSequenceLength ) {
                    final String hexBitMask = byteSequenceSpec.substring( stringPos, stringPos + 3);
                    matcher = BitMaskMatcher.fromExpression( hexBitMask );
                    stringPos += 3;
                } else {
                    throw new IllegalArgumentException( "No hex byte specified for & bit mask.");
                }
            }


            // hex bytes
            else { // might be a hex byte or sequence of hex bytes:
                // locate the end of the hex byte sequence:
                final int lastHexBytePos = getLastHexBytePosition(byteSequenceSpec, stringPos);
                if (lastHexBytePos > stringPos) {
                    final String hexBytes = byteSequenceSpec.substring(stringPos, lastHexBytePos + 1);
                    matcher = ByteSequenceMatcher.fromExpression(hexBytes);
                    stringPos = lastHexBytePos + 1;
                } else {
                    throw new IllegalArgumentException("Hex bytes not specified.");
                }
            }

            if ( matcher != null) {
                matchers.add(matcher);
            }
        }
    }

    private int getLastHexBytePosition(final String sequence, int fromPosition) {
        int searchPosition = fromPosition;
        while (searchPosition < sequence.length()) {
            final Character currentChar = sequence.charAt(searchPosition);
            if ((currentChar >= '0' && currentChar <= '9') ||
                (currentChar >= 'a' && currentChar <= 'f') ||
                (currentChar >= 'A' && currentChar <= 'F')) {
                searchPosition += 1;
            } else {
                break;
            }
        }
        return searchPosition - 1;
    }

    private int getClosingSetPosition(final String sequence, int fromPosition) {
        int searchPosition = fromPosition;
        boolean closingTagFound = false;
        int openingTags = 1;
        boolean inCaseSensitiveString = false;
        boolean inCaseInsensitiveString = false;
        while (searchPosition < sequence.length()) {
            final Character currentChar = sequence.charAt(searchPosition);
            if (inCaseSensitiveString) {
                inCaseSensitiveString = !currentChar.equals('\'');
            } else if (inCaseInsensitiveString) {
                inCaseInsensitiveString = !currentChar.equals('`');
            } else if (currentChar.equals('\'')) {
                inCaseSensitiveString = true;
            } else if (currentChar.equals('`')) {
                inCaseInsensitiveString = true;
            } else if (currentChar.equals('[')) {
                openingTags += 1;
            } else if (currentChar.equals(']')) {
                openingTags -= 1;
                closingTagFound = openingTags == 0;
                break;
            }
            searchPosition += 1;
        }
        return closingTagFound ? searchPosition : -1;
    }

}
