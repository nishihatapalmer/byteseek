/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import java.util.ArrayList;
import java.util.List;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.Bytes;

/**
 *
 * @author matt
 */
public class SingleByteSequenceMatcher implements SequenceMatcher {

    private List<SingleByteMatcher> matcherSequence = new ArrayList<SingleByteMatcher>();
    private int length;

    public SingleByteSequenceMatcher(List<SingleByteMatcher> sequence) {
        this.matcherSequence.addAll(sequence);
        this.length = this.matcherSequence.size();
    }

    @Override
    public boolean matchesBytes(Bytes reader, long matchFrom) {
        boolean result = true;
        final List<SingleByteMatcher> matchList = this.matcherSequence;
        final int localStop = length;
        for ( int byteIndex = 0; result && byteIndex < localStop; byteIndex++) {
            final SingleByteMatcher byteMatcher = matchList.get(byteIndex);
            final byte byteRead = reader.getByte(matchFrom + byteIndex);
            result = byteMatcher.matchesByte(byteRead);
        }
        return result;
    }

    @Override
    public SingleByteMatcher getByteMatcherForPosition(int position) {
        return matcherSequence.get(position);
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public String toRegularExpression(boolean prettyPrint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
