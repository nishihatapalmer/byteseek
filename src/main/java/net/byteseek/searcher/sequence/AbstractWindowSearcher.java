/*
 * Copyright Matt Palmer 2011-2017, All rights reserved.
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

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.SequenceMatcher;

/**
 * This abstract base class for sequence searchers provides generic implementations of:
 * <ul>
 * <li>{@link #searchForwards(net.byteseek.io.reader.WindowReader, long, long)}
 * <li>{@link #searchBackwards(net.byteseek.io.reader.WindowReader, long, long)}
 * </ul>
 * These allocate searching for sequences efficiently between searching in the
 * byte arrays provided by {@link net.byteseek.io.reader.windows.Window}s when the sequence fits in a single window,
 * only using the less efficient reader interface on the sequence for times when
 * the sequence crosses over Window boundaries.
 * <p>
 * It defines two new abstract methods:
 * <ul>
 * <li>{@link #doSearchForwards(net.byteseek.io.reader.WindowReader, long, long) }
 * <li>{@link #doSearchBackwards(net.byteseek.io.reader.WindowReader, long, long) }
 * </ul>
 * which require the implementor to use the reader interface on the sequence for
 * matching (or otherwise provide for searching sequences which cross window boundaries).
 *
 * @author Matt Palmer
 */
public abstract class AbstractWindowSearcher<T> extends AbstractSequenceSearcher<T> {

    /**
     * Constructs a sequence searcher given a {@link SequenceMatcher}
     * to search for.
     *
     * @param sequence The SequenceMatcher to search for.
     */
    public AbstractWindowSearcher(final T sequence) {
        super(sequence);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation of searchForwards allocates forward searching between
     * searching directly on a window byte array when the sequence fits inside
     * a window, and using the abstract search method:
     * {@link #doSearchForwards(net.byteseek.io.reader.WindowReader, long, long) }
     * for searching across window boundaries.
     * <p>
     * This method does no searching itself - it simply calculates how to
     * efficiently search using a sequence, and calls the appropriate search
     * methods on the search implementation.  Therefore, this is entirely generic for
     * any search algorithm that operates over sequences.
     *
     * @throws IOException If the reader encounters a problem reading bytes.
     */
    @Override
    public long searchSequenceForwards(final WindowReader reader,
                                       final long fromPosition, final long toPosition) throws IOException {
        // Initialise:
        final int sequenceLength = getSequenceLength();
        final int lastSequencePosition = sequenceLength - 1;
        long searchPosition = fromPosition > 0? fromPosition : 0;

        // While there is data to search in:
        Window window = null;
        while (searchPosition <= toPosition && (window = reader.getWindow(searchPosition)) != null) {

            // Does the sequence fit into the searchable bytes of this window?
            // It may not if the start position of the window is already close
            // to the end of the window, or the sequence is long (potentially
            // could be longer than any single window - but mostly won't be):
            final long windowStartPosition = window.getWindowPosition();
            final int windowLength = window.length();
            final int arrayStartPosition = reader.getWindowOffset(searchPosition);
            final int arrayLastPosition = windowLength - 1;
            if (arrayStartPosition + lastSequencePosition <= arrayLastPosition) {

                // Find the last point in the array where the sequence still fits
                // inside the array, or the toPosition if it is smaller.
                final int lastMatchingPosition = arrayLastPosition - lastSequencePosition;
                final long distanceToEnd = toPosition - windowStartPosition;
                final int arrayMaxPosition = distanceToEnd < lastMatchingPosition?
                                       (int) distanceToEnd : lastMatchingPosition;

                // Search forwards in the byte array of the window:
                final int arrayResult = searchSequenceForwards(window.getArray(), arrayStartPosition, arrayMaxPosition);

                // Did we find a match?
                if (arrayResult >= 0) {
                    return searchPosition - arrayStartPosition + arrayResult;
                }

                // Continue the search from the next available position (subtract negative arrayResult to get shift):
                searchPosition += (arrayMaxPosition - arrayStartPosition - arrayResult);

                // Did we pass the final toPosition?  In which case, we're finished.
                if (searchPosition > toPosition) {
                    return toPosition - searchPosition; // return next possible shift as a negative number.
                }
            }

            // It is likely that the sequence now crosses over into another window.
            // If so, we have to search across the window boundary, up to the end of the current window.
            // It is also possible we have shifted entirely into another window.

            //Calculate the last possible search position.
            final long lastWindowPosition = windowStartPosition + arrayLastPosition;
            final long lastSearchPosition = toPosition < lastWindowPosition? toPosition : lastWindowPosition;

            // If we are still within the current window, search up to the end of it using the reader searcher:
            if (searchPosition <= lastSearchPosition) {
                final long readerResult = doSearchForwards(reader, searchPosition, lastSearchPosition);

                // Did we find a match?
                if (readerResult >= 0) {
                    return readerResult;
                }

                // Continue the search, moving on by subtracting the (negative) reader result.
                searchPosition = lastSearchPosition - readerResult;
            }
        }

        return window == null? NO_MATCH_SAFE_SHIFT : toPosition - searchPosition;
    }

    /**
     * This method searches forwards crossing window boundaries.  It is
     * called by the {@link #searchForwards(net.byteseek.io.reader.WindowReader, long, long)}
     * method when it encounters a sequence which crosses from one window to the next window.
     * <p>
     * A simple way to implement this method is to use the WindowReader interface on the
     * matcher sequence. This at least removes window boundaries from validating
     * that a match exists. It will still be necessary to deal with window management
     * in the operation of the search algorithm itself.
     * <p>
     * This method is called by the searchForwards() method, which will always request a search
     * from the position in its current window where a potential match would have to cross over into another window,
     * up to the last position in its window or the requested end of the search, whichever comes first.
     * It does not guarantee that the length of the WindowReader input source is long enough for a match
     * (e.g. the next window may not exist).
     * <p>
     * If a match is found, the method returns the position of the match.  If no match is found, the algorithm
     * should return a safe shift to make as a *negative* number.  It is always safe to return -1.
     *
     * @param reader The reader providing bytes to search in.
     * @param fromPosition The search position to search from.
     * @param toPosition The search position to search to.
     * @return The position of a match, or a negative number if no match was found.
     * @throws IOException If the reader encounters difficulties reading bytes.
     */
    protected abstract long doSearchForwards(WindowReader reader, long fromPosition, long toPosition) throws IOException;

    /**
     * {@inheritDoc}
     * <p>
     * This implementation of searchBackwards allocates backwards searching between
     * searching directly on a window byte array when the sequence fits inside
     * a window, and using the abstract search method:
     * {@link #doSearchBackwards(net.byteseek.io.reader.WindowReader, long, long) }
     * for searching across window boundaries.
     * <p>
     * This method does no searching itself - it simply calculates how to
     * efficiently search using a sequence, and calls the appropriate search
     * methods on the search implementation.  Therefore, this is entirely generic for
     * any search algorithm that operates over sequences.
     *
     * @throws IOException If the reader encounters a problem reading bytes.
     */
    @Override
    public long searchSequenceBackwards(final WindowReader reader,
                                        final long fromPosition, final long toPosition) throws IOException {

        // Initialise:
        final int lastSequencePosition = getSequenceLength() - 1;
        final long finalSearchPosition = Math.max(0L, toPosition);
        long searchPosition = withinLength(reader, fromPosition);

        // While there is data to search in:
        Window window = null;
        while (searchPosition >= finalSearchPosition && (window = reader.getWindow(searchPosition)) != null) {

            // Get some info about the window:
            final long windowStartPosition     = window.getWindowPosition();
            final int arrayStartSearchPosition = reader.getWindowOffset(searchPosition);
            final int arrayLastPosition        = window.length() - 1;

            // Does the sequence fit into the searchable bytes of this window 
            // from the current search position?  If it does, we can search
            // directly on the byte array of this window, which is faster:
            if (arrayStartSearchPosition + lastSequencePosition <= arrayLastPosition) {

                // Search either up to the beginning of the array, or the final
                // search position, if it happens to fall past the start of this window:
                final long endOfSearchRelativeToWindow = finalSearchPosition - windowStartPosition;
                final int arrayEndSearchPosition = endOfSearchRelativeToWindow > 0?
                                             (int) endOfSearchRelativeToWindow : 0;

                // Search backwards in the byte array of the window:
                final int arrayResult = searchSequenceBackwards(window.getArray(), arrayStartSearchPosition, arrayEndSearchPosition);

                // Did we find any matches?
                if (arrayResult >= 0) {
                    return searchPosition - arrayStartSearchPosition + arrayResult;
                }

                // Calculate the search position for the next place to search.
                searchPosition -= arrayStartSearchPosition - arrayEndSearchPosition - arrayResult;

                // Did we pass the final search position already?
                if (searchPosition < finalSearchPosition) {
                    return searchPosition - finalSearchPosition;
                }
            }

            //TODO: IMPORTANT!!! since we are now potentially shifting by more than one, if we shift by search length, we might end up back in an array search.

            // The sequence is either just crossing back into the previous window,
            // or it is crossing over into the next window (it can be both for a long sequence, but this doesn't matter,
            // what matters is whether the searchPosition is within the current window or the previous one,
            // so we can calculate the correct position to search to.

            final long possiblePos = windowStartPosition - lastSequencePosition +               // end of pattern aligned with current window start.
                    (searchPosition < windowStartPosition? 0 : window.length()); // make it the next window if we're still in the current one.
            final long searchToPosition = possiblePos > finalSearchPosition? possiblePos : finalSearchPosition;

            // If there is still something to search by the reader (sequence not entirely out of current window)...
            if (searchPosition >= searchToPosition) {

                // Search backwards using the reader when crossing window boundaries.
                final long readerResult = doSearchBackwards(reader, searchPosition, searchToPosition);

                // Did we find a match?
                if (readerResult >= 0) {
                    return readerResult;
                }

                // Continue the search at the next safe position (reader result is negative, so add it to move backwards)
                searchPosition = searchToPosition + readerResult;
            }
        }

        return window == null? NO_MATCH_SAFE_SHIFT : searchPosition - finalSearchPosition;
    }

    /**
     * This abstract method searches backwards crossing window boundaries.  It is
     * called by the {@link #searchBackwards(net.byteseek.io.reader.WindowReader, long, long)}
     * method when it encounters s sequence which crosses from one window to another.
     * <p>
     * A simple way to implement this method is to use the WindowReader interface on the
     * matcher sequence.  This at least removes window boundaries from validating
     * that a match exists.  It may still be necessary to deal with window management
     * in the operation of the search algorithm itself.
     * <p>
     * If a match is found, the method returns the position of the match.  If no match is found, the algorithm
     * should return a safe shift to make as a *negative* number.  It is always safe to return -1.
     *
     * @param reader The reader providing bytes to search in.
     * @param fromPosition The search position to search from.
     * @param toPosition The search position to search to.
     * @return The position of a match, or a negative number if no match was found.
     * @throws IOException If the reader encounters difficulties reading bytes.
     */
    protected abstract long doSearchBackwards(WindowReader reader, long fromPosition, long toPosition) throws IOException;


    /**
     * Returns a string representation of this searcher.
     * The precise format returned is subject to change, but in general it will
     * return the type of searcher and the sequence being searched for.
     *
     * @return String a representation of the searcher.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + sequence + ')';
    }

}
