/*
 * Copyright Matt Palmer 2016-17, All rights reserved.
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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.AbstractSearcher;
import net.byteseek.utils.ArgUtils;

/**
 * An abstract base class for sequence searchers, providing implementations of Searcher list-based methods.
 * <p>
 * The SequenceSearcher defines some new search methods which only return a primitive int or long for a search
 * result.  This is because a sequence searcher can only match a single sequence at a single position, so there
 * is no need in general for lists of results.  Subclasses only need to implement the primitive methods - these
 * convenience methods wrap them in lists for the more general Searcher interface.  Using the more specific
 * primitive methods will avoid the creation of unnecessary search result objects and lists.
 *
 * @author Matt Palmer
 */
public abstract class AbstractSequenceSearcher<T> extends AbstractSearcher implements SequenceSearcher {

    /**
     * A convenient named constant for subclasses to use to indicate no match.
     * Any negative number means no match, don't rely on all implementations using this constant.
     */
    protected static final int NO_MATCH_SAFE_SHIFT = -1;

    /**
     * The Object which the Searcher should search for.
     */
    protected final T sequence;

    /**
     * Constructs a sequence searcher given a {@link SequenceMatcher}
     * to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     */
    public AbstractSequenceSearcher(final T sequence) {
        ArgUtils.checkNullObject(sequence, "Null sequence passed in to searcher.");
        this.sequence = sequence;
    }

    /**
     * Returns the {@link SequenceMatcher} to be searched for.
     *
     * @return SequenceMatcher the sequence matcher to be searched for.
     */
    public T getSequence() {
        return sequence;
    }

    /**
     * A method which returns the length of the sequence being searched.
     * @return the length of the sequence being searched.
     */
    protected abstract int getSequenceLength();

    /**
     * Methods to wrap sequence searcher methods, returning either a single result, or no results.
     */

    @Override
    public int searchForwards(final byte[] bytes,
                               final int fromPosition, final int toPosition,
                               final Collection<MatchResult> results) {
        final int matchPosition = searchSequenceForwards(bytes, fromPosition, toPosition);
        if (matchPosition >= 0) {
            results.add(new MatchResult(matchPosition, getSequenceLength()));
            return ONE_RESULT_FOUND;
        }
        return NO_RESULTS_FOUND;
    }

    @Override
    public int searchForwards(final WindowReader reader,
                              final long fromPosition, final long toPosition,
                              final Collection<MatchResult> results) throws IOException {
        final long matchPosition = searchSequenceForwards(reader, fromPosition, toPosition);
        if (matchPosition >= 0) {
            results.add(new MatchResult(matchPosition, getSequenceLength()));
            return ONE_RESULT_FOUND;
        }
        return NO_RESULTS_FOUND;
    }

    /**
     * Default implementations of the overloaded searchSequenceForwards methods.
     */

    @Override
    public long searchSequenceForwards(final WindowReader reader, final long fromPosition) throws IOException {
        return searchSequenceForwards(reader, fromPosition, Long.MAX_VALUE);
    }

    @Override
    public long searchSequenceForwards(final WindowReader reader) throws IOException {
        return searchSequenceForwards(reader, 0, Long.MAX_VALUE);
    }

    @Override
    public int searchSequenceForwards(final byte[] bytes, final int fromPosition) {
        return searchSequenceForwards(bytes, fromPosition, bytes.length - getSequenceLength());
    }

    @Override
    public int searchSequenceForwards(final byte[] bytes) {
        return searchSequenceForwards(bytes, 0, bytes.length - getSequenceLength());
    }

    /**
     * Methods to wrap sequence searcher methods, returning either a single result, or no results.
     */

    @Override
    public int searchBackwards(final byte[] bytes,
                                final int fromPosition, final int toPosition,
                                final Collection<MatchResult> results) {
        final int matchPosition = searchSequenceBackwards(bytes, fromPosition, toPosition);
        if (matchPosition >= 0) {
            results.add(new MatchResult(matchPosition, getSequenceLength()));
            return ONE_RESULT_FOUND;
        }
        return NO_RESULTS_FOUND;
    }

    @Override
    public int searchBackwards(final WindowReader reader,
                                final long fromPosition, final long toPosition,
                                final Collection<MatchResult> results) throws IOException {
        final long matchPosition = searchSequenceBackwards(reader, fromPosition, toPosition);
        if (matchPosition >= 0) {
            results.add(new MatchResult(matchPosition, getSequenceLength()));
            return ONE_RESULT_FOUND;
        }
        return NO_RESULTS_FOUND;
    }

    /**
     * Default implementations of the overloaded searchSequenceBackwards methods.
     */

    @Override
    public long searchSequenceBackwards(final WindowReader reader, final long fromPosition) throws IOException {
        return searchSequenceBackwards(reader, fromPosition, 0);
    }

    @Override
    public long searchSequenceBackwards(final WindowReader reader) throws IOException {
        return searchSequenceBackwards(reader, reader.length() - getSequenceLength(), 0);
    }

    @Override
    public int searchSequenceBackwards(final byte[] bytes, final int fromPosition) {
        return searchSequenceBackwards(bytes, fromPosition, 0);
    }

    @Override
    public int searchSequenceBackwards(final byte[] bytes) {
        return searchSequenceBackwards(bytes, bytes.length - getSequenceLength(), 0);
    }

}
