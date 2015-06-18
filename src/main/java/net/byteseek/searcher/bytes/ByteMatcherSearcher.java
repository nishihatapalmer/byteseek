/*
 * Copyright Matt Palmer 2015, All rights reserved.
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

package net.byteseek.searcher.bytes;

import net.byteseek.io.reader.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.searcher.AbstractSearcher;
import net.byteseek.searcher.SearchResult;
import net.byteseek.searcher.SearchUtils;
import net.byteseek.utils.ArgUtils;

import java.io.IOException;
import java.util.List;

/**
 * A Searcher which looks for a byte which matches the ByteMatcher.
 * <p>
 * This is an incredibly simple search algorithm, just looking at every single byte until it finds
 * it, or not.
 */
public final class ByteMatcherSearcher extends AbstractSearcher<ByteMatcher> {

    private final ByteMatcher toSearchFor;

    public ByteMatcherSearcher(final ByteMatcher value) {
        ArgUtils.checkNullObject(value, "ByteMatcher passed in cannot be null.");
        toSearchFor = value;
    }

    @Override
    public List<SearchResult<ByteMatcher>> searchForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        final ByteMatcher searchByte = toSearchFor;
        long searchPosition = fromPosition >=0? fromPosition : 0;
        Window window;
        // While we have a window to search in:
        while ( searchPosition <= toPosition && (window = reader.getWindow(searchPosition)) != null) {
            final byte[] array = window.getArray();

            // Determine start and end points in the search for this window:
            final int  startWindowSearchPosition = reader.getWindowOffset(searchPosition);
            final int  distanceToWindowEnd = window.length() - 1 - startWindowSearchPosition;
            final long distanceToSearchEnd = toPosition - searchPosition;
            final int endWindowSearchPosition = distanceToWindowEnd < distanceToSearchEnd?
                    startWindowSearchPosition + distanceToWindowEnd :
                    startWindowSearchPosition + (int) distanceToSearchEnd;

            //TODO: performance tests: is it better to inline an array search method here,
            //      or just call the array search method itself?  Pros: the compiler may inline
            //      the array search method anyway, plus the array search method does bounds
            //      checking on the result, which may enable array bounds optimizations.

            // Search in the window array:
            for (int arraySearchPosition = startWindowSearchPosition;
                 arraySearchPosition <= endWindowSearchPosition; arraySearchPosition++) {
                if (toSearchFor.matches(array[arraySearchPosition])) {
                    final long matchPosition = searchPosition + arraySearchPosition - startWindowSearchPosition;
                    return SearchUtils.singleResult(matchPosition, toSearchFor);
                }
            }

            // Move the search position onwards to the next window:
            searchPosition += (distanceToWindowEnd + 1);
        }
        return SearchUtils.noResults();
    }

    @Override
    public List<SearchResult<ByteMatcher>> searchForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        final ByteMatcher searchByte = toSearchFor;
        final int startPosition = fromPosition >= 0? fromPosition : 0;
        final int endPosition   = toPosition < bytes.length? toPosition : bytes.length - 1;
        for (int searchPosition = startPosition; searchPosition <= endPosition; searchPosition++) {
            if (searchByte.matches(bytes[searchPosition])) {
                return SearchUtils.singleResult(searchPosition, searchByte);
            }
        }
        return SearchUtils.noResults();
    }

    @Override
    public List<SearchResult<ByteMatcher>> searchBackwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        final ByteMatcher searchByte = toSearchFor;
        long searchPosition = fromPosition;
        Window window;
        // While we have a window to search in:
        while ( searchPosition >= toPosition && (window = reader.getWindow(searchPosition)) != null) {
            final byte[] array = window.getArray();

            // Calculate safe start and end positions in the window array:
            final int  startWindowSearchPosition = reader.getWindowOffset(searchPosition);
            final long distanceToSearchEnd       = searchPosition - toPosition;
            final int  endWindowSearchPosition   = distanceToSearchEnd > startWindowSearchPosition?
                    0 : startWindowSearchPosition - (int) distanceToSearchEnd;

            //TODO: performance tests: is it better to inline an array search method here,
            //      or just call the array search method itself?  Pros: the compiler may inline
            //      the array search method anyway, plus the array search method does bounds
            //      checking on the result, which may enable array bounds optimizations.

            // Search in the window array:
            for (int arraySearchPosition = startWindowSearchPosition;
                 arraySearchPosition >= endWindowSearchPosition; arraySearchPosition--) {
                if (searchByte.matches(array[arraySearchPosition])) {
                    final long matchPosition = searchPosition - (startWindowSearchPosition - arraySearchPosition);
                    return SearchUtils.singleResult(matchPosition, searchByte);
                }
            }

            // Move the search position onwards to the next window:
            searchPosition -= (startWindowSearchPosition + 1);
        }
        return SearchUtils.noResults();
    }

    @Override
    public List<SearchResult<ByteMatcher>> searchBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        final ByteMatcher searchByte = toSearchFor;
        final int startPosition = fromPosition < bytes.length? fromPosition : bytes.length - 1;
        final int endPosition   = toPosition > 0? toPosition : 0;
        for (int searchPosition = startPosition; searchPosition >= endPosition; searchPosition--) {
            if (searchByte.matches(bytes[searchPosition])) {
                return SearchUtils.singleResult(searchPosition, searchByte);
            }
        }
        return SearchUtils.noResults();
    }

    @Override
    public void prepareForwards() {
        // Nothing to prepare in order to search.
    }

    @Override
    public void prepareBackwards() {
        // Nothing to prepare in order to search.
    }

    /**
     * Returns a string representation of this searcher.
     * The precise format returned is subject to change, but in general it will
     * return the type of searcher and the sequence being searched for.
     *
     * @return String a representation of the searcher.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '[' + toSearchFor + ']';
    }

}
