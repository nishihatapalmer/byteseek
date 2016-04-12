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

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.OneByteMatcher;
import net.byteseek.searcher.sequence.AbstractSequenceSearcher;

import java.io.IOException;

/**
 * A Searcher which just looks for a single byte value.
 * <p>
 * This is an incredibly simple search algorithm, just looking at every single byte until it finds
 * it, or not.
 */
public final class ByteSearcher extends AbstractSequenceSearcher<Byte> {

    private final byte toSearchFor;

    public ByteSearcher(final byte value) {
        super(value);
        toSearchFor = value;
    }

    public ByteSearcher(final Byte value) {
        super(value);
        toSearchFor = value;
    }

    public ByteSearcher(final OneByteMatcher value) {
        super(value == null? null : value.getMatchingBytes()[0]);
        toSearchFor = sequence.byteValue();
    }

    @Override
    public long searchSequenceForwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        final byte searchByte = toSearchFor;
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
                if (array[arraySearchPosition] == searchByte) {
                    final long matchPosition = searchPosition + arraySearchPosition - startWindowSearchPosition;
                    return matchPosition;
                }
            }

            // Move the search position onwards to the next window:
            searchPosition += (distanceToWindowEnd + 1);
        }
        return NO_MATCH;
    }

    @Override
    public int searchSequenceForwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        final byte searchByte = toSearchFor;
        final int lastPosition = toPosition < bytes.length?
                                 toPosition : bytes.length - 1;
        int searchPosition = fromPosition > 0? fromPosition : 0;
        while (searchPosition <= lastPosition) {
            if (searchByte == bytes[searchPosition]) {
                return searchPosition;
            }
            searchPosition++;
        }
        return NO_MATCH;
    }

    @Override
    public long searchSequenceBackwards(final WindowReader reader, final long fromPosition, final long toPosition) throws IOException {
        final byte searchByte = toSearchFor;
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
                if (array[arraySearchPosition] == searchByte) {
                    final long matchPosition = searchPosition - (startWindowSearchPosition - arraySearchPosition);
                    return matchPosition;
                }
            }

            // Move the search position onwards to the next window:
            searchPosition -= (startWindowSearchPosition + 1);
        }
        return NO_MATCH;
    }

    @Override
    public int searchSequenceBackwards(final byte[] bytes, final int fromPosition, final int toPosition) {
        final byte searchByte = toSearchFor;
        final int lastPosition = toPosition > 0? toPosition : 0;
        int searchPosition = fromPosition < bytes.length? fromPosition : bytes.length - 1;
        while (searchPosition >= lastPosition) {
            if (searchByte == bytes[searchPosition]) {
                return searchPosition;
            }
            searchPosition--;
        }
        return NO_MATCH;
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
        final int value = toSearchFor & 0xFF;
        return this.getClass().getSimpleName() + '[' + String.format("%02X", value) + ']';
    }

}
