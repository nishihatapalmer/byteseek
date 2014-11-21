/*
 * Copyright Matt Palmer 2011-2012, All rights reserved.
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
import java.util.List;

import net.byteseek.io.reader.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.AbstractSearcher;
import net.byteseek.searcher.SearchResult;
import net.byteseek.searcher.SearchUtils;

/**
 * This abstract base class for sequence searchers holds the sequence to be
 * searched for and provides generic implementations of:
 * <ul>
 * <li>{@link #searchForwards(net.byteseek.io.reader.WindowReader, long, long)}
 * <li>{@link #searchBackwards(net.byteseek.io.reader.WindowReader, long, long)}
 * </ul>
 * These allocate searching for sequences efficiently between searching in the
 * byte arrays provided by {@link Window}s when the sequence fits in a single window,
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
public abstract class AbstractSequenceSearcher extends AbstractSearcher<SequenceMatcher> {
    
    /**
     * The SequenceMatcher which the Searcher should search for.
     */
    protected final SequenceMatcher matcher;
    
    /**
     * Constructs a sequence searcher given a {@link SequenceMatcher}
     * to search for.
     * 
     * @param sequence The SequenceMatcher to search for.
     */
    public AbstractSequenceSearcher(final SequenceMatcher sequence) {
        if (sequence == null) {
            throw new IllegalArgumentException("Null sequence passed in to searcher.");
        }        
        this.matcher = sequence;
    }    
    
    
    /**
     * Returns the {@link SequenceMatcher} to be searched for.
     * 
     * @return SequenceMatcher the sequence matcher to be searched for.
     */
    public SequenceMatcher getMatcher() {
        return matcher;
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
    public List<SearchResult<SequenceMatcher>> searchForwards(final WindowReader reader, 
            final long fromPosition, final long toPosition) throws IOException {
        // Initialise:
        final int sequenceLength = matcher.length();
        final int lastSequencePosition = sequenceLength - 1;
        long searchPosition = fromPosition > 0?
                              fromPosition : 0;
        
        // While there is data to search in:
        Window window;        
        while (searchPosition <= toPosition &&
               (window = reader.getWindow(searchPosition)) != null) {
            
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
                final List<SearchResult<SequenceMatcher>> arrayResult = 
                    searchForwards(window.getArray(), arrayStartPosition, arrayMaxPosition);

                // Did we find a match?
                if (!arrayResult.isEmpty()) {
                    final long readerPositionOffset = searchPosition - arrayStartPosition;
                    return SearchUtils.addPositionToResults(arrayResult, readerPositionOffset);
                }
                
                // Continue the search one on from where we last looked:
                searchPosition += (arrayMaxPosition - arrayStartPosition + 1);

                // Did we pass the final toPosition?  In which case, we're finished.
                if (searchPosition > toPosition) {
                    return SearchUtils.noResults();
                }
            }

            // From the current search position, the sequence crosses over in to
            // the next window, so we can't search directly in the window byte array.
            // We must use the reader interface on the sequence to let it match
            // over more bytes than this window has available.
            
            // Search up to the last position in the window, or the toPosition,
            // whichever comes first:
            final long lastWindowPosition = windowStartPosition + arrayLastPosition;
            final long lastSearchPosition = toPosition < lastWindowPosition?
                                            toPosition : lastWindowPosition;
            final List<SearchResult<SequenceMatcher>> readerResult = 
                    doSearchForwards(reader, searchPosition, lastSearchPosition);
            
            // Did we find a match?
            if (!readerResult.isEmpty()) {
                return readerResult;
            }
            
            // Continue the search one on from where we last looked:
            searchPosition = lastSearchPosition + 1;
        }
        
        return SearchUtils.noResults();
    }

    
    /**
     * This method searches forwards crossing window boundaries.  It is
     * called by the {@link #searchForwards(net.byteseek.io.reader.WindowReader, long, long)}
     * method when it encounters a sequence which crosses from one window to another.
     * <p>
     * A simple way to implement this method is to use the WindowReader interface on the
     * matcher sequence. This at least removes window boundaries from validating
     * that a match exists. It will still be necessary to deal with window management
     * in the operation of the search algorithm itself.
     * <p>
     * Implementations of this method do not need to worry about whether the search
     * position parameters are within the reader, as this bounds checking is done
     * by the searchForwards method which calls it.
     * 
     * @param reader The reader providing bytes to search in.
     * @param fromPosition The search position to search from.
     * @param toPosition The search position to search to.
     * @return The position of a match, or a negative number if no match was found.
     * @throws IOException If the reader encounters difficulties reading bytes.
     */
    protected abstract List<SearchResult<SequenceMatcher>> doSearchForwards(WindowReader reader, 
            long fromPosition, long toPosition) throws IOException;

    
    
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
    public List<SearchResult<SequenceMatcher>> searchBackwards(final WindowReader reader, 
            final long fromPosition, final long toPosition) throws IOException {
        // Initialise:
        final int lastSequencePosition = matcher.length() - 1;
        final long finalSearchPosition = toPosition > 0?
                                         toPosition : 0;
        long searchPosition = withinLength(reader, fromPosition);
        
        // While there is data to search in:
        Window window;        
        while (searchPosition >= finalSearchPosition &&
               (window = reader.getWindow(searchPosition)) != null) {
            // Get some info about the window:
            final long windowStartPosition = window.getWindowPosition();
            final int arrayStartSearchPosition = reader.getWindowOffset(searchPosition);              
            final int arrayLastPosition = window.length() - 1;                         
            
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
                final List<SearchResult<SequenceMatcher>> arrayResults = 
                        searchBackwards(window.getArray(), 
                                        arrayStartSearchPosition, 
                                        arrayEndSearchPosition);
                
                // Did we find any matches?
                if (!arrayResults.isEmpty()) {
                    final long readerOffset = searchPosition - arrayStartSearchPosition;
                    return SearchUtils.addPositionToResults(arrayResults, readerOffset);
                }
                
                // Calculate the search position for one behind where we've looked in the array:
                final int arrayBytesSearched = arrayStartSearchPosition - arrayEndSearchPosition + 1;
                searchPosition -= arrayBytesSearched;

                // Did we pass the final search position already?
                if (searchPosition < finalSearchPosition) {
                    return SearchUtils.noResults();
                }
            }

            // From the current search position, the sequence crosses over in to
            // the next window, so we can't search directly in the window byte array.
            // We must use the reader interface on the sequence to let it match
            // over more bytes than this window has available.
            
            // Search back to the first position in this window where the sequence 
            // would fit inside it (so we can use the array search on the next
            // loop around), or the beginning of this window.  Windows may not always
            // have the same length (in particular, the last window), so just because
            // the sequence is too big to fit into one window doesn't mean we can
            // infer it won't fit into subsequent windows.  Therefore, we proceed on
            // a window by window basis.
            final long firstPossibleFitPosition =
                    windowStartPosition + arrayLastPosition - lastSequencePosition;
            final long firstFitPosition = firstPossibleFitPosition < searchPosition?
                                          firstPossibleFitPosition : searchPosition;
            final long searchToPosition = firstFitPosition > windowStartPosition?
                                          firstFitPosition : windowStartPosition;
            
            final List<SearchResult<SequenceMatcher>> readerResult =
                    doSearchBackwards(reader, searchPosition, searchToPosition);
            
            // Did we find a match?
            if (!readerResult.isEmpty()) {
                return readerResult;
            }
            
            // Continue the search one on from where we last looked:
            searchPosition = searchToPosition - 1;
        }
        
        return SearchUtils.noResults();
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
     * 
     * @param reader The reader providing bytes to search in.
     * @param fromPosition The search position to search from.
     * @param toPosition The search position to search to.
     * @return The position of a match, or a negative number if no match was found.
     * @throws IOException If the reader encounters difficulties reading bytes.
     */    
    protected abstract List<SearchResult<SequenceMatcher>> doSearchBackwards(WindowReader reader,
            long fromPosition, long toPosition) throws IOException;
    
    
    
    /**
     * Returns a string representation of this searcher.
     * The precise format returned is subject to change, but in general it will
     * return the type of searcher and the sequence being searched for.
     * 
     * @return String a representation of the searcher.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + matcher + ')';
    }        
}
