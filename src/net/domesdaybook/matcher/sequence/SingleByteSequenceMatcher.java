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

    private final List<SingleByteMatcher> matcherSequence = new ArrayList<SingleByteMatcher>();
    private final int length;

    public SingleByteSequenceMatcher(final List<SingleByteMatcher> sequence) {
        this.matcherSequence.addAll(sequence);
        this.length = this.matcherSequence.size();
    }

    @Override
    public final boolean matches(final Bytes reader, final long matchFrom) {
        boolean result = true;
        final List<SingleByteMatcher> matchList = this.matcherSequence;
        final int localStop = length;
        for ( int byteIndex = 0; result && byteIndex < localStop; byteIndex++) {
            final SingleByteMatcher byteMatcher = matchList.get(byteIndex);
            final byte byteRead = reader.getByte(matchFrom + byteIndex);
            result = byteMatcher.matches(byteRead);
        }
        return result;
    }

    @Override
    public final SingleByteMatcher getByteMatcherForPosition(final int position) {
        return matcherSequence.get(position);
    }

    @Override
    public final int length() {
        return length;
    }

    @Override
    public final String toRegularExpression(final boolean prettyPrint) {
        StringBuilder builder = new StringBuilder();
        for (SingleByteMatcher matcher : matcherSequence) {
            builder.append(matcher.toRegularExpression(prettyPrint));
        }
        return builder.toString();
    }

}
