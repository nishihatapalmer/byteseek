/*
 * Copyright Matt Palmer 2020, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.byteseek.searcher.sequence;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.sequence.factory.SequenceSearcherFactory;
import net.byteseek.utils.ArgUtils;

import java.io.IOException;

/**
 * A class that searches for a subsequence which is bounded to the left or right by some additional sequences.
 * Some patterns contain wildcards, often at the starts or ends of a pattern, which make it hard to search for
 * efficiently.  This allows us to search for an efficient sub-sequence of a longer sequence, then match the bits
java * that lie to the left or right of the subsequence to complete a match of the full sequence.
 */
public class SubsequenceSearcher extends AbstractSequenceSearcher<SequenceMatcher> {

    private final SequenceMatcher subSequence;
    private final SequenceSearcher forwardSearcher;
    private final SequenceSearcher backwardSearcher;
    private final SequenceMatcher leftMatch;
    private final SequenceMatcher rightMatch;
    private final int leftOffset;
    private final int rightOffset;

    /**
     * Constructs a SubsequenceSearcher given the subsequence to search for, a factory to create a searcher for it,
     * an optional leftMatch, which matches to the left of the subsequence, and an optional rightMatch which must
     * match to the right of the subsequence.  Either of these can be null.  If both are null, this is no different
     * to just using a searcher on the subsequence directly (as there is nothing else to the left or right of the
     * subsequence to match).
     *
     * @param subSequence The subsequence to search for.
     * @param searcherFactory A factory to create a search over the subSequence.
     * @param leftMatch A matcher that must match to the left of the subsequence, or null if not required.
     * @param rightMatch A matcher that must match at the right of the subsequence, or null if not required.
     */
    public SubsequenceSearcher(final SequenceMatcher subSequence, final SequenceSearcherFactory searcherFactory,
                               final SequenceMatcher leftMatch, final SequenceMatcher rightMatch) {
        super(subSequence);
        ArgUtils.checkNullObject(searcherFactory, "searcherFactory");
        this.subSequence = subSequence;
        this.forwardSearcher = searcherFactory.createForwards(subSequence);
        this.backwardSearcher = searcherFactory.createBackwards(subSequence);
        this.leftMatch = leftMatch;
        this.rightMatch = rightMatch;
        this.leftOffset = leftMatch == null? 0 : leftMatch.length();
        this.rightOffset = rightMatch == null? 0 : rightMatch.length();
    }

    @Override
    protected int getSequenceLength() {
        return subSequence.length();
    }

    @Override
    public long searchSequenceForwards(WindowReader reader, long fromPosition, long toPosition) throws IOException {
        final SequenceSearcher localSearcher = forwardSearcher;
        final SequenceMatcher localLeftMatcher = leftMatch;
        final SequenceMatcher localRightMatcher = rightMatch;
        final int localLeftOffset = leftOffset;
        long searchPos = addLongPositionsAvoidOverflows(fromPosition, localLeftOffset);
        long searchEnd = addLongPositionsAvoidOverflows(toPosition, localLeftOffset);
        long result;
        while ((result = localSearcher.searchSequenceForwards(reader, searchPos, searchEnd)) >= 0) {
            final long fullSequenceStart = result - localLeftOffset;
            boolean allMatched = localLeftMatcher == null || localLeftMatcher.matches(reader, fullSequenceStart);
            if (allMatched && localRightMatcher != null) {
                allMatched = localRightMatcher.matches(reader, result + subSequence.length());
            }
            if (allMatched) {
                return fullSequenceStart;
            }
            searchPos = result + 1; // no match - start searching one on from last search match.
        }
        return result;
    }

    @Override
    public int searchSequenceForwards(byte[] bytes, int fromPosition, int toPosition) {
        final SequenceSearcher localSearcher = forwardSearcher;
        final SequenceMatcher localLeftMatcher = leftMatch;
        final SequenceMatcher localRightMatcher = rightMatch;
        final int localLeftOffset = leftOffset;
        int searchPos = addIntegerPositionsAvoidOverflows(fromPosition, localLeftOffset);
        int searchEnd = addIntegerPositionsAvoidOverflows(toPosition, localLeftOffset);
        int result;
        while ((result = localSearcher.searchSequenceForwards(bytes, searchPos, searchEnd)) >= 0) {
            final int fullSequenceStart = result - localLeftOffset;
            boolean allMatched = localLeftMatcher == null ? true : localLeftMatcher.matches(bytes, fullSequenceStart);
            if (allMatched && localRightMatcher != null) {
                allMatched = localRightMatcher.matches(bytes, result + subSequence.length());
            }
            if (allMatched) {
                return fullSequenceStart;
            }
            searchPos = result + 1; // no match - start searching one on from last search match.
        }
        return result;
    }

    @Override
    public long searchSequenceBackwards(WindowReader reader, long fromPosition, long toPosition) throws IOException {
        final SequenceSearcher localSearcher = backwardSearcher;
        final SequenceMatcher localLeftMatcher = leftMatch;
        final SequenceMatcher localRightMatcher = rightMatch;
        final int localLeftOffset = leftOffset;
        final int localRightOffset = rightOffset;
        long searchPos = fromPosition - localRightOffset;
        long searchEnd = toPosition - localRightOffset;
        long result;
        while ((result = localSearcher.searchSequenceBackwards(reader, searchPos, searchEnd)) >= 0) {
            final long fullSequenceStart = result - localLeftOffset;
            boolean allMatched = localLeftMatcher == null ? true : localLeftMatcher.matches(reader, fullSequenceStart);
            if (allMatched && localRightMatcher != null) {
                allMatched = localRightMatcher.matches(reader, result + subSequence.length());
            }
            if (allMatched) {
                return fullSequenceStart;
            }
            searchPos = result - 1; // no match - start searching one back from last search match.
        }
        return result;
    }

    @Override
    public int searchSequenceBackwards(byte[] bytes, int fromPosition, int toPosition) {
        final SequenceSearcher localSearcher = backwardSearcher;
        final SequenceMatcher localLeftMatcher = leftMatch;
        final SequenceMatcher localRightMatcher = rightMatch;
        final int localLeftOffset = leftOffset;
        final int localRightOffset = rightOffset;
        int searchPos = fromPosition - localRightOffset;
        int searchEnd = toPosition - localRightOffset;
        int result;
        while ((result = localSearcher.searchSequenceBackwards(bytes, searchPos, searchEnd)) >= 0) {
            final int fullSequenceStart = result - localLeftOffset;
            boolean allMatched = localLeftMatcher == null ? true : localLeftMatcher.matches(bytes, fullSequenceStart);
            if (allMatched && localRightMatcher != null) {
                allMatched = localRightMatcher.matches(bytes, result + subSequence.length());
            }
            if (allMatched) {
                return fullSequenceStart;
            }
            searchPos = result - 1; // no match - start searching one back from last search match.
        }
        return result;
    }

    @Override
    public void prepareForwards() {
        forwardSearcher.prepareForwards();
    }

    @Override
    public void prepareBackwards() {
        backwardSearcher.prepareBackwards();
    }
}
