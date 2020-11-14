/*
 * Copyright Matt Palmer 2016-19, All rights reserved.
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

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.AbstractSearcher;
import net.byteseek.utils.ArgUtils;

/**
 * An abstract base class for sequence searchers, providing default implementations of various methods.
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
     * A named constant for subclasses to use to indicate no match, where the only known safe shift is one.
     */
    protected static final int NO_MATCH_SAFE_SHIFT = -1;

    /**
     * The sequence to search for.
     */
    protected final T sequence;

    /**
     * Constructs a sequence searcher given a {@link SequenceMatcher} to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     */
    public AbstractSequenceSearcher(final T sequence) {
        ArgUtils.checkNullObject(sequence, "Null sequence passed in to searcher.");
        this.sequence = sequence;
    }

    /**
     * Returns the sequence to be searched for.
     *
     * @return the sequence to be searched for.
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
        return searchSequenceForwards(bytes, fromPosition, bytes.length - 1);
    }

    @Override
    public int searchSequenceForwards(final byte[] bytes) {
        return searchSequenceForwards(bytes, 0, bytes.length - 1);
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
        return searchSequenceBackwards(reader, reader.length() - 1, 0);
    }

    @Override
    public int searchSequenceBackwards(final byte[] bytes, final int fromPosition) {
        return searchSequenceBackwards(bytes, fromPosition, 0);
    }

    @Override
    public int searchSequenceBackwards(final byte[] bytes) {
        return searchSequenceBackwards(bytes, bytes.length - 1, 0);
    }

    /**
     * Calculates the addition of two integer positions, avoiding integer overflow.  If the first position < 0,
     * then the result is just the second position.  last sequence position must not be negative.
     *
     * @param position The position to search from/to.
     * @param lastSequencePosition The last position of a sequence to search for.
     * @return The addition of the two positions, avoiding integer overflow and not less than lastsequenceposition.
     */
    protected final int addIntegerPositionsAvoidOverflows(int position, int lastSequencePosition) {
        return position <= 0 ? lastSequencePosition :
                position < Integer.MAX_VALUE - lastSequencePosition ? position + lastSequencePosition : Integer.MAX_VALUE;
    }

    /**
     * Calculates the addition of two integers, one which can be either negative or positive, and a second
     * which must be positive, avoiding any integer overflow in the addition (ceiling is Integer.MAX_VALUE).
     *
     * @param anyInt An integer which can be negative or positive.
     * @param positiveInt An integer to add to the other which must be positive.
     * @return The sum of the two integers, avoiding integer overflow.
     */
    protected final int addIntegerAvoidOverflows(int anyInt, int positiveInt) {
        return anyInt < Integer.MAX_VALUE - positiveInt? anyInt + positiveInt : Integer.MAX_VALUE;
    }

    /**
     * Calculates the addition of two long positions, avoiding long overflow.  If the first position < 0,
     * then the result is just the second position.  last sequence position must not be negative.
     *
     * @param position The position to search from/to.
     * @param lastSequencePosition The last position of a sequence to search for.
     * @return The addition of the two positions, avoiding integer overflow and not less than lastsequenceposition.
     */
    protected final long addLongPositionsAvoidOverflows(long position, long lastSequencePosition) {
        return position <= 0 ? lastSequencePosition :
                position < Long.MAX_VALUE - lastSequencePosition ? position + lastSequencePosition : Long.MAX_VALUE;
    }

    /**
     * Calculates the addition of two longs, one which can be either negative or positive, and a second
     * which must be positive, avoiding any long overflow in the addition (ceiling is Long.MAX_VALUE).
     *
     * @param anyLong A long which can be negative or positive.
     * @param positiveLong A long to add to the other which must be positive.
     * @return The sum of the two longs, avoiding long overflow.
     */
    protected final long addLongAvoidOverflows(long anyLong, long positiveLong) {
        return anyLong < Long.MAX_VALUE - positiveLong? anyLong + positiveLong : Long.MAX_VALUE;
    }

}
